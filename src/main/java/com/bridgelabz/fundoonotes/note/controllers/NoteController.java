package com.bridgelabz.fundoonotes.note.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.Note;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.ViewNote;
import com.bridgelabz.fundoonotes.note.services.NoteService;
import com.bridgelabz.fundoonotes.user.models.Response;

@RestController
@RequestMapping("/note")
public class NoteController {

	@Autowired
	private NoteService noteService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<ViewNote> createNote(@RequestHeader String token, @RequestBody CreateNote newNote)
			throws NoteException {

		ViewNote note = noteService.createNewNote(newNote, token);

		return new ResponseEntity<>(note, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/open/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<ViewNote> openNote(@RequestHeader String token, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {

		ViewNote note = noteService.viewNote(token, noteId);

		return new ResponseEntity<>(note, HttpStatus.OK);
	}
	
	public ResponseEntity<List<ViewNote>> openAllNotes(@RequestHeader String token) throws NoteNotFoundException{
		List<ViewNote> noteList = noteService.viewAllNote(token);
		
		return new ResponseEntity<>(noteList, HttpStatus.OK);
	}

	@RequestMapping(value = "/update/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> updateNote(@RequestHeader String token, @PathVariable String noteId,
			@RequestBody UpdateNote updateNote) throws NoteException, NoteNotFoundException, UnauthorizedException {
		noteService.updateNote(updateNote, token, noteId);

		Response response = new Response();
		response.setMessage("Note Successfully updated");
		response.setStatus(91);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{noteId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteNote(@RequestHeader String token, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		noteService.deleteNote(token, noteId);

		Response response = new Response();
		response.setMessage("Note Successfully moved to trash");
		response.setStatus(92);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/permanentDelete/{noteId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteFromTrash(@RequestHeader String token, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		noteService.permanentNoteDelete(token, noteId);

		Response response = new Response();
		response.setMessage("Note deleted permanently");
		response.setStatus(93);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/emptyTrash", method = RequestMethod.DELETE) public
	 * ResponseEntity<Response> emptyTrash(@RequestHeader String token) throws
	 * NoteNotFoundException{ noteService.emptyNoteTrash(token);
	 * 
	 * Response response = new Response();
	 * response.setMessage("Note deleted permanently"); response.setStatus(93);
	 * return new ResponseEntity<>(response, HttpStatus.OK); }
	 */
}
