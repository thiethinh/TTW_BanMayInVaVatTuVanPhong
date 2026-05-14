package com.papercraft.dao;

import com.papercraft.model.Banner;
import com.papercraft.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BannerDAO {

    public List<Banner> getAllBanner(String keyword) {

        List<Banner> banners = new ArrayList<>();

        String sql = """
                SELECT *
                FROM banner
                WHERE title LIKE ?
                ORDER BY sort_order ASC
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Banner b = new Banner();

                b.setId(rs.getInt("id"));
                b.setTitle(rs.getString("title"));
                b.setImgName(rs.getString("img_name"));
                b.setImagePath(rs.getString("img_name"));
                b.setActive(rs.getBoolean("is_active"));
                b.setSortOrder(rs.getInt("sort_order"));
                b.setCreatedAt(rs.getTimestamp("created_at"));

                banners.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return banners;
    }

    public List<Banner> getActiveBanner() {

        List<Banner> banners = new ArrayList<>();

        String sql = """
                SELECT *
                FROM banner
                WHERE is_active = 1
                ORDER BY sort_order ASC
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Banner b = new Banner();

                b.setId(rs.getInt("id"));
                b.setTitle(rs.getString("title"));
                b.setImgName(rs.getString("img_name"));
                b.setImagePath(rs.getString("img_name"));
                b.setActive(rs.getBoolean("is_active"));
                b.setSortOrder(rs.getInt("sort_order"));
                b.setCreatedAt(rs.getTimestamp("created_at"));

                banners.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return banners;
    }

    public void toggleBanner(int id) {

        String sql = """
                UPDATE banner
                SET is_active = NOT is_active
                WHERE id = ?
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteBanner(int id) {

        String sql = "DELETE FROM banner WHERE id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}