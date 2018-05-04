package login;

public class User {

	private final String username;
	private final String password;
	private final String userID;
	
	public User(String username, String password, String userID) {
		this.username = username;
		this.password = password;
		this.userID = userID;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUserID() {
	    return userID;
	}
}
