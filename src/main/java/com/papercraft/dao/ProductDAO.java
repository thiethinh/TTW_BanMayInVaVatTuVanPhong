package com.papercraft.dao;

import com.papercraft.model.Product;
import com.papercraft.utils.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    //getAllProduct
    public List<Product> getAllProduct(String type){
        List<Product> list = new ArrayList<>();

        String sql= """
                SELECT p.id, p.product_name, p.category_id, p.description_thumbnail, p.brand, p.price, i.img_name
                                    FROM product p
                                    JOIN category c ON p.category_id = c.id
                                    JOIN image i ON p.id = i.entity_id
                                    WHERE c.type = ? AND i.is_thumbnail = 1 AND i.entity_type = 'Product';
                """;
        try (Connection conn = DBConnect.getConnection();
        PreparedStatement ps= conn.prepareStatement(sql)){

            ps.setString(1,type);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Product p = new Product();

                    p.setId(rs.getInt("id"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setProductName(rs.getString("product_name"));
                    p.setDescriptionThumbnail(rs.getString("description_thumbnail"));
                    p.setBrand(rs.getString("brand"));
                    p.setPrice(rs.getBigDecimal("price"));

                    String imgName= rs.getString("img_name");
                    if(imgName != null && !imgName.trim().isEmpty()){
                        p.setThumbnail("images/upload/" + rs.getString("img_name"));
                    }else{
                        p.setThumbnail("images/logo.webp");
                    }
                    list.add(p);
                }
            } catch (SQLException e) {
                System.err.println("Lỗi tại getAllProduct với type = " +type);
                e.printStackTrace();
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // getAllImageOfProduct
    public List<String> getAllImageOfProduct(int id) {
        List<String> images = new ArrayList<>();

        String sql = "SELECT img_name FROM image WHERE entity_id = ? AND entity_type = 'Product' AND is_thumbnail = 0 AND img_name IS NOT NULL";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String imgName = rs.getString("img_name");
                    if (imgName != null && !imgName.trim().isEmpty()) {

                        images.add("images/upload/" + imgName.trim());
                    }
                }
            }
        } catch (Exception e) {

            System.err.println("Lỗi tại getAllImageOfProduct với Product ID: " + id);
            e.printStackTrace();
        }
        return images;
    }

    //Thêm một sản phẩm mới vào cơ sở dữ liệu.
    public boolean insertProduct(Product product) throws Exception {

        String sql = "INSERT INTO product (category_id, product_name, description_thumbnail, product_description, product_detail, " +
                "brand, price, origin_price, discount, stock_quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // Lấy ID tự tăng
        ) {

            ps.setInt(1, product.getCategoryId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getDescriptionThumbnail());
            ps.setString(4, product.getProductDescription());
            ps.setString(5, product.getProductDetail());
            ps.setString(6, product.getBrand());
            ps.setDouble(7, product.getPrice());
            ps.setDouble(8, product.getOriginPrice());
            ps.setDouble(9, product.getDiscount());
            ps.setInt(10, product.getStockQuantity());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        product.setId(generatedId); // Gán ID mới cho đối tượng Product
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("SQL Error when inserting product: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Database error occurred while adding a new product.", e);
        }
    }
    

}
