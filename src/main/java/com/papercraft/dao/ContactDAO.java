package com.papercraft.dao;

import com.papercraft.dto.ContactDTO;
import com.papercraft.utils.DBConnect;
import com.papercraft.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {
    private static final Logger logger = LoggerFactory.getLogger(ContactDAO.class);

    public List<Contact> getAllContact(){
        List<Contact> contacts = new ArrayList<>();
        String sql = """
            SELECT c.id, c.user_fullname, u.email ,c.contact_title, c.content, c.rely
            FROM contact c
            LEFT JOIN users u ON u.id = c.user_id
            ORDER BY c.created_at DESC;
            """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contact contact = new Contact();
                    contact.setId(rs.getInt("id"));
                    contact.setUserFullname(rs.getString("user_fullname"));
                    contact.setEmail(rs.getString("email")); // Lấy từ bảng contact
                    contact.setContactTitle(rs.getString("contact_title"));
                    contact.setContent(rs.getString("content"));
                    contact.setRely(rs.getBoolean("rely"));

                    contacts.add(contact);
                }

            }

        }catch (SQLException e) {
            logger.error("Failed to load contacts", e);
            throw new RuntimeException("Failed to load contacts", e);
        }
        catch (Exception e) {
            logger.error("Unexpected error while loading contacts", e);
            throw new RuntimeException(e);
        }
        return contacts;
    }

    public boolean insertContact(Contact c) {

        String sql = "INSERT INTO contact (user_id, user_fullname, email, contact_title, content, rely, created_at) VALUES (?, ?, ?, ?, ?, 0, NOW())";

        boolean inserted = false;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (c.getUserId() != null) {
                ps.setInt(1, c.getUserId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            ps.setString(2, c.getUserFullname());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getContactTitle());
            ps.setString(5, c.getContent());

            inserted = ps.executeUpdate() > 0;
            if (inserted) {
                logger.info("Contact created, userId={}", c.getUserId());
            }
            return inserted;

        } catch (SQLException e) {
            logger.error("Failed to create contact, userId={}", c.getUserId(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while creating contact, userId={}", c.getUserId(), e);
            throw new RuntimeException(e);
        }
        return inserted;
    }

    public boolean deleteContactById(int id){
        String sql = "DELETE FROM contact WHERE id = ?";
        try(Connection conn =DBConnect.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Contact deleted, contactId={}", id);
                return true;
            }
            logger.warn("Contact delete failed, contactId={}", id);
        }catch (SQLException e) {
            logger.error("Failed to delete contact, contactId={}", id, e);
        }
        catch (Exception e) {
            logger.error("Unexpected error while deleting contact, contactId={}", id, e);
            throw new RuntimeException(e);
        }
        return false;
    }

    public Integer totalUnrepliedContact() {
        String sql = """
                SELECT COUNT(*) AS total_unreplied FROM contact WHERE rely =0;
                """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_unreplied");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to count unreplied contacts", e);
        }
        catch (Exception e) {
            logger.error("Unexpected error while counting unreplied contacts", e);
        }
        return 0;
    }

    public List<Contact> getContact(String keyword, int replied) {
        List<Contact> contacts = new ArrayList<>();
        String sqlRaw = """
                SELECT c.id, c.user_fullname, u.email ,c.contact_title, c.content, c.rely, c.created_at
                FROM contact c
                LEFT JOIN users u ON u.id = c.user_id
                WHERE 1=1
                """;
        StringBuilder sqlBuilder = new StringBuilder(sqlRaw);

        if (keyword != null && !keyword.isEmpty()) {
            sqlBuilder.append(""" 
                    AND (c.user_fullname LIKE ?
                    OR u.email LIKE ?
                    OR c.contact_title LIKE ?
                    OR LOWER(c.content) LIKE ?)
                    """);
        }

        if (replied != -1) {
            sqlBuilder.append(" AND rely = ?");
        }

        sqlBuilder.append(" ORDER BY c.created_at DESC;");

        String sql = sqlBuilder.toString();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            int index = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                for (int i = 1; i <= 4; i++) {
                    ps.setString(index++, "%" + keyword.toLowerCase().trim() + "%");
                }
            }

            if (replied != -1) {
                ps.setInt(index++, replied);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contact contact = new Contact();
                    contact.setId(rs.getInt("id"));
                    contact.setUserFullname(rs.getString("user_fullname"));
                    contact.setEmail(rs.getString("email")); // Lấy từ bảng contact
                    contact.setContactTitle(rs.getString("contact_title"));
                    contact.setContent(rs.getString("content"));
                    contact.setCreatedAt(rs.getTimestamp("created_at"));
                    contact.setRely(rs.getBoolean("rely"));

                    contacts.add(contact);
                }

            }
        }catch (SQLException e) {
            logger.error("Failed to search contacts", e);
        }catch (Exception e) {
            logger.error("Unexpected error while searching contacts", e);
        }
        return contacts;
    }

    public boolean updateStatus(int id, boolean newStatus) {
        String sql = "UPDATE contact SET rely = ? WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newStatus ? 1 : 0);
            ps.setInt(2, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Contact status updated, contactId={}, replied={}", id, newStatus);
                return true;
            }
            logger.warn("Contact status update failed, contactId={}", id);
        }catch (SQLException e) {
            logger.error("Failed to update contact status, contactId={}", id, e);
        }catch (Exception e) {
            logger.error("Unexpected error while updating contact status, contactId={}", id, e);
        }
        return false;
    }


    public List<ContactDTO> getContactsByMonth(int month, int year) {
        List<ContactDTO> list = new ArrayList<>();

        String sql = """
        SELECT *
        FROM contact
        WHERE MONTH(created_at) = ?
        AND YEAR(created_at) = ?
        AND rely = 0
        ORDER BY created_at DESC;
    """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ContactDTO c = new ContactDTO();
                c.setId(rs.getInt("id"));
                c.setUserFullname(rs.getString("user_fullname"));
                c.setEmail(rs.getString("email"));
                c.setContactTitle(rs.getString("contact_title"));
                c.setContent(rs.getString("content"));
                c.setRely(rs.getBoolean("rely"));
                list.add(c);
            }
        }catch (SQLException e) {
            logger.error("Failed to load contacts by month, month={}, year={}", month, year, e);
        }catch (Exception e) {
            logger.error("Unexpected error while loading contacts by month, month={}, year={}", month, year, e);
        }
        return list;
    }
}