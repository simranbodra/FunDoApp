package com.bridgelabz.fundoonotes.user.repositories;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bridgelabz.fundoonotes.user.models.User;

public interface UserElasticsearchRepository extends ElasticsearchRepository<User, String>{

	/**
	 * to find user by emailId
	 * @param email
	 * @return Optional User object
	 */
	public Optional<User> findByEmail(String email);
	
}
