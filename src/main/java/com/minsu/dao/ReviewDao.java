package com.minsu.dao;

import com.minsu.model.entity.Review;
import com.minsu.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDao {
    
    public boolean addReview(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            return false;
        }
        
        String sql = "INSERT INTO reviews (order_id, house_id, guest_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, review.getOrderId());
            stmt.setLong(2, review.getHouseId());
            stmt.setLong(3, review.getGuestId());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getComment());
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Review> getReviewsByHouseId(Long houseId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE house_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, houseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reviews.add(resultSetToReview(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }
    
    private Review resultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        review.setOrderId(rs.getLong("order_id"));
        review.setHouseId(rs.getLong("house_id"));
        review.setGuestId(rs.getLong("guest_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getString("created_at"));
        return review;
    }
} 