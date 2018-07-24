package com.bridgelabz.fundoonotes.note.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.Label;
import com.bridgelabz.fundoonotes.note.models.Note;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.ViewLabel;
import com.bridgelabz.fundoonotes.note.models.ViewNote;
import com.bridgelabz.fundoonotes.note.repositories.LabelRepository;
import com.bridgelabz.fundoonotes.note.repositories.NoteRepository;
import com.bridgelabz.fundoonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	private NoteRepository noteRepository;
	
	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * create a new note
	 * 
	 * @param newNote
	 * @param jwToken
	 * @return ViewNoteDTO
	 * @throws NoteException
	 */
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
		if (newNote.getClass() == null || newNote.getColour().trim().length() == 0) {
			note.setColour("White");
		} else {
			note.setColour(newNote.getColour());
		}
		noteRepository.save(note);

		ViewNote viewNote = modelMapper.map(note, ViewNote.class);
		return viewNote;
	}

	/**
	 * view a note
	 * 
	 * @param token
	 * @param noteId
	 * @return ViewNoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public ViewNote viewNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		Optional<Note> optionalNote = noteRepository.findById(noteId);

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

	/**
	 * view a list of note owned by the user
	 * 
	 * @param token
	 * @return list of ViewNoteDTO
	 * @throws NoteNotFoundException
	 */
	public List<ViewNote> viewAllNote(String token) throws NoteNotFoundException {
		String userId = NoteUtility.parseJWT(token);

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		List<ViewNote> viewNoteList = new LinkedList<>();
		for (int i = 0; i < noteList.size(); i++) {
			if (!noteList.get(i).getTrash()) {
				ViewNote viewNote = new ViewNote();
				Note note = noteList.get(i);
				viewNote.setTitle(note.getTitle());
				viewNote.setDescription(note.getDescription());
				viewNote.setCreatedAt(note.getCreatedAt());
				viewNote.setLastUpdated(note.getLastUpdated());
				viewNote.setReminder(note.getReminder());
				viewNoteList.add(viewNote);
			}
		}
		return viewNoteList;

	}

	/**
	 * update a note
	 * 
	 * @param updateNoteDTO
	 * @param userId
	 * @param noteId
	 * @throws NoteException
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 */
	@Override
	public void updateNote(UpdateNote updateNote, String userId, String noteId)
			throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException {

		// String userId = NoteUtility.parseJWT(token);

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}
		Note note = optionalNote.get();
		if (updateNote.getTitle() != null || updateNote.getTitle().trim().length() != 0) {
			note.setTitle(updateNote.getTitle());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (updateNote.getDescription() != null || updateNote.getDescription().trim().length() != 0) {
			note.setDescription(updateNote.getDescription());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (NoteUtility.validateDate(updateNote.getReminder())) {
			note.setReminder(updateNote.getReminder());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (updateNote.getColour() != null || updateNote.getColour().trim().length() != 0) {
			note.setColour(updateNote.getColour());
		}

		noteRepository.save(note);
	}

	/**
	 * move a note to trash
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void deleteNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setTrash(true);

		noteRepository.save(note);
	}

	/**
	 * delete a note from trash
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void permanentNoteDelete(String token, String noteId, boolean delete)
			throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}
		if (!optionalNote.get().getTrash()) {
			throw new NoteNotFoundException("No such note found in trash");
		}

		if (delete) {
			noteRepository.deleteById(noteId);
		} else {
			Note note = optionalNote.get();
			note.setTrash(false);
			noteRepository.save(note);
		}

	}

	/**
	 * delete all notes in trash
	 * 
	 * @param token
	 */
	@Override
	public void emptyTrash(String token) {
		String userId = NoteUtility.parseJWT(token);

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			if (noteList.get(i).getTrash())
				noteRepository.deleteById(note.getNoteId());
		}
	}

	/**
	 * to add reminder to the note
	 * 
	 * @param token
	 * @param noteId
	 * @param reminderDate
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 */
	@Override
	public void addNoteReminder(String token, String noteId, Date reminderDate)
			throws NoteNotFoundException, UnauthorizedException, ReminderException {
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		NoteUtility.validateDate(reminderDate);

		Note note = optionalNote.get();
		note.setReminder(reminderDate);
		note.setLastUpdated(NoteUtility.getCurrentDate());

		noteRepository.save(note);
	}

	/**
	 * to remove reminder from the note
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void removeReminder(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}
		Note note = optionalNote.get();
		note.setReminder(null);
		note.setLastUpdated(NoteUtility.getCurrentDate());

		noteRepository.save(note);
	}

	/**
	 * Add pin to the note
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void addPinToNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setPin(true);

		noteRepository.save(note);
	}

	/**
	 * Removes pin on the note
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void removePinOnNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setPin(false);

		noteRepository.save(note);
	}

	/**
	 * Adding note to archive
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void archiveNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setArchive(true);

		noteRepository.save(note);
	}

	/**
	 * remove note from archive
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void removeArchiveNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException {
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setArchive(false);

		noteRepository.save(note);
	}

	/**
	 * View archived notes
	 * 
	 * @param token
	 * @return list of archived notes
	 * 
	 */
	@Override
	public List<ViewNote> viewArchivedNote(String token) {
		String userId = NoteUtility.parseJWT(token);

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		List<ViewNote> viewArchiveNoteList = new LinkedList<>();
		for (int i = 0; i < noteList.size(); i++) {
			if (!noteList.get(i).getTrash() && noteList.get(i).getArchive()) {
				ViewNote viewNote = new ViewNote();
				Note note = noteList.get(i);
				viewNote.setTitle(note.getTitle());
				viewNote.setDescription(note.getDescription());
				viewNote.setCreatedAt(note.getCreatedAt());
				viewNote.setLastUpdated(note.getLastUpdated());
				viewNote.setReminder(note.getReminder());
				viewArchiveNoteList.add(viewNote);
			}
		}
		return viewArchiveNoteList;
	}
	
	/**
	 * To create a new Label
	 * @param token
	 * @param lableName
	 * @throws LabelException
	 */
	@Override
	public void createNewLabel(String token, String labelName) throws LabelException {
		if(labelName == null || labelName.trim().length() == 0) {
			throw new LabelException("Invalid LabelName");
		}
		String userId = NoteUtility.parseJWT(token);
		
		Optional<Label> optionalLabel = labelRepository.findByLabelName(labelName);
		if(optionalLabel.isPresent() && optionalLabel.get().getUserId().equals(userId)) {
			throw new LabelException("Label with this name already exists");
		}
		
		Label label = new Label();
		label.setLabelName(labelName);
		label.setUserId(userId);
		
		labelRepository.save(label);
	}
	
	/**
	 * add label to the note
	 * @param token
	 * @param noteId
	 * @return list of note
	 * @throws NoteNotFoundException 
	 * @throws LabelNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelException 
	 */
	@Override
	public void addLabelToNote(String token, String noteId,String labelName) throws NoteNotFoundException, UnauthorizedException, LabelException {
		if(labelName == null || labelName.trim().length() == 0) {
			throw new LabelException("Invalid LabelName");
		}
		String userId = NoteUtility.parseJWT(token);

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}
		
		List<Label> listOfLabel = labelRepository.findAllByUserId(userId);
		
		for(int i=0; i<listOfLabel.size(); i++) {
			if(listOfLabel.get(i).getLabelName().equalsIgnoreCase(labelName)) {
				throw new LabelException("Label with this name already exists");
			}
		}
		
		Note note = optionalNote.get();
		ArrayList<ViewLabel>  listOfNoteLabel = (ArrayList<ViewLabel>) note.getListOfLabel();
		Label label = new Label();
		label.setLabelName(labelName);
		label.setUserId(userId);
		labelRepository.save(label);
		ViewLabel viewLabel = modelMapper.map(label, ViewLabel.class);
		listOfNoteLabel.add(viewLabel);
		note.setListOfLabel(listOfNoteLabel);
		
		noteRepository.save(note);
	}
	
	@Override
	public void updateLabel(String token, String labelId, String labelName) throws UnauthorizedException, LabelNotFoundException {
		String userId = NoteUtility.parseJWT(token);
		
		Optional<Label> optionalLabel = labelRepository.findById(labelId);
		
		if(!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}
		if(!optionalLabel.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorized to access the label");
		}
		
		Label label = optionalLabel.get();
		label.setLabelName(labelName);
		
		labelRepository.save(label);
		
		ArrayList<Note> noteListByUserId = (ArrayList<Note>) noteRepository.findAllByUserId(userId);
		
		for(int i=0; i<noteListByUserId.size(); i++) {
			Note note = noteListByUserId.get(i);
			
		}
	}
}
