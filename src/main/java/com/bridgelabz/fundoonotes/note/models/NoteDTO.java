package com.bridgelabz.fundoonotes.note.models;

import java.util.Date;
import java.util.List;

public class NoteDTO {

	private String noteId;
	private String title;
	private String description;
	private Date createdAt;
	private Date lastUpdated;
	private String reminder;
	private boolean pin;
	private boolean archive;
	private List<LabelDTO> listOfLabel;

	public NoteDTO() {
		super();
	}

	public String getNoteId() {
		return noteId;
	}
	
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}
	
	public boolean getPin() {
		return this.pin;
	}
	
	public void setPin(boolean pin) {
		this.pin = pin;
	}
	
	public boolean getArchive() {
		return this.archive;
	}
	
	public void setArchive(boolean archive) {
		this.archive = archive;
	}
	
	public List<LabelDTO> getListOfLabel(){
		return this.listOfLabel;
	}
	
	public void setListOfLabel(List<LabelDTO> listOfLabel) {
		this.listOfLabel = listOfLabel;
	}
}
