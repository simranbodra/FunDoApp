package com.bridgelabz.fundoonotes.note.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.fundoonotes.note.models.CreateNote;
import com.bridgelabz.fundoonotes.note.models.Label;
import com.bridgelabz.fundoonotes.note.models.Note;
import com.bridgelabz.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.fundoonotes.note.models.NoteDTO;
import com.bridgelabz.fundoonotes.note.repositories.LabelRepository;
import com.bridgelabz.fundoonotes.note.repositories.NoteRepository;
import com.bridgelabz.fundoonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private ModelMapper modelMapper;

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
	public NoteDTO createNewNote(CreateNote newNote, String userId)
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

		List<Label> userLabels = labelRepository.findAllByUserId(userId);

		ArrayList<String> userLabelList = new ArrayList<>();
		for (int j = 0; j < userLabels.size(); j++) {
			userLabelList.add(userLabels.get(j).getLabelName());
		}
		ArrayList<LabelDTO> toBeAddedLabels = new ArrayList<>();

		for (int i = 0; i < labelNameList.size(); i++) {
			if (labelNameList.get(i) != null || labelNameList.get(i).trim().length() != 0) {

				if (!userLabelList.contains(labelNameList.get(i))) {
					Label label = new Label();
					label.setLabelName(labelNameList.get(i));
					label.setUserId(userId);

					labelRepository.save(label);

					Optional<Label> optionalLabelAfterSave = labelRepository.findByLabelName(labelNameList.get(i));

					LabelDTO viewLabelToSave = new LabelDTO();
					viewLabelToSave.setLabelId(optionalLabelAfterSave.get().getLabelId());
					viewLabelToSave.setLabelName(optionalLabelAfterSave.get().getLabelName());
					toBeAddedLabels.add(viewLabelToSave);
				} else {
					Optional<Label> optionalLabelToSave = labelRepository.findByLabelName(labelNameList.get(i));
					LabelDTO viewLabel = new LabelDTO();
					viewLabel.setLabelName(optionalLabelToSave.get().getLabelName());
					viewLabel.setLabelId(optionalLabelToSave.get().getLabelId());
					toBeAddedLabels.add(viewLabel);
				}

			}
		}

		note.setListOfLabel(toBeAddedLabels);

		noteRepository.save(note);

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

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
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

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		List<NoteDTO> viewNoteList = new LinkedList<>();
		for (int i = 0; i < noteList.size(); i++) {
			if (!noteList.get(i).getTrash()) {
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

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
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

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setTrash(true);

		noteRepository.save(note);
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

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}
		if (!optionalNote.get().getTrash()) {
			throw new NoteNotFoundException("No such note found in trash");
		}

		if (delete) {
			noteRepository.deleteById(noteId);
		} else {
			Note note = optionalNote.get();
			note.setTrash(false);
			noteRepository.save(note);
		}

	}

	/**
	 * delete all notes in trash
	 * 
	 * @param userId
	 */
	@Override
	public void emptyTrash(String userId) {

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			if (noteList.get(i).getTrash())
				noteRepository.deleteById(note.getNoteId());
		}
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

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		NoteUtility.validateDate(reminderDate);

		Note note = optionalNote.get();
		note.setReminder(reminderDate);
		note.setLastUpdated(NoteUtility.getCurrentDate());

		noteRepository.save(note);
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

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}
		Note note = optionalNote.get();
		note.setReminder(null);
		note.setLastUpdated(NoteUtility.getCurrentDate());

		noteRepository.save(note);
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

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setPin(true);

		noteRepository.save(note);
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

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setPin(false);

		noteRepository.save(note);
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

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setArchive(true);

		noteRepository.save(note);
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

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		Note note = optionalNote.get();
		note.setArchive(false);

		noteRepository.save(note);
	}

	/**
	 * View archived notes
	 * 
	 * @param userId
	 * @return list of archived notes
	 * 
	 */
	@Override
	public List<NoteDTO> viewArchivedNote(String userId) {

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		List<NoteDTO> viewArchiveNoteList = new LinkedList<>();
		for (int i = 0; i < noteList.size(); i++) {
			if (!noteList.get(i).getTrash() && noteList.get(i).getArchive()) {
				NoteDTO viewNote = new NoteDTO();
				Note note = noteList.get(i);
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
	 * @throws LabelException
	 */
	@Override
	public void createNewLabel(String userId, String labelName) throws LabelException {
		if (labelName == null || labelName.trim().length() == 0) {
			throw new LabelException("Invalid LabelName");
		}

		Optional<Label> optionalLabel = labelRepository.findByLabelName(labelName);
		if (optionalLabel.isPresent() && optionalLabel.get().getUserId().equals(userId)) {
			throw new LabelException("Label with this name already exists");
		}

		Label label = new Label();
		label.setLabelName(labelName);
		label.setUserId(userId);

		labelRepository.save(label);
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
			throws NoteNotFoundException, UnauthorizedException, LabelException {
		if (labelName == null || labelName.trim().length() == 0) {
			throw new LabelException("Invalid LabelName");
		}

		if (!noteRepository.existsById(noteId)) {
			throw new NoteNotFoundException("Note with respective id not found");
		}
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorised to access the note");
		}

		List<Label> listOfLabel = labelRepository.findAllByUserId(userId);

		for (int i = 0; i < listOfLabel.size(); i++) {
			if (listOfLabel.get(i).getLabelName().equalsIgnoreCase(labelName)) {
				throw new LabelException("Label with this name already exists");
			}
		}

		Note note = optionalNote.get();
		ArrayList<LabelDTO> listOfNoteLabel = (ArrayList<LabelDTO>) note.getListOfLabel();
		Label label = new Label();
		label.setLabelName(labelName);
		label.setUserId(userId);
		labelRepository.save(label);
		LabelDTO viewLabel = modelMapper.map(label, LabelDTO.class);
		listOfNoteLabel.add(viewLabel);
		note.setListOfLabel(listOfNoteLabel);

		noteRepository.save(note);
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

		Optional<Label> optionalLabel = labelRepository.findById(labelId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}
		if (!optionalLabel.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorized to access the label");
		}

		Label label = optionalLabel.get();
		label.setLabelName(labelName);

		labelRepository.save(label);

		ArrayList<Note> noteListByUserId = (ArrayList<Note>) noteRepository.findAllByUserId(userId);

		for (int i = 0; i < noteListByUserId.size(); i++) {
			Note note = noteListByUserId.get(i);
			for (int j = 0; j < note.getListOfLabel().size(); j++) {
				if (note.getListOfLabel().get(j).getLabelId().equals(labelId)) {
					note.getListOfLabel().get(j).setLabelName(labelName);
					noteRepository.save(note);
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
		Optional<Label> optionalLabel = labelRepository.findById(labelId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}
		if (!optionalLabel.get().getUserId().equals(userId)) {
			throw new UnauthorizedException("User not authorized to access the label");
		}

		// Label label = optionalLabel.get();

		labelRepository.deleteById(labelId);

		ArrayList<Note> noteListByUserId = (ArrayList<Note>) noteRepository.findAllByUserId(userId);

		for (int i = 0; i < noteListByUserId.size(); i++) {
			Note note = noteListByUserId.get(i);
			for (int j = 0; j < note.getListOfLabel().size(); j++) {
				if (note.getListOfLabel().get(j).getLabelId().equals(labelId)) {
					note.getListOfLabel().remove(i);
					noteRepository.save(note);
				}
			}
		}
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
		Optional<Label> optionalLabel = labelRepository.findById(labelId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

		ArrayList<Note> noteListByUserId = (ArrayList<Note>) noteRepository.findAllByUserId(userId);

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
