package com.github.manosbatsis.scrudbeans.jpa.fs.converter;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.manosbatsis.scrudbeans.api.domain.FileDTO;

public abstract class ToImageConverter {

	public static final Map<String, ToImageConverter> converters = new HashMap<>();

	private static void registerConverter(ToImageConverter converter) {
		List<String> mimeTypes = converter.mimeTypes();
		for (String type : mimeTypes) {
			converters.put(type, converter);
		}
	}

	static {
		registerConverter(new PDFToImageConverter());
		registerConverter(new OfficeToImageConverter());

	}

	public abstract List<String> mimeTypes();

	public abstract BufferedImage toImageFile(FileDTO file);
}
