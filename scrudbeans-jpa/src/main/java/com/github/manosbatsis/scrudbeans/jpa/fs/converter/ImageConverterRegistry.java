package com.github.manosbatsis.scrudbeans.jpa.fs.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ImageConverterRegistry {

	public static final Map<String, ToImageConverter> converters = new HashMap<>();

	private static void registerConverter(ToImageConverter converter) {
		List<String> mimeTypes = converter.mimeTypes();
		for (String type : mimeTypes) {
			converters.put(type, converter);
		}
	}

	static {
		registerConverter(new PDFToImageConverter());
		//registerConverter(new OfficeToImageConverter());

	}

}
