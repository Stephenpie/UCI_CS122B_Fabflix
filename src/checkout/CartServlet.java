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
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(); // Get a instance of current session on the request
        HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("cart"); // Retrieve data named "previousItems" from session

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems ArrayList for the user
        if (cart == null) {
            cart = new HashMap<String, Integer>();
            session.setAttribute("cart", cart); // Add the newly created ArrayList to session, so that it could be retrieved next time

        }

//        String newItem = request.getParameter("add"); // Get parameter that sent by GET request url
//        String prevItem = request.getParameter("update");
        String act = request.getParameter("act");
        String item = request.getParameter("item");
        String qty = request.getParameter("qty");
//        if (qty == null) {
//            qty = "1";
//        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        String title = "Shopping Cart";
        String docType =
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n";
        out.println(String.format("%s<html>\n<head><title>%s</title>", docType, title));
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script type=\"text/javascript\" src=\"movielist.js\"></script></head>");
        out.println(String.format("<body bgcolor=\"#FDF5E6\">\n<h1>%s</h1>", title));
        //out.println("<script type=\"text/javascript\" src=\"movielist.js\"></script>");

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
                cart.remove(item);
            }

            // Display the current previousItems ArrayList
            if (cart.size() == 0) {
                out.println("<i>Empty cart</i>");
            } else {
                out.println("<div class=\"container\">");
                out.println("<table id=\"resulttable\" class=\"table table-bordered table-hover table-striped\">");
                
                // add table header row
                out.println("<thead>");
                
                out.println("<th>Movie Title</th>");
                out.println("<th>Price</th>");
                out.println("<th>Qty</th>");
                out.println("</thead>");
                out.println("</div>");
                out.println("<div>");
                out.println("<tbody>");
                
                int id = 0;
                for (String movieTitle : cart.keySet()) {
                    out.println("<tr>");
                    out.print("<td>" + movieTitle + "</td>");
                    out.print("<td>FREE</td>");
                    out.print("<td><input type=\"text\" id=\"qty" + id + "\" value=\""+ cart.get(movieTitle) +"\">");
                    
                    // javascript needs us add ' when using variable
                    out.print("<button class=\"btn btn-info\" id=\"update\" onclick=\"updateItem('" + movieTitle + "', 'qty" + id + "')\">Update</button>");
                    out.print("<button class=\"btn btn-info\" id=\"delete\" onclick=\"deleteItem('" + movieTitle + "')\">Delete</button></td>");

                    out.println("</tr>");
                    id++;
                }
            }
        }
        out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"checkout\" onclick=\"checkout()\">Checkout</button></div>");
//        out.println("<script src=\"movielist.js\"></script>");
//        } else {
//            out.println("<div class=\"col-md-4 col-md-offset-4\">");
//            out.println("<h2 class=\"text-center\">Checkout</h2>");
//            out.println("<form id=\"checkout_form\" method=\"post\" action=\"#\">");
//            out.println("<label><b>First name</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter first name\" name=\"firstname\">");
//            out.println("<br><label><b>Last name</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter last name\" name=\"lastname\">");
//            out.println("<br><label><b>Password</b></label><input class=\"form-control\" type=\"number\" placeholder=\"Enter credit card\" name=\"creditcard\">");
//            out.println("<br><label><b>Expiration Date</b></label><input class=\"form-control\" type=\"date\" placeholder=\"Expiration date\" name=\"expiration\">");
//            out.println("<br><input class=\"btn btn-info\" type=\"submit\" value=\"Submit Order\"></form></div>");
//        }
        out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"back\">Go Back</button></div>");
        out.println("<script src=\"movielist.js\"></script>");
        // This Line is important!!!
//        out.println("<script>function func(movieTitle, qtyId) {var qty = document.getElementById(qtyId).value; window.location.href = \"cart?act=update&item=\" + movieTitle + \"&qty=\" + qty;}</script>");
        
        out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>");
        out.println("</body></html>");
    }
}