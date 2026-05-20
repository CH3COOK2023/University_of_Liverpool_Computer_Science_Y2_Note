import argparse
import copy
import time

import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import torchvision
from torch.autograd import Variable
from torch.utils.data import DataLoader
from torchvision import transforms

id_ = 201945844

parser = argparse.ArgumentParser(description='PyTorch MNIST Training')
parser.add_argument('--batch-size', type=int, default=128, metavar='N',
                    help='input batch size for training (default: 128)')
parser.add_argument('--test-batch-size', type=int, default=128, metavar='N',
                    help='input batch size for testing (default: 128)')
parser.add_argument('--epochs', type=int, default=10, metavar='N', help='number of epochs to train')
parser.add_argument('--lr', type=float, default=0.01, metavar='LR', help='learning rate')
parser.add_argument('--no-cuda', action='store_true', default=False, help='disables CUDA training')
parser.add_argument('--seed', type=int, default=1, metavar='S', help='random seed (default: Attack_V3.txt)')

args = parser.parse_args(args=[])

# judge cuda is available or not
use_cuda = not args.no_cuda and torch.cuda.is_available()
device = torch.device("cuda" if use_cuda else "cpu")
print("use cuda:", device.type)

torch.manual_seed(args.seed)
kwargs = {'num_workers': 4, 'pin_memory': True} if use_cuda else {}

train_set = torchvision.datasets.FashionMNIST(root='data', train=True, download=True,
                                              transform=transforms.Compose([transforms.ToTensor()]))
train_loader = DataLoader(train_set, batch_size=args.batch_size, shuffle=True)

test_set = torchvision.datasets.FashionMNIST(root='data', train=False, download=True,
                                             transform=transforms.Compose([transforms.ToTensor()]))
test_loader = DataLoader(test_set, batch_size=args.batch_size, shuffle=True)


class Net(nn.Module):
    def __init__(self):
        super(Net, self).__init__()
        self.fc1 = nn.Linear(28 * 28, 128)
        self.fc2 = nn.Linear(128, 64)
        self.fc3 = nn.Linear(64, 32)
        self.fc4 = nn.Linear(32, 10)

    def forward(self, x):
        x = self.fc1(x)
        x = F.relu(x)
        x = self.fc2(x)
        x = F.relu(x)
        x = self.fc3(x)
        x = F.relu(x)
        x = self.fc4(x)
        output = F.log_softmax(x, dim=1)
        return output


def adv_attack(model, X, y, device):
    epsilon = 0.1

    num_restarts = 24
    explore_steps = 5

    finetune_steps = 35

    alpha = 0.015
    decay = 1.0
    num_classes = 10

    model.eval()

    batch_size = X.size(0)
    input_shape = X.shape[1:]

    X_orig_exp = X.repeat_interleave(num_restarts, dim=0).to(device)
    y_exp = y.repeat_interleave(num_restarts, dim=0).to(device)

    X_adv = X_orig_exp.clone()
    X_adv = X_adv + torch.zeros_like(X_adv).uniform_(-epsilon, epsilon).to(device)
    X_adv = torch.clamp(X_adv, 0.0, 1.0)

    grad_momentum = torch.zeros_like(X_adv).to(device)

    for _ in range(explore_steps):
        X_adv.requires_grad = True
        with torch.enable_grad():
            output = model(X_adv)

            true_logits = output.gather(1, y_exp.view(-1, 1)).squeeze(1)
            one_hot_true = F.one_hot(y_exp, num_classes=num_classes).bool()
            other_logits = output.masked_fill(one_hot_true, -1e9)
            max_wrong_logits, _ = other_logits.max(dim=1)

            margin = max_wrong_logits - true_logits
            loss = -margin.mean()

        grad = torch.autograd.grad(loss, X_adv)[0]

        dims_to_sum = list(range(1, grad.dim()))
        grad_norm = grad.abs().sum(dim=dims_to_sum, keepdim=True) + 1e-12
        grad = grad / grad_norm
        grad_momentum = decay * grad_momentum + grad

        X_adv = X_adv.detach() - alpha * torch.sign(grad_momentum)
        delta = torch.clamp(X_adv - X_orig_exp, min=-epsilon, max=epsilon)
        X_adv = torch.clamp(X_orig_exp + delta, min=0.0, max=1.0)

    with torch.no_grad():
        output = model(X_adv)
        true_logits = output.gather(1, y_exp.view(-1, 1)).squeeze(1)
        one_hot_true = F.one_hot(y_exp, num_classes=num_classes).bool()
        other_logits = output.masked_fill(one_hot_true, -1e9)
        max_wrong_logits, _ = other_logits.max(dim=1)

        final_margins = max_wrong_logits - true_logits

        margins_reshaped = final_margins.view(batch_size, num_restarts)

        best_indices_local = margins_reshaped.argmax(dim=1)

        offsets = torch.arange(batch_size, device=device) * num_restarts
        best_indices_global = offsets + best_indices_local

        X_adv_best = X_adv[best_indices_global].clone()
        momentum_best = grad_momentum[best_indices_global].clone()

    X_orig = X.to(device)
    y = y.to(device)

    grad_momentum = momentum_best
    X_adv = X_adv_best

    best_adv_final = X_adv.clone()
    best_margin_final = torch.full((batch_size,), -1e9, device=device)

    for _ in range(finetune_steps):
        X_adv.requires_grad = True
        with torch.enable_grad():
            output = model(X_adv)

            true_logits = output.gather(1, y.view(-1, 1)).squeeze(1)
            one_hot_true = F.one_hot(y, num_classes=num_classes).bool()
            other_logits = output.masked_fill(one_hot_true, -1e9)
            max_wrong_logits, _ = other_logits.max(dim=1)

            current_margin = max_wrong_logits - true_logits
            loss = -current_margin.mean()

        with torch.no_grad():
            update_idx = current_margin > best_margin_final
            best_adv_final[update_idx] = X_adv[update_idx]
            best_margin_final[update_idx] = current_margin[update_idx]

        grad = torch.autograd.grad(loss, X_adv)[0]

        dims_to_sum = list(range(1, grad.dim()))
        grad_norm = grad.abs().sum(dim=dims_to_sum, keepdim=True) + 1e-12
        grad = grad / grad_norm

        grad_momentum = decay * grad_momentum + grad

        X_adv = X_adv.detach() - alpha * torch.sign(grad_momentum)
        delta = torch.clamp(X_adv - X_orig, min=-epsilon, max=epsilon)
        X_adv = torch.clamp(X_orig + delta, min=0.0, max=1.0)

    return best_adv_final.detach()


def p_distance(model, train_loader, device):
    p = []
    for batch_idx, (data, target) in enumerate(train_loader):
        data, target = data.to(device), target.to(device)
        data = data.view(data.size(0), 28 * 28)
        data_ = copy.deepcopy(data.data)
        adv_data = adv_attack(model, data, target, device=device)
        p.append(torch.norm(data_ - adv_data, float('inf')))
    print('epsilon p: ', max(p))


def train_attack(model, X, y, device):
    epsilon = 0.1
    num_steps = 40
    alpha = 0.01
    num_restarts = 5

    model.eval()

    max_loss = torch.zeros(y.shape[0]).to(device) - 1e9
    best_X_adv = X.clone().detach()

    for _ in range(num_restarts):

        delta = torch.zeros_like(X).uniform_(-epsilon, epsilon).to(device)
        delta = torch.clamp(X + delta, 0, 1) - X
        delta.requires_grad = True

        for _ in range(num_steps):
            output = model(X + delta)

            correct_log_prob = output.gather(1, y.view(-1, 1)).view(-1)

            target_mask = torch.zeros_like(output).scatter(1, y.view(-1, 1), 1)

            other_log_prob = (output - target_mask * 1e9).max(1)[0]

            loss = other_log_prob - correct_log_prob

            loss.sum().backward()

            grad = delta.grad.detach()
            delta.data = delta.data + alpha * grad.sign()
            delta.data = torch.clamp(delta.data, -epsilon, epsilon)
            delta.data = torch.clamp(X + delta.data, 0, 1) - X
            delta.grad.zero_()

        with torch.no_grad():
            final_output = model(X + delta)
            correct_log_prob = final_output.gather(1, y.view(-1, 1)).view(-1)
            target_mask = torch.zeros_like(final_output).scatter(1, y.view(-1, 1), 1)
            other_log_prob = (final_output - target_mask * 1e9).max(1)[0]

            all_loss = other_log_prob - correct_log_prob

            update_idx = all_loss > max_loss

            max_loss[update_idx] = all_loss[update_idx]

            best_X_adv[update_idx] = (X + delta)[update_idx]

    return best_X_adv.detach()


def trades_loss(model, x_natural, y, device, optimizer, step_size=0.003, epsilon=0.1, perturb_steps=10, beta=6.0):
    criterion_kl = nn.KLDivLoss(reduction='sum')
    model.eval()

    batch_size = len(x_natural)

    x_adv = x_natural.detach() + 0.001 * torch.randn(x_natural.shape).to(device).detach()

    with torch.no_grad():
        logits_natural = model(x_natural)
        probabilities_natural = F.softmax(logits_natural, dim=1)

    for _ in range(perturb_steps):
        x_adv.requires_grad_()
        with torch.enable_grad():
            logits_adv = model(x_adv)

            loss_kl = criterion_kl(F.log_softmax(logits_adv, dim=1), probabilities_natural)

        grad = torch.autograd.grad(loss_kl, [x_adv])[0]
        x_adv = x_adv.detach() + step_size * torch.sign(grad.detach())
        x_adv = torch.min(torch.max(x_adv, x_natural - epsilon), x_natural + epsilon)
        x_adv = torch.clamp(x_adv, 0.0, 1.0)

    model.train()
    x_adv = Variable(torch.clamp(x_adv, 0.0, 1.0), requires_grad=False)

    optimizer.zero_grad()

    logits_natural = model(x_natural)
    logits_adv = model(x_adv)

    loss_natural = F.nll_loss(logits_natural, y)

    loss_robust = (1.0 / batch_size) * criterion_kl(F.log_softmax(logits_adv, dim=1), F.softmax(logits_natural, dim=1))

    loss = loss_natural + beta * loss_robust
    return loss, loss_natural.item(), loss_robust.item()


def train(args, model, device, train_loader, optimizer, epoch):
    model.train()

    max_eps = 0.1
    if epoch <= args.epochs / 2:
        current_eps = max_eps * (epoch / (args.epochs / 2))
    else:
        current_eps = max_eps

    current_eps = max(current_eps, 0.01)

    step_size = current_eps / 4

    total_loss = 0

    for batch_idx, (data, target) in enumerate(train_loader):
        data, target = data.to(device), target.to(device)
        data = data.view(data.size(0), 28 * 28)

        loss, l_nat, l_rob = trades_loss(model, data, target, device, optimizer, step_size=step_size,
                                         epsilon=current_eps, perturb_steps=10, beta=6.0)

        loss.backward()
        optimizer.step()

        total_loss += loss.item()

    if epoch % 5 == 0:
        print(f"Epoch {epoch} | Eps: {current_eps:.4f} | Beta: 6.0")


def train_model():
    model = Net().to(device)

    optimizer = optim.SGD(model.parameters(), lr=0.01, momentum=0.9, weight_decay=2e-4)

    scheduler = optim.lr_scheduler.MultiStepLR(optimizer, milestones=[int(args.epochs * 0.75), int(args.epochs * 0.9)],
                                               gamma=0.1)

    best_robust_acc = 0.0

    for epoch in range(1, args.epochs + 1):
        start_time = time.time()

        train(args, model, device, train_loader, optimizer, epoch)

        trnloss, trnacc = eval_test(model, device, train_loader)
        advloss, advacc = eval_adv_test(model, device, train_loader)

        scheduler.step()

        print(f'Epoch {epoch}: {int(time.time() - start_time)}s | '
              f'Clean Acc: {100. * trnacc:.2f}% | Adv Acc: {100. * advacc:.2f}%')

        if advacc > best_robust_acc:
            best_robust_acc = advacc
            print(f"   >>> [New Best] Saving model with Adv Acc: {100. * advacc:.2f}%")
            torch.save(model.state_dict(), str(id_) + '.pt')

    return model


def eval_test(model, device, test_loader):
    model.eval()
    test_loss = 0
    correct = 0
    with torch.no_grad():
        for data, target in test_loader:
            data, target = data.to(device), target.to(device)
            data = data.view(data.size(0), 28 * 28)
            output = model(data)
            test_loss += F.nll_loss(output, target, size_average=False).item()
            pred = output.max(1, keepdim=True)[1]
            correct += pred.eq(target.view_as(pred)).sum().item()
    test_loss /= len(test_loader.dataset)
    test_accuracy = correct / len(test_loader.dataset)
    return test_loss, test_accuracy


def eval_adv_test(model, device, test_loader):
    model.eval()
    test_loss = 0
    correct = 0
    for data, target in test_loader:
        data, target = data.to(device), target.to(device)
        data = data.view(data.size(0), 28 * 28)

        adv_data = train_attack(model, data, target, device=device)
        with torch.no_grad():
            output = model(adv_data)
            test_loss += F.nll_loss(output, target, size_average=False).item()
            pred = output.max(1, keepdim=True)[1]
            correct += pred.eq(target.view_as(pred)).sum().item()
    test_loss /= len(test_loader.dataset)
    test_accuracy = correct / len(test_loader.dataset)
    return test_loss, test_accuracy
