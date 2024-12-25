package com.minsu.view.admin;

import com.minsu.util.DatabaseUtil;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;

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
        
        // 创建系统设置面板
        JPanel systemPanel = createSystemPanel();
        mainPanel.add(systemPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 初始化状态
        updateConnectionStatus();
    }
    
    private JPanel createDatabasePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "数据库连接信息",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("微软雅黑", Font.BOLD, 14)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 数据库状态
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("数据库状态:"), gbc);
        statusLabel = new JLabel("检查中...");
        gbc.gridx = 1;
        panel.add(statusLabel, gbc);
        
        // 数据库类型
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("数据库类型:"), gbc);
        dbTypeLabel = new JLabel("MySQL");
        gbc.gridx = 1;
        panel.add(dbTypeLabel, gbc);
        
        // 用户名
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("用户名:"), gbc);
        usernameLabel = new JLabel("root");
        gbc.gridx = 1;
        panel.add(usernameLabel, gbc);
        
        // 连接URL
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("数据库URL:"), gbc);
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
    
    private JPanel createSystemPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "系统设置",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("微软雅黑", Font.BOLD, 14)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 主题设置
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("系统主题:"), gbc);
        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"Light", "Dark"});
        gbc.gridx = 1;
        panel.add(themeCombo, gbc);
        
        // 语言设��
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("系统语言:"), gbc);
        JComboBox<String> langCombo = new JComboBox<>(new String[]{"简体中文", "English"});
        gbc.gridx = 1;
        panel.add(langCombo, gbc);
        
        // 保存按钮
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        JButton saveButton = new JButton("保存设置");
        saveButton.addActionListener(e -> saveSettings());
        panel.add(saveButton, gbc);
        
        return panel;
    }
    
    private void updateConnectionStatus() {
        try {
            Connection conn = DatabaseUtil.getConnection();
            if (conn != null && !conn.isClosed()) {
                statusLabel.setText("已连接");
                statusLabel.setForeground(new Color(46, 204, 113));
                conn.close();
            }
        } catch (Exception e) {
            statusLabel.setText("连接失败");
            statusLabel.setForeground(new Color(231, 76, 60));
            JOptionPane.showMessageDialog(this,
                "数据库连接失败: " + e.getMessage(),
                "连接错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveSettings() {
        // TODO: 实现设置保存逻辑
        JOptionPane.showMessageDialog(this, "设置已保存！");
    }
} 