package ui;

import service.BloodBankManager;
import model.Donor;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DonorPanel extends JPanel {
    private final BloodBankManager manager;
    private final MainFrame mainFrame;

    private final ModernTextField idField;
    private final ModernTextField nameField;
    private final ModernTextField ageField;
    private final ModernTextField phoneField;
    private final JComboBox<String> bloodGroupCombo;
    private final ModernTextField cityField;
    private final ModernTextField dateField;
    private final JCheckBox availableCheck;

    private final ModernTable donorTable;
    private final DefaultTableModel tableModel;
    
    private final JLabel statusLabel;

    public DonorPanel(MainFrame mainFrame, BloodBankManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        setLayout(new BorderLayout(20, 0));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Donor Management");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        JLabel descLabel = new JLabel("Add, edit, or remove blood donors in the network");
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Main content Split (Left: Form, Right: Table)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        contentPanel.setOpaque(false);

        // Left Side: Register/Edit Form Card
        CardPanel formCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // ID (Read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblId = createFormLabel("Donor ID");
        formCard.add(lblId, gbc);
        
        gbc.gridx = 1;
        idField = new ModernTextField("Auto-Increment");
        idField.setEditable(false);
        idField.setBackground(Theme.BG_DARK);
        formCard.add(idField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblName = createFormLabel("Full Name *");
        formCard.add(lblName, gbc);
        
        gbc.gridx = 1;
        nameField = new ModernTextField("Enter donor name...");
        formCard.add(nameField, gbc);

        // Age
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblAge = createFormLabel("Age *");
        formCard.add(lblAge, gbc);
        
        gbc.gridx = 1;
        ageField = new ModernTextField("Enter age...");
        formCard.add(ageField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblPhone = createFormLabel("Phone *");
        formCard.add(lblPhone, gbc);
        
        gbc.gridx = 1;
        phoneField = new ModernTextField("Enter phone number...");
        formCard.add(phoneField, gbc);

        // Blood Group
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblBg = createFormLabel("Blood Group *");
        formCard.add(lblBg, gbc);
        
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
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblCity = createFormLabel("City *");
        formCard.add(lblCity, gbc);
        
        gbc.gridx = 1;
        cityField = new ModernTextField("Enter city (e.g. Indore)...");
        formCard.add(cityField, gbc);

        // Last Donation Date
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblDate = createFormLabel("Last Donation (YYYY-MM-DD)");
        formCard.add(lblDate, gbc);
        
        gbc.gridx = 1;
        dateField = new ModernTextField("YYYY-MM-DD or leave blank");
        formCard.add(dateField, gbc);

        // Available
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblAvail = createFormLabel("Available");
        formCard.add(lblAvail, gbc);
        
        gbc.gridx = 1;
        availableCheck = new JCheckBox("Mark as active donor");
        availableCheck.setSelected(true);
        availableCheck.setFont(Theme.FONT_BODY);
        availableCheck.setForeground(Theme.TEXT_LIGHT);
        availableCheck.setOpaque(false);
        formCard.add(availableCheck, gbc);

        // Buttons Panel inside GridBagLayout
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        btnGrid.setOpaque(false);

        ModernButton btnSave = new ModernButton("Register Donor");
        ModernButton btnUpdate = new ModernButton("Update Info", Theme.BORDER_COLOR, Theme.BG_INPUT, Theme.BG_DARK);
        ModernButton btnDelete = new ModernButton("Delete Donor", new Color(0x7A, 0x0A, 0x1E), new Color(0x9E, 0x0A, 0x22), new Color(0x5A, 0x07, 0x14));
        ModernButton btnClear = new ModernButton("Clear Form", Theme.BORDER_COLOR, Theme.BG_INPUT, Theme.BG_DARK);

        btnGrid.add(btnSave);
        btnGrid.add(btnUpdate);
        btnGrid.add(btnDelete);
        btnGrid.add(btnClear);
        formCard.add(btnGrid, gbc);

        // Status Label
        gbc.gridy = 9;
        gbc.insets = new Insets(8, 8, 0, 8);
        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.COLOR_LOW);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formCard.add(statusLabel, gbc);

        contentPanel.add(formCard);

        // Right Side: Donor Table Card
        CardPanel tableCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Search Bar on Top of Table
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblSearch = new JLabel("ID Lookup:");
        lblSearch.setFont(Theme.FONT_BOLD);
        lblSearch.setForeground(Theme.TEXT_MUTED);
        searchPanel.add(lblSearch, BorderLayout.WEST);

        ModernTextField searchField = new ModernTextField("Search donor by ID...");
        searchPanel.add(searchField, BorderLayout.CENTER);

        ModernButton searchBtn = new ModernButton("Find", Theme.CRIMSON, Theme.CRIMSON_HOVER, Theme.CRIMSON_PRESSED);
        searchBtn.setPreferredSize(new Dimension(80, 35));
        searchPanel.add(searchBtn, BorderLayout.EAST);
        
        tableCard.add(searchPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"ID", "Name", "Age", "Blood", "City", "Eligible"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donorTable = new ModernTable(tableModel);
        donorTable.setColumnWidths(40, 150, 50, 60, 120, 80);
        
        // Selection Listener to populate form
        donorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = donorTable.getSelectedRow();
                if (row >= 0) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    loadDonorToForm(id);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard);
        add(contentPanel, BorderLayout.CENTER);

        // Add Action Listeners
        btnSave.addActionListener(e -> saveDonor());
        btnUpdate.addActionListener(e -> updateDonor());
        btnDelete.addActionListener(e -> deleteDonor());
        btnClear.addActionListener(e -> clearForm());
        
        searchBtn.addActionListener(e -> {
            try {
                String input = searchField.getText().trim();
                if (input.isEmpty()) {
                    showStatus("Please enter an ID!", false);
                    return;
                }
                int id = Integer.parseInt(input);
                loadDonorToForm(id);
                // Highlight row in table
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if ((int) tableModel.getValueAt(i, 0) == id) {
                        donorTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                showStatus("Please enter a valid numeric ID!", false);
            }
        });

        refreshTable();
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
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        phoneField.setText("");
        bloodGroupCombo.setSelectedIndex(0);
        cityField.setText("");
        dateField.setText("");
        availableCheck.setSelected(true);
        donorTable.clearSelection();
        showStatus("Form cleared.", true);
    }

    private void loadDonorToForm(int id) {
        Donor donor = manager.getDonorById(id);
        if (donor != null) {
            idField.setText(String.valueOf(donor.getId()));
            nameField.setText(donor.getName());
            ageField.setText(String.valueOf(donor.getAge()));
            phoneField.setText(donor.getPhone());
            bloodGroupCombo.setSelectedItem(donor.getBloodGroup());
            cityField.setText(donor.getCity());
            dateField.setText(donor.getLastDonationDate() == null ? "" : donor.getLastDonationDate().toString());
            availableCheck.setSelected(donor.isAvailable());
            statusLabel.setText("Loaded donor: " + donor.getName());
            statusLabel.setForeground(Theme.COLOR_LOW);
        } else {
            showStatus("Donor ID " + id + " not found!", false);
        }
    }

    private void saveDonor() {
        try {
            String name = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            String phone = phoneField.getText().trim();
            String blood = (String) bloodGroupCombo.getSelectedItem();
            String city = cityField.getText().trim();
            String dateStr = dateField.getText().trim();
            boolean available = availableCheck.isSelected();

            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty() || city.isEmpty()) {
                showStatus("Please fill in all required (*) fields!", false);
                return;
            }

            int age = Integer.parseInt(ageStr);
            if (age < 18 || age > 65) {
                showStatus("Donor age must be between 18 and 65!", false);
                return;
            }

            LocalDate lastDonation = null;
            if (!dateStr.isEmpty() && !dateStr.equalsIgnoreCase("YYYY-MM-DD or leave blank")) {
                lastDonation = LocalDate.parse(dateStr);
            }

            manager.registerDonor(name, age, phone, blood, city, lastDonation, available);
            clearForm();
            mainFrame.refreshAllPanels();
            showStatus("Donor registered successfully!", true);
        } catch (NumberFormatException ex) {
            showStatus("Age must be a valid number!", false);
        } catch (DateTimeParseException ex) {
            showStatus("Date format must be YYYY-MM-DD!", false);
        } catch (IllegalArgumentException ex) {
            showStatus(ex.getMessage(), false);
        }
    }

    private void updateDonor() {
        try {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty() || idStr.equals("Auto-Increment")) {
                showStatus("No donor selected for update!", false);
                return;
            }
            int id = Integer.parseInt(idStr);

            String name = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            String phone = phoneField.getText().trim();
            String blood = (String) bloodGroupCombo.getSelectedItem();
            String city = cityField.getText().trim();
            String dateStr = dateField.getText().trim();
            boolean available = availableCheck.isSelected();

            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty() || city.isEmpty()) {
                showStatus("Please fill in all required (*) fields!", false);
                return;
            }

            int age = Integer.parseInt(ageStr);
            if (age < 18 || age > 65) {
                showStatus("Donor age must be between 18 and 65!", false);
                return;
            }

            LocalDate lastDonation = null;
            if (!dateStr.isEmpty() && !dateStr.equalsIgnoreCase("YYYY-MM-DD or leave blank")) {
                lastDonation = LocalDate.parse(dateStr);
            }

            manager.updateDonor(id, name, age, phone, blood, city, lastDonation, available);
            mainFrame.refreshAllPanels();
            showStatus("Donor ID " + id + " updated!", true);
        } catch (NumberFormatException ex) {
            showStatus("Age must be a valid number!", false);
        } catch (DateTimeParseException ex) {
            showStatus("Date format must be YYYY-MM-DD!", false);
        } catch (IllegalArgumentException ex) {
            showStatus(ex.getMessage(), false);
        }
    }

    private void deleteDonor() {
        String idStr = idField.getText().trim();
        if (idStr.isEmpty() || idStr.equals("Auto-Increment")) {
            showStatus("No donor selected for deletion!", false);
            return;
        }
        int id = Integer.parseInt(idStr);
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete donor ID: " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            manager.deleteDonor(id);
            clearForm();
            mainFrame.refreshAllPanels();
            showStatus("Donor ID " + id + " deleted.", true);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Donor> donors = manager.getAllDonors();
        for (Donor d : donors) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getAge(),
                d.getBloodGroup(),
                d.getCity(),
                d.isEligible() ? "Yes" : "No"
            });
        }
    }
}
