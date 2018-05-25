package mainPage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/suggestion")
public class SearchSuggestion extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String loginUser = "root";
        String loginPasswd = "tangwang";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
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
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // prepare query  
            String[] queries = query.split(" ");
            String arguments = "";
            for (int i = 0; i < queries.length; i++) {
                arguments += "? ";
            }
            String sqlQuery = "DROP TABLE IF EXISTS ft;";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();
            
            sqlQuery = "CREATE TABLE ft (" + 
                        "id INT AUTO_INCREMENT," + 
                        "title text," + 
                        "PRIMARY KEY (id)," + 
                        "FULLTEXT (title));";
            statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();
            
            sqlQuery = "INSERT INTO ft (title) SELECT title FROM movies";
            statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();
     
            
            sqlQuery = String.format("SELECT title FROM ft WHERE MATCH (title) AGAINST (%s IN BOOLEAN MODE)", arguments);     
            System.out.println(sqlQuery);
            statement = connection.prepareStatement(sqlQuery);
            for (int i = 0; i < queries.length; i++) {
                statement.setString(i+1, "+" + queries[i] + "*");
            }
            // execute query
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JsonObject jso = new JsonObject();
                jso.addProperty("value", resultSet.getString("title"));
                jsonArray.add(jso);
                if (jsonArray.size() == 10) {
                    break;
                }
            }
            
            
            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

}
