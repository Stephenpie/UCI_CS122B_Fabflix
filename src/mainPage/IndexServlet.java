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

@WebServlet(name = "index", urlPatterns = "/index.html")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loginUser = "root";
        String loginPasswd = "tangwang";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        // set response mime type
        response.setContentType("text/html"); 
        
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        String head = "<!doctype html>\n" + 
        		"<html lang=\"en\">\n" + 
        		"\n" + 
        		"<head>\n" + 
        		"	<!-- Required meta tags -->\n" + 
        		"	<meta charset=\"utf-8\">\n" + 
        		"	<meta name=\"viewport\"\n" + 
        		"		content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" + 
        		"	<title>fabflix</title>\n" + 
        		"	<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">\n" + 
        		"    <link href='https://fonts.googleapis.com/css?family=Raleway' rel='stylesheet'>\n" + 
        		"    <!--  Using Bootstrap -->\n" + 
        		"	<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n" + 
        		"	\n" + 
        		"    <!-- Using jQuery -->\n" + 
        		"    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" + 
        		"	<style>\n" + 
        		"		a {\n" + 
        		"    		color: black;\n" + 
        		"    		font-family: 'Raleway', sans-serif;\n" + 
        		"    		font-weight: bold;\n" + 
        		"		}\n" + 
        		"	</style>\n" + 
        		"</head>";
        out.println(head);
        
        
        try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                // create database connection
                Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
                // prepare query  
                String mqlQuery = "SELECT * FROM genres";                
                PreparedStatement statement = connection.prepareStatement(mqlQuery);
                // execute query
                ResultSet resultSet = statement.executeQuery();

                String body = "<body>\n" + 
                		"	<div class=\"indexBackground\">\n" + 
                		"		<div class=\"container\">\n" + 
                		"	    	<div class=\"row\">\n" + 
                		"	    		<div class=\"col-lg-4\"><button class=\"btn btn-info\" id=\"addTo\" onclick=\"viewCart()\">Go to Cart</button></div>\n" + 
                		"	    		<div class=\"col-lg-4 center\"><font size=\"6\">FabFlix</font></div>\n" + 
                		"	    		<div class=\"col-lg-4\">\n" + 
                		"			    	<div class=\"input-group\">\n" + 
                		"			    		<input type=\"text\" class=\"form-control\" placeholder=\"Search a movie...\" id=\"query\">\n" + 
                		"			      		<span class=\"input-group-btn\"><button id=\"search\" class=\"btn btn-default\" type=\"submit\"><i class=\"glyphicon glyphicon-search\"></i></button>\n" + 
                		"			      		</span>\n" + 
                		"			    	</div>\n" + 
                		"			    </div>	\n" + 
                		"	    	</div>\n" + 
                		"    	</div>\n" + 
                		"    	<div><br><br><br></div>\n" + 
                		"    	<div class=\"container\">\n" + 
                		"    	<div class=\"row\">\n" + 
                		"    		<div class=\"col-lg-4\">\n" + 
                		"    			<div class=\"content genre\">\n" + 
                		"	    	   		<p>Browse by Movie Genre</p>\n" + 
                		"			    	    <table class=\"table table-bordered\">\n" + 
                		"			    	   	<tbody>\n";
                out.println(body);
                // add a row for every star result
                int i = 0;
                while (resultSet.next()) {
                	if (i % 4 == 0) {
                		out.println("<tr>");
                	}
                	out.print("<td><a href=browse?genre=");
                	String result = resultSet.getString("name");
                	out.print(result.toLowerCase());
                	out.print("&numOfMovies=25&page=1&sortby=null>");
                	out.print(result);
                	out.println("</a></td>");
                	if (i % 4 == 3) {
                		out.println("</tr>");
                	}
                	i++;
                }
               
                
                String tableEnd = "			</tbody>\n" + 
                		"		</table>\n" + 
                		"	</div>\n" + 
                		"</div>";
                out.println(tableEnd);
                String rest = "				<div class=\"col-lg-4\">\n" + 
                		"    				<div class=\"content initial\">\n" + 
                		"           				<p>Browse by Movie Title</p>\n" + 
                		"			            <table class=\"table table-bordered\">\n" + 
                		"						    <tbody>\n" + 
                		"						      <tr>\n" + 
                		"						        <td><a href=browse?prefix=0&numOfMovies=25&page=1&sortby=null>0</a></td>\n" + 
                		"						        <td><a href=browse?prefix=1&numOfMovies=25&page=1&sortby=null>1</a></td>\n" + 
                		"						        <td><a href=browse?prefix=2&numOfMovies=25&page=1&sortby=null>2</a></td>\n" + 
                		"						        <td><a href=browse?prefix=3&numOfMovies=25&page=1&sortby=null>3</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						      <tr>\n" + 
                		"						      	<td><a href=browse?prefix=4&numOfMovies=25&page=1&sortby=null>4</a></td>\n" + 
                		"						        <td><a href=browse?prefix=5&numOfMovies=25&page=1&sortby=null>5</a></td>\n" + 
                		"						        <td><a href=browse?prefix=6&numOfMovies=25&page=1&sortby=null>6</a></td>\n" + 
                		"						        <td><a href=browse?prefix=7&numOfMovies=25&page=1&sortby=null>7</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						      <tr>\n" + 
                		"						        <td><a href=browse?prefix=8&numOfMovies=25&page=1&sortby=null>8</a></td>\n" + 
                		"						        <td><a href=browse?prefix=9&numOfMovies=25&page=1&sortby=null>9</a></td>\n" + 
                		"						        <td><a href=browse?prefix=a&numOfMovies=25&page=1&sortby=null>A</a></td>\n" + 
                		"						        <td><a href=browse?prefix=b&numOfMovies=25&page=1&sortby=null>B</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						      <tr>\n" + 
                		"						        <td><a href=browse?prefix=c&numOfMovies=25&page=1&sortby=null>C</a></td>\n" + 
                		"						        <td><a href=browse?prefix=d&numOfMovies=25&page=1&sortby=null>D</a></td>\n" + 
                		"						        <td><a href=browse?prefix=e&numOfMovies=25&page=1&sortby=null>E</a></td>\n" + 
                		"						        <td><a href=browse?prefix=f&numOfMovies=25&page=1&sortby=null>F</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						      <tr>\n" + 
                		"						      	<td><a href=browse?prefix=g&numOfMovies=25&page=1&sortby=null>G</a></td>\n" + 
                		"						        <td><a href=browse?prefix=h&numOfMovies=25&page=1&sortby=null>H</a></td>\n" + 
                		"						        <td><a href=browse?prefix=i&numOfMovies=25&page=1&sortby=null>I</a></td>\n" + 
                		"						        <td><a href=browse?prefix=j&numOfMovies=25&page=1&sortby=null>J</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						      <tr>  \n" + 
                		"						        <td><a href=browse?prefix=k&numOfMovies=25&page=1&sortby=null>K</a></td>\n" + 
                		"						        <td><a href=browse?prefix=l&numOfMovies=25&page=1&sortby=null>L</a></td>\n" + 
                		"						        <td><a href=browse?prefix=m&numOfMovies=25&page=1&sortby=null>M</a></td>\n" + 
                		"						        <td><a href=browse?prefix=n&numOfMovies=25&page=1&sortby=null>N</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						      <tr>\n" + 
                		"						        <td><a href=browse?prefix=o&numOfMovies=25&page=1&sortby=null>O</a></td>\n" + 
                		"						        <td><a href=browse?prefix=p&numOfMovies=25&page=1&sortby=null>P</a></td>\n" + 
                		"						        <td><a href=browse?prefix=q&numOfMovies=25&page=1&sortby=null>Q</a></td>\n" + 
                		"						        <td><a href=browse?prefix=r&numOfMovies=25&page=1&sortby=null>R</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						      <tr>		\n" + 
                		"						      	<td><a href=browse?prefix=s&numOfMovies=25&page=1&sortby=null>S</a></td>\n" + 
                		"						        <td><a href=browse?prefix=t&numOfMovies=25&page=1&sortby=null>T</a></td>	        \n" + 
                		"						        <td><a href=browse?prefix=u&numOfMovies=25&page=1&sortby=null>U</a></td>\n" + 
                		"						        <td><a href=browse?prefix=v&numOfMovies=25&page=1&sortby=null>V</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						      <tr>  \n" + 
                		"						        <td><a href=browse?prefix=w&numOfMovies=25&page=1&sortby=null>W</a></td>\n" + 
                		"						        <td><a href=browse?prefix=x&numOfMovies=25&page=1&sortby=null>X</a></td>\n" + 
                		"						        <td><a href=browse?prefix=y&numOfMovies=25&page=1&sortby=null>Y</a></td>\n" + 
                		"						        <td><a href=browse?prefix=z&numOfMovies=25&page=1&sortby=null>Z</a></td>\n" + 
                		"						      </tr>\n" + 
                		"						    </tbody>\n" + 
                		"						</table>\n" + 
                		"        			</div>\n" + 
                		"    			</div>\n" + 
                		"    			<div class=\"col-lg-4\">\n" + 
                		"    				<div class = \"content\">\n" + 
                		"    					<p>Advanced Search</p>\n" + 
                		"    					<form id=\"advancedSearch\" method=\"get\" action=\"advanced\" onsubmit=\"return validateForm()\">\n" + 
                		"	    					<label><b>Title</b></label>\n" + 
                		"	    					<input class=\"form-control\" type=\"text\" placeholder=\"Enter Movie Title\" name=\"title\">\n" + 
                		"	    					<br>\n" + 
                		"	    					<label><b>Year</b></label>\n" + 
                		"	    					<input class=\"form-control\" type=\"number\" placeholder=\"Enter Year\" name=\"year\">\n" + 
                		"	    					<br>\n" + 
                		"	    					<label><b>Director</b></label>\n" + 
                		"	    					<input class=\"form-control\" type=\"text\" placeholder=\"Enter Director\" name=\"director\">\n" + 
                		"	    					<br>\n" + 
                		"	    					<label><b>Star's Name</b></label>\n" + 
                		"	    					<input class=\"form-control\" type=\"text\" placeholder=\"Enter Star's Name\" name=\"star\">\n" + 
                		"	    					<br>\n" + 
                		"	    					<input type=\"hidden\" name=\"numOfMovies\" value=\"25\" />\n" + 
                		"	    					<input type=\"hidden\" name=\"page\" value=\"1\" />\n" + 
                		"	    					<input type=\"hidden\" name=\"sortby\" value=\"null\" />\n" + 
                		"	    					<input class=\"btn btn-info\" type=\"submit\" value=\"Advanced Search\">\n" + 
                		"						</form>\n" + 
                		"					</div>\n" + 
                		"    			</div>	\n" + 
                		"    		</div>   	\n" + 
                		"    	</div>\n" + 
                		"	</div>		\n" + 
                		"    <script src=\"index.js\"></script>\n" + 
                		"    <script src=\"movielist.js\"></script>\n" + 
                		"</body>\n" + 
                		"</html>";
                
                out.println(rest);

                
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
                out.println("</html>"); 
        }
        out.close();
    }

}
