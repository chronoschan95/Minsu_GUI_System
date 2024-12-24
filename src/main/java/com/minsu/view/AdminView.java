package com.minsu.view;

import com.minsu.model.entity.User;
import com.minsu.view.admin.UserManagementView;
import com.minsu.view.admin.SystemMonitorView;
import javax.swing.*;
import java.awt.*;

public class AdminView extends JFrame {
    public AdminView(User admin) {
        setTitle("系统管理面板 - " + admin.getUsername());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 创建顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel welcomeLabel = new JLabel("管理员: " + admin.getUsername());
        JButton logoutButton = new JButton("退出登录");
        topPanel.add(welcomeLabel);
        topPanel.add(logoutButton);
        
        // 创建侧边栏
        JPanel sidePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton userManageBtn = new JButton("用户管理");
        JButton systemMonitorBtn = new JButton("系统监控");
        
        sidePanel.add(userManageBtn);
        sidePanel.add(systemMonitorBtn);

        // 创建内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createTitledBorder("内容区域"));

        // 组装界面
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // 添加事件监听
        logoutButton.addActionListener(e -> logout());
        userManageBtn.addActionListener(e -> showUserManagement());
        systemMonitorBtn.addActionListener(e -> showSystemMonitor());
        
        // 默认显示用户管理界面
        showUserManagement();
    }

    private void logout() {
        this.dispose();
        new LoginView().setVisible(true);
    }

    private void showUserManagement() {
        JPanel contentPanel = (JPanel) ((BorderLayout) getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        UserManagementView userManagementView = new UserManagementView();
        contentPanel.add(userManagementView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSystemMonitor() {
        JPanel contentPanel = (JPanel) ((BorderLayout) getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        SystemMonitorView systemMonitorView = new SystemMonitorView();
        contentPanel.add(systemMonitorView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
} 