package ui.components;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernTextField extends JTextField {
    private final String placeholder;
    private boolean isFocused = false;
    private final int cornerRadius = 8;

    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setForeground(Theme.TEXT_LIGHT);
        setCaretColor(Theme.TEXT_LIGHT);
        setFont(Theme.FONT_BODY);
        setBackground(Theme.BG_INPUT);
        setBorder(new EmptyBorder(8, 12, 8, 12));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Border outline
        g2.setStroke(new BasicStroke(1.5f));
        if (isFocused) {
            g2.setColor(Theme.BORDER_FOCUS);
        } else {
            g2.setColor(Theme.BORDER_COLOR);
        }
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        g2.dispose();

        super.paintComponent(g);

        // Placeholder overlay
        if (getText().isEmpty() && placeholder != null) {
            Graphics2D gp = (Graphics2D) g.create();
            gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gp.setFont(getFont());
            gp.setColor(Theme.TEXT_MUTED);
            Insets insets = getInsets();
            FontMetrics fm = gp.getFontMetrics();
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            gp.drawString(placeholder, insets.left + 1, y);
            gp.dispose();
        }
    }
}
