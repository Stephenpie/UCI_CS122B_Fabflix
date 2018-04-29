package checkout;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import login.User;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/checkout")
public class CheckoutServlet extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(); // Get a instance of current session on the request
        HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("cart");
        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script type=\"text/javascript\" src=\"checkout.js\"></script>");
        out.println("</head>");
        
        out.println("<body>");
        out.println("<div class=\"col-md-4 col-md-offset-4\">");
        out.println("<h2 class=\"text-center\">Checkout</h2>");
        out.println("<form id=\"checkout_form\" method=\"post\" action=\"\">");
        out.println("<label><b>First name</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter first name\" name=\"firstname\">");
        out.println("<br><label><b>Last name</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter last name\" name=\"lastname\">");
        out.println("<br><label><b>Credit Card</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter credit card\" name=\"creditcard\">");
        out.println("<br><label><b>Expiration Date</b></label><input class=\"form-control\" type=\"date\" placeholder=\"Expiration date\" name=\"expiration\">");
        out.println("<br><input class=\"btn btn-info\" type=\"submit\" value=\"Submit Order\"></form></div>");
        
        out.println("<div id=\"checkout_error_message\"></div>");
        out.println("<script src=\"./checkout.js\"></script>");
        out.println("</body></html>");
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	HttpSession session = request.getSession(); // Get a instance of current session on the request
        HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("cart");
        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script type=\"text/javascript\" src=\"checkout.js\"></script>");
        out.println("</head>");
        
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String creditcard = request.getParameter("creditcard");
        String expirationDate = request.getParameter("expiration");
        
        System.out.println(expirationDate);

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
                        
            String query = "SELECT * FROM creditcards c WHERE c.firstName = '" + firstname + "' AND c.lastName = '" 
            + lastname + "' AND c.id = '" + creditcard + "' AND c.expiration = '" + expirationDate + "'";
            System.out.println(query);
            // execute query
            ResultSet resultSet = statement.executeQuery(query);
            
            
            /* This example only allows username/password to be test/test
            /  in the real project, you should talk to the database to verify username/password
            */
            
            if (resultSet.next()) {
                // Login success:
    
                // set this user into the session
//                request.getSession().setAttribute("user", new User(username, password));
    
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
    
                response.getWriter().write(responseJsonObject.toString());
            } else {
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                
                query = "SELECT * FROM creditcards c WHERE c.id = '" + creditcard + "'";
                resultSet = statement.executeQuery(query);
                
                if (!resultSet.next()) {
                    responseJsonObject.addProperty("message", "Credit card not exist");
                } else {
                    if (!firstname.equals(resultSet.getString("firstName"))) {
                        responseJsonObject.addProperty("message", "First name not correct!");
                    } else if (!lastname.equals(resultSet.getString("lastName"))) {
                        responseJsonObject.addProperty("message", "Last name not correct!");
                    } else if (!expirationDate.equals(resultSet.getString("expiration"))) {
                        responseJsonObject.addProperty("message", "Expiration date not correct!");
                    }
                }
                response.getWriter().write(responseJsonObject.toString());
            }
            out.println("<body>");
            out.println("<div class=\"col-md-4 col-md-offset-4\">");
            out.println("<h2 class=\"text-center\">Checkout</h2>");
            out.println("<form id=\"checkout_form\" method=\"post\" action=\"confirmation\">");
            out.println("<label><b>First name</b></label><input class=\"form-control\" type=\"text\" value=" + firstname + " name=\"firstname\">");
            out.println("<br><label><b>Last name</b></label><input class=\"form-control\" type=\"text\" value=" + lastname + " name=\"lastname\">");
            out.println("<br><label><b>Credit Card</b></label><input class=\"form-control\" type=\"text\" value=" + creditcard + " name=\"creditcard\">");
            out.println("<br><label><b>Expiration Date</b></label><input class=\"form-control\" type=\"date\" value=" + expirationDate + " name=\"expiration\">");
            out.println("<br><input class=\"btn btn-info\" type=\"submit\" value=\"Submit Order\"></form></div>");
            
            out.println("<div id=\"checkout_error_message\"></div>");
            out.println("<script src=\"checkout.js\"></script>");
            out.println("</body></html>");
            
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
