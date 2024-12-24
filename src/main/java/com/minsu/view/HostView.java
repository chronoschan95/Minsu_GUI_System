package com.minsu.view;

import com.minsu.model.entity.User;
import com.minsu.view.house.HouseManagementView;
import com.minsu.view.order.OrderManagementView;
import com.minsu.view.income.IncomeStatisticsView;
import javax.swing.*;
import java.awt.*;

public class HostView extends JFrame {
    private final User host;

    public HostView(User host) {
        this.host = host;
        
        setTitle("房东管理面板");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 创建顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel welcomeLabel = new JLabel("欢迎, " + host.getUsername());
        JButton logoutButton = new JButton("退出登录");
        topPanel.add(welcomeLabel);
        topPanel.add(logoutButton);
        
        // 创建侧边栏
        JPanel sidePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton myHousesBtn = new JButton("我的房源");
        JButton ordersBtn = new JButton("订单管理");
        JButton incomeBtn = new JButton("收入统计");
        
        sidePanel.add(myHousesBtn);
        sidePanel.add(ordersBtn);
        sidePanel.add(incomeBtn);

        // 创建内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createTitledBorder("内容区域"));

        // 组装界面
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // 添加事件监听
        logoutButton.addActionListener(e -> logout());
        myHousesBtn.addActionListener(e -> showMyHouses());
        ordersBtn.addActionListener(e -> showOrders());
        incomeBtn.addActionListener(e -> showIncome());
    }

    private void logout() {
        this.dispose();
        new LoginView().setVisible(true);
    }

    private void showMyHouses() {
        JPanel contentPanel = (JPanel) ((BorderLayout) getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        // 创建新的房源管理视图并添加到内容面板
        HouseManagementView houseManagementView = new HouseManagementView(host);
        contentPanel.add(houseManagementView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOrders() {
        JPanel contentPanel = (JPanel) ((BorderLayout) getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        OrderManagementView orderManagementView = new OrderManagementView(host);
        contentPanel.add(orderManagementView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showIncome() {
        JPanel contentPanel = (JPanel) ((BorderLayout) getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        IncomeStatisticsView incomeView = new IncomeStatisticsView(host);
        contentPanel.add(incomeView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
} 