package com.jasonmejane.pdf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PDFMerger {
	private static final Log logger = LogFactory.getLog(PDFMerger.class);

	public void mergeAndSaveMergedFileList(DefaultListModel<String> fileList, String mergedFilePath) throws PDFException {
		logger.info("Merging list of " + fileList.getSize() + " files to path " + mergedFilePath + ".");

		byte[] mergedPdf;

		try {
			mergedPdf = loadAndMergePdf(fileList);
			savePdf(mergedPdf, mergedFilePath);
		} catch (Exception e) {
			logger.error("Error while merging documents");
			throw new PDFException("Error while merging documents", e);
		}
	}

	private byte[] loadAndMergePdf(DefaultListModel<String> fileList) throws Exception {
		List<InputStream> files;
		byte[] resultFile;

		try {
			files = loadFiles(fileList);
			resultFile = mergeFiles(files);

			return resultFile;
		} catch (Exception e) {
			logger.error("Error while loading and merging documents: " + e.getMessage());
			throw e;
		}
	}

	private List<InputStream> loadFiles(DefaultListModel<String> fileList) throws FileNotFoundException {
		logger.info("Loading files");

		List<InputStream> files = new ArrayList<>();

		for (int i = 0; i < fileList.getSize(); i++) {
			InputStream file = new FileInputStream(fileList.elementAt(i));
			files.add(file);
		}

		return files;
	}

	private byte[] mergeFiles(List<InputStream> files) throws IOException {
		logger.info("Merging files");

		try (ByteArrayOutputStream mergedPDFOutputStream = new ByteArrayOutputStream()) {
			PDFMergerUtility pdfMergerUtility = preparePdfMergerUtility(files, mergedPDFOutputStream);
			pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

			return mergedPDFOutputStream.toByteArray();
		} catch (Exception e) {
			logger.error("Error while merging pdfs");
			throw new IOException("Error while merging pdf", e);
		} finally {
			files.forEach(IOUtils::closeQuietly);
		}
	}

	private PDFMergerUtility preparePdfMergerUtility(List<InputStream> sources, ByteArrayOutputStream outputStream) {
		PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();

		pdfMergerUtility.addSources(sources);
		pdfMergerUtility.setDestinationStream(outputStream);

		return pdfMergerUtility;
	}

	private void savePdf(byte[] mergedPdf, String path) throws IOException {
		logger.info("Saving merged pdf");

		File outputFile = new File(checkAndReturnPath(path));
		FileOutputStream outputStream = new FileOutputStream(outputFile);

		outputStream.write(mergedPdf);
		IOUtils.closeQuietly(outputStream);
	}

	private String checkAndReturnPath(String path) {
		if (path.endsWith(".pdf")) {
			return path;
		} else {
			return path + ".pdf";
		}
	}
}
