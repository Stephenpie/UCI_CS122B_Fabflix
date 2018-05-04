package checkout;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

// Declaring a WebServlet called CartServlet, which maps to url "/cart"
@WebServlet(name = "CartServlet", urlPatterns = "/cart")

public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(); // Get a instance of current session on the request
        HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("cart"); // Retrieve data named "previousItems" from session

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems ArrayList for the user
        if (cart == null) {
            cart = new HashMap<String, Integer>();
            session.setAttribute("cart", cart); // Add the newly created ArrayList to session, so that it could be retrieved next time

        }

        String act = request.getParameter("act");
        String item = request.getParameter("item");
        if (item != null && item.contains("@#")) {
        	item = item.replace("@#", "&");
        }
        String qty = request.getParameter("qty");

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        String title = "Shopping Cart";
        String docType =
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n";
        out.println(String.format("%s<html>\n<head><title>%s</title>", docType, title));
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"movielist.js\"></script>");

        out.println(String.format("</head><body class=\"loginBackgroundColor\">\n<h1>%s</h1>", title));

        // In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we lock the ArrayList while updating
        synchronized (cart) {
            System.out.println(cart.size());
            if (act != null && act.equals("add") && qty != null) {
                if (item != null) {
                    int count = cart.getOrDefault(item, Integer.parseInt(qty));
                    cart.put(item, count); // Add the new item to the previousItems ArrayList
                }
            } else if (act != null && act.equals("add") && qty == null) {
                int count = cart.getOrDefault(item, 0);
                System.out.println(count);
                cart.put(item, ++count);
            } else if (act != null && act.equals("update") && qty != null) {
                int count = Integer.parseInt(qty);
                cart.put(item, count);
            } else if (act != null && act.equals("delete")) {
            	System.out.println(item);
            	cart.get(item);
                cart.remove(item);
            }

            // Display the current previousItems ArrayList
            if (cart.size() == 0) {
                out.println("<br><br><div id=\"cart\"><center>Empty cart</center></div><br><br>");
            } else {
                out.println("<div class=\"container\">");
                out.println("<table id=\"resulttable\" class=\"table table-bordered table-hover table-striped\">");
                
                // add table header row
                out.println("<thead>");
                
                out.println("<th>Movie Title</th>");
                out.println("<th>Price</th>");
                out.println("<th>Qty</th>");
                out.println("<th></th>");
                out.println("</thead>");
                out.println("</div>");
                out.println("<div>");
                out.println("<tbody>");
                
                int id = 0;
                for (String movie : cart.keySet()) {
                    out.println("<tr>");
                    String movieTitle = movie.split("::")[1];
                    System.out.println(movieTitle);
                    out.print("<td>" + movieTitle + "</td>");
                    out.print("<td>FREE</td>");
                    out.print("<td><input type=\"text\" id=\"qty" + id + "\" value=\""+ cart.get(movie) +"\"></td>");
                    
                    // javascript needs us add ' when using variable
                    out.print("<td><button class=\"btn btn-info\" id=\"update\" onclick=\"updateItem('" + movie + "', 'qty" + id + "')\">Update</button>");
                    out.print("<button class=\"btn btn-info\" id=\"delete\" onclick=\"deleteItem('" + movie + "')\">Delete</button></td>");

                    out.println("</tr>");
                    id++;
                }
                out.println("</tbody>");
                out.println("</div>");
                out.println("</table>");
                out.println("</div>");
            }
        }
        
        if (cart.size() != 0) {
        	out.println("<div class=\"box\"><center><button type=\"button\" class=\"btn btn-info\" id=\"checkout\" onclick=\"checkout()\">Checkout</button><button type=\"button\" class=\"btn btn-info\" id=\"back\">Home</button></center></div>");
        } else {
        	out.println("<center><button type=\"button\" class=\"btn btn-info\" id=\"back\">Home</button></center>");
        }
        
        out.println("<script type=\"text/javascript\" src=\"movielist.js\"></script>");
        out.println("</body></html>");
    }
}