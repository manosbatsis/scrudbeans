package com.github.manosbatsis.scrudbeans.jpa.fs.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import com.github.manosbatsis.scrudbeans.api.domain.FileDTO;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.LocalConverter;
import org.jodconverter.filter.text.PageSelectorFilter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;

@Slf4j
public class OfficeToImageConverter extends ToImageConverter {

	// Create an office manager using the default configuration.
	// The default port is 2002. Note that when an office manager
	// is installed, it will be the one used by default when
	// a converter is created.
	public static LocalOfficeManager officeManager = null;

	static {
		// Start an office process and connect to the started instance (on port 2002).
		try {
			officeManager = LocalOfficeManager.install();
			officeManager.start();
		}
		catch (OfficeException e) {
			log.error("Failed starting a LocalOfficeManager: ", e);
			e.printStackTrace();
			// throw new RuntimeException(e);
		}
	}

	public List<String> mimeTypes() {
		List<String> types = new LinkedList<>();
		types.add("application/msword");
		types.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		types.add("application/vnd.openxmlformats-officedocument.wordprocessingml.template");
		types.add("application/vnd.ms-word.document");
		types.add("application/vnd.ms-word");
		types.add("application/vnd.ms-word.document.macroEnabled.12");
		types.add("application/vnd.ms-excel");
		types.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		types.add("application/vnd.openxmlformats-officedocument.spreadsheetml.template");
		types.add("application/vnd.ms-excel.sheet.macroEnabled.12");
		types.add("application/vnd.ms-excel.template.macroEnabled.12");
		types.add("application/vnd.ms-excel.sheet.binary.macroEnabled.12");
		types.add("application/vnd.ms-powerpoint");
		types.add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
		types.add("application/vnd.openxmlformats-officedocument.presentationml.template");
		types.add("application/vnd.openxmlformats-officedocument.presentationml.slideshow");
		types.add("application/vnd.ms-powerpoint.addin.macroEnabled.12");
		types.add("application/vnd.ms-powerpoint.presentation.macroEnabled.12");
		types.add("application/vnd.ms-powerpoint.template.macroEnabled.12");
		types.add("application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
		return types;
	}

	public BufferedImage toImageFile(FileDTO file) {
		//FileDTO scaledFile = null;
		//ByteArrayOutputStream os = null;
		BufferedImage scaled;
		try {
			// Create a page selector filter in order to
			// convert only the first page.
			final PageSelectorFilter selectorFilter = new PageSelectorFilter(1);
			File outFile = File.createTempFile("scrudbeansLocalConverter", "tmp.png");
			LocalConverter
					.builder()
					.filterChain(selectorFilter)
					.build()
					.convert(file.getIn())
					.to(outFile)
					//.as(DocumentFormat.builder().mediaType("image/png").build())
					.execute();
			scaled = ImageIO.read(outFile);
			log.debug("toImageFile, original: {}", file.getIn().getAbsolutePath());
			log.debug("toImageFile, outFile: {}", outFile.getAbsolutePath());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			// Stop the office process
			//LocalOfficeUtils.stopQuietly(officeManager);
		}
		return scaled;
	}

}
