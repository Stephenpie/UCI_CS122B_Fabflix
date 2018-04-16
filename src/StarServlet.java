

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
@WebServlet(urlPatterns = "/movielist")
public class StarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public StarServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password

        String loginUser = "root";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("</head>");
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String query = "SELECT m.title, m.year, m.director, GROUP_CONCAT(DISTINCT ' ', g.name) AS genres, GROUP_CONCAT(DISTINCT ' ', s.name) AS stars, r.rating "
        		        + " from genres g, genres_in_movies gm, movies m, ratings r, stars s, stars_in_movies sm "
                      + "WHERE g.id = gm.genreId AND m.id = sm.movieId AND s.id = sm.starId AND r.movieId = m.id AND m.id = gm.movieId "
                      + "GROUP BY m.id, r.rating ORDER BY r.rating DESC limit 20";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);

        		out.println("<body>");
        		out.println("<div class=\"pageBackground\">");
        		out.println("<h1>Movie List</h1>");
        		
        		out.println("<div class=\"container\">");
        		out.println("<table class=\"table table-bordered table-hover table-striped\">");
        		
        		// add table header row
        		out.println("<thead>");
        		
        		out.println("<tr>");
        		out.println("<th>title</th>");
        		out.println("<th>year</th>");
        		out.println("<th>director</th>");
        		out.println("<th>list of genres</th>");
        		out.println("<th>list of stars</th>");
        		out.println("<th>rating</th>");
        		out.println("</tr>");
        		
        		out.println("</thead>");
        		out.println("</div>");
        		out.println("<tbody>");
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String title = resultSet.getString("title");
        			int year = resultSet.getInt("year");
        			String director = resultSet.getString("director");
        			String genres = resultSet.getString("genres");
        			String stars = resultSet.getString("stars");
        			Float rating = resultSet.getFloat("rating");
        			
        			out.println("<tr>");
        			out.println("<td>" + title + "</td>");
        			out.println("<td>" + year + "</td>");
        			out.println("<td>" + director + "</td>");
        			out.println("<td>" + genres + "</td>");
        			out.println("<td>" + stars + "</td>");
        			out.println("<td>" + rating + "</td>");
        			out.println("</tr>");
        		}
        		out.println("<tbody>");
        		out.println("</table>");
        		out.println("</div>");
        		
        		out.println("<div class=\"box\"><button id=\"back\" class=\"btn btn-info\">Go Back</button></div>");
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
