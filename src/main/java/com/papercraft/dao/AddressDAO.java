package com.papercraft.dao;

import com.papercraft.model.Address;
import com.papercraft.utils.DBConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Objects;

public class AddressDAO {
    private static final Logger logger = LoggerFactory.getLogger(AddressDAO.class);
    // insertAddress
    public boolean insertAddress(Address address) {
        // ID auto increament
        String sql = """
        INSERT INTO address (user_id, fname, lname, nation,city, province_id, province_name,district_id, district_name,ward_code, ward_name,detail_address, postcode, email, phone, is_default)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (
                Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); //Lấy ID Sau khi thêm
        ) {
            ps.setInt(1, address.getUserId());
            ps.setString(2, address.getFname());
            ps.setString(3, address.getLname());
            ps.setString(4, address.getNation());
            ps.setString(5, address.getCity());

            if (address.getProvinceId() == null) {
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(6, address.getProvinceId());
            }

            ps.setString(7, address.getProvinceName());

            if (address.getDistrictId() == null) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, address.getDistrictId());
            }

            ps.setString(9, address.getDistrictName());
            ps.setString(10, address.getWardCode());
            ps.setString(11, address.getWardName());
            ps.setString(12, address.getDetailAddress());
            ps.setString(13, address.getPostcode());
            ps.setString(14, address.getEmail());
            ps.setString(15, address.getPhone());
            ps.setBoolean(16, address.getDefault());


            int rowsAffected = ps.executeUpdate();

            // ID tự tăng -> gán ngược lại cho đối tượng Address
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        address.setId(generatedId);
                        logger.info("Address inserted successfully, addressId={}, userId={}", generatedId, address.getUserId());
                    }
                }
                return true;
            }
            logger.warn("Insert address failed, no rows affected, userId={}", address.getUserId());
            return false;

        } catch (SQLException e) {
            logger.error("SQL error while inserting address for userId={}", address.getUserId(), e);
            throw new RuntimeException("Database error occurred while adding a new address.", e);
        } catch (Exception e) {
            logger.error("Unexpected error while inserting address for userId={}", address.getUserId(), e);
            throw new RuntimeException(e);
        }
    }

    //Tìm địa chỉ mặc định của User để hiển thị lên trang Checkout
    public Address findDefaultAddress(int userId) {
        // Query lấy địa chỉ mặc định (is_default = 1) của user
        String sql = "SELECT * FROM address WHERE user_id = ? AND is_default = 1";


        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Address addr = new Address();
                    addr.setId(rs.getInt("id"));
                    addr.setUserId(rs.getInt("user_id"));
                    addr.setFname(rs.getString("fname"));
                    addr.setLname(rs.getString("lname"));
                    addr.setNation(rs.getString("nation"));
                    addr.setCity(rs.getString("city"));
                    addr.setDetailAddress(rs.getString("detail_address"));
                    addr.setPostcode(rs.getString("postcode"));
                    addr.setEmail(rs.getString("email"));
                    addr.setPhone(rs.getString("phone"));
                    addr.setDefault(rs.getBoolean("is_default"));
                    addr.setProvinceId((Integer) rs.getObject("province_id"));
                    addr.setProvinceName(rs.getString("province_name"));
                    addr.setDistrictId((Integer) rs.getObject("district_id"));
                    addr.setDistrictName(rs.getString("district_name"));
                    addr.setWardCode(rs.getString("ward_code"));
                    addr.setWardName(rs.getString("ward_name"));

                    logger.debug("Default address found, addressId={}, userId={}", addr.getId(), userId);
                    return addr;
                }
            }
            logger.warn("No default address found for userId={}", userId);

        } catch (SQLException e) {
            logger.error("SQL error while finding default address for userId={}", userId, e);
        } catch (Exception e) {
            logger.error("Unexpected error while finding default address for userId={}", userId, e);
            throw new RuntimeException(e);
        }
        return null;
    }


    public boolean updateAddress(Address address, int userId) {
        String sql = """
                UPDATE address 
                SET fname = ?, lname = ?, nation = ?, city = ?, province_id = ?, province_name = ?, district_id = ?, district_name = ?, ward_code = ?, ward_name = ?, detail_address = ?, postcode = ?, phone = ?
                WHERE user_id = ?
                """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, address.getFname());
            ps.setString(2, address.getLname());
            ps.setString(3, address.getNation());
            ps.setString(4, address.getCity());

            if (address.getProvinceId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, address.getProvinceId());
            }

            ps.setString(6, address.getProvinceName());

            if (address.getDistrictId() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, address.getDistrictId());
            }

            ps.setString(8, address.getDistrictName());
            ps.setString(9, address.getWardCode());
            ps.setString(10, address.getWardName());
            ps.setString(11, address.getDetailAddress());
            ps.setString(12, address.getPostcode());
            ps.setString(13, address.getPhone());
            ps.setInt(14, userId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Address updated successfully for userId={}", userId);
                return true;
            }else{
                logger.warn("Address update failed, no rows affected for userId={}", userId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("SQL error while updating address for userId={}", userId, e);
        } catch (Exception e) {
            logger.error("Unexpected error while updating address for userId={}", userId, e);
            throw new RuntimeException(e);
        }
        return false;
    }

    public Address getAddresById(Integer userId) {
        String sql = """
            SELECT id,user_id,lname,fname,nation,city,province_id,province_name,district_id,district_name,ward_code,ward_name,detail_address,postcode,email,phone,is_default
            FROM address
            WHERE user_id = ? AND is_default = 1
            """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Address addr = new Address();
                    addr.setId(rs.getInt("id"));
                    addr.setUserId(rs.getInt("user_id"));
                    addr.setFname(Objects.requireNonNullElse(rs.getString("fname"), ""));
                    addr.setLname(Objects.requireNonNullElse(rs.getString("lname"), ""));
                    addr.setNation(Objects.requireNonNullElse(rs.getString("nation"), ""));
                    addr.setCity(Objects.requireNonNullElse(rs.getString("city"), ""));
                    addr.setDetailAddress(Objects.requireNonNullElse(rs.getString("detail_address"), ""));
                    addr.setPostcode(Objects.requireNonNullElse(rs.getString("postcode"), ""));
                    addr.setEmail(Objects.requireNonNullElse(rs.getString("email"), ""));
                    addr.setPhone(Objects.requireNonNullElse(rs.getString("phone"), ""));
                    addr.setDefault(rs.getBoolean("is_default"));

                    addr.setProvinceId((Integer) rs.getObject("province_id"));
                    addr.setProvinceName(Objects.requireNonNullElse(rs.getString("province_name"), ""));
                    addr.setDistrictId((Integer) rs.getObject("district_id"));
                    addr.setDistrictName(Objects.requireNonNullElse(rs.getString("district_name"), ""));
                    addr.setWardCode(Objects.requireNonNullElse(rs.getString("ward_code"), ""));
                    addr.setWardName(Objects.requireNonNullElse(rs.getString("ward_name"), ""));

                    logger.debug("Address loaded successfully, addressId={}, userId={}", addr.getId(), userId);
                    return addr;
                }
            }

        } catch (SQLException e) {
            logger.error("SQL error while getting address for userId={}", userId, e);
        } catch (Exception e) {
            logger.error("Unexpected error while getting address for userId={}", userId, e);
            throw new RuntimeException(e);
        }

        return null;
    }
}
