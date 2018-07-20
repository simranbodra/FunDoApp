package com.bridgelabz.fundoonotes.note.models;

import java.util.Date;

public class CreateNote {

	private String title;
	private String description;
	private String colour = "white";
	private Date reminder;

	public CreateNote() {
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
	
	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}
	
	public Date getReminder() {
		return reminder;
	}
	
	public void setReminder(Date reminder) {
		this.reminder = reminder;
	}
}
