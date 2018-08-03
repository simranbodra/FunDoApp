package com.bridgelabz.fundoonotes.note.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.bridgelabz.fundoonotes.note.models.Note;

public interface NoteElasticsearchRepository extends ElasticsearchRepository<Note, String>{

	public List<Note> findAllByUserId(String userId);
	
	public Optional<Note> findByUserId(String userId);
	
	public List<Note> findAllByUserIdAndTrash(String userId, boolean isTrashed);
	
	//public List<Note> findAllByUserIdAndLabelId(String userId, String labelId);
}
