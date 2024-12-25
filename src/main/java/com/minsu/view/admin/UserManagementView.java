package com.minsu.view.admin;

import com.minsu.dao.UserDao;
import com.minsu.model.entity.User;
import com.minsu.model.enums.UserRole;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserManagementView extends JPanel {
    private final UserDao userDao;
    private final JTable userTable;
    private final DefaultTableModel tableModel;
    private JCheckBox showPasswordCheckBox;

    public UserManagementView() {
        this.userDao = new UserDao();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("添加用户");
        JButton editButton = new JButton("编辑用户");
        JButton deleteButton = new JButton("删除用户");
        JButton refreshButton = new JButton("刷新");
        showPasswordCheckBox = new JCheckBox("显示密码");

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.add(refreshButton);
        toolBar.add(showPasswordCheckBox);

        // 表格
        String[] columnNames = {"ID", "用户名", "密码", "角色", "创建时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 添加事件监听
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> refreshUserList());
        showPasswordCheckBox.addActionListener(e -> refreshUserList());

        // 初始加载数据
        refreshUserList();
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加用户", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());

        formPanel.add(new JLabel("用户名:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("密码:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("角色:"));
        formPanel.add(roleCombo);

        JButton submitBtn = new JButton("提交");
        submitBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            UserRole role = (UserRole) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名和密码不能为空！");
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole(role);

            if (userDao.addUser(newUser)) {
                JOptionPane.showMessageDialog(dialog, "添加成功！");
                dialog.dispose();
                refreshUserList();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败！");
            }
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(submitBtn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的用户！");
            return;
        }

        Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
        User user = userDao.getUserById(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "用户不存在！");
            return;
        }

        showEditUserDialog(user);
    }

    private void showEditUserDialog(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑用户", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField(user.getUsername());
        JPasswordField passwordField = new JPasswordField(user.getPassword());
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setSelectedItem(user.getRole());

        formPanel.add(new JLabel("用户名:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("密码:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("角色:"));
        formPanel.add(roleCombo);

        JButton submitBtn = new JButton("保存");
        submitBtn.addActionListener(e -> {
            user.setUsername(usernameField.getText().trim());
            user.setPassword(new String(passwordField.getPassword()));
            user.setRole((UserRole) roleCombo.getSelectedItem());

            if (userDao.updateUser(user)) {
                JOptionPane.showMessageDialog(dialog, "更新成功！");
                dialog.dispose();
                refreshUserList();
            } else {
                JOptionPane.showMessageDialog(dialog, "更新失败！");
            }
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(submitBtn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的用户！");
            return;
        }

        Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除用户 \"" + username + "\" 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDao.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                refreshUserList();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！");
            }
        }
    }

    public void refreshUserList() {
        try {
            List<User> users = userDao.getAllUsers();
            tableModel.setRowCount(0);
            for (User user : users) {
                Object[] row = {
                    user.getId(),
                    user.getUsername(),
                    showPasswordCheckBox.isSelected() ? user.getPassword() : "********",
                    user.getRole().getDisplayName(),
                    user.getCreatedAt()
                };
                tableModel.addRow(row);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                "获取用户列表失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 