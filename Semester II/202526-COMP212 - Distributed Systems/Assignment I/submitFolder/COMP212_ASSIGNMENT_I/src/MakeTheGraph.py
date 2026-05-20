import argparse
import os
import matplotlib.pyplot as plt
import numpy as np
import warnings


warnings.filterwarnings('ignore')

def calc_adj_r_squared(y_true, y_pred, p):
    n = len(y_true)
    if n <= p + 1:
        return 0
    ss_res = np.sum((y_true - y_pred) ** 2)
    ss_tot = np.sum((y_true - np.mean(y_true)) ** 2)
    if ss_tot == 0:
        return 0
    r2 = 1 - (ss_res / ss_tot)
    adj_r2 = 1 - (1 - r2) * (n - 1) / (n - p - 1)
    return adj_r2

def fit_best_curve(x, y):
    results = {}

    
    coeffs_lin = np.polyfit(x, y, 1)
    y_pred_lin = np.polyval(coeffs_lin, x)
    results['O(n)'] = (coeffs_lin, calc_adj_r_squared(y, y_pred_lin, 1))

    
    coeffs_sq = np.polyfit(x, y, 2)
    y_pred_sq = np.polyval(coeffs_sq, x)
    results['O(n^2)'] = (coeffs_sq, calc_adj_r_squared(y, y_pred_sq, 2))

    
    valid_idx = x > 0
    x_val = x[valid_idx]
    y_val = y[valid_idx]

    if len(x_val) > 2:
        
        coeffs_log = np.polyfit(np.log(x_val), y_val, 1)
        y_pred_log = coeffs_log[0] * np.log(x_val) + coeffs_log[1]
        results['O(log n)'] = (coeffs_log, calc_adj_r_squared(y_val, y_pred_log, 1))

        
        coeffs_nlogn = np.polyfit(x_val * np.log(x_val), y_val, 1)
        y_pred_nlogn = coeffs_nlogn[0] * x_val * np.log(x_val) + coeffs_nlogn[1]
        results['O(n log n)'] = (coeffs_nlogn, calc_adj_r_squared(y_val, y_pred_nlogn, 1))

        
        coeffs_n2logn = np.polyfit((x_val**2) * np.log(x_val), y_val, 1)
        y_pred_n2logn = coeffs_n2logn[0] * (x_val**2) * np.log(x_val) + coeffs_n2logn[1]
        results['O(n^2 log n)'] = (coeffs_n2logn, calc_adj_r_squared(y_val, y_pred_n2logn, 1))

    
    best_type = max(results, key=lambda k: results[k][1])
    return best_type, results[best_type][0]

def get_equation_string(fit_type, coeffs):
    
    def fmt(c):
        if c == 0: return "0"
        if abs(c) < 1e-3 or abs(c) > 1e4:
            return f"{c:.2e}"
        return f"{c:.4f}"

    if fit_type == 'O(n)':
        eq = f"{fmt(coeffs[0])}x + {fmt(coeffs[1])}"
    elif fit_type == 'O(log n)':
        eq = f"{fmt(coeffs[0])}\\ln(x) + {fmt(coeffs[1])}"
    elif fit_type == 'O(n^2)':
        eq = f"{fmt(coeffs[0])}x^2 + {fmt(coeffs[1])}x + {fmt(coeffs[2])}"
    elif fit_type == 'O(n log n)':
        eq = f"{fmt(coeffs[0])}x\\ln(x) + {fmt(coeffs[1])}"
    elif fit_type == 'O(n^2 log n)':
        eq = f"{fmt(coeffs[0])}x^2\\ln(x) + {fmt(coeffs[1])}"

    
    eq = eq.replace("+ -", "- ")
    
    return f"[{fit_type}] $y = {eq}$"

def get_smooth_y(fit_type, coeffs, x_smooth):
    
    if fit_type == 'O(n)':
        return np.polyval(coeffs, x_smooth)
    elif fit_type == 'O(n^2)':
        return np.polyval(coeffs, x_smooth)
    elif fit_type == 'O(log n)':
        return coeffs[0] * np.log(x_smooth) + coeffs[1]
    elif fit_type == 'O(n log n)':
        return coeffs[0] * x_smooth * np.log(x_smooth) + coeffs[1]
    elif fit_type == 'O(n^2 log n)':
        return coeffs[0] * (x_smooth**2) * np.log(x_smooth) + coeffs[1]

def main():
    parser = argparse.ArgumentParser(description="Generate a graph and its automatic smart-fitting graph.")
    parser.add_argument('-path', required=True, help="Path to the input text file (e.g., taskA.txt)")
    parser.add_argument('-xname', default='X Coordinate', type=str, help="Label for the X-axis")
    args = parser.parse_args()

    input_file = args.path
    x_axis_name = args.xname

    if not os.path.exists(input_file):
        print(f"Error: The file '{input_file}' does not exist.")
        return

    x_data, y1_data, y2_data = [], [], []

    try:
        with open(input_file, 'r', encoding='utf-8') as f:
            for line in f:
                parts = line.strip().split('\t')
                if len(parts) == 3:
                    x_data.append(float(parts[0]))
                    y1_data.append(float(parts[1]))
                    y2_data.append(float(parts[2]))
    except Exception as e:
        print(f"Error reading file: {e}")
        return

    if not x_data:
        print("Error: No valid data.")
        return

    x_arr = np.array(x_data)
    y1_arr = np.array(y1_data)
    y2_arr = np.array(y2_data)

    base_dir, file_name = os.path.split(input_file)
    name_only, _ = os.path.splitext(file_name)

    
    
    
    fig, ax1 = plt.subplots(figsize=(10, 6))
    color1, color2 = 'tab:red', 'tab:blue'

    ax1.set_xlabel(x_axis_name, fontsize=12)
    ax1.set_ylabel('Round', color=color1, fontsize=12, fontweight='bold')
    line1 = ax1.plot(x_arr, y1_arr, color=color1, marker='o', label='Round', linewidth=0, markersize=1.5)
    ax1.tick_params(axis='y', labelcolor=color1)
    ax1.grid(True, linestyle='--', alpha=0.6)

    ax2 = ax1.twinx()
    ax2.set_ylabel('Transmission', color=color2, fontsize=12, fontweight='bold')
    line2 = ax2.plot(x_arr, y2_arr, color=color2, marker='o', label='Transmission', linewidth=0, markersize=1.5)
    ax2.tick_params(axis='y', labelcolor=color2)

    ax1.legend(line1 + line2, [l.get_label() for l in line1 + line2], loc='upper left')
    fig.tight_layout()
    output_file1 = os.path.join(base_dir, f"{name_only}.png")
    plt.savefig(output_file1, dpi=300)
    plt.close(fig)

    
    
    
    fig_fit, ax1_fit = plt.subplots(figsize=(10, 6))

    
    x_min = np.min(x_arr[x_arr > 0]) if np.any(x_arr > 0) else 0.1
    x_smooth = np.linspace(x_min, x_arr.max(), 300)

    
    type1, coeffs1 = fit_best_curve(x_arr, y1_arr)
    type2, coeffs2 = fit_best_curve(x_arr, y2_arr)

    
    eq1_str = get_equation_string(type1, coeffs1)
    eq2_str = get_equation_string(type2, coeffs2)

    y1_smooth = get_smooth_y(type1, coeffs1, x_smooth)
    y2_smooth = get_smooth_y(type2, coeffs2, x_smooth)

    
    ax1_fit.set_xlabel(x_axis_name, fontsize=12)
    ax1_fit.set_ylabel('Round', color=color1, fontsize=12, fontweight='bold')
    ax1_fit.plot(x_arr, y1_arr, color=color1, marker='o', linewidth=0, markersize=1.5, alpha=0.3)
    line1_fit = ax1_fit.plot(x_smooth, y1_smooth, color=color1, linestyle='-', label=f'Round Fit: {eq1_str}', linewidth=2)
    ax1_fit.tick_params(axis='y', labelcolor=color1)
    ax1_fit.grid(True, linestyle='--', alpha=0.6)

    
    ax2_fit = ax1_fit.twinx()
    ax2_fit.set_ylabel('Transmission', color=color2, fontsize=12, fontweight='bold')
    ax2_fit.plot(x_arr, y2_arr, color=color2, marker='o', linewidth=0, markersize=1.5, alpha=0.3)
    line2_fit = ax2_fit.plot(x_smooth, y2_smooth, color=color2, linestyle='-', label=f'Trans Fit: {eq2_str}', linewidth=2)
    ax2_fit.tick_params(axis='y', labelcolor=color2)

    lines_fit = line1_fit + line2_fit
    
    ax1_fit.legend(lines_fit, [l.get_label() for l in lines_fit], loc='upper left', fontsize=9)

    fig_fit.tight_layout()
    output_file2 = os.path.join(base_dir, f"fitting_{name_only}.png")
    plt.savefig(output_file2, dpi=300)
    print(f"Smart-fitting graph successfully generated and saved to: {output_file2}")
    plt.close(fig_fit)

if __name__ == '__main__':
    main()