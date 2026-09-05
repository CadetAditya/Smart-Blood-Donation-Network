package ui;

import service.BloodBankManager;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import model.Donor;

public class DashboardPanel extends JPanel {
    private final BloodBankManager manager;
    private final MainFrame mainFrame;

    
    private final JLabel totalDonorsLabel;
    private final JLabel activeRequestsLabel;
    private final JLabel donationHistoryLabel;
    private final JLabel undoStackLabel;
    private final JPanel distributionContainer;

    public DashboardPanel(MainFrame mainFrame, BloodBankManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        
        JLabel descLabel = new JLabel("System statistics and fast actions");
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_MUTED);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center scroll pane content 
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Count Cards (Grid of 4)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        totalDonorsLabel = new JLabel("0");
        activeRequestsLabel = new JLabel("0");
        donationHistoryLabel = new JLabel("0");
        undoStackLabel = new JLabel("0");

        statsPanel.add(createStatCard("Total Donors", totalDonorsLabel, Theme.CRIMSON));
        statsPanel.add(createStatCard("Pending Requests", activeRequestsLabel, Theme.COLOR_HIGH));
        statsPanel.add(createStatCard("Donation History", donationHistoryLabel, Theme.COLOR_LOW));
        statsPanel.add(createStatCard("Undo Actions", undoStackLabel, Theme.BORDER_FOCUS));

        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Distribution & Quick Actions Split
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);

        // Left Panel: Donor Blood Type Distribution
        CardPanel distCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        distCard.setLayout(new BorderLayout());
        distCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel distTitle = new JLabel("Blood Distribution");
        distTitle.setFont(Theme.FONT_SUBTITLE);
        distTitle.setForeground(Theme.TEXT_LIGHT);
        distTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        distCard.add(distTitle, BorderLayout.NORTH);

        distributionContainer = new JPanel(new GridLayout(4, 2, 10, 10));
        distributionContainer.setOpaque(false);
        distCard.add(distributionContainer, BorderLayout.CENTER);
        
        // Right Panel: System Controls / Quick Actions
        CardPanel controlsCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        controlsCard.setLayout(new BoxLayout(controlsCard, BoxLayout.Y_AXIS));
        controlsCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel ctrlTitle = new JLabel("Quick Shortcuts");
        ctrlTitle.setFont(Theme.FONT_SUBTITLE);
        ctrlTitle.setForeground(Theme.TEXT_LIGHT);
        ctrlTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        controlsCard.add(ctrlTitle);

        ModernButton registerShortcut = new ModernButton("Register New Donor");
        registerShortcut.addActionListener(e -> mainFrame.showPanel("DonorPanel"));
        registerShortcut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        ModernButton requestShortcut = new ModernButton("Create Recipient Request");
        requestShortcut.addActionListener(e -> mainFrame.showPanel("RequestPanel"));
        requestShortcut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        ModernButton queueShortcut = new ModernButton("Process Emergency Queue");
        queueShortcut.addActionListener(e -> mainFrame.showPanel("QueuePanel"));
        queueShortcut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        ModernButton undoShortcut = new ModernButton("Undo Last Action", Theme.BORDER_COLOR, Theme.BG_INPUT, Theme.BG_DARK);
        undoShortcut.addActionListener(e -> {
            manager.undoLastAction();
            mainFrame.refreshAllPanels();
        });
        undoShortcut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        controlsCard.add(registerShortcut);
        controlsCard.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsCard.add(requestShortcut);
        controlsCard.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsCard.add(queueShortcut);
        controlsCard.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsCard.add(undoShortcut);

        splitPanel.add(distCard);
        splitPanel.add(controlsCard);
        centerPanel.add(splitPanel);

        add(centerPanel, BorderLayout.CENTER);
        refresh();
    }

    
    private CardPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        CardPanel card = new CardPanel(12, Theme.BG_CARD, Theme.BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(180, 105));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_SMALL);
        titleLabel.setForeground(Theme.TEXT_MUTED);
        card.add(titleLabel, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void refresh() {
        totalDonorsLabel.setText(String.valueOf(manager.getAllDonors().size()));
        activeRequestsLabel.setText(String.valueOf(manager.getEmergencyQueue().size()));
        donationHistoryLabel.setText(String.valueOf(manager.getDonationHistory().size()));
        undoStackLabel.setText(String.valueOf(manager.getUndoStack().size()));

        // Update distribution UI
        distributionContainer.removeAll();
        String[] bloodGroups = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        HashMap<String, Integer> counts = new HashMap<>();
        for (String bg : bloodGroups) counts.put(bg, 0);

        List<Donor> donors = manager.getAllDonors();
        for (Donor d : donors) {
            String bg = d.getBloodGroup().toUpperCase();
            counts.put(bg, counts.getOrDefault(bg, 0) + 1);
        }

        for (String bg : bloodGroups) {
            JPanel item = new JPanel(new BorderLayout());
            item.setOpaque(false);

            JLabel bgLabel = new JLabel(bg);
            bgLabel.setFont(Theme.FONT_BOLD);
            bgLabel.setForeground(Theme.TEXT_LIGHT);

            int val = counts.get(bg);
            JLabel countLabel = new JLabel(val + " donors");
            countLabel.setFont(Theme.FONT_BODY);
            countLabel.setForeground(val > 0 ? Theme.CRIMSON : Theme.TEXT_MUTED);

            item.add(bgLabel, BorderLayout.WEST);
            item.add(countLabel, BorderLayout.EAST);
            distributionContainer.add(item);
        }

        distributionContainer.revalidate();
        distributionContainer.repaint();
    }
}
