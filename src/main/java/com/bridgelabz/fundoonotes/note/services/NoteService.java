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
import com.bridgelabz.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.NoteDTO;

public interface NoteService {

	/**
	 * create a new note
	 * 
	 * @param newNote
	 * @param jwToken
	 * @return ViewNoteDTO
	 * @throws NoteException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	public NoteDTO createNewNote(CreateNote newNote, String userId) throws NoteException, ReminderException, ParseException;

	/**
	 * To update a note
	 * @param updateNote
	 * @param userId
	 * @param noteId
	 * @throws NoteException
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	public void updateNote(UpdateNote updateNote, String userId, String noteId) throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException, ParseException;

	/**
	 * Move note to trash
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void deleteNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To remove note from trash
	 * @param userId
	 * @param noteId
	 * @param restore
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void permanentNoteDelete(String userId, String noteId, boolean restore) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To get a note
	 * @param userId
	 * @param noteId
	 * @return NoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public NoteDTO getNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To get list of notes
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 */
	public List<NoteDTO> getAllNotes(String userId) throws NoteNotFoundException;

	/**
	 * To empty trash
	 * @param userId
	 * @throws NoteNotFoundException
	 */
	public void emptyTrash(String userId) throws NoteNotFoundException;
	
	/**
	 * To get trash notes
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 */
	public List<NoteDTO> getTrash(String userId) throws NoteNotFoundException;

	/**
	 * To add reminder to note
	 * @param userId
	 * @param noteId
	 * @param reminderDate
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	public void addNoteReminder(String userId, String noteId, String reminderDate) throws NoteNotFoundException, UnauthorizedException, ReminderException, ParseException;

	/**
	 * To remove reminder from note
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void removeReminder(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To pin a note
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void addPinToNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To remove pin on note
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void removePinOnNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To archive a note
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void archiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * TO remove note from archive
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void removeArchiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To get archive notes 
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 */
	public List<NoteDTO> viewArchivedNote(String userId) throws NoteNotFoundException;

	/**
	 * To create a label
	 * @param userId
	 * @param labelName
	 * @throws LabelException
	 */
	public void createNewLabel(String userId, String labelName) throws LabelException;
	
	/**
	 * To get list of label
	 * @param userId
	 * @return list of LabelDTO
	 * @throws LabelNotFoundException
	 */
	public List<LabelDTO> getAllLabel(String userId) throws LabelNotFoundException;

	/**
	 * To add a label to note
	 * @param userId
	 * @param noteId
	 * @param labelName
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelException
	 * @throws LabelNotFoundException
	 */
	public void addLabelToNote(String userId, String noteId, String labelName) throws NoteNotFoundException, UnauthorizedException, LabelException, LabelNotFoundException;

	/**
	 * TO update a label
	 * @param userId
	 * @param labelId
	 * @param labelName
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	public void updateLabel(String userId, String labelId, String labelName) throws UnauthorizedException, LabelNotFoundException;

	/**
	 * To delete a label
	 * @param userId
	 * @param labelId
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	public void deleteLabel(String userId, String labelId) throws UnauthorizedException, LabelNotFoundException;
	
	/**
	 * To delete a label from a note
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	public void deleteLabelFromNote(String userId, String noteId, String labelId) throws NoteNotFoundException, UnauthorizedException, LabelNotFoundException;

	/**
	 * To get notes of a label
	 * @param userId
	 * @param labelId
	 * @return
	 * @throws LabelNotFoundException
	 */
	public List<NoteDTO> getLabel(String userId, String labelId) throws LabelNotFoundException;


}
