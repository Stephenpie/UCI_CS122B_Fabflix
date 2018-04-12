

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// this annotation maps this Java Servlet Class to a URL
@WebServlet("/movielist")
public class StarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public StarServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
        String loginUser = "root";
        String loginPasswd = "fuko_yui94";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");
        
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String query = "SELECT * from genres, genres_in_movies, movies, ratings, stars, stars_in_movies "
        		        + "WHERE genres.id = genres_in_movies.genreId AND movies.id = stars_in_movies.movieId AND "
        		        + "stars.id = stars_in_movies.starId AND ratings.movieId = movies.id AND "
        		        + "movies.id = genres_in_movies.movieId limit 20";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);

        		out.println("<body>");
        		out.println("<h1>MovieDB Stars</h1>");
        		
        		out.println("<table border>");
        		
        		// add table header row
        		out.println("<tr>");
        		out.println("<td>title</td>");
        		out.println("<td>year</td>");
        		out.println("<td>director</td>");
        		out.println("<td>list of genres</td>");
        		out.println("<td>list of stars</td>");
        		out.println("<td>rating</td>");
        		out.println("</tr>");
        		
        		// add a row for every star result
        		HashMap<String, HashSet<String>> genreMap = new HashMap<>();
        		HashMap<String, HashSet<String>> starMap = new HashMap<>();
        		while (resultSet.next()) {
        			// get a star from result set
        			String title = resultSet.getString("title");
        			int year = resultSet.getInt("year");
        			String director = resultSet.getString("director");
//        			ArrayList<String> listOfGenres = new ArrayList<>();
        			String genre = resultSet.getString("genres.name");
        			if (genreMap.containsKey(title)) {
        			    genreMap.get(title).add(genre);
        			} else {
        			    HashSet<String> genreSet = new HashSet<>();
        			    genreSet.add(genre);
        			    genreMap.put(title, genreSet);
        			}
        			
        			String star = resultSet.getString("stars.name");
                    if (starMap.containsKey(title)) {
                        starMap.get(title).add(star);
                    } else {
                        HashSet<String> starSet = new HashSet<>();
                        starSet.add(star);
                        starMap.put(title, starSet);
                    }

        			Float rating = resultSet.getFloat("rating");
        			
        			out.println("<tr>");
        			out.println("<td>" + title + "</td>");
        			out.println("<td>" + year + "</td>");
        			out.println("<td>" + director + "</td>");
        			out.println("<td>" + genreMap.get(genre) + "</td>");
        			out.println("<td>" + starMap.get(star) + "</td>");
        			out.println("<td>" + rating + "</td>");
        			out.println("</tr>");
        		}
        		
        		out.println("</table>");
        		
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
