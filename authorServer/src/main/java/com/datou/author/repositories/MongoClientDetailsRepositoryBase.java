package com.datou.author.repositories;

import com.datou.author.model.MongoClientDetails;

public interface MongoClientDetailsRepositoryBase {
    boolean deleteByClientId(String clientId);

    boolean update(MongoClientDetails mongoClientDetails);

    boolean updateClientSecret(String clientId, String newSecret);

    MongoClientDetails findByClientId(String clientId) throws IllegalArgumentException;
}
