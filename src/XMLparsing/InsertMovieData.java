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
        System.out.println("finishing parsing actors file: " + (double) (System.currentTimeMillis() - start) / 1000);
        
        XMLmovieParser movieParser = new XMLmovieParser();
        movieParser.runParser();
        LinkedList<Movie> movies = movieParser.getMovies();
        HashSet<String> uniqueGenres = movieParser.getUniqueGenres();
        System.out.println("finishing parsing mains file: " + (double) (System.currentTimeMillis() - start) / 1000);
        
        XMLmoviestarParser movieStarParser = new XMLmoviestarParser();
        movieStarParser.runParser();
        HashMap<String, LinkedList<String>> movieStars = movieStarParser.getMoviesStars();
        System.out.println("finishing parsing casts file: " + (double) (System.currentTimeMillis() - start) / 1000);
        
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
			System.out.println("finish inserting stars: " + count + " - " + (double) (System.currentTimeMillis() - start) / 1000);
			
			// Insert new genres, genres_in_movies, and movies
			connection.setAutoCommit(false);
			query = "INSERT INTO genres (name) SELECT * FROM (SELECT ?) AS tmp WHERE NOT EXISTS (SELECT name FROM genres WHERE name = ?)";
			statement = connection.prepareStatement(query);
			for (String genre : uniqueGenres) {
				statement.setString(1, genre);
				statement.setString(2, genre);
				statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			System.out.println("finishing inserting new genres: " + uniqueGenres.size() + " - " + (double) (System.currentTimeMillis() - start) / 1000);
			
			// Insert new movies
			connection.setAutoCommit(false);
			query = "SELECT MAX(id) AS id FROM movies";
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
            String movieID = resultSet.getString("id");
            front = movieID.substring(0, 2);
            back = Integer.parseInt(movieID.substring(2, movieID.length()));
            
            query = "INSERT INTO movies SELECT * FROM (SELECT ? AS id, ? AS title, ? AS year, ? AS director) AS temp "
            		+ "WHERE NOT EXISTS (SELECT m.title, m.year, m.director FROM movies m WHERE m.title = ? AND m.year = ? AND m.director = ?) LIMIT 1";
            statement = connection.prepareStatement(query);
            
            String gQuery = "INSERT INTO genres_in_movies VALUES((SELECT id FROM genres WHERE name = ?), "
            		+ "(SELECT id FROM movies WHERE title = ? and year = ? AND director = ?))";
            PreparedStatement gStatement = connection.prepareStatement(gQuery);
            
            count = 0;
            for (Movie m : movies) {
            	movieID = front + (++back);
            	statement.setString(1, movieID);
            	statement.setString(2, m.getTitle());
            	statement.setInt(3, m.getYear());
            	statement.setString(4, m.getDirector());
            	statement.setString(5, m.getTitle());
            	statement.setInt(6, m.getYear());
            	statement.setString(7, m.getDirector());
            	count++;
            	for (String genre : m.getGenres()) {
            		gStatement.setString(1, genre);
            		gStatement.setString(2, m.getTitle());
            		gStatement.setInt(3, m.getYear());
            		gStatement.setString(4, m.getDirector());
            	}
            	
				statement.addBatch();
				gStatement.addBatch();
				if (count % batchSize == 0) {
					statement.executeBatch();
					gStatement.executeBatch();
				}
            }
			statement.executeBatch();
			gStatement.executeBatch();
			connection.commit();
			System.out.println("finish inserting movies: " + count + " - " + (double) (System.currentTimeMillis() - start) / 1000);
			
			// Insert stars_in_movies
			connection.setAutoCommit(false);
			String sQuery = "INSERT INTO stars SELECT * FROM (SELECT ?, ?, ?) AS temp WHERE NOT EXISTS (SELECT id FROM stars WHERE name = ?)";
			query = "INSERT INTO stars_in_movies(starId, movieId) SELECT * FROM (SELECT (SELECT id FROM stars "
					+ "WHERE name = ? LIMIT 1), (SELECT id FROM movies WHERE title = ? LIMIT 1)) AS tmp WHERE EXISTS (SELECT id FROM movies WHERE title = ? LIMIT 1)";
			PreparedStatement sStatement = connection.prepareStatement(sQuery);
			statement = connection.prepareStatement(query);
			
			count = 0;
			for (String title : movieStars.keySet()) {
				for (String star : movieStars.get(title)) {
					starID = front + (++back);
					sStatement.setString(1, starID);
					sStatement.setString(2, star);
					sStatement.setNull(3, Types.INTEGER);
					sStatement.setString(4, star);
					statement.setString(1, star);
					statement.setString(2, title);
					statement.setString(3, title);
					count++;
				}
				sStatement.addBatch();
				statement.addBatch();
				if (count % batchSize == 0) {
					sStatement.executeBatch();
					statement.executeBatch();
				}
			}
			sStatement.executeBatch();
			statement.executeBatch();
			connection.commit();
			System.out.println("finish inserting stars_in_movies: " + count + " - " + (double) (System.currentTimeMillis() - start) / 1000);
            
			
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
