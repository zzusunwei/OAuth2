package com.datou.author.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientKeyGenerator;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.stereotype.Component;

import com.datou.author.model.MongoOAuth2ClientToken;
import com.datou.author.repositories.MongoOAuth2ClientTokenRepository;

@Component
public class MongoClientTokenServices implements ClientTokenServices {
	@Autowired
    private MongoOAuth2ClientTokenRepository mongoOAuth2ClientTokenRepository;
	@Autowired
    private ClientKeyGenerator clientKeyGenerator;

    @Override
    public OAuth2AccessToken getAccessToken( OAuth2ProtectedResourceDetails resource,
                                             Authentication authentication) {
        MongoOAuth2ClientToken mongoOAuth2ClientToken = mongoOAuth2ClientTokenRepository.findByAuthenticationId(clientKeyGenerator.extractKey(resource, authentication));
        return SerializationUtils.deserialize(mongoOAuth2ClientToken.getToken());
    }

    @Override
    public void saveAccessToken( OAuth2ProtectedResourceDetails resource,
                                 Authentication authentication,
                                 OAuth2AccessToken accessToken) {
        removeAccessToken(resource, authentication);
         MongoOAuth2ClientToken mongoOAuth2ClientToken = new MongoOAuth2ClientToken(UUID.randomUUID().toString(),
                accessToken.getValue(),
                SerializationUtils.serialize(accessToken),
                clientKeyGenerator.extractKey(resource, authentication),
                authentication.getName(),
                resource.getClientId());

        mongoOAuth2ClientTokenRepository.save(mongoOAuth2ClientToken);
    }

    @Override
    public void removeAccessToken( OAuth2ProtectedResourceDetails resource,
                                   Authentication authentication) {
        mongoOAuth2ClientTokenRepository.deleteByAuthenticationId(clientKeyGenerator.extractKey(resource, authentication));
    }
}
