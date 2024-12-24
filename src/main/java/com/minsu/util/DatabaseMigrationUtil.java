package com.minsu.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMigrationUtil {
    private static final List<String> MIGRATIONS = new ArrayList<>();
    
    static {
        // 修改迁移脚本，先检查表是否存在
        MIGRATIONS.add("""
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(50) NOT NULL UNIQUE,
                password VARCHAR(50) NOT NULL,
                role VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
        
        // 添加初始用户数据
        MIGRATIONS.add("""
            INSERT IGNORE INTO users (username, password, role) VALUES 
            ('host1', 'host123', 'HOST'),
            ('host2', 'host123', 'HOST'),
            ('guest1', 'guest123', 'GUEST'),
            ('admin', 'admin123', 'ADMIN')
        """);
    }
    
    public static void migrate() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 检查是否存在版本控制表
            createVersionTableIfNotExists(conn);
            
            // 获取当前数据库版本
            int currentVersion = getCurrentVersion(conn);
            
            // 执行待执行的迁移脚本
            for (int i = currentVersion; i < MIGRATIONS.size(); i++) {
                try (Statement stmt = conn.createStatement()) {
                    System.out.println("Executing migration #" + (i + 1) + ": " + MIGRATIONS.get(i));
                    stmt.execute(MIGRATIONS.get(i));
                    updateVersion(conn, i + 1);
                    System.out.println("Successfully executed migration #" + (i + 1));
                }
            }
            
            // 验证用户数据
            verifyUserData(conn);
            
        } catch (Exception e) {
            System.err.println("Database migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verifyUserData(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            System.out.println("\nVerifying user data:");
            while (rs.next()) {
                System.out.println(String.format("User found: id=%d, username=%s, role=%s",
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("role")));
            }
        } catch (SQLException e) {
            System.err.println("Error verifying user data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createVersionTableIfNotExists(Connection conn) throws Exception {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(null, null, "db_version", null);
        
        if (!rs.next()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS db_version (
                        version INT NOT NULL,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (version)
                    )
                """);
                
                // 插入初始版本
                stmt.execute("INSERT INTO db_version (version) VALUES (0)");
            }
        }
        rs.close();
    }
    
    private static int getCurrentVersion(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT MAX(version) FROM db_version");
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    private static void updateVersion(Connection conn, int version) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO db_version (version) VALUES (" + version + ")");
        }
    }
} 