package com.minsu.view.order;

import com.minsu.dao.OrderDao;
import com.minsu.model.entity.Order;
import com.minsu.model.entity.User;
import com.minsu.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HostOrderView extends JPanel {
    private final User host;
    private final OrderDao orderDao;
    private final JTable orderTable;
    private final DefaultTableModel tableModel;

    public HostOrderView(User host) {
        this.host = host;
        this.orderDao = new OrderDao();
        
        setLayout(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"订单ID", "房源ID", "访客ID", "入住日期", "退房日期", "总价", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        
        // 创建按钮面���
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确认订单");
        JButton rejectButton = new JButton("拒绝订单");
        JButton exportButton = new JButton("导出数据");
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(exportButton);
        
        // 组装界面
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 添加事件监听
        confirmButton.addActionListener(e -> handleOrder("CONFIRMED"));
        rejectButton.addActionListener(e -> handleOrder("CANCELLED"));
        exportButton.addActionListener(e -> ExportUtil.exportTableToCSV(orderTable, "orders"));
        
        // 初始加载数据
        refreshOrderList();
    }
    
    private void refreshOrderList() {
        tableModel.setRowCount(0);
        List<Order> orders = orderDao.getOrdersByHostId(host.getId());
        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                order.getId(),
                order.getHouseId(),
                order.getGuestId(),
                order.getCheckIn(),
                order.getCheckOut(),
                order.getTotalPrice(),
                order.getStatus()
            });
        }
    }
    
    private void handleOrder(String status) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个订单！");
            return;
        }
        
        Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);
        
        if (!"PENDING".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "只能处理待处理的订单！");
            return;
        }
        
        if (orderDao.updateOrderStatus(orderId, status)) {
            JOptionPane.showMessageDialog(this, "订单状态更新成功！");
            refreshOrderList();
        } else {
            JOptionPane.showMessageDialog(this, "操作失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 