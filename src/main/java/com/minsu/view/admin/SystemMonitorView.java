package com.minsu.view.admin;

import com.minsu.dao.HouseDao;
import com.minsu.dao.OrderDao;
import com.minsu.dao.UserDao;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SystemMonitorView extends JPanel {
    private final UserDao userDao;
    private final HouseDao houseDao;
    private final OrderDao orderDao;

    public SystemMonitorView() {
        this.userDao = new UserDao();
        this.houseDao = new HouseDao();
        this.orderDao = new OrderDao();
        
        setLayout(new GridLayout(2, 2, 10, 10));
        
        // 添加统计面板
        add(createUserStatisticsPanel());
        add(createHouseStatisticsPanel());
        add(createOrderStatisticsPanel());
        add(createSystemInfoPanel());
    }
    
    private JPanel createUserStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("用户统计"));
        
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("房东", userDao.countUsersByRole("HOST"));
        dataset.setValue("访客", userDao.countUsersByRole("GUEST"));
        dataset.setValue("管理员", userDao.countUsersByRole("ADMIN"));
        
        JFreeChart chart = ChartFactory.createPieChart(
            "用户角色分布",
            dataset,
            true,
            true,
            false
        );
        
        panel.add(new ChartPanel(chart));
        return panel;
    }
    
    private JPanel createHouseStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("房源统计"));
        
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("可用", houseDao.countHousesByStatus("AVAILABLE"));
        dataset.setValue("已预订", houseDao.countHousesByStatus("BOOKED"));
        dataset.setValue("下线", houseDao.countHousesByStatus("OFFLINE"));
        
        JFreeChart chart = ChartFactory.createPieChart(
            "房源状态分布",
            dataset,
            true,
            true,
            false
        );
        
        panel.add(new ChartPanel(chart));
        return panel;
    }
    
    private JPanel createOrderStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("订单统计"));
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> orderStats = orderDao.getOrderStatistics();
        
        orderStats.forEach((status, count) ->
            dataset.addValue(count, "订单数", status));
        
        JFreeChart chart = ChartFactory.createBarChart(
            "订单状态统计",
            "状态",
            "数量",
            dataset
        );
        
        panel.add(new ChartPanel(chart));
        return panel;
    }
    
    private JPanel createSystemInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("系统信息"));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        
        // 获取系统信息
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        StringBuilder info = new StringBuilder();
        info.append("系统版本: 1.0.0\n");
        info.append("JVM总内存: ").append(totalMemory / 1024 / 1024).append("MB\n");
        info.append("已用内存: ").append(usedMemory / 1024 / 1024).append("MB\n");
        info.append("空闲内存: ").append(freeMemory / 1024 / 1024).append("MB\n");
        info.append("操作系统: ").append(System.getProperty("os.name")).append("\n");
        info.append("系统架构: ").append(System.getProperty("os.arch")).append("\n");
        info.append("Java版本: ").append(System.getProperty("java.version")).append("\n");
        
        infoArea.setText(info.toString());
        
        panel.add(new JScrollPane(infoArea));
        return panel;
    }
} 