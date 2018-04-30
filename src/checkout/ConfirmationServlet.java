package checkout;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import login.User;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/confirmation")
public class ConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
                        
            String userID = ((User) session.getAttribute("user")).getUserID();
            
            out.println("<body>");
            out.println("<h2 class=\"text-center\">Confirmation</h2>");
            out.println("<div class=\"container\">");
            out.println("<table id=\"resulttable\" class=\"table table-bordered table-hover table-striped\">");
            
            // add table header row
            out.println("<thead>");
            
            out.println("<th>Sale ID</th>");
            out.println("<th>Movie Title</th>");
            out.println("<th>Price</th>");
            out.println("<th>Qty</th>");
            out.println("</thead>");
            out.println("</div>");
            out.println("<div>");
            out.println("<tbody>");
        
            for (String movie : cart.keySet()) {
                String movieID = movie.split(":")[0];
                String movieTitle = movie.split(":")[1];
                for (int i = 0; i < cart.get(movie); i++) {
                    String query = "INSERT INTO sales (customerId, movieId, saleDate) VALUES('" + userID + "', '" + movieID + "', CURDATE());";
                    System.out.println(query);
                    int result = statement.executeUpdate(query);
                }
                out.println("<tr>");
                out.print("<td>" + movieTitle + "</td>");
                out.print("<td>FREE</td>");
                out.print("<td>" + cart.get(movie) + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody>");
            out.println("</div>");
            out.println("</table>");
            out.println("</div>");
            
            out.println("<button type=\"button\" class=\"btn btn-info\" id=\"back\">Home</button></div>");
            out.println("<script type=\"text/javascript\" src=\"movielist.js\"></script>");
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
