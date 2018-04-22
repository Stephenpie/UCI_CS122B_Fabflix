package mainPage;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// this annotation maps this Java Servlet Class to a URL
@WebServlet(urlPatterns = "/movies")
public class SingleMoviePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SingleMoviePage() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password

        String loginUser = "user1";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb1";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        // get the parameter in GET
        String movieName = request.getParameter("movie");

        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script type=\"text/javascript\" src=\"index.js\"></script>");
        out.println("</head>");
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String Query = "SELECT m.title, m.year, m.director, GROUP_CONCAT(DISTINCT ' ', g.name) AS genres, GROUP_CONCAT(DISTINCT ' ', s.name) AS stars "
        		        + " from genres g, genres_in_movies gm, movies m, stars s, stars_in_movies sm "
                        + "WHERE m.title = '" + movieName + "' AND g.id = gm.genreId AND m.id = sm.movieId AND s.id = sm.starId AND m.id = gm.movieId "
                        + "GROUP BY m.id";
        		
        		// execute query
        		ResultSet resultSet = statement.executeQuery(Query);

        		out.println("<body>");
        		out.println("<div class=\"pageBackground\">");
        		out.println("<h1><center>Movie List</center></h1>");
        		
        		out.println("<select><option value='10'>10</option><option value='15'>15</option><option value='20'>20</option><option value='25'>25</option></select>");
        		
        		out.println("<div class=\"container\">");
        		out.println("<table id=\"resulttable\" class=\"table table-bordered table-hover table-striped\">");
        		
        		// add table header row
        		out.println("<thead>");
        		
        		out.println("<th>Title</th>");
        		out.println("<th>Year</th>");
        		out.println("<th>Director</th>");
        		out.println("<th>List of genres</th>");
        		out.println("<th>List of stars</th>");

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
        			
        			out.println("<tr>");
        			out.println("<td>" + title + "</td>");
        			out.println("<td>" + year + "</td>");
        			out.println("<td>" + director + "</td>");

        			// For list of genres
        			out.print("<td>");
        			String[] listOfGenres = genres.split(",");
        			StringBuilder sb_genre = new StringBuilder();
        			for (String s : listOfGenres) {
        				sb_genre.append("<a href='search?query=" + s.trim() + "'>"+ s.trim() + "</a>");
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
        				sb_star.append("<a href='stars?star=" + s.trim() + "'>"+ s.trim() + "</a>");
        				sb_star.append(", ");
        			}
        			sb_star.deleteCharAt(sb_star.length() - 1);
        			sb_star.deleteCharAt(sb_star.length() - 1);
        			out.print(sb_star.toString());
        			out.println("</td>");
        			
        			out.println("</tr>");
        		}
        		out.println("</tbody>");
        		out.println("</div>");
        		out.println("</table>");	
        		
        		out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"prev\">Prev</button></div>");
        		out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"back\">Go Back</button></div>");
        		out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"next\">Next</button></div>");
        		out.println("<script src=\"movielist.js\"></script>");
        		out.println("</body>");
        		
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