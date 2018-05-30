package mainPage;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// this annotation maps this Java Servlet Class to a URL
@WebServlet(urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SearchServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        // set response mime type
        response.setContentType("text/html"); 
        
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        // get the parameter in GET
        String query = request.getParameter("query");
        int limit = Integer.parseInt(request.getParameter("numOfMovies"));
        int offset = (Integer.parseInt(request.getParameter("page")) - 1) * limit;
        String sort = request.getParameter("sortby");
        
        
        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script type=\"text/javascript\" src=\"index.js\"></script>");
        out.println("</head>");
        try {           
            out.println("<body class=\"loginBackgroundColor\">");
            out.println("<div>");
            out.println("<h1>Movie List</h1>");

            out.print("<p>Result per page: ");
            out.print("<a href='search?query=" + query + "&numOfMovies=25&page=1&sortby=" + sort + "'>" + "25</a>");
            out.print(" | ");
            out.print("<a href='search?query=" + query + "&numOfMovies=20&page=1&sortby=" + sort + "'>" + "20</a>");
            out.print(" | ");
            out.print("<a href='search?query=" + query + "&numOfMovies=15&page=1&sortby=" + sort + "'>" + "15</a>");
            out.print(" | ");
            out.print("<a href='search?query=" + query + "&numOfMovies=10&page=1&sortby=" + sort + "'>" + "10</a>");
            out.println("</p>");

            out.print("<p>Sort by: ");
            out.print("<a href='search?query=" + query + "&numOfMovies=" + limit
                    + "&page=1&sortby=titledesc'>Title<span class=\"glyphicon glyphicon-triangle-bottom\"></span></a>");
            out.print(" | ");
            out.print("<a href='search?query=" + query + "&numOfMovies=" + limit
                    + "&page=1&sortby=titleasc'>Title<span class=\"glyphicon glyphicon-triangle-top\"></span></a>");
            out.print(" | ");
            out.print("<a href='search?query=" + query + "&numOfMovies=" + limit
                    + "&page=1&sortby=ratingdesc'>Rating<span class=\"glyphicon glyphicon-triangle-bottom\"></span></a>");
            out.print(" | ");
            out.println("<a href='search?query=" + query + "&numOfMovies=" + limit
                    + "&page=1&sortby=ratingasc'>Rating<span class=\"glyphicon glyphicon-triangle-top\"></span></a></p>");

            out.println("<div class=\"container\">");
            out.println("<table id=\"resulttable\" class=\"table table-bordered table-hover table-striped\">");

            // For checkout out button
            out.println("<button class=\"btn btn-info\" id=\"addTo\" onclick=\"viewCart()\">Go to Cart</button></td>");

            // add table header row
            out.println("<thead>");

            out.println("<th>Title</th>");
            out.println("<th>Year</th>");
            out.println("<th>Director</th>");
            out.println("<th>List of genres</th>");
            out.println("<th>List of stars</th>");
            out.println("<th>Rating</th>");
            out.println("<th></th>");

            out.println("</thead>");
            out.println("</div>");
            out.println("<div>"); // container
            out.println("<tbody>");
          
            JsonArray searchResults = SearchHelper.getSearchResult(query, sort, limit, offset, true);
            int i = 0;
            for (; i < searchResults.size() - 1; i++) {
                JsonObject movie = searchResults.get(i).getAsJsonObject();
                String id = movie.get("id").getAsString();
                String title = movie.get("title").getAsString();
                String year = movie.get("year").getAsString();
                String director = movie.get("director").getAsString();
                String genres = movie.get("genres").getAsString();
                String stars = movie.get("stars").getAsString();
                String rating = movie.get("rating").getAsString();
                
                String movieTitle = title;
                if (title.contains("&")) {
                    title = title.replace("&", "@@");
                }
                if (title.contains("+")) {
                    title = title.replace("+", "**");
                }
                
                out.println("<tr>");
                out.println("<td>" + "<a href='movies?movie=" + title.trim() + "'>" + movieTitle.trim() + "</td>");
                out.println("<td>" + year + "</td>");
                out.println("<td>" + director + "</td>");
                out.println("<td>" + genres + "</td>");
                out.print("<td>");
                out.print(stars + "</td>");          
                out.println("<td>" + rating + "</td>");
                
                String movieCart = id + "::" + title;
                out.println("<td>" + "<button class=\"btn btn-info\" id=\"addTo\" onclick=\"addToCart('" + movieCart
                        + "')\">Add to Cart</button></td>");
                out.println("</tr>");
            }
            
            out.println("</tbody>");
            out.println("</div>");
            out.println("</table>");

            out.println("<div class=\"box\">");
            if (offset != 0) {
                out.println("<button type=\"button\" class=\"btn btn-info\" id=\"prev\">Prev</button>");
            }
            if (searchResults.get(i).getAsJsonObject().get("next").getAsBoolean()) {
                out.println("<button type=\"button\" class=\"btn btn-info\" id=\"next\">Next</button>");
            }

            out.println("<button type=\"button\" class=\"btn btn-info\" id=\"back\">Home</button></div>");
            out.println("<script src=\"movielist.js\"></script>");
            out.println("</body>");
        		
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
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>"); 
        out.close();
	}
}