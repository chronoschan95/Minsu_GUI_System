package com.minsu.view.income;

import com.minsu.dao.IncomeDao;
import com.minsu.model.entity.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ui.RectangleInsets;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class IncomeStatisticsView extends JPanel {
    private final User host;
    private final IncomeDao incomeDao;
    private JComboBox<String> periodSelector;
    private ChartPanel chartPanel;
    private JPanel statsPanel;
    
    // 现代化配色方案
    private static final Color PRIMARY_COLOR = new Color(76, 132, 255);
    private static final Color BACKGROUND_COLOR = new Color(250, 251, 252);
    private static final Color GRID_COLOR = new Color(240, 242, 245);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);

    public IncomeStatisticsView(User host) {
        this.host = host;
        this.incomeDao = new IncomeDao();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initializeComponents();
        updateChart();
    }

    private void initializeComponents() {
        // 顶部控制面板
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        // 主图表面板
        chartPanel = new ChartPanel(null);
        chartPanel.setBackground(BACKGROUND_COLOR);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        add(chartPanel, BorderLayout.CENTER);
        
        // 右侧统计面板
        statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.EAST);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] periods = {"今日", "本月", "本季", "本年"};
        periodSelector = new JComboBox<>(periods);
        periodSelector.setBackground(Color.WHITE);
        periodSelector.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        JButton refreshButton = createStyledButton("刷新");
        
        panel.add(new JLabel("统计周期："));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(periodSelector);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(refreshButton);
        panel.add(Box.createHorizontalGlue());

        periodSelector.addActionListener(e -> updateChart());
        refreshButton.addActionListener(e -> updateChart());
        
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panel.setPreferredSize(new Dimension(200, 0));
        return panel;
    }

    private void updateStats() {
        statsPanel.removeAll();
        String period = (String) periodSelector.getSelectedItem();
        
        // 获取统计数据
        Map<String, Double> data = getIncomeData(period);
        double totalIncome = data.values().stream().mapToDouble(Double::doubleValue).sum();
        double avgIncome = data.isEmpty() ? 0 : totalIncome / data.size();
        double maxIncome = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        
        // 添加统计卡片
        addStatCard(statsPanel, "总收入", String.format("%.2f", totalIncome));
        addStatCard(statsPanel, "平均收入", String.format("%.2f", avgIncome));
        addStatCard(statsPanel, "最高收入", String.format("%.2f", maxIncome));
        addStatCard(statsPanel, "数据点数", String.valueOf(data.size()));
        
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void addStatCard(JPanel panel, String title, String value) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(PRIMARY_COLOR);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        panel.add(card);
    }

    private String getChartTitle(String period) {
        return switch (period) {
            case "今日" -> "今日收入统计";
            case "本月" -> "本月收入统计";
            case "本季" -> "本季度收入统计";
            case "本年" -> "本年度收入统计";
            default -> "收入统计";
        };
    }

    private Map<String, Double> initializeDailyData() {
        Map<String, Double> data = new HashMap<>();
        for (int hour = 0; hour < 24; hour++) {
            data.put(String.format("%02d:00", hour), 0.0);
        }
        return data;
    }

    private Map<String, Double> initializeMonthlyData(LocalDate now) {
        Map<String, Double> data = new HashMap<>();
        int daysInMonth = now.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            data.put(String.valueOf(day), 0.0);
        }
        return data;
    }

    private Map<String, Double> initializeQuarterlyData(LocalDate now) {
        Map<String, Double> data = new HashMap<>();
        int currentMonth = now.getMonthValue();
        int quarterStartMonth = ((currentMonth - 1) / 3) * 3 + 1;
        for (int month = quarterStartMonth; month < quarterStartMonth + 3; month++) {
            data.put(String.valueOf(month), 0.0);
        }
        return data;
    }

    private Map<String, Double> initializeYearlyData() {
        Map<String, Double> data = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            data.put(String.valueOf(month), 0.0);
        }
        return data;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    private void updateChart() {
        String period = (String) periodSelector.getSelectedItem();
        if (period == null) return;

        // 获取收入数据
        Map<String, Double> data = getIncomeData(period);
        
        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, value) -> dataset.addValue(value, "收入", key));

        // 创建图表
        JFreeChart chart = ChartFactory.createLineChart(
            getChartTitle(period),          // 图表标题
            "时间",                         // X轴标签
            "收入金额",                     // Y轴标签
            dataset,                        // 数据集
            PlotOrientation.VERTICAL,       // 图表方向
            false,                         // 是否显示图例
            true,                          // 是否显示工具提示
            false                          // 是否生成URL链接
        );

        // 自定义图表样式
        customizeChart(chart);
        
        // 更新图表面板
        chartPanel.setChart(chart);
        
        // 更新统计数据
        updateStats();
    }

    private void customizeChart(JFreeChart chart) {
        // 设置背景
        chart.setBackgroundPaint(BACKGROUND_COLOR);
        
        // 获取绘图区域
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(GRID_COLOR);
        plot.setDomainGridlinePaint(GRID_COLOR);
        plot.setOutlinePaint(null);
        plot.setInsets(new RectangleInsets(5, 5, 5, 5));

        // 设置X轴
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
        domainAxis.setLabelFont(new Font("微软雅黑", Font.BOLD, 14));

        // 设置Y轴
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("微软雅黑", Font.BOLD, 14));

        // 设置线条和数据点
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);
    }

    private Map<String, Double> getIncomeData(String period) {
        LocalDate now = LocalDate.now();
        Map<String, Double> initialData = switch (period) {
            case "今日" -> initializeDailyData();
            case "本月" -> initializeMonthlyData(now);
            case "本季" -> initializeQuarterlyData(now);
            case "本年" -> initializeYearlyData();
            default -> new HashMap<>();
        };

        try {
            // 从数据库获取实际收入数据
            Map<String, Double> actualData = incomeDao.getIncomeData(host.getId(), period);
            // 合并初始化数据和实际数据
            initialData.putAll(actualData);
            return initialData;
        } catch (Exception e) {
            showError("获取收入数据失败：" + e.getMessage());
            return initialData;
        }
    }
}