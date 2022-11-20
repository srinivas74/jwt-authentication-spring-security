package com.spring.security.jwtauthentication.api;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.security.jwtauthentication.model.AuthenticationRequest;
import com.spring.security.jwtauthentication.service.MyUserDetailsService;
import com.spring.security.jwtauthentication.util.JwtUtil;

@RestController
public class RestAPI {
	
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	/**
	 * Getting hold of authentication manager which is responsible for authenticating a user 
	 * (authentication manager implemented by provider manager checks for valid authentication provider using supports() methods and uses user details service to validate against given credentials)
	 * 
	 * Above operation is done by spring security automatically
	 * 
	 * In this implementation we are allowing this api without authentication 
	 * and manually triggering authentication manager to authenticate given user credentials
	 * 
	 * if authentication manager successfully authenticates then we will return a JWT token
	 * 
	 * else an exception
	 * 
	 */
	@GetMapping("/getToken")
	public String getJwtToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
		try {
			System.out.println("Authenticating Request with user details : "+authenticationRequest.toString());
			Authentication authentication= authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
			System.out.println("Is Authenticated ? : "+authentication.isAuthenticated());
		}
		catch (BadCredentialsException e) {
			System.out.println("Exception Occurred while authenticating, error message : "+e.getMessage());
			throw e;
		}
		final UserDetails userDetails = myUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		return jwtUtil.generateToken(userDetails);
	}
	
	/**
	 * @param principal this api is protected user should be authenticated, if filters authenticates the user then request is forwarded to this controller with principal which contains authenticated user details
	 * @return
	 */
	@GetMapping("/welcome")
	public String welcome(Principal principal) {
		return "Hello User :"+ principal.getName();
	}

}
