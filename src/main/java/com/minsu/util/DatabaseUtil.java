package com.minsu.util;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/minsu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "chronos";
    private static final String PASSWORD = "root";

    static {
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 初始化数据库
            initDatabase();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL驱动加载失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    private static void initDatabase() {
        createDatabase();
        
        try (Connection conn = getConnection()) {
            // 创建房源表
            String createHouseTable = """
                CREATE TABLE IF NOT EXISTS houses (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    host_id BIGINT NOT NULL,
                    title VARCHAR(100) NOT NULL,
                    description TEXT,
                    price DECIMAL(10,2) NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                    publish_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (host_id) REFERENCES users(id)
                )
            """;
            
            // 创建订单表
            String createOrderTable = """
                CREATE TABLE IF NOT EXISTS orders (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    house_id BIGINT NOT NULL,
                    guest_id BIGINT NOT NULL,
                    check_in DATE NOT NULL,
                    check_out DATE NOT NULL,
                    total_price DECIMAL(10,2) NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (house_id) REFERENCES houses(id),
                    FOREIGN KEY (guest_id) REFERENCES users(id)
                )
            """;
            
            // 创建评价表
            String createReviewTable = """
                CREATE TABLE IF NOT EXISTS reviews (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    order_id BIGINT NOT NULL,
                    house_id BIGINT NOT NULL,
                    guest_id BIGINT NOT NULL,
                    rating INT NOT NULL,
                    comment TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (house_id) REFERENCES houses(id),
                    FOREIGN KEY (guest_id) REFERENCES users(id)
                )
            """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createHouseTable);
                stmt.execute(createOrderTable);
                stmt.execute(createReviewTable);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库初始化失败: " + e.getMessage(), e);
        }
    }

    private static void createDatabase() {
        String url = "jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE DATABASE IF NOT EXISTS minsu");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "创建数据库失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
