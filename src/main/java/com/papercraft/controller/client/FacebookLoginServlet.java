package com.papercraft.controller.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.papercraft.dao.UserDAO;
import com.papercraft.dto.FacebookUser;
import com.papercraft.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@WebServlet(name = "FacebookLoginServlet", value = "/facebook-login")
public class FacebookLoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FacebookLoginServlet.class);

    private static final String FACEBOOK_APP_ID = "";
    private static final String FACEBOOK_APP_SECRET = "";
    private static final String REDIRECT_URL = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String code = request.getParameter("code");

        if (code == null || code.isEmpty()) {
            logger.warn("Đăng nhập Facebook thất bại: Không nhận được tham số 'code' từ Facebook OAuth.");
            session.setAttribute("msg", "Đăng nhập Facebook thất bại hoặc đã bị hủy.");
            response.sendRedirect("login");
            return;
        }

        logger.info("Nhận được mã code từ Facebook. Bắt đầu tiến trình xác thực tài khoản.");

        try {
            logger.debug("Đang trao đổi mã code để lấy Access Token...");
            String accessToken = getAccessToken(code);

            logger.debug("Đang lấy thông tin người dùng từ Facebook Graph API...");
            FacebookUser facebookUser = getUserInfo(accessToken);

            String email = facebookUser.getEmail();
            logger.info("Xử lý tài khoản Facebook có Email: '{}', Tên: '{}'", email, facebookUser.getName());

            UserDAO userDAO = new UserDAO();
            if (!userDAO.checkEmailExists(email)) {
                logger.info("Email '{}' chưa tồn tại trong hệ thống. Tiến hành tự động đăng ký thành viên mới.", email);
                // Đăng ký nếu email chưa tồn tại
                User newUser = new User();
                newUser.setEmail(email);

                String fullname = facebookUser.getName();
                if (fullname != null && fullname.contains(" ")) {
                    newUser.setFname(fullname.substring(0, fullname.indexOf(" ")));
                    newUser.setLname(fullname.substring(fullname.indexOf(" ") + 1));
                } else {
                    newUser.setFname("");
                    newUser.setLname(fullname == null ? "" : fullname);
                }

                newUser.setPhoneNumber("");
                newUser.setGender("");
                newUser.setPasswordHash("");

                userDAO.signup(newUser);
                logger.info("Đăng ký thành công tài khoản mới từ Facebook cho Email: '{}'", email);
            }

            // Đăng nhập nếu email đã tồn tại
            logger.debug("Đang truy vấn thông tin chi tiết tài khoản từ CSDL cho Email: '{}'", email);
            User loggedUser = userDAO.getUserByEmail(email);
            if (loggedUser != null) {
                logger.info("User ID '{}' đăng nhập thành công bằng tài khoản Facebook.", loggedUser.getId());
                session.setAttribute("acc", loggedUser);
                session.setAttribute("success", "Đăng nhập thành công");
                response.sendRedirect("home");
            } else {
                logger.error("Lỗi logic hệ thống: Đã kiểm tra/đăng ký email '{}' nhưng không thể lấy ra đối tượng User từ CSDL.", email);
                session.setAttribute("msg", "Lỗi khi tải thông tin tài khoản");
                response.sendRedirect("login");
            }
        } catch (Exception e) {
            logger.error("Lỗi hệ thống nghiêm trọng trong tiến trình xác thực Facebook OAuth: ", e);
            session.setAttribute("msg", "Lỗi hệ thống khi kết nối với Facebook");
            response.sendRedirect("login");
        }
    }

    private String getAccessToken(String code) throws IOException {
        String urlStr = "https://graph.facebook.com/v19.0/oauth/access_token?" + "client_id=" + FACEBOOK_APP_ID + "&client_secret=" + FACEBOOK_APP_SECRET + "&redirect_uri=" + REDIRECT_URL + "&code=" + code;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Scanner scanner = new Scanner(new InputStreamReader(conn.getInputStream()));
        String jsonResponse = scanner.useDelimiter("\\A").next();
        scanner.close();

        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
        return jsonObject.get("access_token").getAsString();
    }

    private FacebookUser getUserInfo(String accessToken) throws IOException {
        String urlStr = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Scanner scanner = new Scanner(new InputStreamReader(conn.getInputStream()));
        String jsonResponse = scanner.useDelimiter("\\A").next();
        scanner.close();

        return new Gson().fromJson(jsonResponse, FacebookUser.class);
    }
}
