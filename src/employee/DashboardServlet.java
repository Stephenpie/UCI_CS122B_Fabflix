package employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/fabflix/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private String dropStoredProcedure() {
        return "DROP PROCEDURE IF EXISTS add_movie;";
    }
    
    private String createStoredProcedure() {
        String procedure = 
                "CREATE PROCEDURE add_movie(IN movieID varchar(10), IN title varchar(100), IN year int(11), IN director varchar(100), " + 
                "    IN starID varchar(10), IN star varchar(100), IN genre varchar(32))" + 
                "BEGIN" + 
                "    INSERT INTO movies VALUES(movieID, title, year, director);" + 
                "    INSERT INTO stars(id, name) SELECT * FROM (SELECT starID, star) AS tstars WHERE NOT EXISTS (SELECT * FROM stars WHERE name = star);" + 
                "    INSERT INTO stars_in_movies VALUES(starID, movieID);" + 
                "    INSERT INTO genres (name) SELECT * FROM (SELECT genre) AS tg WHERE NOT EXISTS (SELECT * FROM genres WHERE name = genre);" + 
                "    INSERT INTO genres_in_movies VALUES((SELECT id FROM genres WHERE name = genre), movieID);" + 
                "END";
        
        return procedure;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Fabflix</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\">");
        out.println(
                "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        out.println("</head>");

        out.println("<body>");
        String loginUser = "root";
        String loginPasswd = "tangwang";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            out.println("<body class=\"loginBackgroundColor\">");
            out.println("<h2 class=\"text-center\">Employee Dashboard</h2>");
            
            out.println("<div class=\"container\">");
            	out.println("<div class=\"row\">");
            		out.println("<div class=\"col-lg-6\">");
			            out.println("<h3 \">Add new movie</h3>");
			            out.println("<form id=\"add_movie\" method=\"get\" action=\"\">");
			            out.println("<label><b>Title</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter movie title\" name=\"title\" required>");
			            out.println("<br><label><b>Year</b></label><input class=\"form-control\" type=\"number\" placeholder=\"Enter movie year\" name=\"year\" required>");
			            out.println("<br><label><b>Director</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter director\" name=\"director\" required>");
			            out.println("<br><label><b>Star</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter movie star\" name=\"mstar\" required>");
			            out.println("<br><label><b>Genre</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter movie genre\" name=\"genre\" required>");
			            out.println("<br><input class=\"btn btn-info\" type=\"submit\" value=\"add\"></form>");
			        out.println("</div>");
            
            String query = "";
            PreparedStatement statement = null;
            ResultSet res = null;
            String mtitle = request.getParameter("title");
            String myear = request.getParameter("year");
            String mdirector = request.getParameter("director");
            String mstar = request.getParameter("mstar");
            String mgenre = request.getParameter("genre");
            if (mtitle != null) {
                query = "SELECT * FROM movies WHERE title = ? AND year = ? AND director = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, mtitle.trim());
                statement.setString(2, myear.trim());
                statement.setString(3, mdirector.trim());
                res = statement.executeQuery();
                if (res.next()) { // movie already exist
                    out.println("Movie already exist! Try to add a NEW one!");
                } else { // add new movie
                    query = dropStoredProcedure();
                    statement = connection.prepareStatement(query);
                    statement.execute();
                    
                    query = createStoredProcedure();
                    statement = connection.prepareStatement(query);
                    statement.execute();
                    
                    query = "SELECT MAX(id) AS id FROM movies";
                    statement = connection.prepareStatement(query);
                    res = statement.executeQuery();
                    res.next();
                    String movieID = res.getString("id");
                    System.out.println(movieID);
                    int temp = Integer.parseInt(movieID.substring(2, movieID.length()));
                    movieID = movieID.substring(0, 2) + (++temp);
                    System.out.println("movieID = " + movieID);
                    
                    query = "SELECT * FROM stars WHERE name = ?";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, mstar);
                    res = statement.executeQuery();
                    String mstarID;
                    if (res.next()) { // this star already exists
                        mstarID = res.getString("id");
                    } else {
                        query = "SELECT MAX(id) AS id FROM stars";
                        statement = connection.prepareStatement(query);
                        res = statement.executeQuery();
                        res.next();
                        mstarID = res.getString("id");
                        temp = Integer.parseInt(mstarID.substring(2, mstarID.length()));
                        mstarID = mstarID.substring(0, 2) + (++temp);
                        System.out.println(mstarID);
                    }
                    
                    CallableStatement callStatement = connection.prepareCall("{call add_movie(?,?,?,?,?,?,?)}"); 
                    callStatement.setString(1, movieID);
                    callStatement.setString(2, mtitle);
                    callStatement.setString(3, myear);
                    callStatement.setString(4, mdirector);
                    callStatement.setString(5, mstarID);
                    callStatement.setString(6, mstar);
                    callStatement.setString(7, mgenre);
                    
                    int update = callStatement.executeUpdate();
                    out.println("Successfully added a new movie with provided information!");
                }
            }
            
            		out.println("<div class=\"col-lg-6\">");
			            out.println("<h3 \">Add new star</h3>");
			            out.println("<form id=\"add_star\" method=\"get\" action=\"\">");
			            out.println("<label><b>Star Name</b></label><input class=\"form-control\" type=\"text\" placeholder=\"Enter star name\" name=\"starname\" required>");
			            out.println("<br><label><b>Birth Year</b></label><input class=\"form-control\" type=\"number\" placeholder=\"Enter birth year\" name=\"birthyear\">");
			            out.println("<br><input class=\"btn btn-info\" type=\"submit\" value=\"add\"></form>");
			        
			
            String starName = request.getParameter("starname");
            String birthYear = request.getParameter("birthyear");
            if (starName != null) {
                query = "SELECT MAX(id) AS id FROM stars";
                statement = connection.prepareStatement(query);
                res = statement.executeQuery();
                res.next();
                String starID = res.getString("id");
                System.out.println(starID);
                int temp = Integer.parseInt(starID.substring(2, starID.length()));
                starID = starID.substring(0, 2) + (++temp);
                System.out.println(starID);
                
                query = "INSERT INTO stars (id, name, birthYear) VALUES(?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setString(1, starID);
                statement.setString(2, starName.trim());
                if (birthYear.length() > 0) {
                	statement.setInt(3, Integer.parseInt(birthYear));
                } else {
                	statement.setNull(3, Types.INTEGER);
                }
                int update = statement.executeUpdate();
                out.println("Success!");
            }
            out.println("</div>");
		    out.println("</div>");
		    out.println("</div>");
            query = "SHOW tables";
            statement = connection.prepareStatement(query);
            res = statement.executeQuery();
            ArrayList<String> tables = new ArrayList<>();
            while (res.next()) {
                tables.add(res.getString("Tables_in_moviedb"));
            }

            out.println("<div class=\"container\">");
            //out.println("<div class=\"row\">");
            int i = 0;
            for (String table : tables) {
            	if (i % 2 == 0) {
            		out.println("<div class=\"row\">");
            	}
            	out.println("<div class=\"col-lg-6\">");
            	
                out.println("<table id=\"resulttable\" class=\"table table-bordered table-hover table-striped\">");
    
                query = "SHOW fields FROM " + table;
                statement = connection.prepareStatement(query);
                res = statement.executeQuery();
    
                out.println(String.format("<h4>%s</h4>", table));
                out.println("<thead>");
                out.println("<th>Field</th>");
                out.println("<th>Type</th>");
                out.println("</thead>");
                out.println("<tbody>");
                while (res.next()) {
                	out.println("<tr>");
                    out.println("<td>" + res.getString("Field") + "</td>");
                    out.println("<td>" + res.getString("Type") + "</td>");
                    out.println("</tr>");
                }
                out.println("</tbody>");
                out.println("</table>");
                out.println("</div>");
                if (i % 2 == 1) {
                	out.println("</div>");
                }
                i++;
            }    
            
            	//out.println("</div>");
            
            out.println("</div>");

            out.println("<center><button type=\"button\" class=\"btn btn-info\" id=\"back\">Home</button></center>");

            out.println("</body></html>");

            res.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             * there's no console to see the print messages. Tomcat append all
             * the print messages to the file:
             * tomcat_directory/logs/catalina.out
             * 
             * To view the last n lines (for example, 100 lines) of messages you
             * can use: tail -100 catalina.out This can help you debug your
             * program after deploying it on AWS.
             */
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
