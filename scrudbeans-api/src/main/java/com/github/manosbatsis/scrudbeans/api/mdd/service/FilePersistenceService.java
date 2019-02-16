/**
 *
 * ScrudBeans: Model driven development for Spring Boot
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.api.mdd.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.github.manosbatsis.scrudbeans.api.domain.FileDTO;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.FilePersistence;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.FilePersistencePreview;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstract file persistence
 */
public interface FilePersistenceService {
	Logger LOGGER = LoggerFactory.getLogger(FilePersistenceService.class);

	String BEAN_ID = "filePersistenceService";

	String IMAGE_JPEG = "image/jpeg";

	String IMAGE_PNG = "image/png";

	String IMAGE_GIF = "image/gif";

	String IMAGE_X_MS_BMP = "image/x-ms-bmp";

	String[] MIMES_IMAGE = {IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF, IMAGE_X_MS_BMP};

	Map<String, String> MIME_FORMATS = new HashMap<String, String>();

	public static String getImageIoFormat(String contentType) {
		if (MIME_FORMATS.size() == 0) {
			MIME_FORMATS.put(IMAGE_JPEG, "jpeg");
			MIME_FORMATS.put(IMAGE_PNG, "png");
			MIME_FORMATS.put(IMAGE_GIF, "gif");
		}
		return MIME_FORMATS.get(contentType);
	}

	public static boolean isImage(String contentType) {
		return ArrayUtils.contains(MIMES_IMAGE, contentType.toLowerCase());
	}

	public static void validateContentType(String contentType, FilePersistence config) {
		if (ArrayUtils.isNotEmpty(config.mimeTypeIncludes()) && !ArrayUtils.contains(config.mimeTypeIncludes(), contentType.toLowerCase())) {
			throw new IllegalArgumentException("Unacceptable MIME type: " + contentType);
		}
	}

	String saveFile(Field fileField, MultipartFile multipartFile, String filename);

	void closeFileDto(FileDTO file);

	void deleteFile(Field fileField, MultipartFile multipartFile, String filename);

	/**
	 * The method saves the given multipart file to the pathFragment specified, ignoring the original file name.
	 * @param fileField
	 * @param file
	 * @return the URL for the saved file
	 */
	String saveFile(Field fileField, FileDTO file);

	/**
	 * The method saves the given multipart file to the pathFragment specified, ignoring the original file name.
	 *
	 * @param fileField
	 * @param file
	 * @return the URL for the saved file
	 */
	void deleteFile(Field fileField, FileDTO file);

	String saveScaled(BufferedImage img, String contentType, int maxWidth, int maxHeight, String path) throws IOException;

	Map<String, FilePersistencePreview> getPreviews(Field fileField);

	FileDTO scaleFile(BufferedImage img, String contentType, int maxWidth, int maxHeight) throws IOException;

	String saveFile(BufferedImage img, long contentLength, String contentType, String path) throws IOException;

	String saveFile(InputStream in, long contentLength, String contentType, String path) throws IOException;

	void deleteFiles(String... path);

}