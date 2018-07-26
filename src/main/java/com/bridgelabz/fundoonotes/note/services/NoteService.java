package com.bridgelabz.fundoonotes.note.services;

import java.text.ParseException;
import java.util.List;

import com.bridgelabz.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.NoteDTO;

public interface NoteService {

	public NoteDTO createNewNote(CreateNote newNote, String userId) throws NoteException, ReminderException, ParseException;

	public void updateNote(UpdateNote updateNote, String userId, String noteId) throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException, ParseException;

	public void deleteNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void permanentNoteDelete(String userId, String noteId, boolean restore) throws NoteNotFoundException, UnauthorizedException;

	public NoteDTO getNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public List<NoteDTO> getAllNotes(String userId) throws NoteNotFoundException;

	public void emptyTrash(String userId);

	public void addNoteReminder(String userId, String noteId, String reminderDate) throws NoteNotFoundException, UnauthorizedException, ReminderException, ParseException;

	public void removeReminder(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void addPinToNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void removePinOnNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void archiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void removeArchiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public List<NoteDTO> viewArchivedNote(String userId);

	public void createNewLabel(String userId, String labelName) throws LabelException;

	public void addLabelToNote(String userId, String noteId, String labelName) throws NoteNotFoundException, UnauthorizedException, LabelException;

	public void updateLabel(String userId, String labelId, String labelName) throws UnauthorizedException, LabelNotFoundException;

	public void deleteLabel(String userId, String labelId) throws UnauthorizedException, LabelNotFoundException;

	public List<NoteDTO> getLabel(String userId, String labelId) throws LabelNotFoundException;

}
