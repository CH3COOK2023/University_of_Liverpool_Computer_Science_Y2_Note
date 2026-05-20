
import ClientService.Client;
import ClientService.Server;
import Configuration.GlobalConfiguration;
import Log.Logging;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class VotingGUI extends JFrame {

    // UI Components
    private DefaultListModel<ClientNode> clientListModel;
    private JList<ClientNode> clientList;
    private JPanel rightCardPanel;
    private CardLayout cardLayout;

    // The three main views on the right
    private JPanel emptyPanel;
    private JPanel votePanel;
    private JPanel resultPanel;

    // Voting components
    private JComboBox<Integer> choiceComboBox;
    // Result container
    private JPanel chartContainer;

    private int clientCounter = 0;

    public static void main(String[] args) {
        // 1. Start the Server asynchronously to prevent blocking the GUI thread
        new Thread(() -> {
            try {
                Logging.info("GUI       | Starting RMI Server in background...");
                Server.run();
            } catch (Exception e) {
                Logging.error("GUI       | Server startup failed: {}", e.getMessage());
            }
        }, "Server-Thread").start();

        // Wait briefly to ensure the RMI port is bound before launching the GUI
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 2. Launch the Swing GUI
        SwingUtilities.invokeLater(() -> {
            VotingGUI gui = new VotingGUI();
            gui.setVisible(true);
        });
    }

    public VotingGUI() {
        setTitle("Distributed Voting System - GUI Client");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        /* ================= Left Panel: Client List and Controls ================= */
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        // Top left "+" button
        JButton addClientBtn = new JButton("+ Add Client");
        addClientBtn.setFocusPainted(false);
        addClientBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientList.setFont(new Font("SansSerif", Font.PLAIN, 14));

        leftPanel.add(addClientBtn, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(clientList), BorderLayout.CENTER);

        /* ================= Right Panel: CardLayout Switching ================= */
        cardLayout = new CardLayout();
        rightCardPanel = new JPanel(cardLayout);

        // View 1: Empty View (Initial state)
        emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.add(new JLabel("Please add or select a Client on the left."));

        // View 2: Vote View (Before voting)
        votePanel = new JPanel(new GridBagLayout());
        JPanel voteForm = new JPanel(new GridLayout(3, 1, 10, 10));
        voteForm.add(new JLabel("Please select an option to vote:", SwingConstants.CENTER));

        /*// Dynamically generate dropdown options based on GlobalConfiguration
        Integer[] candidates = new Integer[GlobalConfiguration.NUM_OF_VOTING_OPTIONS];
        for (int i = 0; i < GlobalConfiguration.NUM_OF_VOTING_OPTIONS; i++) {
            candidates[i] = i + 1;
        }
        choiceComboBox = new JComboBox<>(candidates);
        voteForm.add(choiceComboBox);*/

        choiceComboBox = new JComboBox<>();
        voteForm.add(choiceComboBox);

        JButton submitVoteBtn = new JButton("Submit Vote");
        voteForm.add(submitVoteBtn);
        votePanel.add(voteForm);

        // View 3: Result View (After voting)
        resultPanel = new JPanel(new BorderLayout());
        JLabel resultTitle = new JLabel("Live Voting Results", SwingConstants.CENTER);
        resultTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        resultTitle.setBorder(new EmptyBorder(20, 0, 20, 0));
        resultPanel.add(resultTitle, BorderLayout.NORTH);

        chartContainer = new JPanel();
        chartContainer.setLayout(new BoxLayout(chartContainer, BoxLayout.Y_AXIS));
        chartContainer.setBorder(new EmptyBorder(20, 40, 20, 40));
        resultPanel.add(new JScrollPane(chartContainer), BorderLayout.CENTER);

        // Add the three views to the CardLayout container
        rightCardPanel.add(emptyPanel, "EMPTY");
        rightCardPanel.add(votePanel, "VOTE");
        rightCardPanel.add(resultPanel, "RESULT");

        add(leftPanel, BorderLayout.WEST);
        add(rightCardPanel, BorderLayout.CENTER);

        /* ================= Event Listeners ================= */

        // 1. Add new client when "+" is clicked
        addClientBtn.addActionListener(e -> {
            try {
                Logging.info("GUI       | Creating new Client instance...");
                Client newClient = new Client(); // Automatically registers and connects
                newClient.getTicket();           // Automatically fetches a ticket

                clientCounter++;
                ClientNode node = new ClientNode("Client " + clientCounter, newClient);
                clientListModel.addElement(node);

                // Select the newly created client by default
                clientList.setSelectedValue(node, true);

            } catch (Exception ex) {
                Logging.error("GUI       | Failed to add client: {}", ex.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to add client. Is the server running?", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 2. Switch between selected clients
        clientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshRightPanel();
            }
        });

        // 3. Submit vote button clicked
        submitVoteBtn.addActionListener(e -> {
            ClientNode selectedNode = clientList.getSelectedValue();
            if (selectedNode != null && !selectedNode.hasVoted) {
                int choice = (Integer) choiceComboBox.getSelectedItem();
                try {
                    Logging.info("GUI       | {} is submitting vote for choice {}.", selectedNode.name, choice);
                    selectedNode.client.vote(choice); // Call remote vote
                    selectedNode.hasVoted = true;     // Update local state

                    // Refresh the list text (to show "[Voted]")
                    clientList.repaint();
                    // Refresh the right panel (switch to the results chart)
                    refreshRightPanel();

                    JOptionPane.showMessageDialog(this, "Vote submitted successfully!");
                } catch (Exception ex) {
                    Logging.error("GUI       | Voting failed: {}", ex.getMessage());
                    JOptionPane.showMessageDialog(this, "Voting failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Determines what to show on the right panel based on the selected Client's state.
     */
    private void refreshRightPanel() {
        ClientNode selectedNode = clientList.getSelectedValue();
        if (selectedNode == null) {
            cardLayout.show(rightCardPanel, "EMPTY");
            return;
        }

        if (!selectedNode.hasVoted) {
            // --- 新增逻辑：动态从当前选中的 client 获取候选人列表 ---
            java.util.List<Integer> candidates = selectedNode.client.getCandidateList();
            if (candidates != null) {
                choiceComboBox.removeAllItems(); // 清空历史数据，防止重复添加
                for (Integer candidate : candidates) {
                    choiceComboBox.addItem(candidate); // 填充来自 Server 的最新选项
                }
            }
            // -----------------------------------------------------------

            // Not voted yet, force show the voting page
            cardLayout.show(rightCardPanel, "VOTE");
        } else {
            // Voted, fetch results and draw the chart
            Map<Integer, Integer> results = selectedNode.client.fetchVotingResults();
            if (results != null) {
                drawChart(results);
                cardLayout.show(rightCardPanel, "RESULT");
            }
        }
    }

    /**
     * Draws a simple bar chart using JProgressBar.
     */
    private void drawChart(Map<Integer, Integer> results) {
        chartContainer.removeAll();

        // Calculate total votes
        int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<Integer, Integer> entry : results.entrySet()) {
            int candidate = entry.getKey();
            int votes = entry.getValue();

            JPanel barPanel = new JPanel(new BorderLayout(10, 0));
            barPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
            barPanel.setMaximumSize(new Dimension(800, 50));

            JLabel label = new JLabel("Option " + candidate + " :");
            label.setPreferredSize(new Dimension(80, 20));

            // Use JProgressBar as a bar chart
            JProgressBar progressBar = new JProgressBar(0, totalVotes == 0 ? 1 : totalVotes);
            progressBar.setValue(votes);
            progressBar.setStringPainted(true);

            // Calculate percentage
            double percent = totalVotes == 0 ? 0.0 : (votes * 100.0 / totalVotes);
            progressBar.setString(String.format("%d votes (%.1f%%)", votes, percent));

            barPanel.add(label, BorderLayout.WEST);
            barPanel.add(progressBar, BorderLayout.CENTER);

            chartContainer.add(barPanel);
        }

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    /**
     * Wrapper class used to store the Client instance and its current state in the JList.
     */
    private static class ClientNode {
        String name;
        Client client;
        boolean hasVoted;

        public ClientNode(String name, Client client) {
            this.name = name;
            this.client = client;
            this.hasVoted = false;
        }

        @Override
        public String toString() {
            // This string is what the JList displays on the UI
            return name + (hasVoted ? " [Voted]" : " [Not Voted]");
        }
    }
}