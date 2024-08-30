package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		//fetching user from database;
		
		User user=userRepository.getUerByUserName(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("could not found user");
		}
		CutomUserDetails customUserDetails=new CutomUserDetails(user);
		
		return customUserDetails;
	}

}
