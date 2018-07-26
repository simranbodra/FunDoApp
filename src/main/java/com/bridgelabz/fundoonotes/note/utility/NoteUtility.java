package com.bridgelabz.fundoonotes.note.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class NoteUtility {

	/**
	 * validation for the new note creation
	 * 
	 * @param note
	 * @return boolean
	 * @throws NoteException
	 */
	public static void validateNewNote(CreateNote note) throws NoteException {
		if ((note.getTitle() == null || note.getTitle().trim().length() == 0) && (note.getDescription() == null)
				|| note.getDescription().trim().length() == 0
						&& (note.getColour() == null || note.getColour().trim().length() == 0)) {
			throw new NoteException("Title ,Description and Colour cannot be empty");
		}
	}

	/**
	 * to parse the JWToken
	 * 
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
	 * 
	 * @return Date
	 */
	public static Date getCurrentDate() {
		Date date = new Date();
		return date;
	}
	
	public static boolean validateDate(String date) throws ReminderException, ParseException {
		System.out.println(date);
		Date reminder = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(date);
		System.err.println(reminder);
		System.out.println(reminder);
		if(reminder.before(getCurrentDate())) {
			throw new ReminderException("Date and time should be current date and time or after");
		}
		return true;
	}

}
