package com.minsu.view.order;

import com.minsu.dao.OrderDao;
import com.minsu.dao.ReviewDao;
import com.minsu.model.entity.Order;
import com.minsu.model.entity.Review;
import com.minsu.model.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuestOrderView extends JPanel {
    private final User user;
    private final OrderDao orderDao;
    private final ReviewDao reviewDao;
    private final JTable orderTable;
    private final DefaultTableModel tableModel;

    public GuestOrderView(User user) {
        this.user = user;
        this.orderDao = new OrderDao();
        this.reviewDao = new ReviewDao();
        
        setLayout(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"订单ID", "房源ID", "入住日期", "退房日期", "总价", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("取消订单");
        JButton reviewButton = new JButton("评价");
        buttonPanel.add(cancelButton);
        buttonPanel.add(reviewButton);
        
        // 组装界面
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 添加事件监听
        cancelButton.addActionListener(e -> cancelOrder());
        reviewButton.addActionListener(e -> addReview());
        
        // 初始加载数据
        refreshOrderList();
    }
    
    private void refreshOrderList() {
        tableModel.setRowCount(0);
        for (Order order : orderDao.getOrdersByGuestId(user.getId())) {
            Object[] row = {
                order.getId(),
                order.getHouseId(),
                order.getCheckIn(),
                order.getCheckOut(),
                order.getTotalPrice(),
                order.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void cancelOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个订单！");
            return;
        }
        
        Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        
        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(this, "只能取消待确认的订单！");
            return;
        }
        
        if (orderDao.updateOrderStatus(orderId, "CANCELLED")) {
            JOptionPane.showMessageDialog(this, "订单已取消！");
            refreshOrderList();
        } else {
            JOptionPane.showMessageDialog(this, "取消失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addReview() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个订单！");
            return;
        }
        
        Long orderId = (Long) tableModel.getValueAt(selectedRow, 0);
        Long houseId = (Long) tableModel.getValueAt(selectedRow, 1);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        
        if (!"COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(this, "只能评价已完成的订单！");
            return;
        }
        
        // 创建评价对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加评价", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        JTextArea commentArea = new JTextArea(3, 20);
        JButton submitButton = new JButton("提交评价");
        
        panel.add(new JLabel("评分 (1-5):"));
        panel.add(ratingSpinner);
        panel.add(new JLabel("评价内容:"));
        panel.add(new JScrollPane(commentArea));
        panel.add(new JLabel());
        panel.add(submitButton);
        
        submitButton.addActionListener(e -> {
            Review review = new Review();
            review.setOrderId(orderId);
            review.setHouseId(houseId);
            review.setGuestId(user.getId());
            review.setRating((Integer) ratingSpinner.getValue());
            review.setComment(commentArea.getText());
            
            if (reviewDao.addReview(review)) {
                JOptionPane.showMessageDialog(dialog, "评价成功！");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "评��失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
} 