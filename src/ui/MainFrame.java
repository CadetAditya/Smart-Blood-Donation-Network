package ui;

import service.BloodBankManager;
import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private final BloodBankManager manager;
    private CardLayout cardLayout;
    private JPanel mainContentCardPanel;
    
    // Panels
    private LoginPanel loginPanel;
    private PublicRegisterPanel publicRegisterPanel;
    private JPanel appLayoutContainer;
    
    private DashboardPanel dashboardPanel;
    private DonorPanel donorPanel;
    private RequestPanel requestPanel;
    private QueuePanel queuePanel;
    private SearchPanel searchPanel;
    private HistoryPanel historyPanel;
    private UndoPanel undoPanel;
    
    // Sidebar nav buttons
    private ModernButton[] navButtons;
    private final String[] cardNames = {"DashboardPanel", "DonorPanel", "RequestPanel", "QueuePanel", "SearchPanel", "HistoryPanel", "UndoPanel"};

    public MainFrame() {
        this.manager = new BloodBankManager();
        setupFrame();
        initializePanels();
        showLogin();
    }

    private void setupFrame() {
        setTitle("Smart Blood Donation Network");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);
        
        cardLayout = new CardLayout();
        setLayout(cardLayout);
    }

    private void initializePanels() {
        // 1. Login Panel
        loginPanel = new LoginPanel(this);
        add(loginPanel, "LoginPanel");
        
        // 1.5. Public Register Panel
        publicRegisterPanel = new PublicRegisterPanel(this, manager);
        add(publicRegisterPanel, "PublicRegisterPanel");
        
        // 2. App Container (Split into Sidebar and Main content CardPanel)
        appLayoutContainer = new JPanel(new BorderLayout());
        appLayoutContainer.setBackground(Theme.BG_DARK);
        
        // Sidebar (Left Navigation)
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setBackground(Theme.BG_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER_COLOR));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // Sidebar Branding logo
        JLabel logoText = new JLabel("SMART BLOOD");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoText.setForeground(Theme.CRIMSON);
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoSubText = new JLabel("Donation Network");
        logoSubText.setFont(Theme.FONT_SMALL);
        logoSubText.setForeground(Theme.TEXT_MUTED);
        logoSubText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(logoText);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(logoSubText);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Navigation Items
        String[] menuLabels = {
            "Dashboard",
            "Donor Management",
            "Recipient Request",
            "Emergency Queue",
            "Search Donor Index",
            "Donation Ledger",
            "Undo Center"
        };
        
        navButtons = new ModernButton[menuLabels.length];
        for (int i = 0; i < menuLabels.length; i++) {
            final String cardName = cardNames[i];
            final int index = i;
            navButtons[i] = new ModernButton(menuLabels[i], Theme.BG_SIDEBAR, Theme.BG_INPUT, Theme.BG_DARK);
            navButtons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            navButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            navButtons[i].addActionListener(e -> {
                showPanel(cardName);
                highlightNavButton(index);
            });
            sidebar.add(navButtons[i]);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        sidebar.add(Box.createVerticalGlue());

        // Logout Button
        ModernButton logoutBtn = new ModernButton("Log Out", Theme.CRIMSON, Theme.CRIMSON_HOVER, Theme.CRIMSON_PRESSED);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> logout());
        sidebar.add(logoutBtn);

        appLayoutContainer.add(sidebar, BorderLayout.WEST);

        // Center card panel holding different views
        mainContentCardPanel = new JPanel(new CardLayout());
        mainContentCardPanel.setOpaque(false);

        // Individual panels
        dashboardPanel = new DashboardPanel(this, manager);
        donorPanel = new DonorPanel(this, manager);
        requestPanel = new RequestPanel(this, manager);
        queuePanel = new QueuePanel(this, manager);
        searchPanel = new SearchPanel(this, manager);
        historyPanel = new HistoryPanel(this, manager);
        undoPanel = new UndoPanel(this, manager);

        mainContentCardPanel.add(dashboardPanel, "DashboardPanel");
        mainContentCardPanel.add(donorPanel, "DonorPanel");
        mainContentCardPanel.add(requestPanel, "RequestPanel");
        mainContentCardPanel.add(queuePanel, "QueuePanel");
        mainContentCardPanel.add(searchPanel, "SearchPanel");
        mainContentCardPanel.add(historyPanel, "HistoryPanel");
        mainContentCardPanel.add(undoPanel, "UndoPanel");

        appLayoutContainer.add(mainContentCardPanel, BorderLayout.CENTER);
        add(appLayoutContainer, "AppContainerPanel");
    }

    public void loginSuccess() {
        cardLayout.show(getContentPane(), "AppContainerPanel");
        showPanel("DashboardPanel");
        highlightNavButton(0);
        refreshAllPanels();
    }

    public void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            showLogin();
        }
    }

    public void showLogin() {
        cardLayout.show(getContentPane(), "LoginPanel");
    }

    public void showPublicRegistration() {
        cardLayout.show(getContentPane(), "PublicRegisterPanel");
    }

    public void showPanel(String panelName) {
        CardLayout cl = (CardLayout) mainContentCardPanel.getLayout();
        cl.show(mainContentCardPanel, panelName);
        
        // Match nav button highlight if called programmatically
        for (int i = 0; i < cardNames.length; i++) {
            if (cardNames[i].equals(panelName)) {
                highlightNavButton(i);
                break;
            }
        }
        
        refreshPanelData(panelName);
    }

    private void highlightNavButton(int index) {
        for (int i = 0; i < navButtons.length; i++) {
            if (i == index) {
                navButtons[i].setBackgroundColor(Theme.CRIMSON);
                navButtons[i].setForeground(Color.WHITE);
            } else {
                navButtons[i].setBackgroundColor(Theme.BG_SIDEBAR);
                navButtons[i].setForeground(Theme.TEXT_MUTED);
            }
        }
    }

    private void refreshPanelData(String panelName) {
        switch (panelName) {
            case "DashboardPanel":
                dashboardPanel.refresh();
                break;
            case "DonorPanel":
                donorPanel.refreshTable();
                break;
            case "RequestPanel":
                requestPanel.refreshPreviewTable();
                break;
            case "QueuePanel":
                queuePanel.refreshQueueTable();
                break;
            case "HistoryPanel":
                historyPanel.refreshTable();
                break;
            case "UndoPanel":
                undoPanel.refreshTable();
                break;
        }
    }

    public void refreshAllPanels() {
        dashboardPanel.refresh();
        donorPanel.refreshTable();
        requestPanel.refreshPreviewTable();
        queuePanel.refreshQueueTable();
        historyPanel.refreshTable();
        undoPanel.refreshTable();
    }
}
