package mainPage;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        // get the parameter in GET
        String query = request.getParameter("query");
        String limit = request.getParameter("numOfMovies");
        int offset = (Integer.parseInt(request.getParameter("page")) - 1) * Integer.parseInt(limit);
        String sort = request.getParameter("sortby");
        int nextOffset = Integer.parseInt(request.getParameter("page")) * Integer.parseInt(limit);
        
        
        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("<script type=\"text/javascript\" src=\"index.js\"></script>");
//        out.println("<script type=\"text/javascript\" src=\"movielist.js\"></script>");
        out.println("</head>");
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		PreparedStatement statement = null;
        		// prepare query
        		String mqlQuery = "SELECT m.id, m.title, m.year, m.director, m.genres, m.stars, r.rating FROM (SELECT t2.id, t2.title, "
        		        + "t2.year, t2.director, GROUP_CONCAT(DISTINCT ' ', g.name) AS genres, t2.stars FROM genres g, genres_in_movies gm, "
        		        + "(SELECT * FROM (SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT ' ', s.name) AS stars "
        		        + "FROM movies m, stars s, stars_in_movies sm WHERE m.id = sm.movieId AND s.id = sm.starId GROUP BY m.id) t1 WHERE "
        		        + "t1.title LIKE ? OR t1.year = ? OR t1.director LIKE ? OR t1.stars LIKE ?) t2 "
        		        + "WHERE g.id = gm.genreId AND gm.movieId = t2.id GROUP BY t2.id) m LEFT JOIN ratings r ON m.id = r.movieId";
        		if (!sort.equals("null")) {
        		    if (sort.substring(0, 5).equals("title") && sort.substring(5, sort.length()).equals("asc")) {
        		        mqlQuery += " ORDER BY m.title ASC LIMIT ? OFFSET ?";
        		    } else if (sort.substring(0, 5).equals("title") && sort.substring(5, sort.length()).equals("desc")) {
        		        mqlQuery += " ORDER BY m.title DESC LIMIT ? OFFSET ?";
        		    } else if (sort.substring(0, 6).equals("rating") && sort.substring(6, sort.length()).equals("asc")) {
        		        mqlQuery += " ORDER BY r.rating ASC LIMIT ? OFFSET ?";
        		    } else {
        		        mqlQuery += " ORDER BY r.rating DESC LIMIT ? OFFSET ?";
        		    }
        		} else {
        		    mqlQuery += " LIMIT ? OFFSET ?";
        		}
        		
        		statement = connection.prepareStatement(mqlQuery);
        		statement.setString(1, "%" + query + "%");
        		statement.setString(2, query);
        		statement.setString(3, "%" + query + "%");
        		statement.setString(4, "%" + query + "%");
        		statement.setInt(5, Integer.parseInt(limit));
        		statement.setInt(6, offset);
        		// execute query
        		ResultSet resultSet = statement.executeQuery();

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
        		out.print("<a href='search?query=" + query + "&numOfMovies=" + limit + "&page=1&sortby=titledesc'>Title<span class=\"glyphicon glyphicon-triangle-bottom\"></span></a>");
        		out.print(" | ");
        		out.print("<a href='search?query=" + query + "&numOfMovies=" + limit + "&page=1&sortby=titleasc'>Title<span class=\"glyphicon glyphicon-triangle-top\"></span></a>");
        		out.print(" | ");
        		out.print("<a href='search?query=" + query + "&numOfMovies=" + limit + "&page=1&sortby=ratingdesc'>Rating<span class=\"glyphicon glyphicon-triangle-bottom\"></span></a>");
        		out.print(" | ");
        		out.println("<a href='search?query=" + query + "&numOfMovies=" + limit + "&page=1&sortby=ratingasc'>Rating<span class=\"glyphicon glyphicon-triangle-top\"></span></a></p>");
        		
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
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String movieID = resultSet.getString("id");
        			String title = resultSet.getString("title");
        			int year = resultSet.getInt("year");
        			String director = resultSet.getString("director");
        			String genres = resultSet.getString("genres");
        			String stars = resultSet.getString("stars");
        			Float rating = resultSet.getFloat("rating");
        			
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
        			String movie = movieID + "::" + title;
//        			if (movie.contains("&")) {
//                    	movie = movie.replace("&", "@#");
//                    }
        			out.println("<td>" + "<button class=\"btn btn-info\" id=\"addTo\" onclick=\"addToCart('" + movie + "')\">Add to Cart</button></td>");

        			out.println("</tr>");
        		}
        		out.println("</tbody>");
        		out.println("</div>");
        		out.println("</table>");
        		
        		statement.setInt(6, nextOffset);
        		ResultSet nextPage = statement.executeQuery();
        		
        		out.println("<div class=\"box\">");
        		if (offset != 0) {
        		    out.println("<button type=\"button\" class=\"btn btn-info\" id=\"prev\">Prev</button>");
        		}
        		if (nextPage.next()) {
        		    out.println("<button type=\"button\" class=\"btn btn-info\" id=\"next\">Next</button>");
        		}
        		
                out.println("<button type=\"button\" class=\"btn btn-info\" id=\"back\">Home</button></div>");
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