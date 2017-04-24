package com.datou.author.model;

import java.util.Collection;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Document(collection = "user")
public class MongoUser extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MongoUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

}
