package com.minsu.view.settings;

import com.minsu.util.DatabaseUtil;
import javax.swing.*;
import java.awt.*;

public class SettingsView extends JPanel {
    
    public SettingsView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建数据库连接信息面板
        JPanel dbPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        dbPanel.setBorder(BorderFactory.createTitledBorder("数据库连接信息"));

        dbPanel.add(new JLabel("数据库状态:"));
        JLabel statusLabel = new JLabel("已连接");
        statusLabel.setForeground(Color.GREEN);
        dbPanel.add(statusLabel);

        dbPanel.add(new JLabel("数据库类型:"));
        dbPanel.add(new JLabel("MySQL"));

        dbPanel.add(new JLabel("用户名:"));
        dbPanel.add(new JLabel("chronos"));

        // 添加到主面板
        add(dbPanel, BorderLayout.NORTH);
    }
} 