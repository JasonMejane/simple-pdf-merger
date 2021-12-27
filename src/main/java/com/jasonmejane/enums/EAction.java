package com.jasonmejane.enums;

public enum EAction {
	ADD_FILE("Add file(s)"),
	CHOOSE_DESTINATION("Choose destination file"),
	MERGE("Merge"),
	MOVE_DOWN("Move down"),
	MOVE_UP("Move up"),
	QUIT("Quit"),
	REMOVE_FILE("Remove file");

	private String text;

	EAction(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
