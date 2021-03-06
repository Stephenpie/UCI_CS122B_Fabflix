package XMLparsing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class InsertMovieData {
	
	private static final int batchSize = 1000;
	private static String query;
	private static PreparedStatement statement;
	private static ResultSet resultSet;
	
	public static void main(String[] args) {
		String inconsistency = "";
		
		long start = System.currentTimeMillis();
        XMLstarParser starParser = new XMLstarParser();
        starParser.runParser();
        HashSet<Star> stars = starParser.getStars();
        System.out.println("finishing parsing actors file: " + (double) (System.currentTimeMillis() - start) / 1000);
        
        XMLmovieParser movieParser = new XMLmovieParser();
        movieParser.runParser();
        LinkedList<Movie> movies = movieParser.getMovies();
        HashSet<String> uniqueGenres = movieParser.getUniqueGenres();
        HashSet<String> movieIDs = movieParser.getMovieIDs();
        System.out.println("finishing parsing mains file: " + (double) (System.currentTimeMillis() - start) / 1000);
        
        XMLmoviestarParser movieStarParser = new XMLmoviestarParser(movieIDs);
        movieStarParser.runParser();
//        HashMap<String, LinkedList<String>> movieStars = movieStarParser.getMoviesStars();
        HashMap<String, Movie> movieStars = movieStarParser.getMoviesStars();
        System.out.println("finishing parsing casts file: " + (double) (System.currentTimeMillis() - start) / 1000);
        
        try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbcon = ds.getConnection();
			
//			HashMap<String, String> starIds = new HashMap<>();
			
			query = "SELECT DISTINCT name, birthYear, id FROM stars";
			statement = dbcon.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			HashMap<String , HashSet<Integer>> existingStars = new HashMap<>();
			while (result.next()) {
				HashSet<Integer> ts = existingStars.getOrDefault(result.getString("name"), new HashSet<Integer>());
				ts.add(result.getInt("birthYear"));
				existingStars.put(result.getString("name"), ts);
			}
			System.out.println(existingStars.size());
			
			// Insert new stars
			dbcon.setAutoCommit(false);
			query = "SELECT MAX(id) AS id FROM stars";
			statement = dbcon.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
            String starID = resultSet.getString("id");
            String front = starID.substring(0, 2);
            int back = Integer.parseInt(starID.substring(2, starID.length()));
			
            query = "INSERT INTO stars (id, name, birthYear) VALUES(?, ?, ?)";
            statement = dbcon.prepareStatement(query);
            int count = 0;
			for (Star s : stars) {
				if (existingStars.containsKey(s.getStarName()) && existingStars.get(s.getStarName()).contains(s.getBirthYear())) {
					continue;
				}
				starID = front + (++back);
				if (s.getBirthYear() == 0) {
					statement.setString(1, starID);
					statement.setString(2, s.getStarName());
					statement.setNull(3, Types.INTEGER);
					inconsistency += "star: " + s.getStarName() + ", no birth year\n";
				} else {
					statement.setString(1, starID);
					statement.setString(2, s.getStarName());
					statement.setInt(3, s.getBirthYear());
				}					
				HashSet<Integer> toadd = existingStars.getOrDefault(s.getStarName(), new HashSet<Integer>());
				toadd.add(s.getBirthYear());
				existingStars.put(s.getStarName(), toadd);
				count++;
				statement.addBatch();
				if (count % batchSize == 0) {
					statement.executeBatch();
				}
			}

			statement.executeBatch();
			dbcon.commit();
			System.out.println("finish inserting stars: " + count + " - " + (double) (System.currentTimeMillis() - start) / 1000);
			
//			HashMap<String, String> movieIdTitle = new HashMap<>();
			
			HashMap<String, HashMap<String, Integer>> existingMovies = new HashMap<>();
			query = "SELECT id, title, year, director FROM movies";
			statement = dbcon.prepareStatement(query);
			result = statement.executeQuery();
			while (result.next()) {
				HashMap<String, Integer> dmovies = existingMovies.getOrDefault(result.getString("director"), new HashMap<String, Integer>());
				dmovies.put(result.getString("title"), result.getInt("year"));
				existingMovies.put(result.getString("director"), dmovies);
//				movieIdTitle.put(result.getString("title"), result.getString("id"));
			}
			System.out.println(existingMovies.size());
			// Insert new genres, genres_in_movies, and movies
			dbcon.setAutoCommit(false);
			query = "INSERT INTO genres (name) SELECT * FROM (SELECT ?) AS tmp WHERE NOT EXISTS (SELECT name FROM genres WHERE name = ?)";
			statement = dbcon.prepareStatement(query);
			for (String genre : uniqueGenres) {
				statement.setString(1, genre);
				statement.setString(2, genre);
				statement.addBatch();
			}
			statement.executeBatch();
			dbcon.commit();
			System.out.println("finishing inserting new genres: " + uniqueGenres.size() + " - " + (double) (System.currentTimeMillis() - start) / 1000);
			
			// Insert new movies
			dbcon.setAutoCommit(false);
			query = "SELECT MAX(id) AS id FROM movies";
			statement = dbcon.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
            String movieID = resultSet.getString("id");
            front = movieID.substring(0, 2);
            back = Integer.parseInt(movieID.substring(2, movieID.length()));
            
            query = "INSERT INTO movies VALUES(?, ?, ?, ?)";
            statement = dbcon.prepareStatement(query);
            
            String gQuery = "INSERT INTO genres_in_movies VALUES((SELECT id FROM genres WHERE name = ?), "
            		+ "(SELECT id FROM movies WHERE title = ? and year = ? AND director = ? LIMIT 1))";
            PreparedStatement gStatement = dbcon.prepareStatement(gQuery);
            
            count = 0;
            for (Movie m : movies) {
            	if (existingMovies.containsKey(m.getDirector()) && 
            			existingMovies.get(m.getDirector()).containsKey(m.getTitle()) && 
            			existingMovies.get(m.getDirector()).get(m.getTitle()).equals(m.getYear())) {
            		continue;
            	}
	            	movieID = front + (++back);
	            	statement.setString(1, movieID);
	            	statement.setString(2, m.getTitle());
	            	statement.setInt(3, m.getYear());
	            	statement.setString(4, m.getDirector());
	            	HashMap<String, Integer> dmovies = existingMovies.getOrDefault(m.getDirector(), new HashMap<String, Integer>());
	            	dmovies.put(m.getTitle(), m.getYear());
	            	existingMovies.put(m.getDirector(), dmovies);
//	            	movieIdTitle.put(m.getTitle(), m.getID()); // here
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
			dbcon.commit();
			System.out.println("finish inserting movies: " + count + " - " + (double) (System.currentTimeMillis() - start) / 1000);
			
			// Trying something 
			query = "SELECT DISTINCT name FROM stars";
			statement = dbcon.prepareStatement(query);
			result = statement.executeQuery();
			HashSet<String> uniqueStars = new HashSet<>();
			while (result.next()) {
				uniqueStars.add(result.getString("name"));
			}
			
			
			query = "SELECT MAX(id) AS id FROM stars";
			statement = dbcon.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
            starID = resultSet.getString("id");
            front = starID.substring(0, 2);
            back = Integer.parseInt(starID.substring(2, starID.length()));
            
            dbcon.setAutoCommit(false);
			String sQuery = "INSERT INTO stars VALUES(?, ?, ?)";
//			query = "INSERT INTO stars_in_movies(starId, movieId) SELECT * FROM (SELECT (SELECT id FROM stars "
//					+ "WHERE name = ? LIMIT 1), (SELECT id FROM movies WHERE title = ? AND director = ? LIMIT 1)) AS tmp WHERE EXISTS (SELECT id FROM movies WHERE title = ? AND director = ? LIMIT 1)";
			query = "INSERT INTO stars_in_movies(starId, movieId)  VALUES((SELECT id FROM stars "
					+ "WHERE name = ? LIMIT 1), (SELECT id FROM movies WHERE title = ? AND director = ? LIMIT 1))";
			PreparedStatement sStatement = dbcon.prepareStatement(sQuery);
			statement = dbcon.prepareStatement(query);
			
			count = 0;
			for (String star : movieStars.keySet()) {
				if (existingMovies.containsKey(movieStars.get(star).getDirector()) && 
						existingMovies.get(movieStars.get(star).getDirector()).containsKey(movieStars.get(star).getTitle())) {
					if (!existingStars.containsKey(star)) {
						sStatement.setString(1, front + (++back));
						sStatement.setString(2, star);
						sStatement.setNull(3, Types.INTEGER);
						sStatement.addBatch();
						HashSet<Integer> info = existingStars.getOrDefault(star, new HashSet<>());
						info.add(0);
						existingStars.put(star, info);
					}
					
					statement.setString(1, star);
					statement.setString(2, movieStars.get(star).getTitle());
					statement.setString(3, movieStars.get(star).getDirector());
//					statement.setString(4, movieStars.get(star).getTitle());
//					statement.setString(5, movieStars.get(star).getDirector());
					statement.addBatch();
					count++;
					if (count % batchSize == 0) {
						sStatement.executeBatch();
						statement.executeBatch();
						System.out.println(count);
					}
				} else {
					inconsistency += "no matching movie exist for star: " + star + ", " + movieStars.get(star) + "\n";
				}
			}
			sStatement.executeBatch();
			statement.executeBatch();
			dbcon.commit();
			
			System.out.println("finish inserting stars_in_movies: " + count + " - " + (double) (System.currentTimeMillis() - start) / 1000);
			
			// End of trying
		
			
	        resultSet.close();
	        statement.close();
	        dbcon.close();
            long end = System.currentTimeMillis();
            System.out.println(end - start);
            System.out.println(inconsistency);
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
