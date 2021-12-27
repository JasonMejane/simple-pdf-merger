package com.jasonmejane.gui;

import com.jasonmejane.SimplePDFMerger;
import com.jasonmejane.enums.EAction;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GUIHandler implements ActionListener {
	private final static int BORDER_SIZE = 8;
	private final static int LIST_HEIGHT = 320;
	private final static int LIST_WIDTH = 640;

	private final JTextField mergedFileNameField = new JTextField();
	private final JList<String> jList;
	private final SimplePDFMerger simplePDFMerger;

	private final JFrame frame;
	private final JPanel frameInsidePanel;
	private final JLabel listTitle = new JLabel();
	private final JPanel listPanel = new JPanel();
	private final JLabel destinationTitle = new JLabel();
	private final JPanel destinationPanel = new JPanel();
	private final JPanel footerPanel = new JPanel();

	public GUIHandler(SimplePDFMerger simplePDFMerger, DefaultListModel<String> fileList) {
		this.simplePDFMerger = simplePDFMerger;
		this.jList = new JList<>(fileList);
		frame = new JFrame();
		frameInsidePanel = new JPanel();

		initialize();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		simplePDFMerger.handleActionCommand(e.getActionCommand());
	}

	public void showErrorDialog(String title, String message) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public void showWarningDialog(String title, String message) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public void showSuccessDialog(String title, String message) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public File[] openAndGetFileSelection() {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("PDF File (.pdf)", "pdf");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);

		int dialog = fileChooser.showOpenDialog(frame);

		if (dialog == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFiles();
		}

		return new File[]{};
	}

	public void openDestinationFileSelection() {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("PDF File (.pdf)", "pdf");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);

		int dialog = fileChooser.showSaveDialog(frame);

		if (dialog == JFileChooser.APPROVE_OPTION) {
			mergedFileNameField.setText(getPathWithExtension(fileChooser.getSelectedFile().getAbsolutePath()));
		}
	}

	public void clearSelection() {
		jList.clearSelection();
	}

	public int getSelectedIndex() {
		return jList.getSelectedIndex();
	}

	public String getSelectedValue() {
		return jList.getSelectedValue();
	}

	public String getMergedFileName() {
		return mergedFileNameField.getText();
	}

	public void createAndShowGUI() {
		createAndAddSelectionList();
		createAndAddDestinationGroup();
		createAndAddActionFooter();

		prepareAndShowFrame();
	}

	private void initialize() {
		frameInsidePanel.setLayout(new GridLayout(0, 1, BORDER_SIZE, BORDER_SIZE));
		frame.getContentPane().add(frameInsidePanel);

		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mergedFileNameField.setEnabled(false);

		listTitle.setText("Files to merge");
		destinationTitle.setText("Destination");
	}

	private void createAndAddSelectionList() {
		JPanel buttonGroup = new JPanel();
		JButton addFileButton = createListenedButton(EAction.ADD_FILE);
		JButton removeFileButton = createListenedButton(EAction.REMOVE_FILE);
		JButton upButton = createListenedButton(EAction.MOVE_UP);
		JButton downButton = createListenedButton(EAction.MOVE_DOWN);

		buttonGroup.add(addFileButton);
		buttonGroup.add(removeFileButton);
		buttonGroup.add(new JLabel());
		buttonGroup.add(upButton);
		buttonGroup.add(downButton);
		buttonGroup.setLayout(new GridLayout(0, 1, BORDER_SIZE, BORDER_SIZE));

		JScrollPane scrollableList = new JScrollPane(jList);
		scrollableList.setPreferredSize(new Dimension(LIST_WIDTH, LIST_HEIGHT));
		listPanel.add(scrollableList);
		listPanel.add(buttonGroup);
		listPanel.setLayout(new FlowLayout(FlowLayout.LEADING, BORDER_SIZE, BORDER_SIZE));
	}

	private void createAndAddDestinationGroup() {
		JButton openDestinationButton = createListenedButton(EAction.CHOOSE_DESTINATION);

		JScrollPane scrollableField = new JScrollPane(mergedFileNameField);
		scrollableField.setPreferredSize(new Dimension(LIST_WIDTH, 40));
		scrollableField.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		destinationPanel.add(scrollableField);
		destinationPanel.add(openDestinationButton);
		destinationPanel.setLayout(new FlowLayout(FlowLayout.LEADING, BORDER_SIZE, BORDER_SIZE));
	}

	private void createAndAddActionFooter() {
		JButton quitButton = createListenedButton(EAction.QUIT);
		JButton mergeButton = createListenedButton(EAction.MERGE);

		footerPanel.add(mergeButton);
		footerPanel.add(quitButton);
		footerPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, BORDER_SIZE, BORDER_SIZE));
	}

	private JButton createListenedButton(EAction action) {
		JButton button = new JButton(action.getText());
		button.setActionCommand(action.toString());
		button.addActionListener(this);

		return button;
	}

	private String getPathWithExtension(String path) {
		if (path.endsWith(".pdf")) {
			return path;
		} else {
			return path + ".pdf";
		}
	}

	private void prepareAndShowFrame() {
		// Frame layout
		GroupLayout groupLayout = new GroupLayout(frameInsidePanel);
		frameInsidePanel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setHorizontalGroup(
			groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(listTitle)
					.addComponent(listPanel)
					.addComponent(destinationTitle)
					.addComponent(destinationPanel)
					.addComponent(footerPanel))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createSequentialGroup()
				.addComponent(listTitle)
				.addComponent(listPanel)
				.addComponent(destinationTitle)
				.addComponent(destinationPanel)
				.addComponent(footerPanel)
		);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Simple PDF Merger");
		frame.pack();
		frame.setVisible(true);
	}
}
