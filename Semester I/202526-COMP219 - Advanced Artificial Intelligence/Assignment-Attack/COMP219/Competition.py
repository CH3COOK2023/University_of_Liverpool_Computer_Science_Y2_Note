############################################################################
### Written by Gaojie Jin and updated by Xiaowei Huang, 2021
###
### For a 2-nd year undergraduate student competition on
### the robustness of deep neural networks, where a student
### needs to develop
### Attack_V3.txt. an attack algorithm, and
### 2. an adversarial training algorithm
###
### The score is based on both algorithms.
############################################################################

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

# Change this id to your student id
id_ = 201945844

# setup training parameters
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

############################################################################
################    don't change the below code    #####################
############################################################################
train_set = torchvision.datasets.FashionMNIST(root='data', train=True, download=True,
                                              transform=transforms.Compose([transforms.ToTensor()]))
train_loader = DataLoader(train_set, batch_size=args.batch_size, shuffle=True)

test_set = torchvision.datasets.FashionMNIST(root='data', train=False, download=True,
                                             transform=transforms.Compose([transforms.ToTensor()]))
test_loader = DataLoader(test_set, batch_size=args.batch_size, shuffle=True)


# define fully connected network
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


##############################################################################
#############    end of "don't change the below code"   ######################
##############################################################################


def adv_attack(model, X, y, device):
    # --- 策略参数 ---
    epsilon = 0.1

    # 阶段 1：广撒网 (Exploration)
    num_restarts = 24  # 撒 num_restarts 个网
    explore_steps = 5  # 每个网试探 explore_steps 步

    # 阶段 2：重点突破 (Exploitation)
    finetune_steps = 35  # 选中最好的那个，再跑 finetune_steps 步

    # 通用参数
    alpha = 0.015
    decay = 1.0
    num_classes = 10

    model.eval()

    # 获取输入维度信息
    batch_size = X.size(0)
    input_shape = X.shape[1:]  # e.g., (1, 28, 28) or (784,)

    # -------------------------------------------------------------------
    # 阶段 1: 广撒网 (Parallel Exploration)
    # 我们通过 repeat_interleave 将 batch 扩大 num_restarts 倍
    # 比如 batch=64, restarts=40 -> 实际计算 batch=2560
    # -------------------------------------------------------------------

    X_orig_exp = X.repeat_interleave(num_restarts, dim=0).to(device)
    y_exp = y.repeat_interleave(num_restarts, dim=0).to(device)

    # 随机初始化所有分身
    X_adv = X_orig_exp.clone()
    X_adv = X_adv + torch.zeros_like(X_adv).uniform_(-epsilon, epsilon).to(device)
    X_adv = torch.clamp(X_adv, 0.0, 1.0)

    # 初始化动量
    grad_momentum = torch.zeros_like(X_adv).to(device)

    # 简单跑几轮，看看苗头
    for _ in range(explore_steps):
        X_adv.requires_grad = True
        with torch.enable_grad():
            output = model(X_adv)

            # 计算 CW Margin Loss
            true_logits = output.gather(1, y_exp.view(-1, 1)).squeeze(1)
            one_hot_true = F.one_hot(y_exp, num_classes=num_classes).bool()
            other_logits = output.masked_fill(one_hot_true, -1e9)
            max_wrong_logits, _ = other_logits.max(dim=1)

            # Margin越大越好
            margin = max_wrong_logits - true_logits
            loss = -margin.mean()

        grad = torch.autograd.grad(loss, X_adv)[0]

        # 归一化 & 动量
        dims_to_sum = list(range(1, grad.dim()))
        grad_norm = grad.abs().sum(dim=dims_to_sum, keepdim=True) + 1e-12
        grad = grad / grad_norm
        grad_momentum = decay * grad_momentum + grad

        X_adv = X_adv.detach() - alpha * torch.sign(grad_momentum)
        delta = torch.clamp(X_adv - X_orig_exp, min=-epsilon, max=epsilon)
        X_adv = torch.clamp(X_orig_exp + delta, min=0.0, max=1.0)

    # -------------------------------------------------------------------
    # 阶段 2: 优胜劣汰 (Selection)
    # 评估当前所有分身的 Margin，每个原始样本只保留 Margin 最大的那个分身
    # -------------------------------------------------------------------

    with torch.no_grad():
        output = model(X_adv)
        true_logits = output.gather(1, y_exp.view(-1, 1)).squeeze(1)
        one_hot_true = F.one_hot(y_exp, num_classes=num_classes).bool()
        other_logits = output.masked_fill(one_hot_true, -1e9)
        max_wrong_logits, _ = other_logits.max(dim=1)

        final_margins = max_wrong_logits - true_logits

        # 将 Margin 变形为 [Batch, Restarts]
        margins_reshaped = final_margins.view(batch_size, num_restarts)

        # 找到每个样本最好的那个 restart 的索引 (argmax)
        best_indices_local = margins_reshaped.argmax(dim=1)  # Shape: [Batch]

        # 计算在展平的大 Batch 中的真实索引
        # 比如第 0 个样本选了第 5 个 restart，索引就是 0*40 + 5
        offsets = torch.arange(batch_size, device=device) * num_restarts
        best_indices_global = offsets + best_indices_local

        # 提取出最强的 X_adv 和 对应的动量
        X_adv_best = X_adv[best_indices_global].clone()
        momentum_best = grad_momentum[best_indices_global].clone()

    # -------------------------------------------------------------------
    # 阶段 3: 重点突破 (Focused Finetuning)
    # 现在 batch size 变回了原始大小，我们拿着最好的状态继续冲刺
    # -------------------------------------------------------------------

    # 这里的 X_orig 是原始大小的
    X_orig = X.to(device)
    y = y.to(device)

    # 继承之前的动量
    grad_momentum = momentum_best
    X_adv = X_adv_best

    # 同样使用 Best Keeping 策略记录后续过程中的最佳值
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

        # 更新 Best Keeping
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

# 确保你写的攻击算法没有“犯规”, 其epsilon < 0.11
def p_distance(model, train_loader, device):
    p = []
    for batch_idx, (data, target) in enumerate(train_loader):
        data, target = data.to(device), target.to(device)
        data = data.view(data.size(0), 28 * 28)
        data_ = copy.deepcopy(data.data)
        adv_data = adv_attack(model, data, target, device=device)
        p.append(torch.norm(data_ - adv_data, float('inf')))
    print('epsilon p: ', max(p))



#############################################################################
#############################################################################
#############################################################################
#############################################################################
###########################CORE CODE TRAIN###################################
#############################################################################
#############################################################################
#############################################################################
#############################################################################
def train_attack(model, X, y, device):
    # --- 参数设置 ---
    epsilon = 0.1  # 题目限制 < 0.11
    num_steps = 40  # 迭代步数，攻击时可以设大一点
    alpha = 0.01  # 步长
    num_restarts = 5  # 【关键改进1】随机重启次数 (越多越强，但越慢)

    model.eval()

    # 用于记录所有重启中找到的最强对抗样本
    max_loss = torch.zeros(y.shape[0]).to(device) - 1e9
    best_X_adv = X.clone().detach()

    for _ in range(num_restarts):
        # 1. 随机初始化 (Random Start)
        delta = torch.zeros_like(X).uniform_(-epsilon, epsilon).to(device)
        delta = torch.clamp(X + delta, 0, 1) - X
        delta.requires_grad = True

        for _ in range(num_steps):
            output = model(X + delta)

            # 【关键改进2】使用 CW Loss (Margin Loss) 替代 NLLLoss
            # 这种 Loss 旨在最大化：(最强错误类别的概率 - 正确类别的概率)
            # 即使模型已经分错，它也会继续推动，让错误更“稳固”

            # 获取正确类别的 log_prob
            # gather(1, y.view(-1, 1)) 会挑出 target 对应的那个值
            correct_log_prob = output.gather(1, y.view(-1, 1)).view(-1)

            # 获取"除正确类别外"的最大 log_prob
            # 我们先生成一个 mask，把正确类别的位置设为负无穷
            target_mask = torch.zeros_like(output).scatter(1, y.view(-1, 1), 1)
            # 减去一个大数，排除正确标签
            other_log_prob = (output - target_mask * 1e9).max(1)[0]

            # 我们希望：other_log_prob 越大越好，correct_log_prob 越小越好
            # 所以我们要最大化 loss
            loss = other_log_prob - correct_log_prob

            # 求和用于反向传播
            loss.sum().backward()

            # 更新 delta
            grad = delta.grad.detach()
            delta.data = delta.data + alpha * grad.sign()
            delta.data = torch.clamp(delta.data, -epsilon, epsilon)
            delta.data = torch.clamp(X + delta.data, 0, 1) - X
            delta.grad.zero_()

        # --- 记录本次重启的结果 ---
        # 再次计算最终的 Loss，保留 Loss 更大的样本
        with torch.no_grad():
            final_output = model(X + delta)
            correct_log_prob = final_output.gather(1, y.view(-1, 1)).view(-1)
            target_mask = torch.zeros_like(final_output).scatter(1, y.view(-1, 1), 1)
            other_log_prob = (final_output - target_mask * 1e9).max(1)[0]
            # 计算最终的 margin loss
            all_loss = other_log_prob - correct_log_prob

            # 找出哪些样本在这次重启中表现更好
            # update_idx 是一个布尔向量
            update_idx = all_loss > max_loss

            # 更新最大 Loss 记录
            max_loss[update_idx] = all_loss[update_idx]

            # 只更新那些表现更好的样本
            best_X_adv[update_idx] = (X + delta)[update_idx]

    return best_X_adv.detach()

# 辅助函数：TRADES Loss
def trades_loss(model, x_natural, y, device, optimizer, step_size=0.003, epsilon=0.1, perturb_steps=10, beta=6.0):
    # 1. 定义 KL 散度标准
    criterion_kl = nn.KLDivLoss(reduction='sum')
    model.eval() # 生成对抗样本时设为 eval

    batch_size = len(x_natural)

    # 2. 生成对抗样本 (针对 TRADES 的特殊生成方式)
    # 目标是找到让输出分布与干净样本差异最大的点
    x_adv = x_natural.detach() + 0.001 * torch.randn(x_natural.shape).to(device).detach()

    # 获取干净样本的 logits (作为 Target)
    with torch.no_grad():
        logits_natural = model(x_natural)
        probabilities_natural = F.softmax(logits_natural, dim=1)

    for _ in range(perturb_steps):
        x_adv.requires_grad_()
        with torch.enable_grad():
            logits_adv = model(x_adv)
            # 计算 KL(Adv || Clean)
            loss_kl = criterion_kl(F.log_softmax(logits_adv, dim=1), probabilities_natural)

        grad = torch.autograd.grad(loss_kl, [x_adv])[0]
        x_adv = x_adv.detach() + step_size * torch.sign(grad.detach())
        x_adv = torch.min(torch.max(x_adv, x_natural - epsilon), x_natural + epsilon)
        x_adv = torch.clamp(x_adv, 0.0, 1.0)

    model.train() # 恢复训练模式
    x_adv = Variable(torch.clamp(x_adv, 0.0, 1.0), requires_grad=False)

    optimizer.zero_grad()

    # 3. 计算最终 Loss
    logits_natural = model(x_natural)
    logits_adv = model(x_adv)

    # Loss 1: 干净样本的分类准确度
    loss_natural = F.nll_loss(logits_natural, y)

    # Loss 2: 鲁棒性正则项 (强迫 adv 和 natural 输出一致)
    # size_average=False means reduction='sum', distinct by batch_size
    loss_robust = (1.0 / batch_size) * criterion_kl(F.log_softmax(logits_adv, dim=1), F.softmax(logits_natural, dim=1))

    loss = loss_natural + beta * loss_robust
    return loss, loss_natural.item(), loss_robust.item()

# 替换 train 函数
def train(args, model, device, train_loader, optimizer, epoch):
    model.train()

    # --- 课程学习 (Curriculum Learning) ---
    # 在前 50% 的 epoch 中，epsilon 从 0.01 线性增加到 0.1
    # 这样可以让模型先学会简单的特征，再慢慢适应强攻击
    max_eps = 0.1
    if epoch <= args.epochs / 2:
        current_eps = max_eps * (epoch / (args.epochs / 2))
    else:
        current_eps = max_eps

    # 确保 epsilon 至少有一点点
    current_eps = max(current_eps, 0.01)

    # 对应的步长调整
    step_size = current_eps / 4

    total_loss = 0

    for batch_idx, (data, target) in enumerate(train_loader):
        data, target = data.to(device), target.to(device)
        data = data.view(data.size(0), 28 * 28)

        # 使用 TRADES 方式计算 Loss 并反向传播
        # beta=6.0 是经验值，如果想要更高的 Robust Accuracy (牺牲一点 Clean Acc)，可以设为 8.0 或 10.0
        loss, l_nat, l_rob = trades_loss(model, data, target, device, optimizer,
                                         step_size=step_size,
                                         epsilon=current_eps,
                                         perturb_steps=10,
                                         beta=6.0)

        loss.backward()
        optimizer.step()

        total_loss += loss.item()

    # 打印当前 Epoch 的策略状态
    if epoch % 5 == 0:
        print(f"Epoch {epoch} | Eps: {current_eps:.4f} | Beta: 6.0")

# 替换 train_model 函数 (主要是 Scheduler 和 保存逻辑)
def train_model():
    model = Net().to(device)

    # 使用 SGD + Momentum，这是训练鲁棒模型最稳的组合
    optimizer = optim.SGD(model.parameters(), lr=0.01, momentum=0.9, weight_decay=2e-4)

    # 学习率调度：在 75% 和 90% 的时候下降
    # 这种阶梯式下降比 Plateau 更适合这种固定 Epoch 的训练
    scheduler = optim.lr_scheduler.MultiStepLR(optimizer, milestones=[int(args.epochs*0.75), int(args.epochs*0.9)], gamma=0.1)

    best_robust_acc = 0.0

    for epoch in range(1, args.epochs + 1):
        start_time = time.time()

        # 训练
        train(args, model, device, train_loader, optimizer, epoch)

        # 评估
        trnloss, trnacc = eval_test(model, device, train_loader)
        advloss, advacc = eval_adv_test(model, device, train_loader)

        scheduler.step()

        print(f'Epoch {epoch}: {int(time.time()-start_time)}s | '
              f'Clean Acc: {100.*trnacc:.2f}% | Adv Acc: {100.*advacc:.2f}%')

        # --- 核心策略：只保存“对抗准确率”最高的模型 ---
        # 我们的目标是 Defense Score，它完全取决于 Adv Acc
        if advacc > best_robust_acc:
            best_robust_acc = advacc
            print(f"   >>> [New Best] Saving model with Adv Acc: {100.*advacc:.2f}%")
            torch.save(model.state_dict(), str(id_) + '.pt')

    return model

# predict function (在干净数据上测试)
def eval_test(model, device, test_loader):
    model.eval() # 设置为评估模式
    test_loss = 0
    correct = 0
    with torch.no_grad(): # 不计算梯度，节省内存
        for data, target in test_loader:
            data, target = data.to(device), target.to(device)
            data = data.view(data.size(0),28*28) # 展平
            output = model(data)
            test_loss += F.nll_loss(output, target, size_average=False).item() # 累加 Loss
            pred = output.max(1, keepdim=True)[1] # 获取概率最大的类别作为预测结果
            correct += pred.eq(target.view_as(pred)).sum().item() # 计算预测正确的数量
    test_loss /= len(test_loader.dataset)
    test_accuracy = correct / len(test_loader.dataset)
    return test_loss, test_accuracy

# (在对抗样本上测试)
def eval_adv_test(model, device, test_loader):
    model.eval()
    test_loss = 0
    correct = 0
    for data, target in test_loader:
        data, target = data.to(device), target.to(device)
        data = data.view(data.size(0),28*28)
        # 这里调用了 adv_attack 生成对抗样本，然后测试模型能否识别这些对抗样本
        adv_data = train_attack(model, data, target, device=device)
        with torch.no_grad():
            output = model(adv_data)
            test_loss += F.nll_loss(output, target, size_average=False).item()
            pred = output.max(1, keepdim=True)[1]
            correct += pred.eq(target.view_as(pred)).sum().item()
    test_loss /= len(test_loader.dataset)
    test_accuracy = correct / len(test_loader.dataset)
    return test_loss, test_accuracy

#############################################################################
#############################################################################
#############################################################################
#############################################################################
#############################################################################
#############################################################################
#############################################################################
#############################################################################
#############################################################################




# train and test
#model = train_model()
model = Net().to(device)
p_distance(model, train_loader, device)
