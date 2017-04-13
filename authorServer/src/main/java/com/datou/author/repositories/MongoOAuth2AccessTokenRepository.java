package com.datou.author.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.datou.author.model.MongoOAuth2AccessToken;

public interface MongoOAuth2AccessTokenRepository extends MongoRepository<MongoOAuth2AccessToken, String>, MongoOAuth2AccessTokenRepositoryBase {

}
