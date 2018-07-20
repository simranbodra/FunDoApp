package com.bridgelabz.fundoonotes.note.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bridgelabz.fundoonotes.note.models.Note;

public interface NoteRepository extends MongoRepository<Note, String>{

	public boolean findByUserId(String userId);
	public List<Note> findAllByUserId(String userId);
	//public void deleteAll(List<String> userIdList);
}
