package com.bridgelabz.fundonotes.usermodule.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bridgelabz.fundonotes.usermodule.models.User;

public interface UserRepository extends MongoRepository<User, String>{

}
