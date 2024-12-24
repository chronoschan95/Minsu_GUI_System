package com.minsu.view.admin;

import com.minsu.dao.UserDao;
import com.minsu.model.entity.User;
import com.minsu.model.enums.UserRole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementView extends JPanel {
    private final UserDao userDao;
    private final JTable userTable;
    private final DefaultTableModel tableModel;

    public UserManagementView() {
        this.userDao = new UserDao();
        
        setLayout(new BorderLayout());
        
        // 创建工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("添加用户");
        JButton deleteButton = new JButton("删除用户");
        JButton resetPwdButton = new JButton("重置密码");
        JButton refreshButton = new JButton("刷新");
        
        toolBar.add(addButton);
        toolBar.add(deleteButton);
        toolBar.add(resetPwdButton);
        toolBar.add(refreshButton);
        
        // 创建表格
        String[] columnNames = {"ID", "用户名", "角色", "创建时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        // 添加到面板
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // 添加事件监听
        addButton.addActionListener(e -> showAddUserDialog());
        deleteButton.addActionListener(e -> deleteUser());
        resetPwdButton.addActionListener(e -> resetPassword());
        refreshButton.addActionListener(e -> refreshUserList());
        
        // 初始加载数据
        refreshUserList();
    }
    
    private void refreshUserList() {
        tableModel.setRowCount(0);
        List<User> users = userDao.getAllUsers();
        
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getRole().toString(),
                user.getCreatedAt()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加用户", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<UserRole> roleComboBox = new JComboBox<>(UserRole.values());
        JButton submitButton = new JButton("添加");
        
        panel.add(new JLabel("用户名:"));
        panel.add(usernameField);
        panel.add(new JLabel("密码:"));
        panel.add(passwordField);
        panel.add(new JLabel("角色:"));
        panel.add(roleComboBox);
        panel.add(new JLabel());
        panel.add(submitButton);
        
        submitButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            UserRole role = (UserRole) roleComboBox.getSelectedItem();
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名和密码不能为空！");
                return;
            }
            
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            
            if (userDao.addUser(user)) {
                JOptionPane.showMessageDialog(dialog, "添加成功！");
                dialog.dispose();
                refreshUserList();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户！");
            return;
        }
        
        Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除用户 " + username + " 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDao.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                refreshUserList();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户！");
            return;
        }
        
        Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        String newPassword = JOptionPane.showInputDialog(this,
            "请输入 " + username + " 的新密码：",
            "重置密码",
            JOptionPane.QUESTION_MESSAGE);
            
        if (newPassword != null && !newPassword.isEmpty()) {
            if (userDao.resetPassword(userId, newPassword)) {
                JOptionPane.showMessageDialog(this, "密码重置成功！");
            } else {
                JOptionPane.showMessageDialog(this, "密码重置失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 