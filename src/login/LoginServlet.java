package login;

import com.google.gson.JsonObject;

import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String loginUser = "root";
        String loginPasswd = "tangwang";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        try {
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
			// create database connection
			Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			// declare statement
//			Statement statement = connection.createStatement();
			// prepare query
						
			String query = "SELECT id, email, password FROM customers WHERE email=?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			// execute query
    		ResultSet resultSet = statement.executeQuery();
    		
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
            
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
    		
	        /* This example only allows username/password to be test/test
	        /  in the real project, you should talk to the database to verify username/password
	        */
            boolean success = false;
	        if (resultSet.next()) {
	            // Login success:
	        	// get the encrypted password from the database
				String encryptedPassword = resultSet.getString("password");
				
				// use the same encryptor to compare the user input password with encrypted password stored in DB
				success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
	        	
				if (success == true) {
					// set this user into the session
		            String userID = resultSet.getString("id");
		            request.getSession().setAttribute("user", new User(username, password, userID));
		
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