package com.bridgelabz.fundoonotes.note.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.Reminder;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.ViewNote;
import com.bridgelabz.fundoonotes.note.services.NoteService;
import com.bridgelabz.fundoonotes.user.models.Response;

@RestController
@RequestMapping("/note")
public class NoteController {

	@Autowired
	private NoteService noteService;

	/**
	 * to create a new note
	 * 
	 * @param token
	 * @param newNote
	 * @return ViewNoteDTO
	 * @throws NoteException
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<ViewNote> createNote(@RequestHeader String token, @RequestBody CreateNote newNote)
			throws NoteException {

		ViewNote note = noteService.createNewNote(newNote, token);

		return new ResponseEntity<>(note, HttpStatus.CREATED);
	}

	/**
	 * open a note with given note Id
	 * 
	 * @param token
	 * @param noteId
	 * @return ViewNoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/open/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<ViewNote> openNote(@RequestHeader String token, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {

		ViewNote note = noteService.viewNote(token, noteId);

		return new ResponseEntity<>(note, HttpStatus.OK);
	}

	/**
	 * Open all note of user
	 * 
	 * @param token
	 * @return List of notes
	 * @throws NoteNotFoundException
	 */
	@RequestMapping(value = "/openAllNotes", method = RequestMethod.POST)
	public ResponseEntity<List<ViewNote>> openAllNotes(@RequestHeader String token) throws NoteNotFoundException {
		List<ViewNote> noteList = noteService.viewAllNote(token);

		return new ResponseEntity<>(noteList, HttpStatus.OK);
	}

	/**
	 * Update a note
	 * 
	 * @param token
	 * @param noteId
	 * @param updateNote
	 * @return ResponseDTO
	 * @throws NoteException
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException 
	 */
	@RequestMapping(value = "/update/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> updateNote(HttpServletRequest request, @PathVariable String noteId,
			@RequestBody UpdateNote updateNote) throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException {
		
		String userId = (String) request.getAttribute("token");
		System.out.println("UserId : " + userId);
		
		noteService.updateNote(updateNote, userId, noteId);

		Response response = new Response();
		response.setMessage("Note Successfully updated");
		response.setStatus(91);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * to move a note to trash
	 * 
	 * @param token
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/delete/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> deleteNote(@RequestHeader String token, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		noteService.deleteNote(token, noteId);

		Response response = new Response();
		response.setMessage("Note Successfully moved to trash");
		response.setStatus(92);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * delete note from trash
	 * 
	 * @param token
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/permanentDeleteRestore/{noteId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteFromTrash(@RequestHeader String token, @PathVariable String noteId, @RequestParam boolean delete)
			throws NoteNotFoundException, UnauthorizedException {
		noteService.permanentNoteDelete(token, noteId, delete);

		Response response = new Response();
		response.setMessage("Note deleted permanently");
		response.setStatus(93);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * to empty the trash notes
	 * @param token
	 * @return ResponseDTO
	 */
	@RequestMapping(value = "/emptyTrash", method = RequestMethod.DELETE)
	public ResponseEntity<Response> emptyTrash(@RequestHeader String token){
		
		noteService.emptyTrash(token);
		
		Response response = new Response();
		response.setMessage("Trash is emptied");
		response.setStatus(94);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * to add reminder to the note
	 * @param token
	 * @param noteId
	 * @param reminderDate
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 */
	@RequestMapping(value = "/addReminder/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addReminder(@RequestHeader String token, @PathVariable String noteId, @RequestBody Reminder reminder) throws NoteNotFoundException, UnauthorizedException, ReminderException{
		
		noteService.addNoteReminder(token, noteId, reminder.getReminderDate());
		
		Response response = new Response();
		response.setMessage("Reminder added to the note");
		response.setStatus(80);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * to remove reminder on the note
	 * @param token
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/removeReminder/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> removeReminder(@RequestHeader String token, @PathVariable String noteId) throws NoteNotFoundException, UnauthorizedException{
		
		noteService.removeReminder(token, noteId);
		
		Response response = new Response();
		response.setMessage("Removed reminder on the note");
		response.setStatus(81);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * To add pin to the note
	 * @param token
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/addPin/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addPin(@RequestHeader String token, @PathVariable String noteId) throws NoteNotFoundException, UnauthorizedException{
		
		noteService.addPinToNote(token, noteId);
		
		Response response = new Response();
		response.setMessage("Pinned the note");
		response.setStatus(70);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Remove pin on the note
	 * @param token
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/removePin/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> removePin(@RequestHeader String token, @PathVariable String noteId) throws NoteNotFoundException, UnauthorizedException{
		
		noteService.removePinOnNote(token, noteId);
		
		Response response = new Response();
		response.setMessage("Pin removed on the note");
		response.setStatus(71);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Add note to archive
	 * @param token
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/addToArchive/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addArchive(@RequestHeader String token, @PathVariable String noteId) throws NoteNotFoundException, UnauthorizedException{
		
		noteService.archiveNote(token, noteId);
		
		Response response = new Response();
		response.setMessage("Archived the note");
		response.setStatus(60);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * remove note from archive
	 * @param token
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/removeFromArchive/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> removeArchive(@RequestHeader String token, @PathVariable String noteId) throws NoteNotFoundException, UnauthorizedException{
		
		noteService.removeArchiveNote(token, noteId);
		
		Response response = new Response();
		response.setMessage("Remove archive note");
		response.setStatus(61);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * View archived notes
	 * @param token
	 * @return ResponseDTO
	 */
	@RequestMapping(value = "/viewArchiveNotes", method = RequestMethod.POST)
	public ResponseEntity<List<ViewNote>> viewArchiveNotes(@RequestHeader String token){
		List<ViewNote> archivedNoteList = noteService.viewArchivedNote(token);

		return new ResponseEntity<>(archivedNoteList, HttpStatus.OK);
	}
	
	/**
	 * to create a new Label
	 * @param token
	 * @param labelName
	 * @return ResponseDTO
	 * @throws LabelException
	 */
	@RequestMapping(value = "/createLabel", method = RequestMethod.POST)
	public ResponseEntity<Response> createLabel(@RequestHeader String token, @RequestParam String labelName) throws LabelException{
		
		noteService.createNewLabel(token, labelName);
		
		Response response = new Response();
		response.setMessage("New label created");
		response.setStatus(200);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Add label to the note
	 * @param token
	 * @param noteId
	 * @param labelName
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelException
	 */
	@RequestMapping(value = "/addLabelToNote/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addLabel(@RequestHeader String token, @PathVariable String noteId, @RequestParam String labelName) throws NoteNotFoundException, UnauthorizedException, LabelException{
		
		noteService.addLabelToNote(token, noteId, labelName);
		
		Response response = new Response();
		response.setMessage("Label added to the note");
		response.setStatus(201);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateLabel", method = RequestMethod.PUT)
	public ResponseEntity<Response> editLabel(@RequestHeader String token, @PathVariable String labelId, @RequestParam String labelName) throws UnauthorizedException, LabelNotFoundException{
		
		noteService.updateLabel(token, labelId, labelName);
		
		Response response = new Response();
		response.setMessage("Label edited");
		response.setStatus(202);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
