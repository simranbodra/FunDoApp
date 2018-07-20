package com.bridgelabz.fundoonotes.note.services;

import java.util.List;

import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.Note;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.ViewNote;

public interface NoteService {

	public ViewNote createNewNote(CreateNote newNote, String jwToken) throws NoteException;

	public void updateNote(UpdateNote updateNote, String token, String noteId) throws NoteException, NoteNotFoundException, UnauthorizedException;

	public void deleteNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void permanentNoteDelete(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public ViewNote viewNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public List<ViewNote> viewAllNote(String token) throws NoteNotFoundException;

	//public void emptyNoteTrash(String token) throws NoteNotFoundException;
}
