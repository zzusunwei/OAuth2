package com.datou.author.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.datou.author.model.MongoOAuth2ClientToken;

public interface MongoOAuth2ClientTokenRepository extends MongoRepository<MongoOAuth2ClientToken, String>, MongoOAuth2ClientTokenRepositoryBase {
}
