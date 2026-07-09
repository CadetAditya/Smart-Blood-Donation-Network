package ui;

import service.BloodBankManager;
import model.Action;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Stack;

public class UndoPanel extends JPanel {
    private final BloodBankManager manager;
    private final MainFrame mainFrame;

    private final ModernTable stackTable;
    private final DefaultTableModel tableModel;
    private final JLabel stackSizeLabel;

    public UndoPanel(MainFrame mainFrame, BloodBankManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Transaction History & Undo");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        JLabel descLabel = new JLabel("Revert recent changes in the database utilizing the Stack DSA");
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Content panel split
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);

        // Top Control Panel
        CardPanel controlCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        controlCard.setLayout(new BorderLayout());
        controlCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statsPanel.setOpaque(false);
        stackSizeLabel = new JLabel("Actions in Stack: 0");
        stackSizeLabel.setFont(Theme.FONT_SUBTITLE);
        stackSizeLabel.setForeground(Theme.TEXT_LIGHT);
        statsPanel.add(stackSizeLabel);
        controlCard.add(statsPanel, BorderLayout.WEST);

        ModernButton undoBtn = new ModernButton("Undo Last Action", Theme.CRIMSON, Theme.CRIMSON_HOVER, Theme.CRIMSON_PRESSED);
        undoBtn.setPreferredSize(new Dimension(180, 40));
        controlCard.add(undoBtn, BorderLayout.EAST);

        contentPanel.add(controlCard, BorderLayout.NORTH);

        // Bottom Stack Table View Card
        CardPanel stackCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        stackCard.setLayout(new BorderLayout());
        stackCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel stackTitle = new JLabel("Undo History Stack (LIFO Order)");
        stackTitle.setFont(Theme.FONT_SUBTITLE);
        stackTitle.setForeground(Theme.TEXT_LIGHT);
        stackTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        stackCard.add(stackTitle, BorderLayout.NORTH);

        String[] columns = {"Stack Level (Top First)", "Action Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stackTable = new ModernTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(stackTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        stackCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(stackCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Listener
        undoBtn.addActionListener(e -> {
            if (manager.getUndoStack().isEmpty()) {
                JOptionPane.showMessageDialog(this, "The Undo Stack is empty!", "No Actions", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            manager.undoLastAction();
            mainFrame.refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Last action reversed successfully!", "Undo Complete", JOptionPane.INFORMATION_MESSAGE);
        });

        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        Stack<Action> stack = manager.getUndoStack();
        stackSizeLabel.setText("Actions in Stack: " + stack.size());

        // LIFO: Print from top of the stack to bottom
        for (int i = stack.size() - 1; i >= 0; i--) {
            tableModel.addRow(new Object[]{
                stack.size() - i, // Top-down level index (e.g. 1st from top, 2nd from top...)
                stack.get(i).getDescription()
            });
        }
    }
}
