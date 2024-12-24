package com.minsu.view;

import com.minsu.model.entity.User;
import com.minsu.view.house.HouseListView;
import com.minsu.view.order.GuestOrderView;
import javax.swing.*;
import java.awt.*;

public class GuestView extends JFrame {
    private final User guest;

    public GuestView(User guest) {
        this.guest = guest;
        
        setTitle("访客界面");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 创建顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel welcomeLabel = new JLabel("欢迎, " + guest.getUsername());
        JButton logoutButton = new JButton("退出登录");
        topPanel.add(welcomeLabel);
        topPanel.add(logoutButton);
        
        // 创建侧边栏
        JPanel sidePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton browseHousesBtn = new JButton("浏览房源");
        JButton myOrdersBtn = new JButton("我的订单");
        
        sidePanel.add(browseHousesBtn);
        sidePanel.add(myOrdersBtn);

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
        browseHousesBtn.addActionListener(e -> showHouseList());
        myOrdersBtn.addActionListener(e -> showMyOrders());
        
        // 默认显示房源列表
        showHouseList();
    }

    private void logout() {
        this.dispose();
        new LoginView().setVisible(true);
    }

    private void showHouseList() {
        JPanel contentPanel = (JPanel) ((BorderLayout) getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        HouseListView houseListView = new HouseListView(guest);
        contentPanel.add(houseListView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMyOrders() {
        JPanel contentPanel = (JPanel) ((BorderLayout) getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        GuestOrderView orderView = new GuestOrderView(guest);
        contentPanel.add(orderView, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
} 