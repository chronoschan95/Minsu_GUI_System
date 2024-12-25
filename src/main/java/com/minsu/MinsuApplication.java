package com.minsu;

import com.formdev.flatlaf.FlatLightLaf;
import com.minsu.util.ThemeUtil;
import com.minsu.view.LoginView;

import javax.swing.*;

public class MinsuApplication {
    public static void main(String[] args) {
        try {
            // 设置全局UI风格
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // 应用全局样式
            ThemeUtil.applyGlobalStyle();
            
            // 启动登录界面
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                ThemeUtil.applyTheme(loginView);
                loginView.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "系统启动失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 