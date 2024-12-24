package com.minsu.dao;

import com.minsu.model.entity.Order;
import com.minsu.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDao {
    
    public boolean addOrder(Order order) {
        String sql = "INSERT INTO orders (house_id, guest_id, check_in, check_out, status, total_price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, order.getHouseId());
            stmt.setLong(2, order.getGuestId());
            stmt.setString(3, order.getCheckIn());
            stmt.setString(4, order.getCheckOut());
            stmt.setString(5, order.getStatus());
            stmt.setDouble(6, order.getTotalPrice());
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Order> getOrdersByHostId(Long hostId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.* FROM orders o JOIN houses h ON o.house_id = h.id WHERE h.host_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(resultSetToOrder(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public List<Order> getOrdersByGuestId(Long guestId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE guest_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, guestId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(resultSetToOrder(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public boolean updateOrderStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setLong(2, orderId);
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Map<String, Integer> getOrderStatistics() {
        Map<String, Integer> statistics = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM orders GROUP BY status";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                statistics.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statistics;
    }
    
    public List<Order> searchOrders(Long userId, String userRole, String status, String dateRange) {
        List<Order> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT o.* FROM orders o");
        
        if ("HOST".equals(userRole)) {
            sql.append(" JOIN houses h ON o.house_id = h.id WHERE h.host_id = ?");
        } else {
            sql.append(" WHERE o.guest_id = ?");
        }
        
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        if (status != null && !status.isEmpty() && !"ALL".equals(status)) {
            sql.append(" AND o.status = ?");
            params.add(status);
        }
        
        if (dateRange != null && !dateRange.isEmpty()) {
            sql.append(" AND o.created_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY)");
            params.add(Integer.parseInt(dateRange));
        }
        
        sql.append(" ORDER BY o.created_at DESC");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(resultSetToOrder(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public boolean isDateRangeAvailable(Long houseId, String checkIn, String checkOut) {
        try {
            LocalDate checkInDate = LocalDate.parse(checkIn);
            LocalDate checkOutDate = LocalDate.parse(checkOut);
            
            if (checkInDate.isBefore(LocalDate.now()) || checkOutDate.isBefore(checkInDate)) {
                return false;
            }
            
            String sql = """
                SELECT COUNT(*) FROM orders 
                WHERE house_id = ? 
                AND status != 'CANCELLED'
                AND (
                    (check_in <= ? AND check_out > ?) OR
                    (check_in < ? AND check_out >= ?) OR
                    (check_in >= ? AND check_out <= ?)
                )
            """;
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setLong(1, houseId);
                stmt.setString(2, checkOut);
                stmt.setString(3, checkIn);
                stmt.setString(4, checkOut);
                stmt.setString(5, checkIn);
                stmt.setString(6, checkIn);
                stmt.setString(7, checkOut);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) == 0; // 返回true表示日期可用
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false; // 发生错误时返回false表示日期不可用
    }
    
    private Order resultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setHouseId(rs.getLong("house_id"));
        order.setGuestId(rs.getLong("guest_id"));
        order.setCheckIn(rs.getString("check_in"));
        order.setCheckOut(rs.getString("check_out"));
        order.setStatus(rs.getString("status"));
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setCreatedAt(rs.getString("created_at"));
        return order;
    }
} 