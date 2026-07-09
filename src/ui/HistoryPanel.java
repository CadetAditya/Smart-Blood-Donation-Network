package ui;

import service.BloodBankManager;
import model.Donation;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    private final BloodBankManager manager;
    private final MainFrame mainFrame;

    private final ModernTable historyTable;
    private final DefaultTableModel tableModel;

    public HistoryPanel(MainFrame mainFrame, BloodBankManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Donation History Ledger");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        JLabel descLabel = new JLabel("Audit log of all completed matches stored sequentially in a LinkedList");
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Content Table Card
        CardPanel card = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel cardTitle = new JLabel("Completed Match Records");
        cardTitle.setFont(Theme.FONT_SUBTITLE);
        cardTitle.setForeground(Theme.TEXT_LIGHT);
        cardTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(cardTitle, BorderLayout.NORTH);

        String[] columns = {"Donation ID", "Donor Name", "Patient Name", "Blood Type", "Units", "Location", "Donation Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new ModernTable(tableModel);
        historyTable.setColumnWidths(80, 130, 130, 80, 60, 100, 120);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Donation> list = manager.getDonationHistory();
        for (Donation d : list) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getDonor().getName(),
                d.getRequest().getPatientName(),
                d.getDonor().getBloodGroup(),
                d.getUnits(),
                d.getDonor().getCity(),
                d.getDonationDate().toString()
            });
        }
    }
}
