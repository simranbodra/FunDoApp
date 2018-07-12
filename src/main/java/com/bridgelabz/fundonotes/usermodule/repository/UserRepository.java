package com.bridgelabz.fundonotes.usermodule.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.usermodule.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>{

}
