package com.datou.author.service;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import com.datou.author.model.MongoOAuth2AccessToken;
import com.datou.author.model.MongoOAuth2RefreshToken;
import com.datou.author.repositories.MongoOAuth2AccessTokenRepository;
import com.datou.author.repositories.MongoOAuth2RefreshTokenRepository;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

@Component
public class MongoTokenStore implements TokenStore {
	@Autowired
    private MongoOAuth2AccessTokenRepository mongoOAuth2AccessTokenRepository;
	@Autowired
    private MongoOAuth2RefreshTokenRepository mongoOAuth2RefreshTokenRepository;
	@Autowired
    private AuthenticationKeyGenerator authenticationKeyGenerator;

    @Override
    public OAuth2Authentication readAuthentication(final OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(final String token) {
         String tokenId = extractTokenKey(token);

         MongoOAuth2AccessToken mongoOAuth2AccessToken = mongoOAuth2AccessTokenRepository.findByTokenId(tokenId);

        if (mongoOAuth2AccessToken != null) {
            try {
                return deserializeAuthentication(mongoOAuth2AccessToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                removeAccessToken(token);
            }
        }

        return null;
    }

    @Override
    public void storeAccessToken( OAuth2AccessToken token,
                                  OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (readAccessToken(token.getValue())!=null) {
            removeAccessToken(token.getValue());
        }

         String tokenKey = extractTokenKey(token.getValue());

         MongoOAuth2AccessToken oAuth2AccessToken = new MongoOAuth2AccessToken(tokenKey,
                serializeAccessToken(token),
                authenticationKeyGenerator.extractKey(authentication),
                authentication.isClientOnly() ? null : authentication.getName(),
                authentication.getOAuth2Request().getClientId(),
                serializeAuthentication(authentication),
                extractTokenKey(refreshToken));

        mongoOAuth2AccessTokenRepository.save(oAuth2AccessToken);
    }

    public void removeAccessToken(String tokenValue) {
         String tokenKey = extractTokenKey(tokenValue);
        mongoOAuth2AccessTokenRepository.deleteByTokenId(tokenKey);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
         String tokenKey = extractTokenKey(tokenValue);
         MongoOAuth2AccessToken mongoOAuth2AccessToken = mongoOAuth2AccessTokenRepository.findByTokenId(tokenKey);
        if (mongoOAuth2AccessToken != null) {
            try {
                return deserializeAccessToken(mongoOAuth2AccessToken.getToken());
            } catch (IllegalArgumentException e) {
                removeAccessToken(tokenValue);
            }
        }
        return null;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken,
                                   OAuth2Authentication oAuth2Authentication) {
         String tokenKey = extractTokenKey(refreshToken.getValue());
         byte[] token = serializeRefreshToken(refreshToken);
         byte[] authentication = serializeAuthentication(oAuth2Authentication);

        MongoOAuth2RefreshToken oAuth2RefreshToken = new MongoOAuth2RefreshToken(tokenKey, token, authentication);

        mongoOAuth2RefreshTokenRepository.save(oAuth2RefreshToken);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
         String tokenKey = extractTokenKey(tokenValue);
         MongoOAuth2RefreshToken mongoOAuth2RefreshToken = mongoOAuth2RefreshTokenRepository.findByTokenId(tokenKey);

        if (mongoOAuth2RefreshToken != null) {
            try {
                return deserializeRefreshToken(mongoOAuth2RefreshToken.getToken());
            } catch (IllegalArgumentException e) {
                removeRefreshToken(tokenValue);
            }
        }

        return null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;

        String key = authenticationKeyGenerator.extractKey(authentication);

        MongoOAuth2AccessToken oAuth2AccessToken = mongoOAuth2AccessTokenRepository.findByAuthenticationId(key);

        if (oAuth2AccessToken != null) {
            accessToken = deserializeAccessToken(oAuth2AccessToken.getToken());
        }

        if (accessToken != null
                && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken.getValue());
            // Keep the store consistent (maybe the same user is represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<MongoOAuth2AccessToken> oAuth2AccessTokens = mongoOAuth2AccessTokenRepository.findByUsernameAndClientId(userName, clientId);
        Collection<MongoOAuth2AccessToken> noNullsTokens = filter(oAuth2AccessTokens, byNotNulls());
        return transform(noNullsTokens, toOAuth2AccessToken());
    }

    private Predicate<MongoOAuth2AccessToken> byNotNulls() {
        return new Predicate<MongoOAuth2AccessToken>() {
            @Override
            public boolean apply(final MongoOAuth2AccessToken token) {
                return token != null;
            }
        };
    }

    private Function<MongoOAuth2AccessToken, OAuth2AccessToken> toOAuth2AccessToken() {
        return new Function<MongoOAuth2AccessToken, OAuth2AccessToken>() {
            @Override
            public OAuth2AccessToken apply(MongoOAuth2AccessToken token) {
                return SerializationUtils.deserialize(token.getToken());
            }
        };
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(final String clientId) {
        List<MongoOAuth2AccessToken> oAuth2AccessTokens = mongoOAuth2AccessTokenRepository.findByClientId(clientId);
        Collection<MongoOAuth2AccessToken> noNullTokens = filter(oAuth2AccessTokens, byNotNulls());
        return transform(noNullTokens, toOAuth2AccessToken());
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

    protected byte[] serializeAccessToken(OAuth2AccessToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return SerializationUtils.serialize(authentication);
    }

    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return SerializationUtils.deserialize(authentication);
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
        String tokenId = extractTokenKey(value);

        MongoOAuth2RefreshToken mongoOAuth2RefreshToken = mongoOAuth2RefreshTokenRepository.findByTokenId(tokenId);

        if (mongoOAuth2RefreshToken != null) {
            try {
                return deserializeAuthentication(mongoOAuth2RefreshToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                removeRefreshToken(value);
            }
        }

        return null;
    }

    public void removeRefreshToken(String token) {
        String tokenId = extractTokenKey(token);
        mongoOAuth2RefreshTokenRepository.deleteByTokenId(tokenId);
    }

    public void removeAccessTokenUsingRefreshToken(final String refreshToken) {
        String tokenId = extractTokenKey(refreshToken);
        mongoOAuth2AccessTokenRepository.deleteByRefreshTokenId(tokenId);

    }
}
