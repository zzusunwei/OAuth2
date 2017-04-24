package com.datou.author.config;

import static com.google.common.collect.Lists.newArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoClientConfiguration {
	@Autowired  
    private MongoSettings mongoSettings;  

    @Bean
    public MongoClient mongoClient() throws Exception {
        ServerAddress serverAddress = new ServerAddress(
                mongoSettings.getHost(), mongoSettings.getPort());

        MongoCredential credential = MongoCredential.createCredential(
                mongoSettings.getUsername(),
                mongoSettings.getDatabase(),
                mongoSettings.getPassword().toCharArray());

        return new MongoClient(
                serverAddress, newArrayList(credential));
    }
}