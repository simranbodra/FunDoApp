package com.bridgelabz.fundoonotes.note.utility;

import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class NoteUtility {

	/**
	 * validation for the new note creation
	 * @param note
	 * @return boolean
	 * @throws NoteException
	 */
	public static void validateNewNote(CreateNote note) throws NoteException {
		if(note.getTitle() == null || note.getTitle().length() == 0) {
			throw new NoteException("Note title cannot be empty");
		}
		if(note.getDescription() == null || note.getDescription().length() == 0) {
			throw new NoteException("Description cannot be empty");
		}
	}
	
	/**
	 * to parse the JWToken
	 * @param jwtoken
	 * @return String containing userId
	 */
	public static String parseJWT(String jwtoken) {
		String key = "simran";

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key)).parseClaimsJws(jwtoken)
				.getBody();

		return claims.getId();
	}
	/**
	 * to get the current system date;
	 * @return Date
	 */
	public static Date getCurrentDate() {
		Date date = new Date();
		return date;
	}

	public static void validateNoteForUpdate(UpdateNote updateNote) throws NoteException {
		if((updateNote.getTitle() == null || updateNote.getTitle().length() == 0)&& (updateNote.getDescription() == null) || updateNote.getDescription().length() == 0) {
			throw new NoteException("Both title and description cannot be empty");
		}
	}
}
