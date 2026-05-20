import matplotlib.pyplot as plt
import numpy as np

def parse_data(filepath):
    """
    Parse the data file and return the iterations and dictionaries for encryption and decryption times.
    """
    iterations = []
    enc_times = {'A': [], 'B': [], 'C': [], 'D': []}
    dec_times = {'A': [], 'B': [], 'C': [], 'D': []}

    with open(filepath, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue

            # Remove parentheses to treat it as comma-separated values
            clean_line = line.replace('(', '').replace(')', '')
            parts = clean_line.split(',')

            # Convert to float
            vals = [float(p) for p in parts]

            iterations.append(vals[0])

            enc_times['A'].append(vals[1])
            dec_times['A'].append(vals[2])

            enc_times['B'].append(vals[3])
            dec_times['B'].append(vals[4])

            enc_times['C'].append(vals[5])
            dec_times['C'].append(vals[6])

            enc_times['D'].append(vals[7])
            dec_times['D'].append(vals[8])

    return np.array(iterations), enc_times, dec_times

def plot_data(iterations, enc_times, dec_times):
    ciphers = ['A', 'B', 'C', 'D']
    colors = ['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728'] # Blue, Orange, Green, Red

    # Set the figure size to be wider horizontally (e.g., 14 inches wide, 5 inches tall)
    # Ideal for a single row per figure in academic papers
    fig_size = (14, 5)
    # Increase marker size slightly since connecting lines are removed
    pt_size = 2

    # ---------------------------------------------------------
    # 1. Plot encryption times (scatter points only)
    # ---------------------------------------------------------
    plt.figure(figsize=fig_size)
    for i, cipher in enumerate(ciphers):
        # Use linestyle='none' to remove connecting lines
        plt.plot(iterations, enc_times[cipher], label=f'Encryption {cipher}',
                 color=colors[i], marker='o', markersize=pt_size, linestyle='none')

    plt.title('Figure 1: Encryption Times for Four Ciphers')
    plt.xlabel('Iteration Time')
    plt.ylabel('Time (ms)')
    plt.legend()
    plt.grid(True, linestyle='--', alpha=0.6)
    plt.tight_layout()
    plt.savefig('plot1_encryption_times.png', dpi=300) # Added high DPI for paper quality
    plt.show()

    # ---------------------------------------------------------
    # 2. Plot decryption times (scatter points only)
    # ---------------------------------------------------------
    plt.figure(figsize=fig_size)
    for i, cipher in enumerate(ciphers):
        plt.plot(iterations, dec_times[cipher], label=f'Decryption {cipher}',
                 color=colors[i], marker='s', markersize=pt_size, linestyle='none')

    plt.title('Figure 2: Decryption Times for Four Ciphers')
    plt.xlabel('Iteration Time')
    plt.ylabel('Time (ms)')
    plt.legend()
    plt.grid(True, linestyle='--', alpha=0.6)
    plt.tight_layout()
    plt.savefig('plot2_decryption_times.png', dpi=300)
    plt.show()

    # ---------------------------------------------------------
    # 3. Plot encryption times with linear fitting
    # ---------------------------------------------------------
    plt.figure(figsize=fig_size)
    for i, cipher in enumerate(ciphers):
        y = np.array(enc_times[cipher])
        # Original data as faint scatter points
        plt.plot(iterations, y, color=colors[i], alpha=0.3, marker='o',
                 markersize=pt_size, linestyle='none', label=f'Encryption {cipher} ')

        # Linear fitting (polynomial of degree 1: y = kx + b)
        k, b = np.polyfit(iterations, y, 1)
        fit_y = k * iterations + b

        # Plot the fitted line (keep solid and thick)
        plt.plot(iterations, fit_y, color=colors[i], linewidth=2, label=f'Encryption {cipher} (Fit: y={k:.2e}x+{b:.2e})')

    plt.title('Figure 3: Encryption Times and Linear Fit')
    plt.xlabel('Iteration Time')
    plt.ylabel('Time (ms)')
    plt.legend(bbox_to_anchor=(1.01, 1), loc='upper left')
    plt.grid(True, linestyle='--', alpha=0.6)
    plt.tight_layout()
    plt.savefig('plot3_encryption_fit.png', dpi=300)
    plt.show()

    # ---------------------------------------------------------
    # 4. Plot decryption times with linear fitting
    # ---------------------------------------------------------
    plt.figure(figsize=fig_size)
    for i, cipher in enumerate(ciphers):
        y = np.array(dec_times[cipher])
        # Original data as faint scatter points
        plt.plot(iterations, y, color=colors[i], alpha=0.3, marker='s',
                 markersize=pt_size, linestyle='none', label=f'Decryption {cipher}')

        # Linear fitting
        k, b = np.polyfit(iterations, y, 1)
        fit_y = k * iterations + b

        # Plot the fitted line
        plt.plot(iterations, fit_y, color=colors[i], linewidth=2, label=f'Decryption {cipher} (Fit: y={k:.2e}x+{b:.2e})')

    plt.title('Figure 4: Decryption Times and Linear Fit')
    plt.xlabel('Iteration Time')
    plt.ylabel('Time (ms)')
    plt.legend(bbox_to_anchor=(1.01, 1), loc='upper left')
    plt.grid(True, linestyle='--', alpha=0.6)
    plt.tight_layout()
    plt.savefig('plot4_decryption_fit.png', dpi=300)
    plt.show()

if __name__ == '__main__':
    # Assuming your data is stored in 'data.txt'
    # Ensure the file path is correct
    file_path = 'data.txt'

    # Extract data
    iterations, enc_times, dec_times = parse_data(file_path)

    # Plot data
    plot_data(iterations, enc_times, dec_times)