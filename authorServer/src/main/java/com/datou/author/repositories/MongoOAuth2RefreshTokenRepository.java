package com.datou.author.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.datou.author.model.MongoOAuth2RefreshToken;

public interface MongoOAuth2RefreshTokenRepository extends MongoRepository<MongoOAuth2RefreshToken, String>, MongoOAuth2RefreshTokenRepositoryBase {
}
