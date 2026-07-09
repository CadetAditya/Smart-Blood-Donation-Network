package ui;

import service.BloodBankManager;
import model.Donor;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PublicRegisterPanel extends JPanel {
    private final MainFrame mainFrame;
    private final BloodBankManager manager;

    private final ModernTextField nameField;
    private final ModernTextField ageField;
    private final ModernTextField phoneField;
    private final JComboBox<String> bloodGroupCombo;
    private final ModernTextField cityField;
    private final ModernTextField dateField;
    private final JLabel statusLabel;

    public PublicRegisterPanel(MainFrame mainFrame, BloodBankManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_DARK);

        CardPanel card = new CardPanel(20, Theme.BG_CARD, Theme.BORDER_COLOR);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 25, 30, 25));
        card.setPreferredSize(new Dimension(480, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Title Section (Row 0, 1)
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 2, 10);
        JLabel titleLabel = new JLabel("DONOR REGISTRATION");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.CRIMSON);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 10, 25, 10);
        JLabel quoteLabel = new JLabel("Become a Hero. Save Up to 3 Lives.");
        quoteLabel.setFont(Theme.FONT_SMALL);
        quoteLabel.setForeground(Theme.TEXT_MUTED);
        quoteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(quoteLabel, gbc);

        // Initialize Input Fields
        nameField = new ModernTextField("Enter your full name...");
        ageField = new ModernTextField("Enter age...");
        phoneField = new ModernTextField("Enter phone number...");
        cityField = new ModernTextField("Enter city...");
        dateField = new ModernTextField("YYYY-MM-DD (or leave empty)");

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

        // 1. Full Name (Row 2, 3 - Full Width)
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 10, 2, 10);
        card.add(createFormLabel("Full Name *"), gbc);
        
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 10, 14, 10);
        card.add(nameField, gbc);

        // 2. Age & Phone (Row 4, 5 - Split Columns)
        // Labels
        gbc.gridy = row; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(5, 10, 2, 5);
        card.add(createFormLabel("Age *"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.5;
        gbc.insets = new Insets(5, 5, 2, 10);
        card.add(createFormLabel("Phone Number *"), gbc);
        
        row++;
        // Fields
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 10, 14, 5);
        card.add(ageField, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 5, 14, 10);
        card.add(phoneField, gbc);

        row++;
        // 3. Blood Group & City (Row 6, 7 - Split Columns)
        // Labels
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(5, 10, 2, 5);
        card.add(createFormLabel("Blood Group *"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.5;
        gbc.insets = new Insets(5, 5, 2, 10);
        card.add(createFormLabel("City *"), gbc);
        
        row++;
        // Fields/Combos
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 10, 14, 5);
        card.add(bloodGroupCombo, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 5, 14, 10);
        card.add(cityField, gbc);

        row++;
        // 4. Last Donation Date (Row 8, 9 - Full Width)
        gbc.gridy = row++; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.gridx = 0;
        gbc.insets = new Insets(5, 10, 2, 10);
        card.add(createFormLabel("Last Donation (YYYY-MM-DD)"), gbc);
        
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 10, 20, 10);
        card.add(dateField, gbc);

        // Setup Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        ModernButton submitBtn = new ModernButton("Register Now");
        ModernButton backBtn = new ModernButton("Back to Login", Theme.BORDER_COLOR, Theme.BG_INPUT, Theme.BG_DARK);
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);

        // 5. Buttons (Row 10 - Full Width)
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 10, 5, 10);
        card.add(buttonPanel, gbc);

        // 6. Status Error Label (Row 11 - Full Width)
        gbc.gridy = row++;
        gbc.insets = new Insets(8, 10, 0, 10);
        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.COLOR_CRITICAL);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(statusLabel, gbc);

        add(card);

        // Action Listeners
        backBtn.addActionListener(e -> {
            clearForm();
            statusLabel.setText(" ");
            mainFrame.showLogin();
        });

        submitBtn.addActionListener(e -> registerDonor());
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
        nameField.setText("");
        ageField.setText("");
        phoneField.setText("");
        bloodGroupCombo.setSelectedIndex(0);
        cityField.setText("");
        dateField.setText("");
    }

    private void registerDonor() {
        try {
            String name = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            String phone = phoneField.getText().trim();
            String blood = (String) bloodGroupCombo.getSelectedItem();
            String city = cityField.getText().trim();
            String dateStr = dateField.getText().trim();

            if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty() || city.isEmpty()) {
                showStatus("Please fill in all required (*) fields!", false);
                return;
            }

            int age = Integer.parseInt(ageStr);
            if (age < 18 || age > 65) {
                showStatus("Age must be between 18 and 65 to donate!", false);
                return;
            }

            LocalDate lastDonation = null;
            if (!dateStr.isEmpty() && !dateStr.equalsIgnoreCase("YYYY-MM-DD (or leave empty)")) {
                lastDonation = LocalDate.parse(dateStr);
            }

            // Register directly
            manager.registerDonor(name, age, phone, blood, city, lastDonation, true);
            
            List<Donor> all = manager.getAllDonors();
            int newId = all.get(all.size() - 1).getId();

            JOptionPane.showMessageDialog(this,
                String.format("Thank you, %s! Your life-saving registration is complete.\nYour registered Donor ID is: %d", name, newId),
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            statusLabel.setText(" ");
            mainFrame.refreshAllPanels();
            mainFrame.showLogin();
        } catch (NumberFormatException ex) {
            showStatus("Age must be a valid number!", false);
        } catch (DateTimeParseException ex) {
            showStatus("Date format must be YYYY-MM-DD!", false);
        } catch (IllegalArgumentException ex) {
            showStatus(ex.getMessage(), false);
        }
    }
}
