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
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

public class IncomeStatisticsView extends JPanel {
    private final User host;
    private final IncomeDao incomeDao;
    private JComboBox<String> periodSelector;
    private ChartPanel chartPanel;
    private JPanel statsPanel;
    
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(76, 132, 255);
    private static final Color SECONDARY_COLOR = new Color(82, 196, 26);
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
        // Top control panel with modern styling
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        // Main chart panel
        chartPanel = new ChartPanel(null);
        chartPanel.setBackground(BACKGROUND_COLOR);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        add(chartPanel, BorderLayout.CENTER);
        
        // Right statistics panel
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

    private void updateChart() {
        try {
            String period = (String) periodSelector.getSelectedItem();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            
            // Get data based on selected period
            Map<String, Double> data = getIncomeData(period);
            
            // Format data points for better display
            if ("今日".equals(period)) {
                // For daily data, show hourly intervals
                for (int hour = 0; hour < 24; hour++) {
                    String timeKey = String.format("%02d:00", hour);
                    double value = data.getOrDefault(timeKey, 0.0);
                    dataset.addValue(value, "收入", timeKey);
                }
            } else {
                // Sort and format other period data
                data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        String formattedKey = formatDataKey(entry.getKey(), period);
                        dataset.addValue(entry.getValue(), "收入", formattedKey);
                    });
            }

            // Create modern styled chart
            JFreeChart chart = createModernChart(dataset, period);
            
            // Update chart panel
            chartPanel.setChart(chart);
            chartPanel.revalidate();
            chartPanel.repaint();
            
            // Update statistics with proper number formatting
            updateStats();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("更新图表时发生错误: " + e.getMessage());
        }
    }
    
    private String formatDataKey(String key, String period) {
        try {
            switch (period) {
                case "本月":
                    return key + "日";
                case "本季":
                    return key + "月";
                case "本年":
                    return key + "月";
                default:
                    return key;
            }
        } catch (Exception e) {
            return key;
        }
    }

    private JFreeChart createModernChart(DefaultCategoryDataset dataset, String period) {
        JFreeChart chart = ChartFactory.createLineChart(
            getChartTitle(period),    // Chart title
            "",                       // X-axis label (empty for clean look)
            "收入 (元)",               // Y-axis label
            dataset,
            PlotOrientation.VERTICAL,
            false,                    // No legend
            true,                     // Show tooltips
            false                     // No URLs
        );

        // Modern styling
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        
        // Background
        chart.setBackgroundPaint(BACKGROUND_COLOR);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(GRID_COLOR);
        plot.setRangeGridlinePaint(GRID_COLOR);
        plot.setOutlineVisible(false);
        plot.setAxisOffset(new RectangleInsets(10, 10, 10, 10));
        
        // Line renderer
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setDefaultShapesVisible(true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));
        renderer.setSeriesShapesFilled(0, true);
        plot.setRenderer(renderer);

        // Axes
        CategoryAxis domainAxis = plot.getDomainAxis();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        
        // Style axes
        domainAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
        domainAxis.setTickLabelPaint(TEXT_COLOR);
        rangeAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
        rangeAxis.setTickLabelPaint(TEXT_COLOR);
        
        return chart;
    }

    private Map<String, Double> getIncomeData(String period) {
        LocalDate now = LocalDate.now();
        try {
            Map<String, Double> rawData = switch (period) {
                case "今日" -> incomeDao.getDailyIncome(host.getId(), now);
                case "本月" -> incomeDao.getMonthlyDetailIncome(host.getId(), now.getYear(), now.getMonthValue());
                case "本季" -> incomeDao.getQuarterlyIncome(host.getId(), now.getYear(), (now.getMonthValue() - 1) / 3 + 1);
                case "本年" -> incomeDao.getYearlyIncome(host.getId(), now.getYear());
                default -> new HashMap<>();
            };

            if (rawData == null || rawData.isEmpty()) {
                return switch (period) {
                    case "今日" -> initializeDailyData();
                    case "本月" -> initializeMonthlyData(now);
                    case "本季" -> initializeQuarterlyData(now);
                    case "本年" -> initializeYearlyData();
                    default -> new HashMap<>();
                };
            }
            
            return rawData;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // 初始化每日24小时数据
    private Map<String, Double> initializeDailyData() {
        Map<String, Double> hourlyData = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyData.put(String.format("%02d:00", i), 0.0);
        }
        return hourlyData;
    }

    // 初始化月度数据
    private Map<String, Double> initializeMonthlyData(LocalDate now) {
        Map<String, Double> dailyData = new HashMap<>();
        for (int i = 1; i <= now.lengthOfMonth(); i++) {
            dailyData.put(String.valueOf(i), 0.0);
        }
        return dailyData;
    }

    // 初始化季度数据
    private Map<String, Double> initializeQuarterlyData(LocalDate now) {
        Map<String, Double> quarterlyData = new HashMap<>();
        int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
        int startMonth = (currentQuarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;
        
        for (int i = startMonth; i <= endMonth; i++) {
            quarterlyData.put(String.valueOf(i), 0.0);
        }
        return quarterlyData;
    }

    // 初始化年度数据
    private Map<String, Double> initializeYearlyData() {
        Map<String, Double> monthlyData = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyData.put(String.valueOf(i), 0.0);
        }
        return monthlyData;
    }

    private String getChartTitle(String period) {
        LocalDate now = LocalDate.now();
        return switch (period) {
            case "今日" -> "今日收入统计";
            case "本月" -> String.format("%d年%d月收入统计", now.getYear(), now.getMonthValue());
            case "本季" -> String.format("%d年第%d季度收入统计", now.getYear(), (now.getMonthValue() - 1) / 3 + 1);
            case "本年" -> String.format("%d年收入统计", now.getYear());
            default -> "收入统计";
        };
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRID_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(250, 400));
        return panel;
    }

    private void updateStats() {
        try {
            statsPanel.removeAll();
            String period = (String) periodSelector.getSelectedItem();
            
            // 获取对应周期的统计数据
            double periodIncome = calculatePeriodIncome(period);
            
            // 初始化 orderStats，避免空指针
            Map<String, Integer> orderStats = new HashMap<>();
            try {
                orderStats = incomeDao.getOrderCountStats(host.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // 添加样式化的统计组件
            addStatComponent(statsPanel, "总收入", String.format("¥%.2f", periodIncome), PRIMARY_COLOR);
            
            int confirmedOrders = orderStats.getOrDefault("CONFIRMED", 0) + 
                                orderStats.getOrDefault("COMPLETED", 0);
            addStatComponent(statsPanel, "已确认订单", 
                formatNumber(confirmedOrders), 
                SECONDARY_COLOR);
            
            addStatComponent(statsPanel, "待确认订单", 
                formatNumber(orderStats.getOrDefault("PENDING", 0)), 
                new Color(255, 152, 0));
                
            addStatComponent(statsPanel, "已取消订单", 
                formatNumber(orderStats.getOrDefault("CANCELLED", 0)), 
                new Color(244, 67, 54));
            
            statsPanel.revalidate();
            statsPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            showError("更新统计信息时发生错误");
        }
    }
    
    private double calculatePeriodIncome(String period) {
        Map<String, Double> data = getIncomeData(period);
        return data.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    
    private String formatNumber(Number value) {
        if (value instanceof Double || value instanceof Float) {
            return String.format("¥%.2f", value.doubleValue());
        }
        return String.format("%,d", value.intValue());
    }

    private void addStatComponent(JPanel panel, String label, String value, Color color) {
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.Y_AXIS));
        statPanel.setBackground(Color.WHITE);
        statPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_COLOR);
        
        statPanel.add(valueLabel);
        statPanel.add(Box.createVerticalStrut(5));
        statPanel.add(titleLabel);
        
        panel.add(statPanel);
        panel.add(Box.createVerticalStrut(15));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}