package com.bridgelabz.fundoonotes.note.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.Note;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.ViewNote;
import com.bridgelabz.fundoonotes.note.repositories.NoteRepository;
import com.bridgelabz.fundoonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	private NoteRepository repository;
	
	private ModelMapper modelMapper;

	@Override
	public ViewNote createNewNote(CreateNote newNote, String jwToken) throws NoteException {
		NoteUtility.validateNewNote(newNote);

		String userId = NoteUtility.parseJWT(jwToken);

		Note note = new Note();
		note.setTitle(newNote.getTitle());
		note.setDescription(newNote.getDescription());
		note.setCreatedAt(NoteUtility.getCurrentDate());
		note.setLastUpdated(NoteUtility.getCurrentDate());
		note.setUserId(userId);
		note.setColour(newNote.getColour());
		note.setReminder(null);

		repository.save(note);

		ViewNote viewNote = new ViewNote();
		viewNote.setTitle(note.getTitle());
		viewNote.setDescription(note.getDescription());
		viewNote.setCreatedAt(note.getCreatedAt());
		viewNote.setLastUpdated(note.getLastUpdated());
		viewNote.setReminder(note.getReminder());

		return viewNote;
	}
	
	public ViewNote viewNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		Optional<Note> optionalNote = repository.findById(noteId);
		
		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}
		
		Note note = optionalNote.get();
		
		ViewNote viewNote = new ViewNote();
		viewNote.setTitle(note.getTitle());
		viewNote.setDescription(note.getDescription());
		viewNote.setCreatedAt(note.getCreatedAt());
		viewNote.setLastUpdated(note.getLastUpdated());
		viewNote.setReminder(note.getReminder());
		
		return viewNote;
		
	}
	
	public List<ViewNote> viewAllNote(String token) throws NoteNotFoundException{
		String userId = NoteUtility.parseJWT(token);

		if(!repository.findByUserId(userId)) {
			throw new NoteNotFoundException("No notes found");
		}
		
		List<Note> noteList = repository.findAllByUserId(userId);
		
		List<ViewNote> viewNoteList = new LinkedList<>();
		for(int i=0; i<noteList.size(); i++) {
			ViewNote viewNote = new ViewNote();
			Note note = noteList.get(i);
			viewNote.setTitle(note.getTitle());
			viewNote.setDescription(note.getDescription());
			viewNote.setCreatedAt(note.getCreatedAt());
			viewNote.setLastUpdated(note.getLastUpdated());
			viewNote.setReminder(note.getReminder());
			viewNoteList.add(viewNote);
		}
		return viewNoteList;
		
	}

	@Override
	public void updateNote(UpdateNote updateNote, String token, String noteId)
			throws NoteException, NoteNotFoundException, UnauthorizedException {
		NoteUtility.validateNoteForUpdate(updateNote);

		String userId = NoteUtility.parseJWT(token);
		
		Optional<Note> optionalNote = repository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		if (updateNote.getTitle() != null || updateNote.getTitle().length() != 0) {
			note.setTitle(updateNote.getTitle());
		}
		if (updateNote.getDescription() != null || updateNote.getDescription().length() != 0) {
			note.setDescription(updateNote.getDescription());
		}
		if (updateNote.getReminder() != null) {
			note.setReminder(updateNote.getReminder());
		}
		if (updateNote.getColour() != null || updateNote.getColour().length() != 0) {
			note.setColour(updateNote.getColour());
		}
		note.setLastUpdated(NoteUtility.getCurrentDate());

		repository.save(note);
	}

	public void deleteNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!repository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = repository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setTrash(true);

		repository.save(note);
	}

	public void permanentNoteDelete(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!repository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = repository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}
		if(optionalNote.get().getTrash() != true) {
			throw new NoteNotFoundException("No such note found in trash");
		}
		
		repository.deleteById(noteId);
	}
	
	/*public void emptyNoteTrash(String token) throws NoteNotFoundException {
		String userId = NoteUtility.parseJWT(token);
	
		if(!repository.findByUserId(userId)) {
			throw new NoteNotFoundException("No notes found");
		}
		
		List<Note> noteList = repository.findAllByUserId(userId);
		
		List<String> noteIdList = new LinkedList<>();
		for(int i=0; i<noteList.size(); i++) {
			noteIdList.add(noteList.get(i).getNoteId());
		}
		repository.deleteAll(noteIdList);
	}*/
}













