package com.minsu.view.host;

import com.minsu.dao.OrderDao;
import com.minsu.model.entity.Order;
import com.minsu.model.entity.User;
import com.minsu.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {
    private final User host;
    private final OrderDao orderDao;
    private final JTable orderTable;
    private final DefaultTableModel tableModel;

    public OrderPanel(User host) {
        this.host = host;
        this.orderDao = new OrderDao();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton confirmButton = new JButton("确认订单");
        JButton rejectButton = new JButton("拒绝订单");
        JButton exportButton = new JButton("导出");
        JButton refreshButton = new JButton("刷新");
        
        toolBar.add(confirmButton);
        toolBar.add(rejectButton);
        toolBar.add(exportButton);
        toolBar.add(refreshButton);

        // 表格
        String[] columnNames = {"订单ID", "房源ID", "客户ID", "入住日期", "退房日期", "状态", "总价", "创建时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 事件监听
        confirmButton.addActionListener(e -> confirmOrder());
        rejectButton.addActionListener(e -> rejectOrder());
        exportButton.addActionListener(e -> ExportUtil.exportTableToCSV(orderTable, "orders"));
        refreshButton.addActionListener(e -> refreshOrderList());

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
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt()
            });
        }
    }

    private void confirmOrder() {
        updateOrderStatus("CONFIRMED");
    }

    private void rejectOrder() {
        updateOrderStatus("CANCELLED");
    }

    private void updateOrderStatus(String status) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个订单！");
            return;
        }

        Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

        if (!"PENDING".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "只能处理待确认的订单！");
            return;
        }

        if (orderDao.updateOrderStatus(orderId, status)) {
            JOptionPane.showMessageDialog(this, "订单状态更新成功！");
            refreshOrderList();
        } else {
            JOptionPane.showMessageDialog(this, "订单状态更新失败！");
        }
    }
} 