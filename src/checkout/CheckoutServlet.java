package checkout;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/checkout")
public class CheckoutServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"checkout.js\"></script>");
        out.println("<script src=\"movielist.js\"></script>");
        out.println("</head>");
        
        out.println("<body class=\"loginBackgroundColor\">");
        out.println("<div class=\"col-md-4 col-md-offset-4\">");
        out.println("<h2 class=\"text-center\">Checkout</h2>");
        out.println("<form id=\"checkout_form\" method=\"post\" action=\"\">");
        out.println("<label><b>First name</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter first name\" name=\"firstname\">");
        out.println("<br><label><b>Last name</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter last name\" name=\"lastname\">");
        out.println("<br><label><b>Credit Card</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter credit card\" name=\"creditcard\">");
        out.println("<br><label><b>Expiration Date</b></label><input class=\"form-control\" type=\"date\" placeholder=\"Expiration date\" name=\"expiration\">");
        out.println("<br><input class=\"btn btn-info\" type=\"submit\" value=\"Submit Order\"><button type=\"button\" class=\"btn btn-info\" id=\"home\">Home</button><button class=\"btn btn-info\" id=\"addTo\" onclick=\"viewCart()\">Go to Cart</button></form>");
        
        out.println("<center><strong><div id=\"checkout_error_message\"></div><strong></center>");
        out.println("</div>");
        out.println("<script src=\"movielist.js\"></script>");
        out.println("<script src=\"./checkout.js\"></script>");
        out.println("</body></html>");
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
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
            // prepare query       
            String query = "SELECT * FROM creditcards c WHERE c.firstName = ? AND c.lastName = ? AND c.id = ? AND c.expiration = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, firstname);
            statement.setString(2, lastname);
            statement.setString(3, creditcard);
            statement.setString(4, expirationDate);
            // execute query
            ResultSet resultSet = statement.executeQuery();
            
            
            if (resultSet.next()) {
    
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
    
                response.getWriter().write(responseJsonObject.toString());
            } else {
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                
                query = "SELECT * FROM creditcards c WHERE c.id = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, creditcard);
                resultSet = statement.executeQuery();
                
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
