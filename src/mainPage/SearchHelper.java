package mainPage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SearchHelper {
    private static String loginUser = "root";
    private static String loginPasswd = "tangwang";
    private static String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
    private static PreparedStatement statement = null;
    
    public SearchHelper() {
        
    }
    
    public static JsonArray getSearchResult(String query, String sort, int limit, int offset, boolean web) {
        
        JsonArray jsonArray = new JsonArray();
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
                        
            // prepare query
            String[] queries = query.split(" ");
            String arguments = "";
            for (int i = 0; i < queries.length; i++) {
                arguments += "? ";
            }

            String sqlQuery = String.format("SELECT * FROM (SELECT movieId, title, year, director FROM ft WHERE MATCH (title) "
                    + "AGAINST (%s IN BOOLEAN MODE)) m LEFT JOIN ratings r ON m.movieId = r.movieId", arguments.substring(0, arguments.length()-1));
            if (!sort.equals("null")) {
                if (sort.substring(0, 5).equals("title") && sort.substring(5, sort.length()).equals("asc")) {
                    sqlQuery += " ORDER BY m.title ASC LIMIT ? OFFSET ?";
                } else if (sort.substring(0, 5).equals("title") && sort.substring(5, sort.length()).equals("desc")) {
                    sqlQuery += " ORDER BY m.title DESC LIMIT ? OFFSET ?";
                } else if (sort.substring(0, 6).equals("rating") && sort.substring(6, sort.length()).equals("asc")) {
                    sqlQuery += " ORDER BY r.rating ASC LIMIT ? OFFSET ?";
                } else {
                    sqlQuery += " ORDER BY r.rating DESC LIMIT ? OFFSET ?";
                }
            } else {
                sqlQuery += " LIMIT ? OFFSET ?";
            }
            statement = connection.prepareStatement(sqlQuery);
            int i = 0;
            for (; i < queries.length; i++) {
                statement.setString(i+1, "+" + queries[i] + "*");
            }
            statement.setInt(++i, limit);
            statement.setInt(++i, offset);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("movieId");
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String rating = resultSet.getString("rating");
                if (rating == null) {
                    rating = "0";
                }
                
                String temp = "SELECT name FROM genres g, genres_in_movies gm WHERE gm.movieId = ? AND g.id = gm.genreId";
                PreparedStatement ps = connection.prepareStatement(temp);
                ps.setString(1, id);
                ResultSet res = ps.executeQuery();
                String genres = "";
                while (res.next()) {
                    genres += res.getString("name") + ", ";
                }
                
                temp = "SELECT name FROM stars s, stars_in_movies sm WHERE sm.movieId = ? AND s.id = sm.starId";
                ps = connection.prepareStatement(temp);
                ps.setString(1, id);
                res = ps.executeQuery();
                String stars = "";
                if (web) {
                    while (res.next()) {
                        stars += "<a href='stars?star=" + res.getString("name") + "'>" + res.getString("name") + "</a>, ";
                    }
                } else {
                    while (res.next()) {
                        stars += res.getString("name") + ", ";
                    }
                }
                
                JsonObject movie = new JsonObject();
                movie.addProperty("id", id);
                movie.addProperty("title", title);
                movie.addProperty("year", year);
                movie.addProperty("director", director);
                movie.addProperty("genres", genres.substring(0, genres.length() - 2));
                movie.addProperty("stars", stars.substring(0, stars.length() - 2));
                movie.addProperty("rating", rating);
                jsonArray.add(movie);
            }
            statement.setInt(i, offset + limit);
            resultSet = statement.executeQuery();
            
            JsonObject nextPage = new JsonObject();
            if (resultSet.next()) {
                nextPage.addProperty("next", true);
            } else {
                nextPage.addProperty("next", false);
            }
            jsonArray.add(nextPage);
            
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
            System.out.println(e.getMessage());
        }
        return jsonArray;
    }

}
