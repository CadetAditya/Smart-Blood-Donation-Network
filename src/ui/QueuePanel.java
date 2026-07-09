package ui;

import service.BloodBankManager;
import model.Donor;
import model.Request;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.PriorityQueue;

public class QueuePanel extends JPanel {
    private final BloodBankManager manager;
    private final MainFrame mainFrame;

    private Request currentRequest = null;
    
    private final ModernTable queueTable;
    private final DefaultTableModel queueModel;

    private final CardPanel workflowCard;
    private final JLabel requestDetailsLabel;
    
    private final ModernTable donorMatchTable;
    private final DefaultTableModel donorMatchModel;
    
    private final ModernButton processBtn;
    private final ModernButton discardBtn;
    private final ModernButton assignBtn;
    private final ModernButton cancelWorkflowBtn;

    private final JLabel statusLabel;

    public QueuePanel(MainFrame mainFrame, BloodBankManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Emergency Request Queue");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        JLabel descLabel = new JLabel("Manage and resolve active patient demands using priority match-making");
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center split: Top: Queue Table, Bottom: Process Work Area
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Card 1: Active Queue Table
        CardPanel queueCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        queueCard.setLayout(new BorderLayout());
        queueCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel queueHeader = new JPanel(new BorderLayout());
        queueHeader.setOpaque(false);
        queueHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel queueTitle = new JLabel("Active Requests Queue (Priority Order)");
        queueTitle.setFont(Theme.FONT_SUBTITLE);
        queueTitle.setForeground(Theme.TEXT_LIGHT);
        queueHeader.add(queueTitle, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        processBtn = new ModernButton("Process Next Request");
        discardBtn = new ModernButton("Discard Front Request", Theme.BORDER_COLOR, Theme.BG_INPUT, Theme.BG_DARK);
        ModernButton refreshBtn = new ModernButton("Refresh", Theme.BORDER_COLOR, Theme.BG_INPUT, Theme.BG_DARK);
        
        btnPanel.add(processBtn);
        btnPanel.add(discardBtn);
        btnPanel.add(refreshBtn);
        queueHeader.add(btnPanel, BorderLayout.EAST);
        
        queueCard.add(queueHeader, BorderLayout.NORTH);

        String[] qCols = {"ID", "Patient Name", "Blood Group", "City", "Units", "Priority", "Hospital"};
        queueModel = new DefaultTableModel(qCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        queueTable = new ModernTable(queueModel);
        queueTable.setColumnWidths(40, 150, 80, 100, 60, 90, 140);
        JScrollPane qScroll = new JScrollPane(queueTable);
        qScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        qScroll.getViewport().setBackground(Theme.BG_DARK);
        queueCard.add(qScroll, BorderLayout.CENTER);

        gbc.gridy = 0;
        gbc.weighty = 0.40;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(queueCard, gbc);

        // Card 2: Workflow area
        workflowCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        workflowCard.setLayout(new BorderLayout());
        workflowCard.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Details on Left, Match results on Right
        JPanel workflowSplit = new JPanel(new GridLayout(1, 2, 20, 0));
        workflowSplit.setOpaque(false);

        // Left Panel: Current request overview
        JPanel reqDetailsPanel = new JPanel();
        reqDetailsPanel.setOpaque(false);
        reqDetailsPanel.setLayout(new BoxLayout(reqDetailsPanel, BoxLayout.Y_AXIS));

        JLabel flowLabel = new JLabel("Match-Making Workflow");
        flowLabel.setFont(Theme.FONT_SUBTITLE);
        flowLabel.setForeground(Theme.CRIMSON);
        flowLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        reqDetailsPanel.add(flowLabel);

        requestDetailsLabel = new JLabel("<html><body style='width: 250px;'><b style='color:#a6adc8;'>No request in progress.</b><br/>Click \"Process Next Request\" to poll the highest priority item from the queue.</body></html>");
        requestDetailsLabel.setFont(Theme.FONT_BODY);
        requestDetailsLabel.setForeground(Theme.TEXT_LIGHT);
        reqDetailsPanel.add(requestDetailsLabel);
        
        reqDetailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel flowActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        flowActions.setOpaque(false);
        assignBtn = new ModernButton("Complete Match & Donate");
        assignBtn.setEnabled(false);
        cancelWorkflowBtn = new ModernButton("Re-queue Request", Theme.BORDER_COLOR, Theme.BG_INPUT, Theme.BG_DARK);
        cancelWorkflowBtn.setEnabled(false);
        
        flowActions.add(assignBtn);
        flowActions.add(cancelWorkflowBtn);
        reqDetailsPanel.add(flowActions);

        reqDetailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.COLOR_LOW);
        reqDetailsPanel.add(statusLabel);

        workflowSplit.add(reqDetailsPanel);

        // Right Panel: Compatible Eligible Donors List
        JPanel donorMatchPanel = new JPanel(new BorderLayout());
        donorMatchPanel.setOpaque(false);
        
        JLabel matchTitle = new JLabel("Compatible Eligible Donors (O(1) Map Lookups)");
        matchTitle.setFont(Theme.FONT_BOLD);
        matchTitle.setForeground(Theme.TEXT_MUTED);
        matchTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        donorMatchPanel.add(matchTitle, BorderLayout.NORTH);

        String[] dCols = {"ID", "Name", "Blood", "City", "Phone", "Last Donation"};
        donorMatchModel = new DefaultTableModel(dCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donorMatchTable = new ModernTable(donorMatchModel);
        donorMatchTable.setColumnWidths(40, 120, 60, 100, 110, 120);
        donorMatchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = donorMatchTable.getSelectedRow();
                assignBtn.setEnabled(row >= 0 && currentRequest != null);
            }
        });

        JScrollPane dScroll = new JScrollPane(donorMatchTable);
        dScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        dScroll.getViewport().setBackground(Theme.BG_DARK);
        donorMatchPanel.add(dScroll, BorderLayout.CENTER);

        workflowSplit.add(donorMatchPanel);
        workflowCard.add(workflowSplit, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.weighty = 0.60;
        gbc.insets = new Insets(10, 0, 0, 0);
        centerPanel.add(workflowCard, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Listeners
        processBtn.addActionListener(e -> startWorkflow());
        discardBtn.addActionListener(e -> discardFrontRequest());
        cancelWorkflowBtn.addActionListener(e -> requeueRequest());
        assignBtn.addActionListener(e -> completeMatch());
        refreshBtn.addActionListener(e -> {
            mainFrame.refreshAllPanels();
            showStatus("Queue refreshed.", true);
        });

        refreshQueueTable();
    }

    private void showStatus(String msg, boolean success) {
        statusLabel.setForeground(success ? Theme.COLOR_LOW : Theme.COLOR_CRITICAL);
        statusLabel.setText(msg);
    }

    private void startWorkflow() {
        if (currentRequest != null) {
            showStatus("Complete or cancel the current workflow first!", false);
            return;
        }

        currentRequest = manager.pollNextRequest();
        if (currentRequest == null) {
            showStatus("The request queue is empty!", false);
            return;
        }

        // Disable queue buttons during process
        processBtn.setEnabled(false);
        discardBtn.setEnabled(false);
        cancelWorkflowBtn.setEnabled(true);

        // Load details
        String detailsHtml = String.format(
            "<html><body>" +
            "<h2>Patient: %s</h2>" +
            "<table style='font-size:12px; color:#cdd6f4;'>" +
            "<tr><td><b>Blood Group:</b></td><td style='color:#f38ba8;'><b>%s</b></td></tr>" +
            "<tr><td><b>Location:</b></td><td>%s</td></tr>" +
            "<tr><td><b>Emergency:</b></td><td style='color:#fab387;'><b>%s</b></td></tr>" +
            "<tr><td><b>Units Required:</b></td><td>%d units</td></tr>" +
            "<tr><td><b>Hospital:</b></td><td>%s</td></tr>" +
            "</table>" +
            "</body></html>",
            currentRequest.getPatientName(),
            currentRequest.getBloodGroup(),
            currentRequest.getCity(),
            currentRequest.getPriority().getDisplayName(),
            currentRequest.getUnitsRequired(),
            currentRequest.getHospital()
        );
        requestDetailsLabel.setText(detailsHtml);

        // Perform DSA Indexed lookup
        loadMatchingDonors(currentRequest.getBloodGroup(), currentRequest.getCity());
        mainFrame.refreshAllPanels();
        showStatus("Polled highest priority request. Select a donor to match.", true);
    }

    private void loadMatchingDonors(String blood, String city) {
        donorMatchModel.setRowCount(0);
        List<Donor> compatible = manager.searchCompatibleDonors(blood, city);
        for (Donor d : compatible) {
            donorMatchModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getBloodGroup(),
                d.getCity(),
                d.getPhone(),
                d.getLastDonationDate() == null ? "Never" : d.getLastDonationDate().toString()
            });
        }
        if (compatible.isEmpty()) {
            showStatus("No compatible, eligible donors found in the index!", false);
        }
    }

    private void requeueRequest() {
        if (currentRequest != null) {
            manager.addRequestInternal(currentRequest);
            showStatus("Request returned to queue.", true);
            clearWorkflow();
            mainFrame.refreshAllPanels();
        }
    }

    private void discardFrontRequest() {
        Request r = manager.pollNextRequest();
        if (r != null) {
            JOptionPane.showMessageDialog(this, "Discarded request for: " + r.getPatientName(), "Request Removed", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.refreshAllPanels();
        } else {
            showStatus("Queue is empty!", false);
        }
    }

    private void clearWorkflow() {
        currentRequest = null;
        requestDetailsLabel.setText("<html><body style='width: 250px;'><b style='color:#a6adc8;'>No request in progress.</b><br/>Click \"Process Next Request\" to poll the highest priority item from the queue.</body></html>");
        donorMatchModel.setRowCount(0);
        processBtn.setEnabled(true);
        discardBtn.setEnabled(true);
        assignBtn.setEnabled(false);
        cancelWorkflowBtn.setEnabled(false);
    }

    private void completeMatch() {
        int row = donorMatchTable.getSelectedRow();
        if (row < 0 || currentRequest == null) {
            showStatus("Please select a donor first!", false);
            return;
        }

        int donorId = (int) donorMatchModel.getValueAt(row, 0);
        Donor donor = manager.getDonorById(donorId);
        if (donor != null) {
            manager.assignDonation(donor, currentRequest, currentRequest.getUnitsRequired());
            showStatus("Match completed! Donor " + donor.getName() + " assigned.", true);
            clearWorkflow();
            mainFrame.refreshAllPanels();
        }
    }

    public void refreshQueueTable() {
        queueModel.setRowCount(0);
        PriorityQueue<Request> queueCopy = new PriorityQueue<>(manager.getEmergencyQueue());
        while (!queueCopy.isEmpty()) {
            Request r = queueCopy.poll();
            queueModel.addRow(new Object[]{
                r.getId(),
                r.getPatientName(),
                r.getBloodGroup(),
                r.getCity(),
                r.getUnitsRequired(),
                r.getPriority().getDisplayName(),
                r.getHospital()
            });
        }
    }
}
