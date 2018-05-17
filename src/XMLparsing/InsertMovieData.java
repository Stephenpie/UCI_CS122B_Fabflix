package XMLparsing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class InsertMovieData {
	
	private static final int batchSize = 1000;
	private static String query;
	private static PreparedStatement statement;
	private static ResultSet resultSet;
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
        XMLstarParser starParser = new XMLstarParser();
        starParser.runParser();
        LinkedList<Star> stars = starParser.getStars();
        
        XMLmovieParser movieParser = new XMLmovieParser();
        movieParser.runParser();
        LinkedList<Movie> movies = movieParser.getMovies();
        HashSet<String> uniqueGenres = movieParser.getUniqueGenres();
//        
//        XMLmoviestarParser movieStarParser = new XMLmoviestarParser();
//        movieStarParser.runParser();
//        HashMap<String, LinkedList<String>> movieStars = movieStarParser.getMoviesStars();
        
        String loginUser = "root";
        String loginPasswd = "tangwang";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        try {
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
			// create database connection
			Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			
			// Insert new stars
			connection.setAutoCommit(false);
			query = "SELECT MAX(id) AS id FROM stars";
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
            String starID = resultSet.getString("id");
            String front = starID.substring(0, 2);
            int back = Integer.parseInt(starID.substring(2, starID.length()));
			
            query = "INSERT INTO stars (id, name, birthYear) VALUES(?, ?, ?)";
            statement = connection.prepareStatement(query);
            int count = 0;
			for (Star s : stars) {
				starID = front + (++back);
				if (s.getBirthYear() == 0) {
					statement.setString(1, starID);
					statement.setString(2, s.getStarName());
					statement.setNull(3, Types.INTEGER);
				} else {
					statement.setString(1, starID);
					statement.setString(2, s.getStarName());
					statement.setInt(3, s.getBirthYear());
				}
				count++;
				statement.addBatch();
				if (count % batchSize == 0) {
					statement.executeBatch();
				}
			}

			statement.executeBatch();
			connection.commit();
			System.out.println("finish inserting stars: " + count);
			
			// Insert new genres, genres_in_movies, and movies
			connection.setAutoCommit(false);
			query = "INSERT INTO genres (name) VALUES (?)";
			statement = connection.prepareStatement(query);
			for (String genre : uniqueGenres) {
				statement.setString(1, genre);
				statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			System.out.println("finishing inserting new genres: " + uniqueGenres.size());
			
			// Insert new movies
			connection.setAutoCommit(false);
			query = "SELECT MAX(id) AS id FROM movies";
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
            String movieID = resultSet.getString("id");
            front = movieID.substring(0, 2);
            back = Integer.parseInt(starID.substring(2, starID.length()));
            
            for (Movie m : movies) {
            	
            }
			
	        resultSet.close();
	        statement.close();
            connection.close();
            long end = System.currentTimeMillis();
            System.out.println(end - start);
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
    		System.out.println(e.getMessage());
        }
    }
}
