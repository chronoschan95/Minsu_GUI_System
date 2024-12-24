package com.minsu.view.house;

import com.minsu.dao.HouseDao;
import com.minsu.dao.OrderDao;
import com.minsu.model.entity.House;
import com.minsu.model.entity.Order;
import com.minsu.model.entity.User;
import com.minsu.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class HouseListView extends JPanel {
    private final User user;
    private final HouseDao houseDao;
    private final OrderDao orderDao;
    private final JTable houseTable;
    private final DefaultTableModel tableModel;
    private final JPanel buttonPanel;

    public HouseListView(User user) {
        this.user = user;
        this.houseDao = new HouseDao();
        this.orderDao = new OrderDao();
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        setLayout(new BorderLayout());
        
        // 创建搜索面板
        createSearchPanel();
        
        // 创建表格
        String[] columnNames = {"ID", "标题", "描述", "价格/天", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        houseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(houseTable);
        
        // 创建按钮
        JButton bookButton = new JButton("预订");
        buttonPanel.add(bookButton);
        
        // 添加导出按钮
        addExportButton();
        
        // 组装界面
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 添加事件监听
        bookButton.addActionListener(e -> bookHouse());
        
        // 初始加载数据
        refreshHouseList();
    }
    
    private void refreshHouseList() {
        tableModel.setRowCount(0);
        for (House house : houseDao.getAllAvailableHouses()) {
            Object[] row = {
                house.getId(),
                house.getTitle(),
                house.getDescription(),
                house.getPrice(),
                house.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // 关键词搜索
        JTextField keywordField = new JTextField(15);
        
        // 价格范围
        JTextField minPriceField = new JTextField(8);
        JTextField maxPriceField = new JTextField(8);
        
        // 搜索按钮
        JButton searchButton = new JButton("搜索");
        JButton resetButton = new JButton("重置");
        
        // 组装搜索面板
        searchPanel.add(new JLabel("关键词："));
        searchPanel.add(keywordField);
        searchPanel.add(new JLabel("价格区间："));
        searchPanel.add(minPriceField);
        searchPanel.add(new JLabel("-"));
        searchPanel.add(maxPriceField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        
        // 添加事件监听
        searchButton.addActionListener(e -> {
            String keyword = keywordField.getText();
            Double minPrice = null;
            Double maxPrice = null;
            
            try {
                if (!minPriceField.getText().isEmpty()) {
                    minPrice = Double.parseDouble(minPriceField.getText());
                }
                if (!maxPriceField.getText().isEmpty()) {
                    maxPrice = Double.parseDouble(maxPriceField.getText());
                }
                
                if (maxPrice != null && minPrice != null && maxPrice < minPrice) {
                    JOptionPane.showMessageDialog(this, 
                        "最高价格不能小于最低价格！", 
                        "输入错误", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                searchHouses(keyword, minPrice, maxPrice);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "价格必须是有效的数字！", 
                    "输入错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        resetButton.addActionListener(e -> {
            keywordField.setText("");
            minPriceField.setText("");
            maxPriceField.setText("");
            refreshHouseList();
        });
        
        add(searchPanel, BorderLayout.NORTH);
    }
    
    private void searchHouses(String keyword, Double minPrice, Double maxPrice) {
        tableModel.setRowCount(0);
        List<House> houses = houseDao.searchHouses(keyword, minPrice, maxPrice);
        
        if (houses.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "没有找到符合条件的房源！", 
                "搜索结果", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        for (House house : houses) {
            Object[] row = {
                house.getId(),
                house.getTitle(),
                house.getDescription(),
                house.getPrice(),
                house.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void bookHouse() {
        int selectedRow = houseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个房源！");
            return;
        }
        
        Long houseId = (Long) tableModel.getValueAt(selectedRow, 0);
        Double pricePerDay = (Double) tableModel.getValueAt(selectedRow, 3);
        
        // 创建预订对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "预订房源", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 获取当前日期
        LocalDate today = LocalDate.now();
        
        // 创建入住日期选择器
        JComboBox<LocalDate> checkInBox = new JComboBox<>();
        // 添加未来30天的日期选项
        for (int i = 0; i < 30; i++) {
            checkInBox.addItem(today.plusDays(i));
        }
        
        // 创建退房日期选择器
        JComboBox<LocalDate> checkOutBox = new JComboBox<>();
        // 初始化退房日期选项（基于入住日期）
        updateCheckOutDates(checkOutBox, (LocalDate) checkInBox.getSelectedItem());
        
        // 当入住日期改变时，更新退房日期选项
        checkInBox.addActionListener(e -> {
            LocalDate selectedCheckIn = (LocalDate) checkInBox.getSelectedItem();
            updateCheckOutDates(checkOutBox, selectedCheckIn);
        });
        
        // 添加价格预览标签
        JLabel priceLabel = new JLabel("总价: ¥0.00");
        
        // 当日期选择改变时更新价格
        ItemListener priceUpdater = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                LocalDate checkIn = (LocalDate) checkInBox.getSelectedItem();
                LocalDate checkOut = (LocalDate) checkOutBox.getSelectedItem();
                if (checkIn != null && checkOut != null) {
                    long days = ChronoUnit.DAYS.between(checkIn, checkOut);
                    double totalPrice = days * pricePerDay;
                    priceLabel.setText(String.format("总价: ¥%.2f", totalPrice));
                }
            }
        };
        
        checkInBox.addItemListener(priceUpdater);
        checkOutBox.addItemListener(priceUpdater);
        
        JButton submitButton = new JButton("确认预订");
        
        panel.add(new JLabel("入住日期:"));
        panel.add(checkInBox);
        panel.add(new JLabel("退房日期:"));
        panel.add(checkOutBox);
        panel.add(new JLabel("价格预览:"));
        panel.add(priceLabel);
        panel.add(new JLabel(""));
        panel.add(submitButton);
        
        submitButton.addActionListener(e -> {
            LocalDate checkIn = (LocalDate) checkInBox.getSelectedItem();
            LocalDate checkOut = (LocalDate) checkOutBox.getSelectedItem();
            
            // 检查日期是否可用
            if (!orderDao.isDateRangeAvailable(houseId, checkIn.toString(), checkOut.toString())) {
                JOptionPane.showMessageDialog(dialog,
                    "选择的日期已被预订，请重新选择！",
                    "日期冲突",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            long days = ChronoUnit.DAYS.between(checkIn, checkOut);
            double totalPrice = days * pricePerDay;
            
            // 二次确认预订信息
            int confirm = JOptionPane.showConfirmDialog(dialog,
                String.format("请确认预订信息：\n\n" +
                    "入住日期：%s\n" +
                    "退房日期：%s\n" +
                    "住宿天数：%d天\n" +
                    "总价：¥%.2f\n\n" +
                    "是否确认预订？",
                    checkIn, checkOut, days, totalPrice),
                "确认预订",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            Order order = new Order();
            order.setHouseId(houseId);
            order.setGuestId(user.getId());
            order.setCheckIn(checkIn.toString());
            order.setCheckOut(checkOut.toString());
            order.setStatus("PENDING");
            order.setTotalPrice(totalPrice);
            
            if (orderDao.addOrder(order)) {
                JOptionPane.showMessageDialog(dialog,
                    String.format("预订成功！\n" +
                        "订单已生成，请在我的订单中查看详情。\n\n" +
                        "入住日期：%s\n" +
                        "退房日期：%s\n" +
                        "总价：¥%.2f",
                        checkIn, checkOut, totalPrice));
                dialog.dispose();
                refreshHouseList();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "预订失败！请稍后重试",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // 辅助方法：更新退房日期选项
    private void updateCheckOutDates(JComboBox<LocalDate> checkOutBox, LocalDate checkInDate) {
        checkOutBox.removeAllItems();
        // 添加从入住日期后一天开始的30天选项
        for (int i = 1; i <= 30; i++) {
            checkOutBox.addItem(checkInDate.plusDays(i));
        }
    }
    
    private void addExportButton() {
        JButton exportButton = new JButton("导出数据");
        exportButton.addActionListener(e -> 
            ExportUtil.exportTableToCSV(houseTable, "houses"));
        buttonPanel.add(exportButton);
    }
} 