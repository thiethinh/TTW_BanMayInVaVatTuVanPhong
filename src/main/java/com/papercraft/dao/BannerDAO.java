package com.papercraft.dao;

import com.papercraft.config.CloudinaryConfig;
import com.papercraft.model.Banner;
import com.papercraft.utils.DBConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BannerDAO {
    public static final String IMAGE_BASE_URL = CloudinaryConfig.IMAGE_BASE_URL;
    private static final Logger logger = LoggerFactory.getLogger(BannerDAO.class);

    public List<Banner> getAllBanner(String keyword) {

        List<Banner> banners = new ArrayList<>();

        String sql = """
                SELECT *
                FROM banner
                WHERE title LIKE ? AND is_deleted=0
                ORDER BY sort_order ASC
                """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            banners = mapBannerList(rs);
        } catch (SQLException e) {
            logger.error("Failed to load banner list, keyword={}", keyword, e);
        }
        catch (Exception e) {
            logger.error("Unexpected error while loading banner list, keyword={}", keyword, e);
        }
        return banners;
    }

    public List<Banner> getActiveBanner() {

        List<Banner> banners = new ArrayList<>();

        String sql = """
                SELECT *
                FROM banner
                WHERE is_active = 1 AND is_deleted=0
                ORDER BY sort_order ASC
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            banners = mapBannerList(rs);
        }catch (Exception e) {
            logger.error("Failed to load active banners", e);
        }

        return banners;
    }

    public void toggleBanner(int id) {
        String sql = """
                UPDATE banner
                SET is_active = NOT is_active
                WHERE id = ? AND is_deleted=0
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Banner status toggled, bannerId={}", id);
            } else {
                logger.warn("Banner status toggle failed, bannerId={} not found", id);
            }
        }catch (Exception e) {
            logger.error("Failed to toggle banner status, bannerId={}", id, e);
        }
    }

    public void deleteBanner(int id) {

        String sql = "UPDATE banner SET is_deleted = 1 WHERE id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Banner soft deleted, bannerId={}", id);
            } else {
                logger.warn("Banner delete failed, bannerId={} not found", id);
            }
        }catch (Exception e) {
            logger.error("Failed to delete banner, bannerId={}", id, e);
        }
    }

    public void updateBanner(Banner b) {

        String sql = """
            UPDATE banner
            SET title = ?, img_name = ?,is_active = ?,sort_order = ?
            WHERE id = ?
            """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getTitle());
            ps.setString(2, b.getImgName());
            ps.setBoolean(3, b.isActive());
            ps.setInt(4, b.getSortOrder());
            ps.setInt(5, b.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Banner updated, bannerId={}", b.getId());
            } else {
                logger.warn("Banner update failed, bannerId={} not found", b.getId());
            }

        } catch (Exception e) {
            logger.error("Failed to update banner, bannerId={}", b.getId(), e);
        }
    }

    public Banner getBannerById(int id) {
        String sql = "SELECT * FROM banner WHERE id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery(); if(rs.next()){

                Banner b = new Banner();

                b.setId(rs.getInt("id"));
                b.setTitle(rs.getString("title"));
                b.setImgName(rs.getString("img_name"));
                b.setImagePath(rs.getString("img_name"));
                b.setActive(rs.getBoolean("is_active"));
                b.setSortOrder(rs.getInt("sort_order"));
                b.setImagePath(IMAGE_BASE_URL+rs.getString("img_name"));
                return b;
            }

        } catch (Exception e) {
            logger.error("Failed to load banner, bannerId={}", id, e);
        }
        return null;
    }

    public int insertBanner(Banner b) {
        String sql = """
            
                INSERT INTO banner(title,img_name,is_active,sort_order)
                            VALUES(?,?,?,?)
            """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1,b.getTitle());
            ps.setString(2,b.getImgName());
            ps.setBoolean(3,b.isActive());
            ps.setInt(4,b.getSortOrder());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Banner created, title={}", b.getTitle());
            }
            return affectedRows;
        } catch (Exception e) {
            logger.error("Failed to create banner, title={}", b.getTitle(), e);
        }
        return  0;
    }

    public List<String> getActiveUrlBannerImage() {
        List<String> imageUrls = new ArrayList<>();
        String sql = """
                SELECT img_name
                FROM banner
                WHERE is_deleted=0 AND is_active=1
                ORDER BY sort_order ASC;
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                imageUrls.add(IMAGE_BASE_URL + rs.getString("img_name"));
            }
        }catch (SQLException e) {
            logger.error("Failed to load active banner images", e);
        }
        catch (Exception e) {
            logger.error("Unexpected error while loading active banner images", e);
        }
        return imageUrls;
    }

    private List<Banner> mapBannerList(ResultSet rs) throws SQLException {
        List<Banner> banners = new ArrayList<>();
        while (rs.next()){
            Banner b = new Banner();

            b.setId(rs.getInt("id"));
            b.setTitle(rs.getString("title"));
            b.setImgName(rs.getString("img_name"));
            b.setImagePath(rs.getString("img_name"));
            b.setActive(rs.getBoolean("is_active"));
            b.setSortOrder(rs.getInt("sort_order"));
            b.setCreatedAt(rs.getTimestamp("created_at"));
            b.setImagePath(IMAGE_BASE_URL+rs.getString("img_name"));
            banners.add(b);
        }
        return banners;
    }
}