package com.bridgelabz.fundoonotes.user.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bridgelabz.fundoonotes.user.models.User;

public interface UserRepository extends MongoRepository<User, String> {

	public Optional<User> findByEmail(String email);
}
