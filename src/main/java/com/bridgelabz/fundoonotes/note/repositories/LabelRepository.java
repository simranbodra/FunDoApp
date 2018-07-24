package com.bridgelabz.fundoonotes.note.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.bridgelabz.fundoonotes.note.models.Label;

public interface LabelRepository extends MongoRepository<Label, String>{

	public Optional<Label> findByLabelName(String labelName);
	public List<Label> findAllByUserId(String userId);
}
