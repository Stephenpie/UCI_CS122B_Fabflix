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
@WebServlet(urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SearchServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password

        String loginUser = "root";
        String loginPasswd = "tangwang";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 
        
//        response.setContentType("UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        // get the parameter in GET
        String query = request.getParameter("query");
        String limit = request.getParameter("numOfMovies");
        String offset = request.getParameter("offset");
        
        if (limit == null) {
        	limit = "25";
        }
        if (offset == null) {
        	offset = "0";
        }
        
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
        		String Query = "SELECT t2.title, t2.year, t2.director, GROUP_CONCAT(DISTINCT ' ', g.name) AS genres, t2.stars, r.rating FROM ratings r, genres g, genres_in_movies gm, "
        				+ "(SELECT * FROM (SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT ' ', s.name) AS stars"
                        + " FROM movies m, stars s, stars_in_movies sm WHERE m.id = sm.movieId AND s.id = sm.starId "
                        + "GROUP BY m.id) t1 WHERE t1.title LIKE '%" + query + "%' OR t1.year LIKE '" + query + "' OR t1.director LIKE '%" + query + "%' OR t1.stars LIKE '%" + query + "%') t2 "
                      	+ "WHERE r.movieId = t2.id AND g.id = gm.genreId AND gm.movieId = t2.id GROUP BY t2.id, r.rating"
                      	+ " LIMIT " + limit + " OFFSET " + offset;
        		
        		// execute query
        		ResultSet resultSet = statement.executeQuery(Query);

        		out.println("<body>");
        		out.println("<div class=\"pageBackground\">");
        		out.println("<h1><center>Movie List</center></h1>");
        		
        		/*
        		out.println("<select onchange=\"handleShow(" + query + ", this);\"" + ">"
        					+ "<option value=\"empty\">Select a num</option>"
        					+ "<option value=\"25\">25</option>"
        					+ "<option value=\"20\">20</option>"
        					+ "<option value=\"15\">15</option>"
        					+ "<option value=\"10\">10</option>" 
        					+ "</select>");
        		*/
        		out.print("<p>Result per page: ");
        		out.print("<a href='search?query=" + query + "&numOfMovies=25'>" + "25</a>");
                out.print(" | ");
        		out.print("<a href='search?query=" + query + "&numOfMovies=20'>" + "20</a>");
                out.print(" | ");
        		out.print("<a href='search?query=" + query + "&numOfMovies=15'>" + "15</a>");
                out.print(" | ");
        		out.print("<a href='search?query=" + query + "&numOfMovies=10'>" + "10</a>");
        		out.println("</p>");
        		
        		out.println("<div class=\"container\">");
        		out.println("<table id=\"resulttable\" class=\"table table-bordered table-hover table-striped\">");
        		
        		// add table header row
        		out.println("<thead>");
        		
        		out.println("<th>Title<span onclick=\"sortTable(0, 0)\" class=\"glyphicon glyphicon-triangle-bottom\"></span><span onclick=\"sortTable(0, 1)\" class=\"glyphicon glyphicon-triangle-top\"></span></th>");
        		
        		out.println("<th>Year</th>");
        		out.println("<th>Director</th>");
        		out.println("<th>List of genres</th>");
        		out.println("<th>List of stars</th>");
        		out.println("<th>Rating<span onclick=\"sortTable(5, 0)\" class=\"glyphicon glyphicon-triangle-bottom\"></span><span onclick=\"sortTable(5, 1)\" class=\"glyphicon glyphicon-triangle-top\"></span></th>");
        		//out.println("</tr>");
        		
        		out.println("</thead>");
        		out.println("</div>");
        		out.println("<div>");
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
        			out.println("<td>" + "<a href='movies?movie=" + title.trim() + "'>" + title.trim() + "</td>");
        			out.println("<td>" + year + "</td>");
        			out.println("<td>" + director + "</td>");
        			out.println("<td>" + genres + "</td>");

        			out.print("<td>");
        			String[] listOfStars = stars.split(",");
        			StringBuilder sb = new StringBuilder();
        			for (String s : listOfStars) {
        				sb.append("<a href='stars?star=" + s.trim() + "'>"+ s.trim() + "</a>");
        				sb.append(", ");
        			}
        			sb.deleteCharAt(sb.length() - 1);
        			sb.deleteCharAt(sb.length() - 1);
        			out.print(sb.toString());
        			out.println("</td>");
        			
        			out.println("<td>" + rating + "</td>");
        			out.println("</tr>");
        		}
        		out.println("</tbody>");
        		out.println("</div>");
        		out.println("</table>");
        		
        		out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"prev\">Prev</button></div>");
        		out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"next\">Next</button></div>");
        		out.println("<div class=\"box\"><button type=\"button\" class=\"btn btn-info\" id=\"back\">Go Back</button></div>");
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