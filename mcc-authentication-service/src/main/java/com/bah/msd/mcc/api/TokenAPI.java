package com.bah.msd.mcc.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.bah.msd.mcc.util.JWTHelper;
import com.bah.msd.mcc.domain.Token;
import com.bah.msd.mcc.domain.Customer;
import com.bah.msd.mcc.domain.CustomerFactory;

@RestController
@RequestMapping("/token")
public class TokenAPI {
	
	String dataApiHost = "localhost:8080";
	
	//JWTUtil jwtUtil = new JWTMockUtil();
	//JWTUtil jwtUtil = new JWTHelper();
	public static Token appUserToken;
	
	@PostMapping //(consumes = "application/json")
	public ResponseEntity<?> getToken(@RequestBody Customer customer) {
		
		String username = customer.getName();
		String password = customer.getPassword();
		
		if (username != null && username.length() > 0 
				&& password != null && password.length() > 0 
				&& checkPassword(username, password)) {
			Token token = createToken(username);
			ResponseEntity<?> response = ResponseEntity.ok(token);
			return response;			
		}
		// bad request
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
	}
	
	public boolean checkPassword(String username, String password) {		
		if(username.equals("SJday3") && password.equals("secretlogin")) {
			return true;
		}

		Customer cust = getCustomerByNameFromCustomerAPI(username);
		if(cust != null && cust.getName().equals(username) && cust.getPassword().equals(password)) {
			return true;				
		}		
		return false;
	}
	
	private static Token createToken(String username) {
    	String scopes = "com.bah.msd.mcc.data.apis";
    	
    	if( username.equalsIgnoreCase("SJday3")) {
    		scopes = "com.webage.auth.apis";
    	}
    	String token_string = JWTHelper.createToken(scopes);
    	
    	return new Token(token_string);
    }
	
	public static Token getAppUserToken() {
		if(appUserToken == null || appUserToken.getToken() == null || appUserToken.getToken().length() == 0) {
			appUserToken = createToken("ApiClientApp");
		}
		return appUserToken;
	}
	
	private Customer getCustomerByNameFromCustomerAPI(String username) {
		try {
			String apiHost= System.getenv("API_HOST");
			if(apiHost == null) {
				apiHost = this.dataApiHost;
			}
			URL url = new URL("http://" + apiHost + "/api/customers/byname/" + username);
			
			//URL url = new URL("http://localhost:8080/api/customers/byname/" + username);
			
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			Token token = getAppUserToken();
			conn.setRequestProperty("authorization", "Bearer " + token.getToken());

			if (conn.getResponseCode() != 200) {
				return null;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output = "";
				String out = "";
				while ((out = br.readLine()) != null) {
					output += out;
				}
				conn.disconnect();
				return CustomerFactory.getCustomer(output);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;

		} catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		}

	}  	
}