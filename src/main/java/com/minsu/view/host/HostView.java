package com.minsu.view.host;

import com.minsu.model.entity.User;
import com.minsu.util.ThemeUtil;
import com.minsu.view.LoginView;
import com.minsu.view.income.IncomeStatisticsView;
import com.minsu.view.settings.SettingsView;
import javax.swing.*;
import java.awt.*;

public class HostView extends JFrame {
    private final User user;
    private JPanel contentPanel;

    public HostView(User user) {
        this.user = user;
        initComponents();
        ThemeUtil.registerWindow(this);
    }

    private void initComponents() {
        setTitle("民宿管理系统 - 房东界面 - " + user.getUsername());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 添加新的功能按钮
        JPanel sidePanel = new JPanel(new GridLayout(6, 1, 5, 5));
        JButton houseManageBtn = new JButton("房源管理");
        JButton orderManageBtn = new JButton("订单管理");
        JButton incomeStatBtn = new JButton("收入统计");
        JButton profileBtn = new JButton("个人信息");
        JButton settingsBtn = new JButton("系统设置");
        JButton logoutBtn = new JButton("退出登录");

        // 添加按钮事件
        houseManageBtn.addActionListener(e -> showHouseManagement());
        orderManageBtn.addActionListener(e -> showOrderManagement());
        incomeStatBtn.addActionListener(e -> showIncomeStatistics());
        profileBtn.addActionListener(e -> showProfile());
        settingsBtn.addActionListener(e -> showSettings());
        logoutBtn.addActionListener(e -> logout());

        // 添加到侧边栏
        sidePanel.add(houseManageBtn);
        sidePanel.add(orderManageBtn);
        sidePanel.add(incomeStatBtn);
        sidePanel.add(profileBtn);
        sidePanel.add(settingsBtn);
        sidePanel.add(logoutBtn);

        // 主内容面板
        contentPanel = new JPanel(new BorderLayout());
        
        // 默认显示房源管理
        showHouseManagement();

        // 设置布局
        setLayout(new BorderLayout());
        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void showHouseManagement() {
        contentPanel.removeAll();
        HouseListPanel houseListPanel = new HouseListPanel(user);
        contentPanel.add(houseListPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOrderManagement() {
        contentPanel.removeAll();
        OrderPanel orderPanel = new OrderPanel(user);
        contentPanel.add(orderPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showIncomeStatistics() {
        contentPanel.removeAll();
        IncomeStatisticsView incomeView = new IncomeStatisticsView(user);
        contentPanel.add(incomeView);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSettings() {
        contentPanel.removeAll();
        SettingsView settingsView = new SettingsView();
        contentPanel.add(settingsView);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfile() {
        contentPanel.removeAll();
        ProfilePanel profilePanel = new ProfilePanel(user);
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
