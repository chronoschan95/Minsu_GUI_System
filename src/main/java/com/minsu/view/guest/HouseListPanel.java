package com.minsu.view.guest;

import com.minsu.dao.HouseDao;
import com.minsu.dao.OrderDao;
import com.minsu.model.entity.House;
import com.minsu.model.entity.User;
import com.minsu.model.entity.Order;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HouseListPanel extends JPanel {
    private final User user;
    private final HouseDao houseDao;
    private final OrderDao orderDao;
    private final JTable houseTable;
    private final DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField minPriceField;
    private JTextField maxPriceField;

    public HouseListPanel(User user) {
        this.user = user;
        this.houseDao = new HouseDao();
        this.orderDao = new OrderDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 搜索面板
        add(createSearchPanel(), BorderLayout.NORTH);

        // 房源表格
        String[] columnNames = {"ID", "标题", "描述", "价格", "状态", "发布时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        houseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(houseTable);
        add(scrollPane, BorderLayout.CENTER);

        // 添加预订按钮面板
        add(createButtonPanel(), BorderLayout.SOUTH);

        // 加载房源数据
        refreshHouseList();
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchField = new JTextField(20);
        minPriceField = new JTextField(8);
        maxPriceField = new JTextField(8);
        JButton searchButton = new JButton("搜索");
        JButton resetButton = new JButton("重置");

        panel.add(new JLabel("关键词:"));
        panel.add(searchField);
        panel.add(new JLabel("价格区间:"));
        panel.add(minPriceField);
        panel.add(new JLabel("-"));
        panel.add(maxPriceField);
        panel.add(searchButton);
        panel.add(resetButton);

        searchButton.addActionListener(e -> searchHouses());
        resetButton.addActionListener(e -> resetSearch());

        return panel;
    }

    private void searchHouses() {
        String keyword = searchField.getText().trim();
        Double minPrice = null;
        Double maxPrice = null;

        try {
            if (!minPriceField.getText().trim().isEmpty()) {
                minPrice = Double.parseDouble(minPriceField.getText().trim());
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的价格范围！");
            return;
        }

        List<House> houses = houseDao.searchHouses(keyword, minPrice, maxPrice);
        updateTableData(houses);
    }

    private void resetSearch() {
        searchField.setText("");
        minPriceField.setText("");
        maxPriceField.setText("");
        refreshHouseList();
    }

    private void refreshHouseList() {
        List<House> houses = houseDao.getAllAvailableHouses();
        updateTableData(houses);
    }

    private void updateTableData(List<House> houses) {
        tableModel.setRowCount(0);
        for (House house : houses) {
            tableModel.addRow(new Object[]{
                house.getId(),
                house.getTitle(),
                house.getDescription(),
                house.getPrice(),
                house.getStatus(),
                house.getCreatedAt()
            });
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("预订房源");
        bookButton.addActionListener(e -> bookHouse());
        panel.add(bookButton);
        return panel;
    }

    private void bookHouse() {
        int selectedRow = houseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个房源！");
            return;
        }

        Long houseId = (Long) tableModel.getValueAt(selectedRow, 0);
        Double price = (Double) tableModel.getValueAt(selectedRow, 3);
        String status = (String) tableModel.getValueAt(selectedRow, 4);

        if (!"AVAILABLE".equals(status)) {
            JOptionPane.showMessageDialog(this, "该房源当前不可预订！");
            return;
        }

        // 创建预订对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "预订房源", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加日期选择器
        JTextField checkInField = new JTextField(10);
        JTextField checkOutField = new JTextField(10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("入住日期:"), gbc);
        gbc.gridx = 1;
        dialog.add(checkInField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("退房日期:"), gbc);
        gbc.gridx = 1;
        dialog.add(checkOutField, gbc);

        // 添加确认按钮
        JButton confirmButton = new JButton("确认预订");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        dialog.add(confirmButton, gbc);

        confirmButton.addActionListener(e -> {
            try {
                LocalDate checkIn = LocalDate.parse(checkInField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate checkOut = LocalDate.parse(checkOutField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);

                if (checkIn.isAfter(checkOut)) {
                    JOptionPane.showMessageDialog(dialog, "入住日期不能晚于退房日期！");
                    return;
                }

                if (checkIn.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(dialog, "入住日期不能早于今天！");
                    return;
                }

                // 使用 addOrder 方法替代 createOrder
                Order order = new Order();
                order.setHouseId(houseId);
                order.setGuestId(user.getId());
                order.setCheckIn(checkIn.toString());
                order.setCheckOut(checkOut.toString());
                order.setTotalPrice(price);
                order.setStatus("PENDING");

                if (orderDao.addOrder(order)) {  // 使用 addOrder 方法
                    JOptionPane.showMessageDialog(dialog, "预订成功！");
                    dialog.dispose();
                    refreshHouseList();
                } else {
                    JOptionPane.showMessageDialog(dialog, "预订失败，请重试！");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "请输入正确的日期格式 (YYYY-MM-DD)！");
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
} 