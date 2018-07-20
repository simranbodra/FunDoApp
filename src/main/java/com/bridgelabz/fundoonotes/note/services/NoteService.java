package com.bridgelabz.fundoonotes.note.services;

import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.ViewNote;

public interface NoteService {

	public ViewNote createNewNote(CreateNote newNote, String jwToken) throws NoteException;

	public void updateNote(UpdateNote updateNote, String token, String noteId) throws NoteException, NoteNotFoundException, UnauthorizedException;

	public void deleteNote(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void permanentNoteDelete(String token, String noteId) throws NoteNotFoundException, UnauthorizedException;

	public void emptyNoteTrash(String token) throws NoteNotFoundException;
}
