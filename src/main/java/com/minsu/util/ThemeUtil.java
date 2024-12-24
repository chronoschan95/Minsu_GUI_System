// Use the entire ThemeUtil class implementation from your improved code
// The implementation includes:
// - Color constants
// - Theme toggle functionality
// - Custom UI components (GradientPanel, ModernButtonUI, RoundBorder)
// - Utility methods for styling components
package com.minsu.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ThemeUtil {
    // Theme colors
    public static final Color PRIMARY_COLOR = new Color(255, 182, 193);    // 浅粉色
    public static final Color SECONDARY_COLOR = new Color(255, 218, 224);  // 更浅的粉色
    public static final Color BACKGROUND_COLOR_START = new Color(255, 248, 250); // 白色渐变开始
    public static final Color BACKGROUND_COLOR_END = new Color(255, 240, 245);   // 白色渐变结束
    public static final Color TEXT_COLOR = new Color(51, 51, 51);          // 深灰色文字
    
    public static final Color DARK_PRIMARY_COLOR = new Color(219, 112, 147);     // 深粉色
    public static final Color DARK_SECONDARY_COLOR = new Color(199, 92, 127);    // 更深的粉色
    public static final Color DARK_BACKGROUND_COLOR_START = new Color(30, 30, 30); // 深色渐变开始
    public static final Color DARK_BACKGROUND_COLOR_END = new Color(45, 45, 45);   // 深色渐变结束
    public static final Color DARK_TEXT_COLOR = new Color(240, 240, 240);        // 浅色文字

    public static final int CORNER_RADIUS = 15; // iOS风格圆角
    private static boolean isDarkMode = false;
    private static final List<Window> registeredWindows = new ArrayList<>();

    public static void registerWindow(Window window) {
        if (!registeredWindows.contains(window)) {
            registeredWindows.add(window);
        }
    }

    public static void unregisterWindow(Window window) {
        registeredWindows.remove(window);
    }

    public static Color getCurrentTextColor() {
        return isDarkMode ? DARK_TEXT_COLOR : TEXT_COLOR;
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new ModernButtonUI());
        button.setBackground(isDarkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new RoundBorder(CORNER_RADIUS));
        return button;
    }

    public static void styleTextField(JTextField field) {
        field.setBorder(new RoundBorder(CORNER_RADIUS));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setForeground(getCurrentTextColor());
        field.setCaretColor(getCurrentTextColor());
        field.setOpaque(false);
        
        // 添加圆角效果
        field.putClientProperty("JComponent.roundRect", true);
        field.putClientProperty("JTextField.placeholderText", "请输入...");
    }

    public static JPanel createThemeTogglePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton themeToggle = createStyledButton(isDarkMode ? "切换明亮模式" : "切换暗黑模式");
        themeToggle.addActionListener(e -> toggleDarkMode());
        panel.add(themeToggle);
        return panel;
    }

    public static void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        try {
            UIManager.setLookAndFeel(isDarkMode ? new FlatDarkLaf() : new FlatLightLaf());
            for (Window window : registeredWindows) {
                SwingUtilities.updateComponentTreeUI(window);
                applyThemeToComponent(window);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyTheme(Window window) {
        applyThemeToComponent(window);
        window.revalidate();
        window.repaint();
    }

    private static void applyThemeToComponent(Component component) {
        if (component instanceof JPanel) {
            component.setBackground(isDarkMode ? DARK_BACKGROUND_COLOR_START : BACKGROUND_COLOR_START);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyThemeToComponent(child);
            }
        }
    }

    public static class ModernButtonUI extends BasicButtonUI {
        @Override
        protected void paintButtonPressed(Graphics g, AbstractButton b) {
            paintButton(g, b, true);
        }

        @Override
        protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(b.getForeground());
            g2d.drawString(text, textRect.x, textRect.y + textRect.height - g2d.getFontMetrics().getDescent());
            g2d.dispose();
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            paintButton(g, b, b.getModel().isPressed());
            super.paint(g, c);
        }

        private void paintButton(Graphics g, AbstractButton b, boolean isPressed) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color baseColor = isDarkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR;
            if (isPressed) {
                baseColor = baseColor.darker();
            }
            
            GradientPaint gradient = new GradientPaint(
                0, 0, baseColor,
                0, b.getHeight(), baseColor.brighter()
            );
            
            g2d.setPaint(gradient);
            g2d.fill(new RoundRectangle2D.Float(0, 0, b.getWidth(), b.getHeight(), CORNER_RADIUS, CORNER_RADIUS));
            g2d.dispose();
        }
    }

    public static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gradient = new GradientPaint(
                0, 0,
                isDarkMode ? DARK_BACKGROUND_COLOR_START : BACKGROUND_COLOR_START,
                0, getHeight(),
                isDarkMode ? DARK_BACKGROUND_COLOR_END : BACKGROUND_COLOR_END
            );
            
            g2d.setPaint(gradient);
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS));
            g2d.dispose();
        }
    }

    public static class RoundBorder extends AbstractBorder {
        private final int radius;

        public RoundBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(isDarkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
            g2d.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2d.dispose();
        }
    }

    public static void applyGlobalStyle() {
        // 设置全局圆角
        UIManager.put("Button.arc", CORNER_RADIUS);
        UIManager.put("Component.arc", CORNER_RADIUS);
        UIManager.put("TextComponent.arc", CORNER_RADIUS);
        UIManager.put("ScrollBar.thumbArc", CORNER_RADIUS);
        UIManager.put("ScrollBar.trackArc", CORNER_RADIUS);
        UIManager.put("TabbedPane.tabArc", CORNER_RADIUS);
        UIManager.put("TabbedPane.selectedTabArc", CORNER_RADIUS);
        UIManager.put("ComboBox.arc", CORNER_RADIUS);
        UIManager.put("Spinner.arc", CORNER_RADIUS);
        UIManager.put("ProgressBar.arc", CORNER_RADIUS);
        UIManager.put("List.selectionArc", CORNER_RADIUS);
        UIManager.put("Tree.selectionArc", CORNER_RADIUS);
        UIManager.put("Table.selectionArc", CORNER_RADIUS);
        
        // 添加全局圆角边框
        UIManager.put("TextField.border", new RoundBorder(CORNER_RADIUS));
        UIManager.put("PasswordField.border", new RoundBorder(CORNER_RADIUS));
        UIManager.put("TextArea.border", new RoundBorder(CORNER_RADIUS));
        UIManager.put("ComboBox.border", new RoundBorder(CORNER_RADIUS));
        UIManager.put("Spinner.border", new RoundBorder(CORNER_RADIUS));
        
        // 设置文本框样式
        UIManager.put("TextField.background", new Color(0, 0, 0, 0));
        UIManager.put("PasswordField.background", new Color(0, 0, 0, 0));
        UIManager.put("TextArea.background", new Color(0, 0, 0, 0));
    }
}