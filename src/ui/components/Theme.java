package ui.components;

import java.awt.Color;
import java.awt.Font;

public class Theme {
    // Cohesive Palette (Catppuccin Mocha + Crimson accents)
    public static final Color BG_DARK = new Color(0x11, 0x11, 0x1B);      // Deep Background
    public static final Color BG_CARD = new Color(0x1E, 0x1E, 0x2E);      // Panel / Container Background
    public static final Color BG_SIDEBAR = new Color(0x18, 0x18, 0x25);   // Left sidebar
    
    // Crimson Accents (Blood colors)
    public static final Color CRIMSON = new Color(0xD2, 0x0F, 0x39);      // Blood Red Primary
    public static final Color CRIMSON_HOVER = new Color(0xE6, 0x4A, 0x19); // Brighter Orange-Red Hover
    public static final Color CRIMSON_PRESSED = new Color(0x9E, 0x0A, 0x22);
    
    // Text colors
    public static final Color TEXT_LIGHT = new Color(0xCD, 0xD6, 0xF4);   // Soft Light Text
    public static final Color TEXT_MUTED = new Color(0xA6, 0xAD, 0xC8);   // Muted Secondary Text
    public static final Color TEXT_DARK = new Color(0x1E, 0x1E, 0x2E);    // Dark text for bright overlays
    
    // Borders & UI elements
    public static final Color BORDER_COLOR = new Color(0x31, 0x32, 0x44); // Slate Border
    public static final Color BORDER_FOCUS = new Color(0xF3, 0x8B, 0xA8); // Pink-Crimson Focus highlight
    public static final Color BG_INPUT = new Color(0x31, 0x32, 0x44);     // Input Field background
    
    // Priority Status colors
    public static final Color COLOR_CRITICAL = new Color(0xF3, 0x8B, 0xA8); // Pastel Red
    public static final Color COLOR_HIGH = new Color(0xFA, 0xB3, 0x87);     // Pastel Orange
    public static final Color COLOR_MEDIUM = new Color(0xF9, 0xE2, 0xAF);   // Pastel Yellow
    public static final Color COLOR_LOW = new Color(0xA6, 0xE3, 0xA1);      // Pastel Green

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
}
