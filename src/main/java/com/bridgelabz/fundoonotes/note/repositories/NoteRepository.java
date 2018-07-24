package com.bridgelabz.fundoonotes.note.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bridgelabz.fundoonotes.note.models.Note;

public interface NoteRepository extends MongoRepository<Note, String> {

	public List<Note> findAllByUserId(String userId);
	
	public Optional<Note> findByUserId(String userId);
}
