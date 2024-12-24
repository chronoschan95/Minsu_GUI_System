package com.minsu.view.host;

import com.minsu.dao.UserDao;
import com.minsu.model.entity.User;
import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private final User host;
    private final UserDao userDao;

    public ProfilePanel(User host) {
        this.host = host;
        this.userDao = new UserDao();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建个人信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加个人信息字段
        addField(infoPanel, gbc, "用户名:", host.getUsername());
        addField(infoPanel, gbc, "角色:", host.getRole().toString());
        addField(infoPanel, gbc, "注册时间:", host.getCreatedAt());

        // 添加修改密码按钮
        JButton changePasswordBtn = new JButton("修改密码");
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        infoPanel.add(changePasswordBtn, gbc);

        // 将信息面板添加到主面板
        add(infoPanel, BorderLayout.NORTH);

        // 添加修改密码事件监听
        changePasswordBtn.addActionListener(e -> showChangePasswordDialog());
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(value), gbc);
        gbc.gridy++;
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改密码", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JButton submitButton = new JButton("确认修改");

        panel.add(new JLabel("原密码:"));
        panel.add(oldPasswordField);
        panel.add(new JLabel("新密码:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("确认新密码:"));
        panel.add(confirmPasswordField);
        panel.add(new JLabel());
        panel.add(submitButton);

        submitButton.addActionListener(e -> {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "所有字段都必须填写！");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的新密码不一致！");
                return;
            }

            if (userDao.verifyPassword(host.getId(), oldPassword)) {
                if (userDao.updatePassword(host.getId(), newPassword)) {
                    JOptionPane.showMessageDialog(dialog, "密码修改成功！");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "密码修改失败！");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "原密码错误！");
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }
} 