package com.example.api;

public class TokenRequestData {
	String username;
	String password;
	String scopes;
	
	public TokenRequestData(String username, String password, String scopes) {
		super();
		this.username = username;
		this.password = password;
		this.scopes = scopes;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getScopes() {
		return scopes;
	}
	
	public void setScopes(String scopes) {
		this.scopes = scopes;
	}	
	
}
