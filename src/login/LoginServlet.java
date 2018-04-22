package login;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.IOException;

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
			Statement statement = connection.createStatement();
			// prepare query
						
			String query = "SELECT c.email, c.password FROM customers c WHERE c.email = "+ "'" + username + "'" + " AND c.password = " + "'" + password + "'";
			// execute query
    		ResultSet resultSet = statement.executeQuery(query);
    		
    		
	        /* This example only allows username/password to be test/test
	        /  in the real project, you should talk to the database to verify username/password
	        */
	        
	        if (resultSet.next()) {
	            // Login success:
	
	            // set this user into the session
	            request.getSession().setAttribute("user", new User(username, password));
	
	            JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");
	
	            response.getWriter().write(responseJsonObject.toString());
	        } else {
	            // Login fail
	            JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
	            
	            responseJsonObject.addProperty("message", "Either username or password doesn't exist");
	            /*
	            if (!username.equals("anteater")) {
	                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
	            } else if (!password.equals("123456")) {
	                responseJsonObject.addProperty("message", "incorrect password");
	            }
	            */
	            response.getWriter().write(responseJsonObject.toString());
	        }
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