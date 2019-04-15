package com.github.manosbatsis.scrudbeans.jpa.fs.converter;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import com.github.manosbatsis.scrudbeans.api.domain.FileDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFToImageConverter extends ToImageConverter {

	public List<String> mimeTypes() {
		List<String> types = new LinkedList<>();
		types.add("application/pdf");
		return types;
	}

	public BufferedImage toImageFile(FileDTO file) {
		//FileDTO scaledFile = null;
		//ByteArrayOutputStream os = null;
		BufferedImage scaled;
		try {
			PDDocument document = PDDocument.load(file.getIn());
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			scaled = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
			//ImageIOUtil.writeImage(scaled, "png", os, 300);
			document.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			//IOUtils.closeQuietly(os);
		}
		return scaled;
	}

}
