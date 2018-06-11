package login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.JsonObject;

public class LoginVerifyUtils {
    
    public static JsonObject verifyUsernamePassword(String username, String password) {
        
        JsonObject responseJsonObject = new JsonObject();
        
        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbcon = ds.getConnection();
                        
            String query = "SELECT id, email, password FROM customers WHERE email=?";
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, username);
            // execute query
            ResultSet resultSet = statement.executeQuery();
            
            /* This example only allows username/password to be test/test
            /  in the real project, you should talk to the database to verify username/password
            */
            boolean success = false;
            if (resultSet.next()) {
                
                // Login success:
                // get the encrypted password from the database
                String encryptedPassword = resultSet.getString("password");
                
                // use the same encryptor to compare the user input password with encrypted password stored in DB
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                
                if (success == true) {           
        
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    responseJsonObject.addProperty("userID", resultSet.getString("id"));
                } else {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Either username or password doesn't exist");
                }
            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Either username or password doesn't exist");
            }
            resultSet.close();
            statement.close();
            dbcon.close();
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
        return responseJsonObject;
    }

}