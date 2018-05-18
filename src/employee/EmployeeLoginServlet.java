package employee;

import com.google.gson.JsonObject;

import login.RecaptchaVerifyUtils;
import login.User;

import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//
@WebServlet(urlPatterns = "/fabflix/_dashboard")
public class EmployeeLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private boolean validated = false;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head><title>Fabflix Employee Dashboard</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>");
        out.println("<script src='https://www.google.com/recaptcha/api.js'></script>");
        out.println("<script src=\"../elogin.js\"></script>");
        out.println("</head>");
        
        out.println("<body class=\"loginBackgroundColor\">");
        out.println("<div class=\"col-md-4 col-md-offset-4\">");
        out.println("<h2 class=\"text-center\">Employee Log in</h2>");
        out.println("<form id=\"elogin_form\" method=\"post\" action=\"\">");
        out.println("<label><b>Email</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter email\" name=\"email\">");
        out.println("<br><label><b>Password</b></label><input class=\"form-control\" type=\"password\" placeholder=\"Enter password\" name=\"password\">");
        out.println("<br><input class=\"btn btn-info\" type=\"submit\" value=\"Login\">");
        out.println("<div class=\"g-recaptcha\" data-sitekey=\"6LcEZlgUAAAAAODZNvZ7KBJGaoZLWEdytB4AOgJp\"></div></form>");
        
        out.println("<center><strong><div id=\"elogin_error_message\"></div><strong></center>");
        out.println("<script src=\"../elogin.js\"></script>");
        out.println("</div>");
        out.println("</body></html>");
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullname = request.getParameter("fullname");

        String loginUser = "root";
        String loginPasswd = "tangwang";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // prepare query       
            String query = "SELECT * FROM employees c WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            
            // execute query
            ResultSet resultSet = statement.executeQuery();
            
            boolean success = false;
            if (resultSet.next()) {
                if (!validated) {
                    String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
                    System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
                    
                    RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                    validated = true;
                }
                
                // Login success:
                // get the encrypted password from the database
                String encryptedPassword = resultSet.getString("password");
                
                // use the same encryptor to compare the user input password with encrypted password stored in DB
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                
                if (success == true) {
                    // set this user into the session
                    request.getSession().setAttribute("user", new User(email, password, fullname));
        
                    JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    response.getWriter().write(responseJsonObject.toString());
                } else {
                    // Login fail
                    JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("status", "fail");
                    
                    responseJsonObject.addProperty("message", "Either username or password doesn't exist");
                    response.getWriter().write(responseJsonObject.toString());
                }
            } else {
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                
                responseJsonObject.addProperty("message", "Either username or password doesn't exist");
                response.getWriter().write(responseJsonObject.toString());
            }
            resultSet.close();
            statement.close();
            connection.close();
            
        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             * 
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}