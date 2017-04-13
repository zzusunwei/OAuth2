package com.datou.author.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.datou.author.model.User;

public interface UserRepository extends MongoRepository<User, String>, UserRepositoryBase {

}
