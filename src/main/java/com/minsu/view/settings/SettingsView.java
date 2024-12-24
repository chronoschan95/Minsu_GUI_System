package com.minsu.view.settings;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SettingsView extends JPanel {
    private JLabel statusLabel;
    private JLabel dbTypeLabel;
    private JLabel usernameLabel;
    private JLabel urlLabel;
    
    public SettingsView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        
        // 创建数据库连接信息面板
        JPanel dbPanel = createDatabasePanel();
        mainPanel.add(dbPanel, BorderLayout.NORTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 初始化状态
        updateConnectionStatus();
    }
    
    private JPanel createDatabasePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 数据库状态
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel statusTitleLabel = new JLabel("数据库状态:");
        statusTitleLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        panel.add(statusTitleLabel, gbc);
        
        statusLabel = new JLabel("检查中...");
        gbc.gridx = 1;
        panel.add(statusLabel, gbc);
        
        // 数据库类型
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel typeTitleLabel = new JLabel("数据库类型:");
        typeTitleLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        panel.add(typeTitleLabel, gbc);
        
        dbTypeLabel = new JLabel("MySQL");
        gbc.gridx = 1;
        panel.add(dbTypeLabel, gbc);
        
        // 用户名
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel usernameTitleLabel = new JLabel("用户名:");
        usernameTitleLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        panel.add(usernameTitleLabel, gbc);
        
        usernameLabel = new JLabel("chronos");
        gbc.gridx = 1;
        panel.add(usernameLabel, gbc);
        
        // 连接URL
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel urlTitleLabel = new JLabel("数据库URL:");
        urlTitleLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        panel.add(urlTitleLabel, gbc);
        
        urlLabel = new JLabel("jdbc:mysql://localhost:3306/minsu");
        gbc.gridx = 1;
        panel.add(urlLabel, gbc);
        
        // 添加测试连接按钮
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        JButton testButton = new JButton("测试连接");
        testButton.addActionListener(e -> updateConnectionStatus());
        panel.add(testButton, gbc);
        
        return panel;
    }
    
    private void updateConnectionStatus() {
        try {
            try (Connection conn = DriverManager.getConnection(
                    urlLabel.getText(),
                    usernameLabel.getText(),
                    "")) {
                statusLabel.setText("已连接");
                statusLabel.setForeground(new Color(46, 204, 113));
            }
        } catch (SQLException e) {
            statusLabel.setText("连接失败");
            statusLabel.setForeground(new Color(231, 76, 60));
            JOptionPane.showMessageDialog(this,
                "数据库连接失败: " + e.getMessage(),
                "连接错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 