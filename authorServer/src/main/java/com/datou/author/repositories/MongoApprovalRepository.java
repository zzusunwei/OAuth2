package com.datou.author.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.datou.author.model.MongoApproval;

public interface MongoApprovalRepository extends MongoRepository<MongoApproval, String>, MongoApprovalRepositoryBase {
}
