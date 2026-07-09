package ui;

import service.BloodBankManager;
import model.Priority;
import model.Request;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.PriorityQueue;

public class RequestPanel extends JPanel {
    private final BloodBankManager manager;
    private final MainFrame mainFrame;

    private final ModernTextField patientNameField;
    private final JComboBox<String> bloodGroupCombo;
    private final ModernTextField cityField;
    private final ModernTextField unitsField;
    private final JComboBox<Priority> priorityCombo;
    private final ModernTextField hospitalField;

    private final ModernTable previewTable;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;

    public RequestPanel(MainFrame mainFrame, BloodBankManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        setLayout(new BorderLayout(20, 0));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Recipient Requests");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        JLabel descLabel = new JLabel("Add recipient demands to the prioritised emergency queue");
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Layout Split
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        contentPanel.setOpaque(false);

        // Left Side: Request Form
        CardPanel formCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblFormTitle = new JLabel("Create Request");
        lblFormTitle.setFont(Theme.FONT_SUBTITLE);
        lblFormTitle.setForeground(Theme.CRIMSON);
        formCard.add(lblFormTitle, gbc);
        gbc.gridwidth = 1;

        // Patient Name
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createFormLabel("Patient Name *"), gbc);
        gbc.gridx = 1;
        patientNameField = new ModernTextField("Enter patient name...");
        formCard.add(patientNameField, gbc);

        // Blood Group
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(createFormLabel("Blood Group *"), gbc);
        gbc.gridx = 1;
        String[] bloodGroups = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
        bloodGroupCombo = new JComboBox<>(bloodGroups);
        bloodGroupCombo.setFont(Theme.FONT_BODY);
        bloodGroupCombo.setBackground(Theme.BG_INPUT);
        bloodGroupCombo.setForeground(Theme.TEXT_LIGHT);
        bloodGroupCombo.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        bloodGroupCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? Theme.CRIMSON : Theme.BG_CARD);
                c.setForeground(Theme.TEXT_LIGHT);
                return c;
            }
        });
        formCard.add(bloodGroupCombo, gbc);

        // City
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(createFormLabel("City *"), gbc);
        gbc.gridx = 1;
        cityField = new ModernTextField("Enter city (e.g. Indore)...");
        formCard.add(cityField, gbc);

        // Units
        gbc.gridx = 0; gbc.gridy = 4;
        formCard.add(createFormLabel("Units Required *"), gbc);
        gbc.gridx = 1;
        unitsField = new ModernTextField("e.g. 2");
        formCard.add(unitsField, gbc);

        // Priority Level
        gbc.gridx = 0; gbc.gridy = 5;
        formCard.add(createFormLabel("Emergency Level *"), gbc);
        gbc.gridx = 1;
        priorityCombo = new JComboBox<>(Priority.values());
        priorityCombo.setFont(Theme.FONT_BODY);
        priorityCombo.setBackground(Theme.BG_INPUT);
        priorityCombo.setForeground(Theme.TEXT_LIGHT);
        priorityCombo.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        priorityCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? Theme.CRIMSON : Theme.BG_CARD);
                c.setForeground(Theme.TEXT_LIGHT);
                return c;
            }
        });
        formCard.add(priorityCombo, gbc);

        // Hospital
        gbc.gridx = 0; gbc.gridy = 6;
        formCard.add(createFormLabel("Hospital *"), gbc);
        gbc.gridx = 1;
        hospitalField = new ModernTextField("Enter hospital name...");
        formCard.add(hospitalField, gbc);

        // Submit Button
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        ModernButton submitBtn = new ModernButton("Queue Recipient Request");
        formCard.add(submitBtn, gbc);

        // Status Label
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 10, 0, 10);
        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.COLOR_LOW);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formCard.add(statusLabel, gbc);
        
        contentPanel.add(formCard);

        // Right Side: Current Queue Preview (Read-only overview)
        CardPanel queuePreviewCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        queuePreviewCard.setLayout(new BorderLayout());
        queuePreviewCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel previewTitle = new JLabel("Active Queue Preview");
        previewTitle.setFont(Theme.FONT_SUBTITLE);
        previewTitle.setForeground(Theme.TEXT_LIGHT);
        previewTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        queuePreviewCard.add(previewTitle, BorderLayout.NORTH);

        String[] columns = {"Patient", "Blood", "City", "Priority", "Units"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        previewTable = new ModernTable(tableModel);
        previewTable.setColumnWidths(140, 60, 110, 100, 60);

        JScrollPane scrollPane = new JScrollPane(previewTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        queuePreviewCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(queuePreviewCard);
        add(contentPanel, BorderLayout.CENTER);

        // Listener
        submitBtn.addActionListener(e -> submitRequest());

        refreshPreviewTable();
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.TEXT_MUTED);
        return label;
    }

    private void showStatus(String msg, boolean success) {
        statusLabel.setForeground(success ? Theme.COLOR_LOW : Theme.COLOR_CRITICAL);
        statusLabel.setText(msg);
    }

    private void clearForm() {
        patientNameField.setText("");
        cityField.setText("");
        unitsField.setText("");
        hospitalField.setText("");
        bloodGroupCombo.setSelectedIndex(0);
        priorityCombo.setSelectedIndex(0);
    }

    private void submitRequest() {
        try {
            String name = patientNameField.getText().trim();
            String city = cityField.getText().trim();
            String unitsStr = unitsField.getText().trim();
            String hospital = hospitalField.getText().trim();
            String blood = (String) bloodGroupCombo.getSelectedItem();
            Priority priority = (Priority) priorityCombo.getSelectedItem();

            if (name.isEmpty() || city.isEmpty() || unitsStr.isEmpty() || hospital.isEmpty()) {
                showStatus("Please fill in all fields!", false);
                return;
            }

            int units = Integer.parseInt(unitsStr);
            if (units <= 0 || units > 10) {
                showStatus("Units required must be between 1 and 10!", false);
                return;
            }

            manager.addRequest(name, blood, city, units, priority, hospital);
            clearForm();
            mainFrame.refreshAllPanels();
            showStatus("Request successfully queued!", true);
        } catch (NumberFormatException ex) {
            showStatus("Units must be a valid number!", false);
        }
    }

    public void refreshPreviewTable() {
        tableModel.setRowCount(0);
        // We clone or copy elements to show ordering without polling!
        PriorityQueue<Request> queueCopy = new PriorityQueue<>(manager.getEmergencyQueue());
        while (!queueCopy.isEmpty()) {
            Request r = queueCopy.poll();
            tableModel.addRow(new Object[]{
                r.getPatientName(),
                r.getBloodGroup(),
                r.getCity(),
                r.getPriority().getDisplayName(),
                r.getUnitsRequired()
            });
        }
    }
}
