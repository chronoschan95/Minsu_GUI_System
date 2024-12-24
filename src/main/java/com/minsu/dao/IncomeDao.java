package com.minsu.dao;

import com.minsu.util.DatabaseUtil;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.TreeMap;
import java.time.format.DateTimeFormatter;

public class IncomeDao {
    
    public double getTotalIncome(Long hostId) {
        String sql = """
            SELECT SUM(total_price) as total
            FROM orders o
            JOIN houses h ON o.house_id = h.id
            WHERE h.host_id = ?
            AND o.status IN ('CONFIRMED', 'COMPLETED')
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public Map<String, Double> getMonthlyIncome(Long hostId, int year) {
        Map<String, Double> monthlyIncome = new HashMap<>();
        String sql = "SELECT MONTH(o.created_at) as month, SUM(o.total_price) as total " +
                    "FROM orders o " +
                    "JOIN houses h ON o.house_id = h.id " +
                    "WHERE h.host_id = ? AND YEAR(o.created_at) = ? AND o.status = 'COMPLETED' " +
                    "GROUP BY MONTH(o.created_at)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String month = String.format("%d月", rs.getInt("month"));
                monthlyIncome.put(month, rs.getDouble("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthlyIncome;
    }
    
    public Map<Long, Double> getIncomeByHouse(Long hostId) {
        Map<Long, Double> houseIncome = new HashMap<>();
        String sql = "SELECT h.id, h.title, SUM(o.total_price) as total " +
                    "FROM orders o " +
                    "JOIN houses h ON o.house_id = h.id " +
                    "WHERE h.host_id = ? AND o.status = 'COMPLETED' " +
                    "GROUP BY h.id, h.title";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                houseIncome.put(rs.getLong("id"), rs.getDouble("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return houseIncome;
    }
    
    public Map<String, Double> getDailyIncome(Long userId) {
        Map<String, Double> dailyIncome = new HashMap<>();
        String sql = "SELECT HOUR(created_at) as hour, SUM(amount) as total " +
                     "FROM incomes " +
                     "WHERE user_id = ? " +
                     "AND DATE(created_at) = CURRENT_DATE " +
                     "GROUP BY HOUR(created_at)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String hour = String.format("%02d:00", rs.getInt("hour"));
                double total = rs.getDouble("total");
                dailyIncome.put(hour, total);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dailyIncome;
    }
    
    public Map<String, Double> getMonthlyDetailIncome(Long hostId, int year, int month) {
        Map<String, Double> dailyIncome = new TreeMap<>();
        String sql = """
            SELECT DAY(created_at) as day, SUM(total_price) as total
            FROM orders o
            JOIN houses h ON o.house_id = h.id
            WHERE h.host_id = ? 
            AND YEAR(created_at) = ?
            AND MONTH(created_at) = ?
            AND o.status IN ('CONFIRMED', 'COMPLETED')
            GROUP BY DAY(created_at)
            ORDER BY day
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            
            YearMonth yearMonth = YearMonth.of(year, month);
            int daysInMonth = yearMonth.lengthOfMonth();
            
            for (int i = 1; i <= daysInMonth; i++) {
                dailyIncome.put(String.format("%02d", i), 0.0);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String day = String.format("%02d", rs.getInt("day"));
                dailyIncome.put(day, rs.getDouble("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dailyIncome;
    }
    
    public Map<String, Double> getQuarterlyIncome(Long hostId, int year, int quarter) {
        Map<String, Double> monthlyIncome = new HashMap<>();
        // ... 实现查询逻辑 ...
        return monthlyIncome;
    }
    
    public Map<String, Double> getYearlyIncome(Long hostId, int year) {
        Map<String, Double> monthlyIncome = new TreeMap<>();
        String sql = """
            SELECT 
                MONTH(created_at) as month,
                SUM(total_price) as total
            FROM orders o
            JOIN houses h ON o.house_id = h.id
            WHERE h.host_id = ? 
            AND YEAR(created_at) = ?
            AND o.status IN ('CONFIRMED', 'COMPLETED')
            GROUP BY MONTH(created_at)
            ORDER BY month
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            stmt.setInt(2, year);
            
            // 初始化12个月的数据
            for (int i = 1; i <= 12; i++) {
                monthlyIncome.put(String.format("%02d月", i), 0.0);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int month = rs.getInt("month");
                double total = rs.getDouble("total");
                monthlyIncome.put(String.format("%02d月", month), total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthlyIncome;
    }
    
    public Map<String, Double> getAllTimeIncome(Long hostId) {
        Map<String, Double> allTimeIncome = new HashMap<>();
        // ... 实现查询逻辑 ...
        return allTimeIncome;
    }
    
    public Map<String, Integer> getOrderCountStats(Long hostId) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT status, COUNT(*) as count
            FROM orders o
            JOIN houses h ON o.house_id = h.id
            WHERE h.host_id = ?
            GROUP BY status
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    public Map<String, Double> getAllIncomeByMonth(Long hostId) {
        Map<String, Double> monthlyIncome = new TreeMap<>();
        String sql = """
            SELECT 
                DATE_FORMAT(created_at, '%Y-%m') as month,
                SUM(total_price) as total
            FROM orders o
            JOIN houses h ON o.house_id = h.id
            WHERE h.host_id = ? 
            AND o.status IN ('CONFIRMED', 'COMPLETED')
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            ORDER BY month
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                monthlyIncome.put(rs.getString("month"), rs.getDouble("total"));
            }
            
            if (monthlyIncome.isEmpty()) {
                LocalDate now = LocalDate.now();
                for (int i = 5; i >= 0; i--) {
                    LocalDate date = now.minusMonths(i);
                    String month = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    monthlyIncome.putIfAbsent(month, 0.0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthlyIncome;
    }
    
    public Map<String, Double> getIncomeData(Long hostId, String period) {
        Map<String, Double> data = new HashMap<>();
        LocalDate now = LocalDate.now();
        
        String sql = switch (period) {
            case "今日" -> """
                SELECT HOUR(created_at) as time_key, SUM(total_price) as income 
                FROM orders o
                JOIN houses h ON o.house_id = h.id
                WHERE h.host_id = ? 
                AND DATE(created_at) = ? 
                AND o.status = 'COMPLETED' 
                GROUP BY HOUR(created_at)
                """;
                
            case "本月" -> """
                SELECT DAY(created_at) as time_key, SUM(total_price) as income 
                FROM orders o
                JOIN houses h ON o.house_id = h.id
                WHERE h.host_id = ? 
                AND YEAR(created_at) = ? 
                AND MONTH(created_at) = ? 
                AND o.status = 'COMPLETED' 
                GROUP BY DAY(created_at)
                """;
                
            case "本季" -> """
                SELECT MONTH(created_at) as time_key, SUM(total_price) as income 
                FROM orders o
                JOIN houses h ON o.house_id = h.id
                WHERE h.host_id = ? 
                AND YEAR(created_at) = ? 
                AND QUARTER(created_at) = ? 
                AND o.status = 'COMPLETED' 
                GROUP BY MONTH(created_at)
                """;
                
            case "本年" -> """
                SELECT MONTH(created_at) as time_key, SUM(total_price) as income 
                FROM orders o
                JOIN houses h ON o.house_id = h.id
                WHERE h.host_id = ? 
                AND YEAR(created_at) = ? 
                AND o.status = 'COMPLETED' 
                GROUP BY MONTH(created_at)
                """;
                
            default -> throw new IllegalArgumentException("无效的统计周期");
        };

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            
            // 根据不同时期设置查询参数
            switch (period) {
                case "今日" -> {
                    stmt.setDate(2, Date.valueOf(now));
                }
                case "本月" -> {
                    stmt.setInt(2, now.getYear());
                    stmt.setInt(3, now.getMonthValue());
                }
                case "本季" -> {
                    stmt.setInt(2, now.getYear());
                    stmt.setInt(3, (now.getMonthValue() - 1) / 3 + 1);
                }
                case "本年" -> {
                    stmt.setInt(2, now.getYear());
                }
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String timeKey = rs.getString("time_key");
                double income = rs.getDouble("income");
                data.put(timeKey, income);
            }
            
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取收入数据失败", e);
        }
    }
} 