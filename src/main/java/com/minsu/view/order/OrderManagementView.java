package com.minsu.view.order;

import com.minsu.dao.OrderDao;
import com.minsu.model.entity.Order;
import com.minsu.model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderManagementView extends JPanel {
    private final User user;
    private final OrderDao orderDao;
    private final JTable orderTable;
    private final DefaultTableModel tableModel;

    public OrderManagementView(User user) {
        this.user = user;
        this.orderDao = new OrderDao();
        
        setLayout(new BorderLayout());
        
        // 创建顶部工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("刷新");
        toolBar.add(refreshButton);
        
        // 创建表格
        String[] columnNames = {"订单ID", "房源ID", "入住日期", "退房日期", "状态", "总价", "创建时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        
        // 添加到面板
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // 添加右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem confirmItem = new JMenuItem("确认订单");
        JMenuItem cancelItem = new JMenuItem("取消订单");
        JMenuItem completeItem = new JMenuItem("完成订单");
        
        popupMenu.add(confirmItem);
        popupMenu.add(cancelItem);
        popupMenu.add(completeItem);
        
        orderTable.setComponentPopupMenu(popupMenu);
        
        // 添加事件监听
        refreshButton.addActionListener(e -> refreshOrderList());
        confirmItem.addActionListener(e -> updateOrderStatus("CONFIRMED"));
        cancelItem.addActionListener(e -> updateOrderStatus("CANCELLED"));
        completeItem.addActionListener(e -> updateOrderStatus("COMPLETED"));
        
        // 初始加载数据
        refreshOrderList();
    }
    
    private void refreshOrderList() {
        tableModel.setRowCount(0);
        List<Order> orders = orderDao.getOrdersByHostId(user.getId());
        
        for (Order order : orders) {
            Object[] row = {
                order.getId(),
                order.getHouseId(),
                order.getCheckIn(),
                order.getCheckOut(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt()
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateOrderStatus(String status) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个订单！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
        if (orderDao.updateOrderStatus(orderId, status)) {
            JOptionPane.showMessageDialog(this, "状态更新成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            refreshOrderList();
        } else {
            JOptionPane.showMessageDialog(this, "状态更新失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 