package com.bridgelabz.fundoonotes.note.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.Label;
import com.bridgelabz.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.fundoonotes.note.models.Note;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.NoteDTO;
import com.bridgelabz.fundoonotes.note.repositories.LabelElasticsearchRepository;
//import com.bridgelabz.fundoonotes.note.repositories.LabelElasticsearchRepository;
import com.bridgelabz.fundoonotes.note.repositories.LabelRepository;
import com.bridgelabz.fundoonotes.note.repositories.NoteElasticsearchRepository;
//mport com.bridgelabz.fundoonotes.note.repositories.NoteElasticsearchRepository;
import com.bridgelabz.fundoonotes.note.repositories.NoteRepository;
import com.bridgelabz.fundoonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private NoteElasticsearchRepository noteElasticsearchRepository;

	@Autowired
	private LabelElasticsearchRepository labelElasticsearchRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private Environment environment;

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
	@Override
	public NoteDTO createNote(CreateNote newNote, String userId)
			throws NoteException, ReminderException, ParseException {
		NoteUtility.validateNewNote(newNote);

		Note note = new Note();
		note.setTitle(newNote.getTitle());
		note.setDescription(newNote.getDescription());
		note.setCreatedAt(NoteUtility.getCurrentDate());
		note.setLastUpdated(NoteUtility.getCurrentDate());
		note.setUserId(userId);

		if (newNote.getColour() != null || newNote.getColour().trim().length() != 0) {
			note.setColour(newNote.getColour());
		}
		if (newNote.getReminder() != null || newNote.getReminder().trim().length() != 0) {
			NoteUtility.validateDate(newNote.getReminder());
			note.setReminder(newNote.getReminder());
		}

		note.setPin(newNote.getPin());
		note.setArchive(newNote.getArchive());

		ArrayList<String> labelNameList = (ArrayList<String>) newNote.getListOfLabel();

		List<Label> userLabels = labelElasticsearchRepository.findAllByUserId(userId);

		ArrayList<String> userLabelList = new ArrayList<>();
		for (int j = 0; j < userLabels.size(); j++) {
			userLabelList.add(userLabels.get(j).getLabelName());
		}
		ArrayList<LabelDTO> toBeAddedLabels = new ArrayList<>();

		for (int i = 0; i < labelNameList.size(); i++) {
			if (labelNameList.get(i) != null || labelNameList.get(i).trim().equals("")) {

				if (!userLabelList.contains(labelNameList.get(i))) {
					Label label = new Label();
					label.setLabelName(labelNameList.get(i));
					label.setUserId(userId);

					labelRepository.save(label);

					labelElasticsearchRepository.save(label);

					LabelDTO viewLabelToSave = new LabelDTO();
					viewLabelToSave.setLabelId(label.getLabelId());
					viewLabelToSave.setLabelName(label.getLabelName());
					toBeAddedLabels.add(viewLabelToSave);

				} else {
					List<Label> optionalLabelToSave = labelElasticsearchRepository
							.findAllByLabelName(labelNameList.get(i));

					for (int j = 0; j < optionalLabelToSave.size(); j++) {
						if (optionalLabelToSave.get(j).getUserId().equalsIgnoreCase(userId)) {
							LabelDTO viewLabel = new LabelDTO();
							viewLabel.setLabelName(optionalLabelToSave.get(j).getLabelName());
							viewLabel.setLabelId(optionalLabelToSave.get(j).getLabelId());
							toBeAddedLabels.add(viewLabel);
						}
					}

				}

			}
		}

		note.setListOfLabel(toBeAddedLabels);

		noteRepository.save(note);
		
		noteElasticsearchRepository.save(note);

		NoteDTO noteDto = modelMapper.map(note, NoteDTO.class);
		return noteDto;
	}

	/**
	 * view a note
	 * 
	 * @param userId
	 * @param noteId
	 * @return ViewNoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public NoteDTO getNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();

		NoteDTO noteDto = new NoteDTO();
		noteDto.setNoteId(note.getNoteId());
		noteDto.setTitle(note.getTitle());
		noteDto.setDescription(note.getDescription());
		noteDto.setCreatedAt(note.getCreatedAt());
		noteDto.setLastUpdated(note.getLastUpdated());
		noteDto.setReminder(note.getReminder());
		noteDto.setArchive(note.getArchive());
		noteDto.setPin(note.getPin());
		noteDto.setListOfLabel(note.getListOfLabel());

		return noteDto;

	}

	/**
	 * view a list of note owned by the user
	 * 
	 * @param userId
	 * @return list of ViewNoteDTO
	 * @throws NoteNotFoundException
	 */
	public List<NoteDTO> getAllNotes(String userId) throws NoteNotFoundException {

		List<Note> noteList = noteElasticsearchRepository.findAllByUserId(userId);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		List<NoteDTO> viewNoteList = new LinkedList<>();
		for (int i = 0; i < noteList.size(); i++) {
			if (!noteList.get(i).getTrash() && noteList.get(i).getPin()) {
				NoteDTO noteDto = new NoteDTO();
				Note note = noteList.get(i);

				noteDto.setNoteId(note.getNoteId());
				noteDto.setTitle(note.getTitle());
				noteDto.setDescription(note.getDescription());
				noteDto.setCreatedAt(note.getCreatedAt());
				noteDto.setLastUpdated(note.getLastUpdated());
				noteDto.setReminder(note.getReminder());
				noteDto.setArchive(note.getArchive());
				noteDto.setPin(note.getPin());
				noteDto.setListOfLabel(note.getListOfLabel());

				viewNoteList.add(noteDto);
			}
		}

		for (int j = 0; j < noteList.size(); j++) {
			if (!noteList.get(j).getTrash() && !noteList.get(j).getPin()) {
				NoteDTO noteDto = new NoteDTO();
				Note note = noteList.get(j);

				noteDto.setNoteId(note.getNoteId());
				noteDto.setTitle(note.getTitle());
				noteDto.setDescription(note.getDescription());
				noteDto.setCreatedAt(note.getCreatedAt());
				noteDto.setLastUpdated(note.getLastUpdated());
				noteDto.setReminder(note.getReminder());
				noteDto.setArchive(note.getArchive());
				noteDto.setPin(note.getPin());
				noteDto.setListOfLabel(note.getListOfLabel());

				viewNoteList.add(noteDto);
			}
		}
		return viewNoteList;

	}

	/**
	 * update a note
	 * 
	 * @param updateNoteDTO
	 * @param userId
	 * @param noteId
	 * @throws NoteException
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	@Override
	public void updateNote(UpdateNote updateNote, String userId, String noteId)
			throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException, ParseException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}
		Note note = optionalNote.get();
		if (updateNote.getTitle() != null || updateNote.getTitle().trim().length() != 0) {
			note.setTitle(updateNote.getTitle());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (updateNote.getDescription() != null || updateNote.getDescription().trim().length() != 0) {
			note.setDescription(updateNote.getDescription());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (NoteUtility.validateDate(updateNote.getReminder())) {
			note.setReminder(updateNote.getReminder());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (updateNote.getColour() != null || updateNote.getColour().trim().length() != 0) {
			note.setColour(updateNote.getColour());
		}

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * move a note to trash
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void deleteNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setTrash(true);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * delete a note from trash
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void permanentNoteDelete(String userId, String noteId, boolean delete)
			throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		if (!optionalNote.get().getTrash()) {
			throw new NoteNotFoundException("No such note found in trash");
		}

		if (delete) {
			noteRepository.deleteById(noteId);
			
			noteElasticsearchRepository.deleteById(noteId);
		} else {
			Note note = optionalNote.get();
			note.setTrash(false);
			noteRepository.save(note);

			noteElasticsearchRepository.save(note);
		}

	}

	/**
	 * delete all notes in trash
	 * 
	 * @param userId
	 * @throws NoteNotFoundException
	 */
	@Override
	public void emptyTrash(String userId) throws NoteNotFoundException {

		List<Note> noteList = noteElasticsearchRepository.findAllByUserId(userId);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			if (noteList.get(i).getTrash()) {
				noteRepository.deleteById(note.getNoteId());
				
				noteElasticsearchRepository.deleteById(note.getNoteId());
			}

		}
	}

	/**
	 * To view all trashed notes
	 * 
	 * @param userId
	 * @return list of notes
	 * @throws NoteNotFoundException
	 * 
	 */
	@Override
	public List<NoteDTO> getTrash(String userId) throws NoteNotFoundException {
		List<Note> noteList = noteElasticsearchRepository.findAllByUserId(userId);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No note found");
		}

		List<NoteDTO> viewNoteList = new LinkedList<>();
		for (int i = 0; i < noteList.size(); i++) {
			if (noteList.get(i).getTrash()) {
				NoteDTO noteDto = new NoteDTO();
				Note note = noteList.get(i);

				noteDto.setNoteId(note.getNoteId());
				noteDto.setTitle(note.getTitle());
				noteDto.setDescription(note.getDescription());
				noteDto.setCreatedAt(note.getCreatedAt());
				noteDto.setLastUpdated(note.getLastUpdated());
				noteDto.setReminder(note.getReminder());
				noteDto.setArchive(note.getArchive());
				noteDto.setPin(note.getPin());
				noteDto.setListOfLabel(note.getListOfLabel());

				viewNoteList.add(noteDto);
			}
		}
		return viewNoteList;
	}

	/**
	 * To set color on the note
	 * 
	 * @param userId
	 * @param noteId
	 * @param colour
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws NoteException
	 */
	@Override
	public void addColour(String userId, String noteId, String colour)
			throws NoteNotFoundException, UnauthorizedException, NoteException {
		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		if (colour == null || colour.trim().length() == 0) {
			throw new NoteException("Color cannot be empty");
		}
		Note note = optionalNote.get();
		note.setColour(colour);
		note.setLastUpdated(NoteUtility.getCurrentDate());

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * to add reminder to the note
	 * 
	 * @param userId
	 * @param noteId
	 * @param reminderDate
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	@Override
	public void addNoteReminder(String userId, String noteId, String reminderDate)
			throws NoteNotFoundException, UnauthorizedException, ReminderException, ParseException {

		NoteUtility.validateDate(reminderDate);

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setReminder(reminderDate);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * to remove reminder from the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void removeReminder(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setReminder(null);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * Add pin to the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void addPinToNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setArchive(false);
		note.setPin(true);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * Removes pin on the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void removePinOnNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setPin(false);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * Adding note to archive
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void archiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setPin(false);
		note.setArchive(true);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * remove note from archive
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void removeArchiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setArchive(false);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * View archived notes
	 * 
	 * @param userId
	 * @return list of archived notes
	 * @throws NoteNotFoundException
	 * 
	 */
	@Override
	public List<NoteDTO> viewArchivedNote(String userId) throws NoteNotFoundException {

		List<Note> noteList = noteElasticsearchRepository.findAllByUserId(userId);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No note found");
		}

		List<NoteDTO> viewArchiveNoteList = new LinkedList<>();
		for (int i = 0; i < noteList.size(); i++) {
			if (!noteList.get(i).getTrash() && noteList.get(i).getArchive()) {
				NoteDTO viewNote = new NoteDTO();
				Note note = noteList.get(i);
				viewNote.setNoteId(note.getNoteId());
				viewNote.setTitle(note.getTitle());
				viewNote.setDescription(note.getDescription());
				viewNote.setCreatedAt(note.getCreatedAt());
				viewNote.setLastUpdated(note.getLastUpdated());
				viewNote.setReminder(note.getReminder());
				viewArchiveNoteList.add(viewNote);
			}
		}
		return viewArchiveNoteList;
	}

	/**
	 * To create a new Label
	 * 
	 * @param userId
	 * @param lableName
	 * @return LabelDTO
	 * @throws LabelException
	 */
	@Override
	public LabelDTO createNewLabel(String userId, String labelName) throws LabelException {
		if (labelName == null || labelName.trim().length() == 0) {
			throw new LabelException("Invalid LabelName");
		}

		Optional<Label> optionalLabel = labelElasticsearchRepository.findByLabelName(labelName);
		if (optionalLabel.isPresent() && optionalLabel.get().getUserId().equals(userId)) {
			throw new LabelException("Label with this name already exists");
		}

		Label label = new Label();
		label.setLabelName(labelName);
		label.setUserId(userId);

		labelRepository.save(label);

		labelElasticsearchRepository.save(label);

		LabelDTO labelDto = new LabelDTO();
		labelDto.setLabelId(label.getLabelId());
		labelDto.setLabelName(labelName);

		return labelDto;
	}

	/**
	 * To get all the labels created
	 * 
	 * @param userId
	 * @param labelId
	 * @return list of labelDTO
	 * @throws LabelNotFoundException
	 */
	@Override
	public List<LabelDTO> getAllLabel(String userId) throws LabelNotFoundException {
		List<Label> listOfLabel = labelElasticsearchRepository.findAllByUserId(userId);

		if (listOfLabel.isEmpty()) {
			throw new LabelNotFoundException("No Labels found");
		}

		List<LabelDTO> viewLabelList = new ArrayList<>();
		for (int i = 0; i < listOfLabel.size(); i++) {
			LabelDTO labelDto = new LabelDTO();
			labelDto.setLabelId(listOfLabel.get(i).getLabelId());
			labelDto.setLabelName(listOfLabel.get(i).getLabelName());
			viewLabelList.add(labelDto);
		}
		return viewLabelList;
	}

	/**
	 * add label to the note
	 * 
	 * @param userId
	 * @param noteId
	 * @return list of note
	 * @throws NoteNotFoundException
	 * @throws LabelNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelException
	 */
	@Override
	public void addLabelToNote(String userId, String noteId, String labelName)
			throws NoteNotFoundException, UnauthorizedException, LabelException, LabelNotFoundException {
		if (labelName == null || labelName.trim().length() == 0) {
			throw new LabelException("Invalid LabelName");
		}

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		List<Label> listOfLabel = labelElasticsearchRepository.findAllByUserId(userId);

		if (listOfLabel.isEmpty()) {
			throw new LabelNotFoundException("No label found");
		}

		Note note = optionalNote.get();
		ArrayList<LabelDTO> listOfNoteLabel = (ArrayList<LabelDTO>) note.getListOfLabel();

		List<String> labelNames = new ArrayList<>();
		for (int j = 0; j < listOfLabel.size(); j++) {
			labelNames.add(listOfLabel.get(j).getLabelName());
		}

		if (!labelNames.contains(labelName)) {
			Label label = new Label();
			label.setLabelName(labelName);
			label.setUserId(userId);

			labelRepository.save(label);

			labelElasticsearchRepository.save(label);

			LabelDTO viewLabel = modelMapper.map(label, LabelDTO.class);
			listOfNoteLabel.add(viewLabel);
		} else {
			List<String> noteLabelNames = new ArrayList<>();
			for (int i = 0; i < note.getListOfLabel().size(); i++) {
				noteLabelNames.add(note.getListOfLabel().get(i).getLabelName());
			}
			if (!noteLabelNames.contains(labelName)) {
				Label label = labelElasticsearchRepository.findByLabelName(labelName).get();

				LabelDTO viewLabel = modelMapper.map(label, LabelDTO.class);

				listOfNoteLabel.add(viewLabel);
			}
		}

		note.setListOfLabel(listOfNoteLabel);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * To update the label name
	 * 
	 * @param userId
	 * @param labelId
	 * @param labelName
	 * @throws LabelNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void updateLabel(String userId, String labelId, String labelName)
			throws UnauthorizedException, LabelNotFoundException {

		Optional<Label> optionalLabel = labelElasticsearchRepository.findById(labelId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}
		if (!optionalLabel.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorized to access the label");
		}

		Label label = optionalLabel.get();
		label.setLabelName(labelName);

		labelRepository.save(label);

		labelElasticsearchRepository.save(label);

		List<Note> noteListByUserId = noteElasticsearchRepository.findAllByUserId(userId);

		for (int i = 0; i < noteListByUserId.size(); i++) {
			Note note = noteListByUserId.get(i);
			for (int j = 0; j < note.getListOfLabel().size(); j++) {
				if (note.getListOfLabel().get(j).getLabelId().equals(labelId)) {
					note.getListOfLabel().get(j).setLabelName(labelName);
					noteRepository.save(note);

					noteElasticsearchRepository.save(note);
				}
			}
		}
	}

	/**
	 * To delete label
	 * 
	 * @param userId
	 * @param labelId
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	@Override
	public void deleteLabel(String userId, String labelId) throws UnauthorizedException, LabelNotFoundException {
		Optional<Label> optionalLabel = labelElasticsearchRepository.findById(labelId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}
		if (!optionalLabel.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorized to access the label");
		}

		labelElasticsearchRepository.deleteById(labelId);

		labelRepository.deleteById(labelId);

		ArrayList<Note> noteListByUserId = (ArrayList<Note>) noteElasticsearchRepository.findAllByUserId(userId);

		for (int i = 0; i < noteListByUserId.size(); i++) {
			Note note = noteListByUserId.get(i);
			for (int j = 0; j < note.getListOfLabel().size(); j++) {
				if (note.getListOfLabel().get(j).getLabelId().equals(labelId)) {
					note.getListOfLabel().remove(i);

					noteRepository.save(note);

					noteElasticsearchRepository.save(note);
				}
			}
		}
	}

	/**
	 * to delete a label from the note
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	@Override
	public void deleteLabelFromNote(String userId, String noteId, String labelId)
			throws NoteNotFoundException, UnauthorizedException, LabelNotFoundException {

		Optional<Note> optionalNote = noteElasticsearchRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Optional<Label> optionalLabel = labelElasticsearchRepository.findById(labelId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

		Note note = optionalNote.get();

		if (!note.getTrash()) {
			List<LabelDTO> labelList = note.getListOfLabel();
			for (int i = 0; i < labelList.size(); i++) {
				if (labelList.get(i).getLabelId().equals(labelId)) {
					labelList.remove(i);
				}
			}
			note.setListOfLabel(labelList);
		}

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * To get all notes by label Id
	 * 
	 * @param userId
	 * @param labelId
	 * @return list of note
	 * @throws LabelNotFoundException
	 */
	@Override
	public List<NoteDTO> getLabel(String userId, String labelId) throws LabelNotFoundException {
		Optional<Label> optionalLabel = labelElasticsearchRepository.findById(labelId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

		List<Note> noteListByUserId = noteElasticsearchRepository.findAllByUserId(userId);

		List<NoteDTO> noteList = new ArrayList<>();

		for (int i = 0; i < noteListByUserId.size(); i++) {
			Note note = noteListByUserId.get(i);
			if (!noteListByUserId.get(i).getTrash()) {
				for (int j = 0; j < note.getListOfLabel().size(); j++) {
					if (note.getListOfLabel().get(j).getLabelId().equals(labelId)) {

						NoteDTO noteDto = new NoteDTO();
						noteDto.setNoteId(note.getNoteId());
						noteDto.setTitle(note.getTitle());
						noteDto.setDescription(note.getDescription());
						noteDto.setCreatedAt(note.getCreatedAt());
						noteDto.setLastUpdated(note.getLastUpdated());
						noteDto.setReminder(note.getReminder());
						noteDto.setArchive(note.getArchive());
						noteDto.setPin(note.getPin());
						noteDto.setListOfLabel(note.getListOfLabel());

						noteList.add(noteDto);
					}
				}
			}
		}
		return noteList;
	}
}
