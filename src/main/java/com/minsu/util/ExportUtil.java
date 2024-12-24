package com.minsu.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportUtil {
    
    public static void exportTableToCSV(JTable table, String prefix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String fileName = prefix + "_" + timestamp + ".csv";
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(fileName));
            
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                    TableModel model = table.getModel();
                    
                    // 写入表头
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        writer.write(model.getColumnName(i));
                        if (i < model.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("\n");
                    
                    // 写入数据
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            Object value = model.getValueAt(i, j);
                            writer.write(value != null ? value.toString() : "");
                            if (j < model.getColumnCount() - 1) {
                                writer.write(",");
                            }
                        }
                        writer.write("\n");
                    }
                    
                    JOptionPane.showMessageDialog(null, 
                        "数据导出成功！\n保存位置：" + fileChooser.getSelectedFile().getAbsolutePath(),
                        "导出成功",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "导出失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 