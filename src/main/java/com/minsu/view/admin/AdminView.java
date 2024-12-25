package com.minsu.view.admin;

import com.minsu.model.entity.User;
import com.minsu.util.ThemeUtil;
import com.minsu.view.LoginView;
import javax.swing.*;
import java.awt.*;

public class AdminView extends JFrame {
    private final User admin;
    private JPanel contentPanel;

    public AdminView(User admin) {
        this.admin = admin;
        initComponents();
        ThemeUtil.registerWindow(this);
    }

    private void initComponents() {
        setTitle("民宿管理系统 - 管理员界面 - " + admin.getUsername());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 侧边栏按钮
        JPanel sidePanel = new JPanel(new GridLayout(5, 1, 5, 5));
        JButton userManageBtn = new JButton("用户管理");
        JButton systemMonitorBtn = new JButton("系统监控");
        JButton settingsBtn = new JButton("系统设置");
        JButton logoutBtn = new JButton("退出登录");

        // 添加事件监听
        userManageBtn.addActionListener(e -> showUserManagement());
        systemMonitorBtn.addActionListener(e -> showSystemMonitor());
        settingsBtn.addActionListener(e -> showSettings());
        logoutBtn.addActionListener(e -> logout());

        // 添加到侧边栏
        sidePanel.add(userManageBtn);
        sidePanel.add(systemMonitorBtn);
        sidePanel.add(settingsBtn);
        sidePanel.add(logoutBtn);

        // 主内容面板
        contentPanel = new JPanel(new BorderLayout());
        
        // 默认显示用户管理
        showUserManagement();

        // 设置布局
        setLayout(new BorderLayout());
        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void showUserManagement() {
        contentPanel.removeAll();
        UserManagementView userManagementView = new UserManagementView();
        contentPanel.add(userManagementView);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSystemMonitor() {
        contentPanel.removeAll();
        SystemMonitorView systemMonitorView = new SystemMonitorView();
        contentPanel.add(systemMonitorView);
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