package com.datou.author.repositories;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.datou.author.model.MongoUser;
import com.mongodb.WriteResult;

@Component
public class MongoUserRepositoryImpl implements MongoUserRepositoryBase {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoUserRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean changePassword(final String oldPassword,
                               final String newPassword,
                               final String username) {
        final Query searchUserQuery = new Query(where("username").is(username).andOperator(where("password").is(oldPassword)));
        final WriteResult result = mongoTemplate.updateFirst(searchUserQuery, update("password", newPassword), MongoUser.class);
        return result.getN() == 1? true : false;
    }
}
