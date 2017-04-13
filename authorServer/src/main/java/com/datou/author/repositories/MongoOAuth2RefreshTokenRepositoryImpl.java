package com.datou.author.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.datou.author.model.MongoOAuth2RefreshToken;
import com.mongodb.WriteResult;

@Component
public class MongoOAuth2RefreshTokenRepositoryImpl implements MongoOAuth2RefreshTokenRepositoryBase {

    public static final String ID = "_id";
    private MongoTemplate mongoTemplate;

    @Autowired
    public MongoOAuth2RefreshTokenRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MongoOAuth2RefreshToken findByTokenId(final String tokenId) {
        final Query query = Query.query(Criteria.where(ID).is(tokenId));
        return mongoTemplate.findOne(query, MongoOAuth2RefreshToken.class);
    }

    @Override
    public boolean deleteByTokenId(String tokenId) {
        final Query query = Query.query(Criteria.where(ID).is(tokenId));
        final WriteResult removeResult = mongoTemplate.remove(query, MongoOAuth2RefreshToken.class);
        return removeResult.getN() == 1;
    }
}
