package com.jasonmejane;

import com.jasonmejane.enums.EAction;
import com.jasonmejane.gui.GUIHandler;
import com.jasonmejane.pdf.PDFException;
import com.jasonmejane.pdf.PDFMerger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

public class SimplePDFMerger {
	private static final Log logger = LogFactory.getLog(SimplePDFMerger.class);
	private static final String NO_FILE_SELECTED = "No file selected";
	private final GUIHandler guiHandler;
	private final PDFMerger pdfMerger = new PDFMerger();
	private final DefaultListModel<String> fileList = new DefaultListModel<>();

	public SimplePDFMerger() {
		fileList.addElement(NO_FILE_SELECTED);
		guiHandler = new GUIHandler(this, fileList);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new SimplePDFMerger().initialize());
	}

	public void handleActionCommand(String actionCommand) {
		if (Objects.equals(actionCommand, EAction.QUIT.toString())) {
			System.exit(0);
		} else if (Objects.equals(actionCommand, EAction.ADD_FILE.toString())) {
			getSelectedFiles();
		} else if (Objects.equals(actionCommand, EAction.REMOVE_FILE.toString())) {
			removeFileFromList();
		} else if (Objects.equals(actionCommand, EAction.MOVE_UP.toString())) {
			reorderHighlightedFile(-1);
		} else if (Objects.equals(actionCommand, EAction.MOVE_DOWN.toString())) {
			reorderHighlightedFile(1);
		} else if (Objects.equals(actionCommand, EAction.MERGE.toString())) {
			mergeFiles();
		} else if (Objects.equals(actionCommand, EAction.CHOOSE_DESTINATION.toString())) {
			getDestinationFile();
		}
	}

	/***************
	 PRIVATE METHODS
	 ***************/

	private void initialize() {
		guiHandler.createAndShowGUI();
	}

	private void getSelectedFiles() {
		File[] files = guiHandler.openAndGetFileSelection();

		if (files.length > 0) {
			addFilesToList(files);
		}
	}

	private void addFilesToList(File[] files) {
		if (fileList.contains(NO_FILE_SELECTED)) {
			fileList.remove(0);
		}

		for (File file : files) {
			fileList.addElement(file.getPath());
		}
	}

	private void removeFileFromList() {
		int index = guiHandler.getSelectedIndex();
		fileList.remove(index);
		guiHandler.clearSelection();

		if (fileList.getSize() < 1) {
			fileList.addElement(NO_FILE_SELECTED);
		}
	}

	private void reorderHighlightedFile(int moveStep) {
		int listSize = fileList.getSize() - 1;

		int oldIndex = guiHandler.getSelectedIndex();
		int newIndex = oldIndex + moveStep;

		if (oldIndex != -1 && newIndex >= 0 && newIndex <= listSize) {
			String newValue = guiHandler.getSelectedValue();
			String oldValue = fileList.elementAt(newIndex);

			fileList.add(oldIndex, oldValue);
			fileList.remove(oldIndex + 1);
			fileList.add(newIndex, newValue);
			fileList.remove(newIndex + 1);
		}

		if (newIndex <= 0 || newIndex >= listSize) {
			guiHandler.clearSelection();
		}
	}

	private void mergeFiles() {
		if (fileList.getSize() < 2) {
			guiHandler.showWarningDialog("Cannot merge", "Please select at least 2 files to merge.");
		} else {
			try {
				pdfMerger.mergeAndSaveMergedFileList(fileList, guiHandler.getMergedFileName());
				guiHandler.showSuccessDialog("PDFs merged!", fileList.getSize() + " files successfully merged into " + guiHandler.getMergedFileName() + ".pdf");
			} catch (PDFException e) {
				logger.error("PDFException:" + e.getMessage());
				e.printStackTrace();
				guiHandler.showErrorDialog("Error while merging PDFs", "An error occurred while merging the files. Please check that selected files are valid.");
			}
		}
	}

	private void getDestinationFile() {
		guiHandler.openDestinationFileSelection();
	}
}
