package ui;

import service.BloodBankManager;
import model.Donor;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchPanel extends JPanel {
    private final BloodBankManager manager;
    private final MainFrame mainFrame;

    private final JComboBox<String> bloodGroupCombo;
    private final ModernTextField cityField;
    private final JCheckBox exactMatchCheck;

    private final ModernTable resultsTable;
    private final DefaultTableModel tableModel;
    private final JLabel timeLabel;

    public SearchPanel(MainFrame mainFrame, BloodBankManager manager) {
        this.mainFrame = mainFrame;
        this.manager = manager;
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Index-Based Search");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        JLabel descLabel = new JLabel("Perform O(1) indexed lookups using the blood group and city nested maps");
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);

        // Top Search Form Card
        CardPanel searchCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));

        JLabel lblBg = new JLabel("Blood Group:");
        lblBg.setFont(Theme.FONT_BOLD);
        lblBg.setForeground(Theme.TEXT_MUTED);
        searchCard.add(lblBg);

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
        searchCard.add(bloodGroupCombo);

        JLabel lblCity = new JLabel("City:");
        lblCity.setFont(Theme.FONT_BOLD);
        lblCity.setForeground(Theme.TEXT_MUTED);
        searchCard.add(lblCity);

        cityField = new ModernTextField("e.g. Indore");
        cityField.setPreferredSize(new Dimension(150, 36));
        searchCard.add(cityField);

        exactMatchCheck = new JCheckBox("Exact Match Only");
        exactMatchCheck.setSelected(false);
        exactMatchCheck.setFont(Theme.FONT_BODY);
        exactMatchCheck.setForeground(Theme.TEXT_LIGHT);
        exactMatchCheck.setOpaque(false);
        searchCard.add(exactMatchCheck);

        ModernButton searchBtn = new ModernButton("Query Index", Theme.CRIMSON, Theme.CRIMSON_HOVER, Theme.CRIMSON_PRESSED);
        searchBtn.setPreferredSize(new Dimension(130, 36));
        searchCard.add(searchBtn);

        timeLabel = new JLabel("Query Execution Time: N/A");
        timeLabel.setFont(Theme.FONT_BOLD);
        timeLabel.setForeground(Theme.COLOR_LOW);
        searchCard.add(timeLabel);

        mainContent.add(searchCard, BorderLayout.NORTH);

        // Bottom Results Table Card
        CardPanel resultsCard = new CardPanel(16, Theme.BG_CARD, Theme.BORDER_COLOR);
        resultsCard.setLayout(new BorderLayout());
        resultsCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel resultsTitle = new JLabel("Search Results");
        resultsTitle.setFont(Theme.FONT_SUBTITLE);
        resultsTitle.setForeground(Theme.TEXT_LIGHT);
        resultsTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        resultsCard.add(resultsTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Age", "Blood Group", "Phone", "City", "Availability", "Eligible"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new ModernTable(tableModel);
        resultsTable.setColumnWidths(40, 130, 50, 90, 110, 100, 90, 80);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Theme.BG_DARK);
        resultsCard.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(resultsCard, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Listener
        searchBtn.addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String blood = (String) bloodGroupCombo.getSelectedItem();
        String city = cityField.getText().trim().toLowerCase();

        if (city.isEmpty() || city.equalsIgnoreCase("e.g. indore")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid city name!", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);

        List<Donor> results = new ArrayList<>();
        long startTime = System.nanoTime();

        if (exactMatchCheck.isSelected()) {
            // Direct query: donorIndex.get(bloodGroup).get(city)
            // Retrieve exact match from manager's private/exposed structures
            results = getExactMatchFromIndex(blood, city);
        } else {
            // Compatibility search using manager's searchCompatibleDonors
            results = manager.searchCompatibleDonors(blood, city);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Display execution time
        timeLabel.setText(String.format("Query Executed in: %,d ns (%.3f ms)", duration, duration / 1_000_000.0));

        // Populate table
        for (Donor d : results) {
            tableModel.addRow(new Object[]{
                d.getId(),
                d.getName(),
                d.getAge(),
                d.getBloodGroup(),
                d.getPhone(),
                d.getCity(),
                d.isAvailable() ? "Yes" : "No",
                d.isEligible() ? "Yes" : "No"
            });
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching donors found in the search index.", "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Replicating the raw nested HashMap retrieval directly to show exact O(1) matching:
     * donorIndex.get(bloodGroup).get(city)
     */
    private List<Donor> getExactMatchFromIndex(String blood, String city) {
        List<Donor> results = new ArrayList<>();
        // In a real application, we retrieve this via the manager
        // We will query manager.getAllDonors() and filter using index logic or fetch via helper in manager.
        // Let's implement this in the manager if needed, but since we have all donors we can also simulate it,
        // or actually write a method inside manager for raw exact retrieval. 
        // Wait, does manager have donorIndex exposed? Let's check manager's code. It's private.
        // Let's verify: does manager have a raw query? No, but we can call searchCompatibleDonors with exact match
        // or we can simulate it by fetching the manager's indexed list for that specific group and city!
        // Wait, to keep it highly optimized, let's write a simple method in BloodBankManager:
        // public List<Donor> getExactIndexMatches(String bloodGroup, String city)
        // Let's add it to the manager in a code edit, or since we are simulating, we can fetch all and filter.
        // Wait! Filtering all is O(N). To prove it is O(1) in the GUI, we should query the index directly!
        // Oh! We can just add a public method in BloodBankManager:
        // public synchronized List<Donor> getExactIndexMatches(String bloodGroup, String city)
        // Let's call that method! Wait, does BloodBankManager have this method? No, we haven't added it yet.
        // Let's edit BloodBankManager to add getExactIndexMatches!
        // Wait, let's first check what's in BloodBankManager.
        // Let's write getExactIndexMatches in SearchPanel by getting compatible list of only 1 element, or we can add it to the manager.
        // Actually, if we pass only one blood group to getCompatibleDonorGroups, it is exact match!
        // Yes, if we query exact match, we can just search compatibility for that blood group only.
        // But to make it even cleaner, let's write a custom lookup using the manager's index, or we can edit BloodBankManager.
        // Wait, let's inspect the `SearchPanel` implementation. If we look at the code:
        // `manager.searchCompatibleDonors(blood, city)` checks `getCompatibleDonorGroups`.
        // If we want exact matches, we can add a method in BloodBankManager or we can filter results.
        // Let's edit `BloodBankManager.java` to add:
        // public synchronized List<Donor> getExactIndexMatches(String bloodGroup, String city) {
        //     List<Donor> results = new ArrayList<>();
        //     HashMap<String, ArrayList<Donor>> cityMap = donorIndex.get(bloodGroup.toUpperCase());
        //     if (cityMap != null) {
        //         ArrayList<Donor> donors = cityMap.get(city.trim().toLowerCase());
        //         if (donors != null) {
        //             results.addAll(donors); // include all (even currently ineligible ones, showing exact index state!)
        //         }
        //     }
        //     return results;
        // }
        // That is extremely clean and exposes the exact map state! Let's do that.
        // I will first finish writing SearchPanel, then edit BloodBankManager.java.
        // Wait, I can call the method in SearchPanel now and add it to BloodBankManager in the next turn. Let's do that!
        return manager.getExactIndexMatches(blood, city);
    }
}
