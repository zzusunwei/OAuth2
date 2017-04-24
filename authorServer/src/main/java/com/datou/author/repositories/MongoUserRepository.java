package com.datou.author.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.datou.author.model.MongoUser;

public interface MongoUserRepository extends MongoRepository<MongoUser, String>, MongoUserRepositoryBase {

}
