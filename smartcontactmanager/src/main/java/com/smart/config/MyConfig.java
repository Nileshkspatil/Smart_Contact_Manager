package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

	@Configuration
	@EnableWebSecurity
	public class MyConfig {
	
		  @Bean
		  public BCryptPasswordEncoder passwordEncoder() 
		  
		  { return new BCryptPasswordEncoder(); 
		  
		  }
		
		 
		
		@Bean
		public UserDetailsService getUserDetailsService() {
			
			
			return new UserDetailsServiceImpl();	
		}
		
		@Bean
		public DaoAuthenticationProvider authenticationProvider() {
			
			DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
			daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
			daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
			return daoAuthenticationProvider;
		}
		
		
		
				@Bean
				public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
				
					httpSecurity.csrf().disable()
					.authorizeHttpRequests()
					.requestMatchers("/user/**").hasRole("USER")
					.requestMatchers("/**").permitAll()
					.anyRequest()
					.authenticated()
					.and().formLogin().loginPage("/signin")
					.loginProcessingUrl("/dologin")
					.defaultSuccessUrl("/user/index");
					//.failureUrl("/login-fail")
					
					return httpSecurity.build();
					
				}
				
		
		//configure method
			
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {		
			
			auth.authenticationProvider(authenticationProvider());
			
		}
				
			
	}
