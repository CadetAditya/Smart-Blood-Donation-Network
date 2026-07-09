package ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

public class ModernTable extends JTable {
    
    public ModernTable(TableModel model) {
        super(model);
        setupStyle();
    }
    
    private void setupStyle() {
        setRowHeight(36);
        setFont(Theme.FONT_BODY);
        setForeground(Theme.TEXT_LIGHT);
        setBackground(Theme.BG_CARD);
        setSelectionBackground(new Color(0xD2, 0x0F, 0x39, 45)); // Transparent crimson highlight
        setSelectionForeground(Theme.TEXT_LIGHT);
        setGridColor(Theme.BORDER_COLOR);
        setIntercellSpacing(new Dimension(0, 0));
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setFillsViewportHeight(true);
        
        // Custom Table Header
        JTableHeader header = getTableHeader();
        header.setFont(Theme.FONT_BOLD);
        header.setBackground(Theme.BG_SIDEBAR);
        header.setForeground(Theme.TEXT_LIGHT);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.CRIMSON));
        
        // Flat, modern header renderer (removes default operating system gridlines/bevel borders)
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(Theme.BG_SIDEBAR);
                c.setForeground(Theme.TEXT_LIGHT);
                c.setFont(Theme.FONT_BOLD);
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.CRIMSON),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                return c;
            }
        });
        
        // Custom Cell Renderer for alternating rows and cell padding
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Add left/right padding
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(JLabel.CENTER);
                
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setForeground(Theme.TEXT_LIGHT);
                    if (row % 2 == 0) {
                        setBackground(Theme.BG_CARD);
                    } else {
                        setBackground(Theme.BG_DARK);
                    }
                }
                
                return c;
            }
        });
    }

    /**
     * Easily set preferred column widths.
     */
    public void setColumnWidths(int... widths) {
        if (widths == null || widths.length == 0) return;
        for (int i = 0; i < Math.min(widths.length, getColumnModel().getColumnCount()); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }    
}
