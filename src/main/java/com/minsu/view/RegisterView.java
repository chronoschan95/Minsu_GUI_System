package com.minsu.view;

import com.minsu.controller.UserController;
import com.minsu.model.enums.UserRole;
import com.minsu.util.ThemeUtil;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
    private final UserController userController;
    private final LoginView loginView;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<UserRole> roleComboBox;

    public RegisterView(LoginView loginView) {
        this.loginView = loginView;
        this.userController = new UserController();
        initComponents();
        ThemeUtil.registerWindow(this);
    }

    private void initComponents() {
        setTitle("民宿管理系统 - 注册");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setResizable(false); // 禁止调整窗口大小

        ThemeUtil.GradientPanel mainPanel = new ThemeUtil.GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 主题切换面板
        mainPanel.add(ThemeUtil.createThemeTogglePanel(), BorderLayout.NORTH);

        // 注册表单
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        ThemeUtil.applyTheme(this); // 应用主题
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加标题
        JLabel titleLabel = new JLabel("用户注册", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, gbc);

        // 重置网格宽度和间距
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 用户名
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("用户名:"), gbc);
        usernameField = new JTextField(20);
        ThemeUtil.styleTextField(usernameField);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("密码:"), gbc);
        passwordField = new JPasswordField(20);
        ThemeUtil.styleTextField(passwordField);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("确认密码:"), gbc);
        confirmPasswordField = new JPasswordField(20);
        ThemeUtil.styleTextField(confirmPasswordField);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // 用户角色
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("注册身份:"), gbc);
        roleComboBox = new JComboBox<>(new UserRole[]{UserRole.GUEST, UserRole.HOST});
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);

        JButton registerButton = ThemeUtil.createStyledButton("确认注册");
        JButton backButton = ThemeUtil.createStyledButton("返回登录");

        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> handleBack());

        // 添加键盘监听器
        getRootPane().setDefaultButton(registerButton);

        panel.add(registerButton);
        panel.add(backButton);

        return panel;
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        UserRole role = (UserRole) roleComboBox.getSelectedItem();

        // 验证输入
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都必须填写！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.length() < 3 || username.length() > 20) {
            JOptionPane.showMessageDialog(this, "用户名长度必须在3-20个字符之间！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "密码长度不能少于6个字符！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 注册用户
        if (userController.register(username, password, role)) {
            JOptionPane.showMessageDialog(this, "注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            handleBack();
        } else {
            JOptionPane.showMessageDialog(this, "注册失败，用户名可能已存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBack() {
        this.dispose();
        loginView.setVisible(true);
    }
} 