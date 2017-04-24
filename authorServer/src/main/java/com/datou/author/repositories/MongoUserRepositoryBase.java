package com.datou.author.repositories;

public interface MongoUserRepositoryBase {

    boolean changePassword(String oldPassword, String newPassword, String username);

}
