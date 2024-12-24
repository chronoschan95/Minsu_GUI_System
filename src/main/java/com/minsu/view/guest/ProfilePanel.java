package com.minsu.view.guest;

import com.minsu.model.entity.User;
import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private final User user;

    public ProfilePanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建个人信息卡片
        add(createProfileCard(), BorderLayout.NORTH);
        
        // 创建统计信息面板
        add(createStatsPanel(), BorderLayout.CENTER);
    }

    private JPanel createProfileCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("个人信息"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 用户名
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(user.getUsername()), gbc);

        // 用户角色
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("用户角色:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("游客"), gbc);

        // 注册时间
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("注册时间:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(user.getCreatedAt()), gbc);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("统计信息"));

        // 这里可以添加一些统计信息，比如订单总数、评价数等
        panel.add(createStatCard("订单总数", "0"));
        panel.add(createStatCard("已完成订单", "0"));
        panel.add(createStatCard("待处理订单", "0"));
        panel.add(createStatCard("评价数", "0"));

        return panel;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font(valueLabel.getFont().getName(), Font.BOLD, 24));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
} 