package com.bridgelabz.fundoonotes.note.models;

import java.util.Date;

public class LabelDTO {

	private String labelId;
	private String labelName;
	private Date createdAt;

	public LabelDTO() {
		super();
	}

	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "LabelDTO [labelId=" + labelId + ", labelName=" + labelName + ", createdAt=" + createdAt + "]";
	}
}
