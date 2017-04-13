package com.datou.author.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.datou.author.model.MongoClientDetails;

public interface MongoClientDetailsRepository extends MongoRepository<MongoClientDetails, String>, MongoClientDetailsRepositoryBase {
}
