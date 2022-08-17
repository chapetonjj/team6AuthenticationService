package com.example.api;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class AuthFilter implements Filter {

	//JWTUtil jwtUtil = new JWTMockUtil();
	JWTUtil jwtUtil = new JWTHelper();
	
	private String api_scope = "com.api.customer.r";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		if (uri.startsWith("/token")) {
			// continue on to get-token endpoint
			chain.doFilter(request, response);
			return;
		} else {
			// check JWT token
			String authheader = req.getHeader("authorization");
			if (authheader != null && authheader.length() > 7 && authheader.startsWith("Bearer")) {
				String jwt_token = authheader.substring(7, authheader.length());
				if (jwtUtil.verifyToken(jwt_token)) {
					String request_scopes = jwtUtil.getScopes(jwt_token);
					if (request_scopes.contains(api_scope)) {
						// continue on to api
						chain.doFilter(request, response);
						return;
					}
				}
			}
		}

		// reject request and return error instead of data
		res.sendError(HttpServletResponse.SC_FORBIDDEN, "failed authentication");
	}

}
