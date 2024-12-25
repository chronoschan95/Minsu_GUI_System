package com.minsu.view.host;

import com.minsu.dao.HouseDao;
import com.minsu.model.entity.House;
import com.minsu.model.entity.User;
import com.minsu.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class HouseListPanel extends JPanel {
    private final User host;
    private final HouseDao houseDao;
    private final JTable houseTable;
    private final DefaultTableModel tableModel;

    public HouseListPanel(User host) {
        this.host = host;
        this.houseDao = new HouseDao();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addButton = new JButton("添加房源");
        JButton editButton = new JButton("编辑");
        JButton deleteButton = new JButton("下架");
        JButton exportButton = new JButton("导出");
        JButton refreshButton = new JButton("刷新");
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.add(exportButton);
        toolBar.add(refreshButton);

        // 表格
        String[] columnNames = {"ID", "标题", "描述", "价格", "状态", "发布时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        houseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(houseTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 事件监听
        addButton.addActionListener(e -> showAddHouseDialog());
        editButton.addActionListener(e -> editHouse());
        deleteButton.addActionListener(e -> deleteHouse());
        exportButton.addActionListener(e -> ExportUtil.exportTableToCSV(houseTable, "houses"));
        refreshButton.addActionListener(e -> refreshHouseList());

        // 初始加载数据
        refreshHouseList();
    }

    private void showAddHouseDialog() {
        // 使用 Frame 作为父窗口
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "添加房源", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField priceField = new JTextField();
        
        formPanel.add(new JLabel("标题:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("描述:"));
        formPanel.add(descField);
        formPanel.add(new JLabel("价格:"));
        formPanel.add(priceField);
        
        JButton submitBtn = new JButton("提交");
        submitBtn.addActionListener(e -> {
            try {
                House house = new House();
                house.setHostId(host.getId());
                house.setTitle(titleField.getText().trim());
                house.setDescription(descField.getText().trim());
                house.setPrice(Double.parseDouble(priceField.getText().trim()));
                house.setStatus("AVAILABLE");
                house.setPublishTime(LocalDateTime.now());
                
                if (houseDao.addHouse(house)) {
                    JOptionPane.showMessageDialog(dialog, "添加成功！");
                    dialog.dispose();
                    refreshHouseList();
                } else {
                    JOptionPane.showMessageDialog(dialog, "添加失败！");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的价格！");
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(submitBtn, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editHouse() {
        int selectedRow = houseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个房源！");
            return;
        }

        Long houseId = (Long) tableModel.getValueAt(selectedRow, 0);
        House house = houseDao.getHouseById(houseId);
        if (house == null) {
            JOptionPane.showMessageDialog(this, "房源不存在！");
            return;
        }

        showEditHouseDialog(house);
    }

    private void showEditHouseDialog(House house) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "编辑房源", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField titleField = new JTextField(house.getTitle());
        JTextArea descField = new JTextArea(house.getDescription());
        JTextField priceField = new JTextField(String.valueOf(house.getPrice()));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"AVAILABLE", "OFFLINE"});
        statusBox.setSelectedItem(house.getStatus());
        JButton submitButton = new JButton("保存");

        panel.add(new JLabel("标题:"));
        panel.add(titleField);
        panel.add(new JLabel("描述:"));
        panel.add(new JScrollPane(descField));
        panel.add(new JLabel("价格:"));
        panel.add(priceField);
        panel.add(new JLabel("状态:"));
        panel.add(statusBox);
        panel.add(new JLabel());
        panel.add(submitButton);

        submitButton.addActionListener(e -> {
            try {
                house.setTitle(titleField.getText().trim());
                house.setDescription(descField.getText().trim());
                house.setPrice(Double.parseDouble(priceField.getText().trim()));
                house.setStatus((String) statusBox.getSelectedItem());

                if (houseDao.updateHouse(house)) {
                    JOptionPane.showMessageDialog(dialog, "更新成功！");
                    dialog.dispose();
                    refreshHouseList();
                } else {
                    JOptionPane.showMessageDialog(dialog, "更新失败！");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的价格！");
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteHouse() {
        int selectedRow = houseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个房源！");
            return;
        }

        Long houseId = (Long) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要下架房源 \"" + title + "\" 吗？",
            "确认下架",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (houseDao.deleteHouse(houseId)) {
                JOptionPane.showMessageDialog(this, "下架成功！");
                refreshHouseList();
            } else {
                JOptionPane.showMessageDialog(this, "下架失败！");
            }
        }
    }

    public void refreshHouseList() {
        try {
            List<House> houses = houseDao.getHousesByHostId(host.getId());
            tableModel.setRowCount(0);
            for (House house : houses) {
                Object[] row = {
                    house.getId(),
                    house.getTitle(),
                    house.getDescription(),
                    house.getPrice(),
                    house.getStatus(),
                    house.getPublishTime()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "获取房源列表失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 