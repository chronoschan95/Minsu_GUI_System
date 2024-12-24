package com.minsu.view.guest;

import com.minsu.model.entity.User;
import com.minsu.util.ThemeUtil;
import com.minsu.view.LoginView;
import javax.swing.*;
import java.awt.*;
import com.minsu.view.house.HouseListView;
import com.minsu.view.order.GuestOrderView;
import com.minsu.view.guest.ProfilePanel;

public class GuestView extends JFrame {
    private final User user;
    private JPanel contentPanel;

    public GuestView(User user) {
        this.user = user;
        initComponents();
        ThemeUtil.registerWindow(this);
    }

    private void initComponents() {
        setTitle("民宿预订系统 - 游客界面 - " + user.getUsername());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new ThemeUtil.GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建顶部面板
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 创建左侧菜单
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // 创建内容面板
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        ThemeUtil.applyTheme(this);

        // 默认显示房源列表
        showHouseList();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // 欢迎信息
        JLabel welcomeLabel = new JLabel("欢迎, " + user.getUsername());
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        welcomeLabel.setForeground(ThemeUtil.getCurrentTextColor());
        panel.add(welcomeLabel, BorderLayout.WEST);

        // 右侧工具栏
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolPanel.setOpaque(false);

        JButton themeButton = ThemeUtil.createStyledButton("切换主题");
        JButton logoutButton = ThemeUtil.createStyledButton("退出登录");

        themeButton.addActionListener(e -> ThemeUtil.toggleDarkMode());
        logoutButton.addActionListener(e -> logout());

        toolPanel.add(themeButton);
        toolPanel.add(logoutButton);
        panel.add(toolPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] menuItems = {"浏览房源", "我的订单", "个人信息"};
        for (String item : menuItems) {
            JButton button = ThemeUtil.createStyledButton(item);
            button.setMaximumSize(new Dimension(150, 40));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            switch (item) {
                case "浏览房源" -> button.addActionListener(e -> showHouseList());
                case "我的订单" -> button.addActionListener(e -> showMyOrders());
                case "个人信息" -> button.addActionListener(e -> showProfile());
            }
            
            panel.add(button);
            panel.add(Box.createVerticalStrut(10));
        }

        return panel;
    }

    private void showHouseList() {
        contentPanel.removeAll();
        // TODO: 实现房源列表面板
        JPanel houseListPanel = new HouseListPanel(user);
        contentPanel.add(houseListPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMyOrders() {
        contentPanel.removeAll();
        // TODO: 实现订单列表面板
        JPanel orderPanel = new OrderPanel(user);
        contentPanel.add(orderPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfile() {
        contentPanel.removeAll();
        // TODO: 实现个人信息面板
        JPanel profilePanel = new ProfilePanel(user);
        contentPanel.add(profilePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
            "确定要退出登录吗？",
            "退出确认",
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            ThemeUtil.unregisterWindow(this);
            dispose();
            new LoginView().setVisible(true);
        }
    }
}
