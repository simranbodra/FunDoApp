package com.bridgelabz.fundoonotes.note.services;

import java.util.Date;
import java.util.List;

import com.bridgelabz.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.ViewNote;

public interface NoteService {

	public ViewNote createNewNote(CreateNote newNote, String jwToken) throws NoteException;

	public void updateNote(UpdateNote updateNote, String token, String noteId) throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException;

	public void deleteNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void permanentNoteDelete(String token, String noteId, boolean restore) throws NoteNotFoundException, UnauthorizedException;

	public ViewNote viewNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public List<ViewNote> viewAllNote(String token) throws NoteNotFoundException;

	public void emptyTrash(String token);

	public void addNoteReminder(String token, String noteId, Date reminderDate) throws NoteNotFoundException, UnauthorizedException, ReminderException;

	public void removeReminder(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void addPinToNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void removePinOnNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void archiveNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void removeArchiveNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public List<ViewNote> viewArchivedNote(String token);

	public void createNewLabel(String token, String labelName) throws LabelException;

	public void addLabelToNote(String token, String noteId, String labelName) throws NoteNotFoundException, UnauthorizedException, LabelException;

	public void updateLabel(String token, String labelId, String labelName) throws UnauthorizedException, LabelNotFoundException;

}
