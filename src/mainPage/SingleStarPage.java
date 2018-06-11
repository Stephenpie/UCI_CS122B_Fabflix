package mainPage;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

// this annotation maps this Java Servlet Class to a URL
@WebServlet(urlPatterns = "/stars")
public class SingleStarPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SingleStarPage() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
		
        // set response mime type
        response.setContentType("text/html"); 
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        // get the parameter in GET
        String starName = request.getParameter("star");

        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script type=\"text/javascript\" src=\"index.js\"></script>");
        out.println("</head>");
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbcon = ds.getConnection();
            
        		// declare statement
        		// prepare query
        		String query = "SELECT s.name, s.birthYear, m.title, m.id "
                        + "FROM stars s, stars_in_movies sm, movies m "
                        + "WHERE s.name = ? AND s.id = sm.starId AND sm.movieId = m.id";
        		
        		// execute query
        		PreparedStatement statement = dbcon.prepareStatement(query);
        		statement.setString(1, starName);
        		ResultSet resultSet = statement.executeQuery();

        		out.println("<body class=\"loginBackgroundColor\">");
        		out.println("<h1> Star Profile: "+ starName +"</h1>");
        		out.println("<div>");
        		        		
        		out.println("<div class=\"container\">");
        		out.println("<table id=\"resulttable\" class=\"table table-bordered table-hover table-striped\">");
        		
        		// For checkout out button
    			out.println("<button class=\"btn btn-info\" id=\"addTo\" onclick=\"viewCart()\">Go to Cart</button></td>");
        		
        		// add table header row
        		out.println("<thead>");
        		
        		out.println("<th>Name</th>");
        		out.println("<th>Year of Birth</th>");
        		out.println("<th>List of Movies</th>");
        		
        		out.println("</thead>");
        		out.println("</div>");
        		out.println("<div>");
        		out.println("<tbody>");
        		// add a row for every star result
        		ArrayList<String> movies = new ArrayList<>();
                String name = "";
                String yearOfBirth = "";
                String movieTitle = "";
        		while (resultSet.next()) {
        			// get a star from result set
        			name = resultSet.getString("name");
        			yearOfBirth = resultSet.getString("birthYear");
        			movieTitle = resultSet.getString("title");
        			
        			movies.add(movieTitle);
        			
//        			out.println("<tr>");
//        			out.println("<td>" + name + "</td>");
//        			out.println("<td>" + yearOfBirth + "</td>");
//
//        			// For list of movies
//        			out.print("<td>");
//        			String[] listOfMovies = moviesTitle.split(",");
//        			StringBuilder sb = new StringBuilder();
//        			for (String s : listOfMovies) {
//        				String movieTitle = s;
//        				if (s.contains("&")) {
//        					s = s.replace("&", "@@");
//        				}
//        				sb.append("<a href='movies?movie=" + s.trim() + "'>"+ movieTitle.trim() + "</a>");
//        				sb.append(", ");
//        			}
//        			sb.deleteCharAt(sb.length() - 1);
//        			sb.deleteCharAt(sb.length() - 1);
//        			out.print(sb.toString());
//        			out.println("</td>");
//        			
//        			out.println("</tr>");
        		}
                out.println("<tr>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + yearOfBirth + "</td>");
                out.println("<td>");
                
                System.out.println("MOVIE TITLE : " + movieTitle);
                
            	String moviesOfStar = "";
                for (String movie : movies) {
                	String movieTitle1 = movie;
    				if (movie.contains("&")) {
    					movie = movie.replace("&", "@@");
    				}
    				if (movie.contains("+")) {
    					movie = movie.replace("+", "**");
    				}
                    moviesOfStar += "<a href='movies?movie=" + movie.trim() + "'>"+ movieTitle1.trim() + "</a>, ";
                }
                if (!moviesOfStar.equals(""))
                	out.println(moviesOfStar.substring(0, moviesOfStar.length()-2));
                
                
                out.println("</td>");
                out.println("</tr>");

        		out.println("</tbody>");
        		out.println("</div>");
        		out.println("</table>");

        		out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"back\">Home</button></div>");
        		out.println("<script src=\"movielist.js\"></script>");
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		dbcon.close();
        		
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