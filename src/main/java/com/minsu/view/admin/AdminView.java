package com.minsu.view.admin;

import com.minsu.model.entity.User;
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
        
        // 创建左侧菜单
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        JButton userManagementButton = new JButton("用户管理");
        JButton systemMonitorButton = new JButton("系统监控");
        menuPanel.add(userManagementButton);
        menuPanel.add(systemMonitorButton);
        
        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // 添加到主面板
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // 添加到窗口
        add(mainPanel);
        
        // 添加事件监听
        logoutButton.addActionListener(e -> logout());
        userManagementButton.addActionListener(e -> showUserManagement());
        systemMonitorButton.addActionListener(e -> showSystemMonitor());
        
        // 默认显示用户管理
        showUserManagement();
    }
    
    private void logout() {
        dispose();
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