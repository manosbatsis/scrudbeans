/**
 *
 * Restdude
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
package com.restdude.mdd.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.restdude.domain.FileDTO;
import com.restdude.mdd.annotation.model.FilePersistence;
import com.restdude.mdd.annotation.model.FilePersistencePreview;
import com.restdude.mdd.annotation.model.FilePersistencePreviews;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.imgscalr.Scalr;
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

	public default String saveFile(Field fileField, MultipartFile multipartFile, String filename) {
		String result = null;
		FileDTO file = null;
		try {
			file = new FileDTO.Builder()
					.contentLength(multipartFile.getSize())
					.contentType(multipartFile.getContentType())
					.in(multipartFile.getInputStream())
					.path(filename).build();

			file = convertToPngIfGif(file);

			result = this.saveFile(fileField, file);

		}
		catch (IOException e) {
			throw new RuntimeException("Failed persisting file", e);
		}
		finally {
			closeFileDto(file);
		}
		return result;
	}

	default void closeFileDto(FileDTO file) {
		if (file != null) {
			try {
				if (file.getIn() != null) {
					file.getIn().close();
				}
			}
			catch (Exception e) {
				LOGGER.error("Faild closing file stream", e);
			}
			if (file.getTmpFile() != null) {
				file.getTmpFile().delete();
			}
		}
	}

	public default void deleteFile(Field fileField, MultipartFile multipartFile, String filename) {
		FileDTO file = null;
		try {
			file = new FileDTO.Builder()
					.path(filename).build();

		}
		catch (Exception e) {
			throw new RuntimeException("Failed deleting file", e);
		}
		this.deleteFile(fileField, file);
	}

	/**
	 * The method saves the given multipart file to the pathFragment specified, ignoring the original file name.
	 * @param fileField
	 * @param file
	 * @return the URL for the saved file
	 */
	public default String saveFile(Field fileField, FileDTO file) {
		String url = null;
		try {

			FilePersistence config = fileField.getAnnotation(FilePersistence.class);
			// ensure accepted content type
			validateContentType(file.getContentType(), config);


			BufferedImage img = ImageIO.read(file.getIn());
			// if image that needs scaling
			if (isImage(file.getContentType()) && (config.maxHeight() > 0 || config.maxWidth() > 0)) {
				url = saveScaled(img, file.getContentType(), config.maxWidth(), config.maxHeight(), file.getPath());
			}
			else {
				url = saveFile(img, file.getContentLength(), file.getContentType(), file.getPath());
			}

			// generate previews?
			Map<String, FilePersistencePreview> previews = getPreviews(fileField);
			if (isImage(file.getContentType()) && MapUtils.isNotEmpty(previews)) {
				for (String key : previews.keySet()) {
					FilePersistencePreview previewConfig = previews.get(key);
					saveScaled(img, file.getContentType(), previewConfig.maxWidth(), previewConfig.maxHeight(), file.getPath() + "_" + key);
				}
			}

		}
		catch (IOException e) {
			throw new RuntimeException("Failed persisting file", e);
		}
		finally {
			this.closeFileDto(file);
		}

		return url;
	}

	default FileDTO convertToPngIfGif(FileDTO fileDto) throws IOException {
		// convert GIF to PNG
		if (IMAGE_GIF.equals(fileDto.getContentType())) {
			FileDTO gifDto = fileDto;

			InputStream in = fileDto.getIn();
			ByteArrayOutputStream os = null;
			try {

				// convert
				BufferedImage tmpImg = ImageIO.read(in);
				os = new ByteArrayOutputStream();
				ImageIO.write(tmpImg, "PNG", os);

				// update FileDTO
				fileDto = new FileDTO.Builder().contentLength(os.size())
						.contentType(IMAGE_PNG)
						.in(new ByteArrayInputStream(os.toByteArray()))
						.path(fileDto.getPath()).build();

			}
			catch (Exception e) {
				throw new RuntimeException("Failed persisting file", e);
			}
			finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(os);
			}
			LOGGER.debug("Converted GIF: {} to PNG: {}", gifDto, fileDto);
		}
		return fileDto;
	}

	/**
	 * The method saves the given multipart file to the pathFragment specified, ignoring the original file name.
	 *
	 * @param fileField
	 * @param file
	 * @return the URL for the saved file
	 */
	public default void deleteFile(Field fileField, FileDTO file) {

		FilePersistence config = fileField.getAnnotation(FilePersistence.class);

		// delete file
		List<String> keys = new LinkedList<String>();
		keys.add(file.getPath());

		// generate previews?
		Map<String, FilePersistencePreview> previews = getPreviews(fileField);
		if (isImage(file.getContentType()) && MapUtils.isNotEmpty(previews)) {
			for (String key : previews.keySet()) {
				keys.add(file.getPath() + "_" + key);
			}
		}

		deleteFiles(keys.toArray(new String[keys.size()]));

	}


	public default String saveScaled(BufferedImage img, String contentType, int maxWidth, int maxHeight, String path) throws IOException {
		String url;
		FileDTO tmp = null;
		try {
			tmp = scaleFile(img, contentType, maxWidth, maxHeight);
			url = saveFile(tmp.getIn(), tmp.getContentLength(), tmp.getContentType(), path);
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			this.closeFileDto(tmp);
		}
		return url;
	}

	public default Map<String, FilePersistencePreview> getPreviews(Field fileField) {
		Map<String, FilePersistencePreview> previews = new HashMap<String, FilePersistencePreview>();
		if (fileField.isAnnotationPresent(FilePersistencePreviews.class)) {
			FilePersistencePreview[] tmp = fileField.getAnnotation(FilePersistencePreviews.class).value();
			if (tmp != null) {
				for (int i = 0; i < tmp.length; i++) {
					FilePersistencePreview preview = tmp[i];
					previews.put(preview.maxWidth() + "x" + preview.maxHeight(), preview);
				}
			}
		}
		if (fileField.isAnnotationPresent(FilePersistencePreview.class)) {
			FilePersistencePreview[] tmp = fileField.getAnnotationsByType(FilePersistencePreview.class);
			for (int i = 0; i < tmp.length; i++) {
				FilePersistencePreview preview = tmp[i];
				previews.put(preview.maxWidth() + "x" + preview.maxHeight(), preview);
			}
		}
		return previews;
	}

	public default FileDTO scaleFile(BufferedImage img, String contentType, int maxWidth, int maxHeight) throws IOException {
		FileDTO scaledFile = null;
		ByteArrayOutputStream os = null;
		ByteArrayInputStream in = null;
		try {
			BufferedImage scaled = Scalr.resize(img,
					Scalr.Method.SPEED,
					Scalr.Mode.FIT_TO_WIDTH,
					maxWidth,
					maxHeight,
					Scalr.OP_ANTIALIAS);
			os = new ByteArrayOutputStream();
			ImageIO.write(scaled, getImageIoFormat(contentType), os);
			in = new ByteArrayInputStream(os.toByteArray());
			scaledFile = new FileDTO.Builder()
					.contentLength(os.size())
					.contentType(contentType)
					.in(in)
					.build();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(in);
		}
		return scaledFile;
	}


	public default String saveFile(BufferedImage img, long contentLength, String contentType, String path) throws IOException {
		String result = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		try {
			ImageIO.write(img, getImageIoFormat(contentType), os);
			in = new ByteArrayInputStream(os.toByteArray());
			result = saveFile(in, os.size(), contentType, path);
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(in);
		}

		return result;

	}


	public String saveFile(InputStream in, long contentLength, String contentType, String path) throws IOException;

	public void deleteFiles(String... path);

}