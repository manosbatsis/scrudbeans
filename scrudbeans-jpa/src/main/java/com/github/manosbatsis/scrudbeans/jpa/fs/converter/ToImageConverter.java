package com.github.manosbatsis.scrudbeans.jpa.fs.converter;

import java.awt.image.BufferedImage;
import java.util.List;

import com.github.manosbatsis.scrudbeans.api.domain.FileDTO;

public abstract class ToImageConverter {

	public abstract List<String> mimeTypes();

	public abstract BufferedImage toImageFile(FileDTO file);
}
