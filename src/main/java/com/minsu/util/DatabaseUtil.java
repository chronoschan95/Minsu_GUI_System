package com.minsu.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/minsu?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "chronos";
    private static final String PASSWORD = "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static void initDatabase() {
        try (Connection conn = getConnection()) {
            // 创建用户表
            String createUserTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(50) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // 创建房源表
            String createHouseTable = """
                CREATE TABLE IF NOT EXISTS houses (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    host_id BIGINT NOT NULL,
                    title VARCHAR(100) NOT NULL,
                    description TEXT,
                    price DOUBLE NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
                    status VARCHAR(20) NOT NULL,
                    total_price DOUBLE NOT NULL,
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

            // 执行建表语句
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createUserTable);
                stmt.execute(createHouseTable);
                stmt.execute(createOrderTable);
                stmt.execute(createReviewTable);
                
                // 检查是否需要插入默认管理员账号
                String checkAdmin = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
                ResultSet rs = stmt.executeQuery(checkAdmin);
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertAdmin = """
                        INSERT INTO users (username, password, role) 
                        VALUES ('admin', 'admin123', 'ADMIN')
                    """;
                    stmt.execute(insertAdmin);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "数据库初始化失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void initialize() {
        // 执行数据库迁移
        DatabaseMigrationUtil.migrate();
    }
}
