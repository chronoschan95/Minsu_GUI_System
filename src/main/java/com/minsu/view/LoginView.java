package com.minsu.view;

import com.minsu.controller.LoginController;
import com.minsu.model.entity.User;
import com.minsu.util.ThemeUtil;
import com.minsu.view.admin.AdminView;
import com.minsu.view.guest.GuestView;
import com.minsu.view.host.HostView;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private final LoginController loginController;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginView() {
        loginController = new LoginController();
        initComponents();
        ThemeUtil.registerWindow(this);
    }

    private void initComponents() {
        setTitle("民宿管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);

        JPanel mainPanel = new ThemeUtil.GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel themePanel = ThemeUtil.createThemeTogglePanel();
        themePanel.setOpaque(false);
        mainPanel.add(themePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setForeground(ThemeUtil.getCurrentTextColor());
        usernameField = new JTextField(20);
        ThemeUtil.styleTextField(usernameField);

        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setForeground(ThemeUtil.getCurrentTextColor());
        passwordField = new JPasswordField(20);
        ThemeUtil.styleTextField(passwordField);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
        ThemeUtil.applyTheme(this);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton loginButton = ThemeUtil.createStyledButton("登录");
        JButton registerButton = ThemeUtil.createStyledButton("注册");

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> {
            this.setVisible(false);
            new RegisterView(this).setVisible(true);
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        return buttonPanel;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "用户名和密码不能为空！",
                "输入错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = loginController.login(username, password);

        if (user != null) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                switch (user.getRole()) {
                    case HOST -> new HostView(user).setVisible(true);
                    case GUEST -> new GuestView(user).setVisible(true);
                    case ADMIN -> new AdminView(user).setVisible(true);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this,
                "用户名或密码错误！",
                "登录失败",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}