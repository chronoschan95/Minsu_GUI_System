package com.minsu.view.guest;

import com.minsu.dao.OrderDao;
import com.minsu.model.entity.Order;
import com.minsu.model.entity.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class OrderPanel extends JPanel {
    private final User user;
    private final OrderDao orderDao;
    private final JTable orderTable;
    private final DefaultTableModel tableModel;

    public OrderPanel(User user) {
        this.user = user;
        this.orderDao = new OrderDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 工具栏
        add(createToolBar(), BorderLayout.NORTH);

        // 订单表格
        String[] columnNames = {"订单ID", "房源ID", "入住日期", "退房日期", "状态", "总价", "创建时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // 加载订单数据
        refreshOrderList();
    }

    private JPanel createToolBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "全部订单", "待确认", "已确认", "已取消", "已完成"
        });
        JComboBox<String> timeFilter = new JComboBox<>(new String[]{
            "全部时间", "最近一周", "最近一月", "最近三月"
        });
        JButton refreshButton = new JButton("刷新");
        JButton cancelButton = new JButton("取消订单");

        panel.add(new JLabel("状态:"));
        panel.add(statusFilter);
        panel.add(new JLabel("时间:"));
        panel.add(timeFilter);
        panel.add(refreshButton);
        panel.add(cancelButton);

        statusFilter.addActionListener(e -> filterOrders(
            statusFilter.getSelectedItem().toString(),
            timeFilter.getSelectedItem().toString()
        ));
        
        timeFilter.addActionListener(e -> filterOrders(
            statusFilter.getSelectedItem().toString(),
            timeFilter.getSelectedItem().toString()
        ));

        refreshButton.addActionListener(e -> refreshOrderList());
        cancelButton.addActionListener(e -> cancelOrder());

        return panel;
    }

    private void filterOrders(String status, String timeRange) {
        String statusCode = switch (status) {
            case "待确认" -> "PENDING";
            case "已确认" -> "CONFIRMED";
            case "已取消" -> "CANCELLED";
            case "已完成" -> "COMPLETED";
            default -> "ALL";
        };

        String days = switch (timeRange) {
            case "最近一周" -> "7";
            case "最近一月" -> "30";
            case "最近三月" -> "90";
            default -> "";
        };

        List<Order> orders = orderDao.searchOrders(user.getId(), "GUEST", statusCode, days);
        updateTableData(orders);
    }

    private void cancelOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个订单！");
            return;
        }

        Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 4);

        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(this, "只能取消待确认的订单！");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要取消该订单吗？",
            "确认取消",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (orderDao.updateOrderStatus(orderId, "CANCELLED")) {
                JOptionPane.showMessageDialog(this, "订单已取消！");
                refreshOrderList();
            } else {
                JOptionPane.showMessageDialog(this, "取消失败！");
            }
        }
    }

    private void refreshOrderList() {
        List<Order> orders = orderDao.getOrdersByGuestId(user.getId());
        updateTableData(orders);
    }

    private void updateTableData(List<Order> orders) {
        tableModel.setRowCount(0);
        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                order.getId(),
                order.getHouseId(),
                order.getCheckIn(),
                order.getCheckOut(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt()
            });
        }
    }
} 