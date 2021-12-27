package com.jasonmejane.pdf;

public class PDFException extends Exception {

	public PDFException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
