package mainPage;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

// this annotation maps this Java Servlet Class to a URL
@WebServlet(urlPatterns = "/movies")
public class SingleMoviePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SingleMoviePage() {
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
        String movieName = request.getParameter("movie");
        System.out.println("First " + movieName);
        if (movieName != null && movieName.contains("@@")) {
        	movieName = movieName.replace("@@", "&");
        }
        if (movieName != null && movieName.contains("**")) {
        	movieName = movieName.replace("**", "+");
        }
        System.out.println(movieName);
        
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
			
        		// prepare query
        		String query = "SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT ' ', g.name) AS genres, GROUP_CONCAT(DISTINCT ' ', s.name) AS stars "
        		        + " from genres g, genres_in_movies gm, movies m, stars s, stars_in_movies sm "
                        + "WHERE m.title = ? AND g.id = gm.genreId AND m.id = sm.movieId AND s.id = sm.starId AND m.id = gm.movieId "
                        + "GROUP BY m.id";
        		
        		PreparedStatement statement = dbcon.prepareStatement(query);
        		statement.setString(1, movieName);
        		
        		// execute query
        		ResultSet resultSet = statement.executeQuery();

        		out.println("<body class=\"loginBackgroundColor\">");
        		out.println("<h1> Movie Info: "+ movieName +"</h1>");
        		out.println("<div>");
        		        		
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
        		out.println("<th></th>");

        		out.println("</thead>");
        		out.println("</div>");
        		out.println("<div>");
        		out.println("<tbody>");
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String title = resultSet.getString("title");
        			String year = resultSet.getString("year");
        			String director = resultSet.getString("director");
        			String genres = resultSet.getString("genres");
        			String stars = resultSet.getString("stars");
        			String movieID = resultSet.getString("id");
        			
        			out.println("<tr>");
        			out.println("<td>" + title + "</td>");
        			out.println("<td>" + year + "</td>");
        			out.println("<td>" + director + "</td>");

        			// For list of genres
        			out.print("<td>");
        			String[] listOfGenres = genres.split(",");
        			StringBuilder sb_genre = new StringBuilder();
        			for (String s : listOfGenres) {
        				sb_genre.append("<a href='browse?genre=" + s.trim().toLowerCase() + "&numOfMovies=25&page=1&sortby=null'>"+ s.trim() + "</a>");
        				sb_genre.append(", ");
        			}
        			sb_genre.deleteCharAt(sb_genre.length() - 1);
        			sb_genre.deleteCharAt(sb_genre.length() - 1);
        			out.print(sb_genre.toString());
        			out.println("</td>");
        			
        			// For list of stars
        			out.print("<td>");
        			String[] listOfStars = stars.split(",");
        			StringBuilder sb_star = new StringBuilder();
        			for (String s : listOfStars) {
        				sb_star.append("<a href='stars?star=" + s.trim().toLowerCase() + "'>"+ s.trim() + "</a>");
        				sb_star.append(", ");
        			}
        			sb_star.deleteCharAt(sb_star.length() - 1);
        			sb_star.deleteCharAt(sb_star.length() - 1);
        			out.print(sb_star.toString());
        			out.println("</td>");
        			

        			String movie = movieID + "::" + title;
        			if (movie.contains("&")) {
                    	movie = movie.replace("&", "@@");
                    }
        			if (movie.contains("+")) {
                    	movie = movie.replace("+", "**");
                    }
        			out.println("<td>" + "<button class=\"btn btn-info\" id=\"addTo\" onclick=\"addToCart('" + movie + "')\">Add to Cart</button></td>");
        			out.println("</tr>");
        		}
        		
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