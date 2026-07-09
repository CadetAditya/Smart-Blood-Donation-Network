package ui.components;

import javax.swing.JButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private boolean hovered = false;
    private boolean pressed = false;
    private Color backgroundColor = Theme.CRIMSON;
    private Color hoverColor = Theme.CRIMSON_HOVER;
    private Color pressedColor = Theme.CRIMSON_PRESSED;
    private int cornerRadius = 12;

    public ModernButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(Theme.FONT_BOLD);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    pressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public ModernButton(String text, Color bg, Color hover, Color pressed) {
        this(text);
        this.backgroundColor = bg;
        this.hoverColor = hover;
        this.pressedColor = pressed;
    }

    public void setBackgroundColor(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color color;
        if (!isEnabled()) {
            color = Theme.BORDER_COLOR;
        } else if (pressed) {
            color = pressedColor;
        } else if (hovered) {
            color = hoverColor;
        } else {
            color = backgroundColor;
        }

        // Draw shadow (subtle offset)
        if (isEnabled() && !pressed) {
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRoundRect(2, 3, w - 4, h - 3, cornerRadius, cornerRadius);
        }

        // Draw button face
        g2.setColor(color);
        int offset = pressed ? 1 : 0; // Click displacement effect
        g2.fillRoundRect(0, offset, w - 1, h - 1 - offset, cornerRadius, cornerRadius);

        // Draw button text
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int stringWidth = fm.stringWidth(getText());
        int stringHeight = fm.getAscent();
        g2.setColor(isEnabled() ? getForeground() : Theme.TEXT_MUTED);
        g2.drawString(getText(), (w - stringWidth) / 2, (h + stringHeight) / 2 - 2 + offset);

        g2.dispose();
    }
}
