package com.minsu.dao;

import com.minsu.model.entity.House;
import com.minsu.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HouseDao {
    
    public boolean addHouse(House house) {
        String sql = """
            INSERT INTO houses (host_id, title, description, price, status, publish_time)
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, house.getHostId());
            stmt.setString(2, house.getTitle());
            stmt.setString(3, house.getDescription());
            stmt.setDouble(4, house.getPrice());
            stmt.setString(5, house.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<House> getAllHouses() {
        String sql = """
            SELECT id, host_id, title, description, price, status, publish_time
            FROM houses
            ORDER BY publish_time DESC
        """;
        
        List<House> houses = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                House house = new House();
                house.setId(rs.getLong("id"));
                house.setHostId(rs.getLong("host_id"));
                house.setTitle(rs.getString("title"));
                house.setDescription(rs.getString("description"));
                house.setPrice(rs.getDouble("price"));
                house.setStatus(rs.getString("status"));
                house.setPublishTime(rs.getTimestamp("publish_time").toLocalDateTime());
                houses.add(house);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return houses;
    }
    
    public List<House> getHousesByHostId(Long hostId) {
        List<House> houses = new ArrayList<>();
        String sql = "SELECT * FROM houses WHERE host_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, hostId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                houses.add(resultSetToHouse(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return houses;
    }
    
    public List<House> getAllAvailableHouses() {
        List<House> houses = new ArrayList<>();
        String sql = "SELECT * FROM houses WHERE status = 'AVAILABLE'";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                houses.add(resultSetToHouse(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return houses;
    }
    
    public int countHousesByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM houses WHERE status = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private House resultSetToHouse(ResultSet rs) throws SQLException {
        House house = new House();
        house.setId(rs.getLong("id"));
        house.setHostId(rs.getLong("host_id"));
        house.setTitle(rs.getString("title"));
        house.setDescription(rs.getString("description"));
        house.setPrice(rs.getDouble("price"));
        house.setStatus(rs.getString("status"));
        house.setCreatedAt(rs.getString("created_at"));
        return house;
    }
    
    public List<House> searchHouses(String keyword, Double minPrice, Double maxPrice) {
        List<House> houses = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM houses WHERE status = 'AVAILABLE'");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        
        sql.append(" ORDER BY created_at DESC");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                houses.add(resultSetToHouse(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return houses;
    }
    
    public boolean updateHouse(House house) {
        if (house.getPrice() <= 0) {
            return false;
        }
        
        String sql = "UPDATE houses SET title = ?, description = ?, price = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, house.getTitle());
            stmt.setString(2, house.getDescription());
            stmt.setDouble(3, house.getPrice());
            stmt.setString(4, house.getStatus());
            stmt.setLong(5, house.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public House getHouseById(Long id) {
        String sql = "SELECT * FROM houses WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return resultSetToHouse(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean deleteHouse(Long id) {
        String sql = "DELETE FROM houses WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 