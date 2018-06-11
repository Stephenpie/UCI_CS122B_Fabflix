package mainPage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/suggestion")
public class SearchSuggestion extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // set response mime type
        response.setContentType("text/html"); 
        
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();
            
            // get the query string from parameter
            String query = request.getParameter("query");
            
            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }   
            
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            // create database connection
//            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbcon = ds.getConnection();
            
            // prepare query  
            String[] queries = query.split(" ");
            String arguments = "";
            for (int i = 0; i < queries.length; i++) {
                arguments += "? ";
            }
//            String sqlQuery = "DROP TABLE IF EXISTS ft;";
//            PreparedStatement statement = connection.prepareStatement(sqlQuery);
//            statement.executeUpdate();
//            
//            sqlQuery = "CREATE TABLE ft (" + 
//                        "id INT AUTO_INCREMENT," + 
//                        "movieId VARCHAR(10)," +
//                        "title text," + 
//                        "year INT," +
//                        "director VARCHAR(100)," +
//                        "PRIMARY KEY (id)," + 
//                        "FULLTEXT (title));";
//            statement = connection.prepareStatement(sqlQuery);
//            statement.executeUpdate();
//            
//            sqlQuery = "INSERT INTO ft (movieId, title, year, director) SELECT id, title, year, director FROM movies";
//            statement = connection.prepareStatement(sqlQuery);
//            statement.executeUpdate();
     
            
//            String sqlQuery = String.format("SELECT title FROM ft WHERE MATCH (title) AGAINST (%s IN BOOLEAN MODE) LIMIT 10", arguments);     
//            System.out.println(sqlQuery);
//            PreparedStatement statement = connection.prepareStatement(sqlQuery);
//            for (int i = 0; i < queries.length; i++) {
//                statement.setString(i+1, "+" + queries[i] + "*");
//            }
            String sqlQuery = String.format("SELECT title FROM ft WHERE MATCH (title) AGAINST (%s IN BOOLEAN MODE) OR "
                    + "(SELECT edrec(?, title, ?) = 1) LIMIT 10", arguments);
            System.out.println(sqlQuery);
            PreparedStatement statement = dbcon.prepareStatement(sqlQuery);
            int i = 0;
            for (; i < queries.length; i++) {
                statement.setString(i+1, "+" + queries[i] + "*");
            }
            statement.setString(++i, query);
            if (query.length() < 4) {
                statement.setInt(++i,  0);
            } else if (query.length() < 7) {
                statement.setInt(++i, 1);
            } else if (query.length() < 13) {
                statement.setInt(++i, 2);
            } else {
                statement.setInt(++i, 3);
            }

            // execute query
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JsonObject jso = new JsonObject();
                jso.addProperty("value", resultSet.getString("title"));
                JsonObject additionalDataJsonObject = new JsonObject();
                additionalDataJsonObject.addProperty("category", "Movie");
                jso.add("data", additionalDataJsonObject);
                jsonArray.add(jso);
//                if (jsonArray.size() == 10) {
//                    break;
//                }
            }
            
            resultSet.close();
            statement.close();
            dbcon.close();
            
            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

}
