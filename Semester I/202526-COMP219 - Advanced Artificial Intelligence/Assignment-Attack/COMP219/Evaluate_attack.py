"""
Evaluate Attack Ability - Test Your Attack Against Reference Models
Author: Lingfang Li
"""

import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
from torch.utils.data import DataLoader
import torchvision
from torchvision import transforms
from torch.autograd import Variable
import argparse
import os

class Net(nn.Module):
    def __init__(self):
        super(Net, self).__init__()
        self.fc1 = nn.Linear(28*28, 128)
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

################################################################################################
## Define your attack here (same as in Competition.py)
################################################################################################

def your_attack(model, X, y, device):
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


################################################################################################
## End of attack definition
################################################################################################

def eval_adv_test(model, device, test_loader, attack_method):
    model.eval()
    correct = 0
    ii = 0
    for data, target in test_loader:
        data, target = data.to(device), target.to(device)
        data = data.view(data.size(0), 28*28)

        # Generate adversarial examples (needs gradients)
        adv_data = attack_method(model, data, target, device=device)

        # Evaluate on adversarial examples (no gradients needed)
        with torch.no_grad():
            output = model(adv_data)
            pred = output.max(1, keepdim=True)[1]
            correct += pred.eq(target.view_as(pred)).sum().item()
        ii+=1
        print(f"{ii}/{len(test_loader)}")

    return correct / len(test_loader.dataset)

def main():

    ################################################################################################
    ## Step 1: Download reference models
    ## Go to Canvas → COMP219 Module → Lecture 14 → Download "Defenders.zip"
    ## Extract the zip file, then specify the path below
    ################################################################################################

    defenders_path = "./Defenders/hidden"  # change this to your extracted Defenders folder path
    # defenders_path = "./Defenders/diff"  # change this to your extracted Defenders folder path

    ################################################################################################
    ## Step 2: Your attack function is defined above (see your_attack function)
    ################################################################################################

    attack_to_test = your_attack  # use the attack defined above

    ################################################################################################
    ## End of configuration
    ################################################################################################

    parser = argparse.ArgumentParser(description='Attack Evaluation')
    parser.add_argument('--batch-size', type=int, default=128, metavar='N')
    parser.add_argument('--no-cuda', action='store_true', default=False)
    parser.add_argument('--seed', type=int, default=1, metavar='S')
    args = parser.parse_args(args=[])

    use_cuda = not args.no_cuda and torch.cuda.is_available()
    use_cuda = False
    device = torch.device("cuda" if use_cuda else "cpu")
    torch.manual_seed(args.seed)

    print("="*80)
    print("COMP219 Assignment - Attack Evaluation")
    print("="*80)
    print(f"Defenders path: {defenders_path}")
    print(f"Device: {device}")
    print("="*80)

    # check if defenders path exists
    if not os.path.exists(defenders_path):
        print(f"\nERROR: Defenders folder not found at '{defenders_path}'")
        print("\nPlease:")
        print("1. Go to Canvas → COMP219 Module → Lecture 14")
        print("2. Download 'Defenders.zip'")
        print("3. Extract the zip file")
        print("4. Update 'defenders_path' variable in this script")
        return

    # find all .pt files in defenders folder
    model_files = [f for f in os.listdir(defenders_path) if f.endswith('.pt')]
    model_files.sort()

    if len(model_files) == 0:
        print(f"\nERROR: No .pt model files found in '{defenders_path}'")
        print("Please check the folder contains the extracted model files.")
        return

    print(f"Found {len(model_files)} reference models")
    print("="*80)

    # load test data
    test_set = torchvision.datasets.FashionMNIST(
        root='data', train=False, download=True,
        transform=transforms.Compose([transforms.ToTensor()])
    )
    test_loader = DataLoader(test_set, batch_size=args.batch_size, shuffle=False)

    # build attack matrix: [1 attack] x [N models]
    num_models = len(model_files)
    attack_matrix = np.zeros(num_models)

    print("\nEvaluating your attack...")
    for i, model_file in enumerate(model_files):
        model_path = os.path.join(defenders_path, model_file)
        print(f"  [{i+1}/{num_models}] {model_file}...", end=" ", flush=True)

        # load model
        model = Net().to(device)
        model.load_state_dict(torch.load(model_path, map_location=device))

        # evaluate attack
        robust_acc = eval_adv_test(model, device, test_loader, attack_to_test)
        attack_matrix[i] = robust_acc
        print(f"{robust_acc*100:.2f}%")

        del model

    # calculate attack score (mean of 1/robust_acc)
    attack_scores = 1.0 / (attack_matrix + 1e-10)  # avoid division by zero
    attack_score = np.mean(attack_scores)

    # display results
    print("\n" + "="*80)
    print("ATTACK MATRIX (Your Attack vs Reference Models)")
    print("="*80)
    print(f"\n{'Model':<20} {'Robust Accuracy':<20} {'Attack Score (1/acc)'}")
    print("-"*80)
    for i, model_file in enumerate(model_files):
        print(f"{model_file:<20} {attack_matrix[i]*100:>6.2f}%              {attack_scores[i]:>6.2f}")
    print("-"*80)
    print(f"{'Mean':<20} {np.mean(attack_matrix)*100:>6.2f}%              {attack_score:>6.2f}")
    print("="*80)

    print("\n" + "="*80)
    print("ATTACK SCORE")
    print("="*80)
    print(f"Your Attack Score: {attack_score:.4f}")
    print("="*80)
    print("\nNote:")
    print("  - Higher score is better (stronger attack)")
    print("  - Attack Score = mean(1/robust_accuracy)")
    print("  - Lower robust accuracy → stronger attack → higher score")
    print("  - Final grading will normalize scores across ALL students")
    print("  - This self-test uses reference models only")

if __name__ == '__main__':
    main()
    i = input("Press enter to exit")