package com.datou.author.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.datou.author.repositories.MongoUserRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private MongoUserRepository userRepository;
	
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findOne(username);
    }
}
