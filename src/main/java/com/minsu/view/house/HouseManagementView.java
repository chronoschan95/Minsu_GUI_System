package com.minsu.view.house;

import com.minsu.dao.HouseDao;
import com.minsu.model.entity.House;
import com.minsu.model.entity.User;
import com.minsu.util.ExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HouseManagementView extends JPanel {
    private final User host;
    private final HouseDao houseDao;
    private final JTable houseTable;
    private final DefaultTableModel tableModel;

    public HouseManagementView(User host) {
        this.host = host;
        this.houseDao = new HouseDao();
        
        setLayout(new BorderLayout());
        
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
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("添加房源");
        JButton editButton = new JButton("编辑房源");
        JButton deleteButton = new JButton("删除房源");
        JButton exportButton = new JButton("导出数据");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        
        // 组装界面
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 添加事件监听
        addButton.addActionListener(e -> showHouseDialog(null));
        editButton.addActionListener(e -> editHouse());
        deleteButton.addActionListener(e -> deleteHouse());
        exportButton.addActionListener(e -> ExportUtil.exportTableToCSV(houseTable, "houses"));
        
        // 初始加载数据
        refreshHouseList();
    }
    
    private void refreshHouseList() {
        tableModel.setRowCount(0);
        List<House> houses = houseDao.getHousesByHostId(host.getId());
        for (House house : houses) {
            tableModel.addRow(new Object[]{
                house.getId(),
                house.getTitle(),
                house.getDescription(),
                house.getPrice(),
                house.getStatus()
            });
        }
    }
    
    private void showHouseDialog(House house) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            house == null ? "添加房源" : "编辑房源", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField titleField = new JTextField(house != null ? house.getTitle() : "");
        JTextArea descField = new JTextArea(house != null ? house.getDescription() : "");
        JTextField priceField = new JTextField(house != null ? String.valueOf(house.getPrice()) : "");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"AVAILABLE", "OFFLINE"});
        
        if (house != null) {
            statusBox.setSelectedItem(house.getStatus());
        }
        
        panel.add(new JLabel("标题:"));
        panel.add(titleField);
        panel.add(new JLabel("描述:"));
        panel.add(new JScrollPane(descField));
        panel.add(new JLabel("价格/天:"));
        panel.add(priceField);
        panel.add(new JLabel("状态:"));
        panel.add(statusBox);
        
        JButton submitButton = new JButton(house == null ? "添加" : "保存");
        panel.add(submitButton);
        
        submitButton.addActionListener(e -> {
            try {
                double price = Double.parseDouble(priceField.getText());
                House newHouse = house == null ? new House() : house;
                newHouse.setHostId(host.getId());
                newHouse.setTitle(titleField.getText());
                newHouse.setDescription(descField.getText());
                newHouse.setPrice(price);
                newHouse.setStatus((String) statusBox.getSelectedItem());
                
                boolean success = house == null ? 
                    houseDao.addHouse(newHouse) : 
                    houseDao.updateHouse(newHouse);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "操作成功！");
                    dialog.dispose();
                    refreshHouseList();
                } else {
                    JOptionPane.showMessageDialog(dialog, "操作失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "价格必须是数字！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(panel);
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
        if (house != null) {
            showHouseDialog(house);
        }
    }
    
    private void deleteHouse() {
        int selectedRow = houseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一个房源！");
            return;
        }
        
        Long houseId = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除这个房源吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (houseDao.deleteHouse(houseId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                refreshHouseList();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 