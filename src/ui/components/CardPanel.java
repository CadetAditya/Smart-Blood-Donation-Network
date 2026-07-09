package ui.components;

import javax.swing.JPanel;
import java.awt.*;

public class CardPanel extends JPanel {
    private int cornerRadius = 16;
    private Color borderCol = Theme.BORDER_COLOR;
    private Color bgCol = Theme.BG_CARD;

    public CardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    public CardPanel(int cornerRadius, Color bgCol, Color borderCol) {
        this();
        this.cornerRadius = cornerRadius;
        this.bgCol = bgCol;
        this.borderCol = borderCol;
    }

    public void setBgCol(Color bgCol) {
        this.bgCol = bgCol;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Fill card background
        g2.setColor(bgCol);
        g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Outer border
        g2.setColor(borderCol);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        g2.dispose();
    }
}
