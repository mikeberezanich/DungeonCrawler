package game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	public Connection connect() throws SQLException{
	        
	        String url = "jdbc:mysql://hoolahanphotography.com:3306/dungeoncrawler";
	        
	        try {
	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (Exception ex) {
	            System.out.println(ex.getMessage());
	        }
	        
	        Connection connection = DriverManager.getConnection(url, "gameuser", "gamepass");
	
	        return connection;
	    }
	
}
