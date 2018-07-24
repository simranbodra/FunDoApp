package com.bridgelabz.fundoonotes.note.models;

import java.util.Date;
import java.util.List;

public class ViewNote {

	private String title;
	private String description;
	private Date createdAt;
	private Date lastUpdated;
	private Date reminder;
	private boolean pin;
	private boolean archive;
	private List<String> listOfLabel;

	public ViewNote() {
		super();
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

	public Date getReminder() {
		return reminder;
	}

	public void setReminder(Date reminder) {
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
	
	public List<String> getListOfLabel(){
		return this.listOfLabel;
	}
	
	public void setListOfLabel(List<String> listOfLabel) {
		this.listOfLabel = listOfLabel;
	}
}
