package com.bridgelabz.fundoonotes.note.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.fundoonotes.note.models.NoteDTO;
import com.bridgelabz.fundoonotes.note.services.NoteService;
import com.bridgelabz.fundoonotes.user.models.Response;

@RestController
@RequestMapping("/labels")
public class LabelController {

	@Autowired
	private NoteService noteService;

	/**
	 * to create a new Label
	 * 
	 * @param request
	 * @param labelName
	 * @return ResponseDTO
	 * @throws LabelException
	 */
	@RequestMapping(value = "/createLabel", method = RequestMethod.POST)
	public ResponseEntity<LabelDTO> createLabel(HttpServletRequest request, @RequestParam String labelName)
			throws LabelException {
		String userId = (String) request.getAttribute("UserId");

		LabelDTO labelDto = noteService.createNewLabel(userId, labelName);

		return new ResponseEntity<>(labelDto, HttpStatus.OK);
	}

	/***
	 * To get all labels
	 * 
	 * @param request
	 * @param labelId
	 * @return list of LabelDTO
	 * @throws LabelNotFoundException
	 */
	@RequestMapping(value = "/getLabels", method = RequestMethod.POST)
	public ResponseEntity<List<LabelDTO>> getAllLabel(HttpServletRequest request) throws LabelNotFoundException {
		String userId = (String) request.getAttribute("UserId");

		List<LabelDTO> labelList = noteService.getAllLabel(userId);

		return new ResponseEntity<>(labelList, HttpStatus.OK);
	}

	/**
	 * to update label name
	 * 
	 * @param request
	 * @param labelId
	 * @param labelName
	 * @return ResponseDTO
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	@RequestMapping(value = "/updateLabel/{labelId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> editLabel(HttpServletRequest request, @PathVariable String labelId,
			@RequestParam String labelName) throws UnauthorizedException, LabelNotFoundException {
		String userId = (String) request.getAttribute("UserId");

		noteService.updateLabel(userId, labelId, labelName);

		Response response = new Response();
		response.setMessage("Label edited");
		response.setStatus(202);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To delete label
	 * 
	 * @param request
	 * @param labelId
	 * @return ResponseDTO
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	@RequestMapping(value = "/deleteLabel/{labelId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteLabel(HttpServletRequest request, @PathVariable String labelId)
			throws UnauthorizedException, LabelNotFoundException {
		String userId = (String) request.getAttribute("UserId");

		noteService.deleteLabel(userId, labelId);

		Response response = new Response();
		response.setMessage("Label deleted");
		response.setStatus(202);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To get all notes by label Id
	 * 
	 * @param request
	 * @param labelId
	 * @return List of NoteDTO
	 * @throws LabelNotFoundException
	 */
	@RequestMapping(value = "/getLabel{labelId}", method = RequestMethod.POST)
	public ResponseEntity<List<NoteDTO>> getLabel(HttpServletRequest request, @PathVariable String labelId)
			throws LabelNotFoundException {
		String userId = (String) request.getAttribute("UserId");

		List<NoteDTO> notes = noteService.getLabel(userId, labelId);

		return new ResponseEntity<>(notes, HttpStatus.OK);
	}
}
