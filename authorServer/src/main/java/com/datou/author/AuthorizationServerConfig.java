package com.datou.author;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.token.ClientKeyGenerator;
import org.springframework.security.oauth2.client.token.DefaultClientKeyGenerator;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

import com.datou.author.service.MongoApprovalStore;
import com.datou.author.service.MongoClientDetailsService;
import com.datou.author.service.MongoClientTokenServices;
import com.datou.author.service.MongoTokenStore;
import com.datou.author.service.SecurityContextService;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	private static final Logger log = LoggerFactory.getLogger(AuthorServerApplication.class);

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private MongoTokenStore mongoTokenStore;
	@Autowired
	private MongoClientTokenServices mongoClientTokenServices;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private MongoClientDetailsService mongoClientDetailsService;
	@Autowired
	private MongoApprovalStore mongoApprovalStore;

	/**
	 * Defines the security constraints on the token endpoints /oauth/token_key
	 * and /oauth/check_token Client credentials are required to access the
	 * endpoints
	 *
	 * @param oauthServer
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("isAnonymous() || hasRole('ROLE_TRUSTED_CLIENT')") // permitAll()
				.checkTokenAccess("hasRole('TRUSTED_CLIENT')"); // isAuthenticated()
	}

	/**
	 * Defines the authorization and token endpoints and the token services
	 *
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints

				// Which authenticationManager should be used for the password
				// grant
				// If not provided, ResourceOwnerPasswordTokenGranter is not
				// configured
				.authenticationManager(authenticationManager).tokenStore(mongoTokenStore)
				.userDetailsService(userDetailsService).approvalStore(mongoApprovalStore);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(mongoClientDetailsService)
				// Confidential client where client secret can be kept safe
				// (e.g. server side)

				.withClient("confidential").secret("secret")
				.authorizedGrantTypes("client_credentials", "authorization_code", "refresh_token")
				.scopes("read", "write").redirectUris("http://localhost:8080/client/").and()
				// Public client where client secret is vulnerable (e.g. mobile
				// apps, browsers)
				.withClient("public") // No secret!
				.authorizedGrantTypes("client_credentials", "implicit").scopes("read")
				.redirectUris("http://localhost:8080/client/").and()
				// Trusted client: similar to confidential client but also
				// allowed to handle user password
				.withClient("trusted").secret("secret").authorities("ROLE_TRUSTED_CLIENT")
				.authorizedGrantTypes("client_credentials", "password", "authorization_code", "refresh_token")
				.scopes("read", "write").redirectUris("http://localhost:8080/client/");
	}

	@Bean
	public SecurityContextService securityContextService() {
		return new SecurityContextService();
	}

	@Bean
	public AuthenticationKeyGenerator authenticationKeyGenerator() {
		return new DefaultAuthenticationKeyGenerator();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public ClientKeyGenerator clientKeyGenerator() {
		return new DefaultClientKeyGenerator();
	}
}
