package ui;

import ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final MainFrame mainFrame;
    
    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_DARK);
        
        CardPanel card = new CardPanel(20, Theme.BG_CARD, Theme.BORDER_COLOR);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(400, 520));
        
        // Logo / Title
        JLabel titleLabel = new JLabel("SMART BLOOD NETWORK");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.CRIMSON);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Administrative Portal");
        subtitleLabel.setFont(Theme.FONT_SMALL);
        subtitleLabel.setForeground(Theme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(subtitleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Input Fields
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.FONT_BOLD);
        userLabel.setForeground(Theme.TEXT_LIGHT);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        ModernTextField userField = new ModernTextField("Enter username...");
        userField.setText("admin"); // Pre-fill for ease of review
        userField.setMaximumSize(new Dimension(320, 40));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.FONT_BOLD);
        passLabel.setForeground(Theme.TEXT_LIGHT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Styled JPasswordField
        JPasswordField passField = new JPasswordField() {
            private final int cornerRadius = 8;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
                g2.setColor(Theme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        passField.setText("password"); // Pre-fill for ease of review
        passField.setOpaque(false);
        passField.setCaretColor(Theme.TEXT_LIGHT);
        passField.setForeground(Theme.TEXT_LIGHT);
        passField.setBorder(new EmptyBorder(8, 12, 8, 12));
        passField.setFont(Theme.FONT_BODY);
        passField.setMaximumSize(new Dimension(320, 40));
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(userLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(userField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(passField);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Login Button
        ModernButton loginBtn = new ModernButton("Access Dashboard");
        loginBtn.setMaximumSize(new Dimension(320, 45));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Public Self Registration Button
        ModernButton registerBtn = new ModernButton("Register as Donor", Theme.BG_SIDEBAR, Theme.BG_INPUT, Theme.BG_DARK);
        registerBtn.setMaximumSize(new Dimension(320, 40));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.COLOR_CRITICAL);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            if ("admin".equals(user) && "password".equals(pass)) {
                errorLabel.setText(" ");
                mainFrame.loginSuccess();
            } else {
                errorLabel.setText("Invalid credentials! Try admin / password.");
            }
        });
        
        registerBtn.addActionListener(e -> mainFrame.showPublicRegistration());
        
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(registerBtn);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(errorLabel);
        
        add(card);
    }
}
