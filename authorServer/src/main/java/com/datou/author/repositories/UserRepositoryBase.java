package com.datou.author.repositories;

public interface UserRepositoryBase {

    boolean changePassword(String oldPassword, String newPassword, String username);

}
