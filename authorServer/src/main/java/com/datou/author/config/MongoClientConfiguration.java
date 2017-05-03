package com.datou.author.config;

import static com.google.common.collect.Lists.newArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.datou.author.AuthorServerApplication;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;



public class MongoClientConfiguration {
	private static final Logger log = LoggerFactory.getLogger(AuthorServerApplication.class);
	@Autowired  
    private MongoSettings mongoSettings;  

    @Bean
    public MongoClient mongoClient() throws Exception {
		log.info("mongo config info: {}", mongoSettings);
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