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
package com.github.manosbatsis.scrudbeans.jpa.fs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.github.manosbatsis.scrudbeans.api.domain.FileDTO;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.FilePersistence;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.FilePersistencePreview;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.FilePersistencePreviews;
import com.github.manosbatsis.scrudbeans.api.mdd.service.FilePersistenceService;
import com.github.manosbatsis.scrudbeans.jpa.fs.converter.ToImageConverter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

/**
 * A dummy implementation of {@link FilePersistenceService} that does not save files
 */
public abstract class AbstractFilePersistenceServiceImpl implements FilePersistenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFilePersistenceServiceImpl.class);

	public String saveFile(Field fileField, MultipartFile multipartFile, String filename) {
		String result = null;
		FileDTO file = null;
		try {
			// Convert to file
			File tmpFile = streamToTmpFile(multipartFile.getInputStream(), true);
			// Build DTO
			file = new FileDTO.Builder()
					.contentLength(multipartFile.getSize())
					.contentType(multipartFile.getContentType())
					.in(tmpFile)
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

	private static File streamToTmpFile(InputStream in, boolean closeStream) throws IOException {
		final File tempFile = File.createTempFile("scrudbeansUpload", ".tmp");
		tempFile.deleteOnExit();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(tempFile);
			IOUtils.copy(in, out);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (out != null && closeStream) {
				IOUtils.closeQuietly(in);
			}
		}
		return tempFile;
	}
	@Override
	public void closeFileDto(FileDTO file) {
		if (file != null) {
			if (file.getTmpFile() != null) {
				file.getTmpFile().delete();
			}
		}
	}

	public void deleteFile(Field fileField, MultipartFile multipartFile, String filename) {
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
	public String saveFile(Field fileField, FileDTO file) {
		String url = null;
		FileInputStream in = null;
		try {

			FilePersistence config = fileField.getAnnotation(FilePersistence.class);
			// Ensure accepted content type
			FilePersistenceService.validateContentType(file.getContentType(), config);
			// Get intented previews
			Map<String, FilePersistencePreview> previews = getPreviews(fileField);
			// Base for previews
			BufferedImage img = null;
			// Is it an image?
			if (FilePersistenceService.isImage(file.getContentType())) {
				in = new FileInputStream(file.getIn());
				img = ImageIO.read(in);
				IOUtils.closeQuietly(in);
				// iNeeds scaling?
				if (FilePersistenceService.isImage(file.getContentType()) && (config.maxHeight() > 0 || config.maxWidth() > 0)) {
					url = saveScaledImage(img, file.getContentType(), config.maxWidth(), config.maxHeight(), file.getPath());
				}
				else {
					url = saveImageFile(img, file.getContentLength(), file.getContentType(), file.getPath());
				}
			}
			// Other file types
			else {
				// Converter?
				ToImageConverter converter = ToImageConverter.converters.get(file.getContentType());
				if (converter != null) {
					try {
						img = converter.toImageFile(file);
					}
					catch (Exception e) {
						LOGGER.error("Failed converting file to image", e);
					}
				}
				// Save actual file
				url = saveFile(file.getIn(), file.getContentLength(), file.getContentType(), file.getPath());

			}

			// Generate previews?
			if (img != null && MapUtils.isNotEmpty(previews)) {
				for (String key : previews.keySet()) {
					FilePersistencePreview previewConfig = previews.get(key);
					saveScaledImage(img, "image/png", previewConfig.maxWidth(), previewConfig.maxHeight(), file.getPath() + "_" + key);
				}
			}

		}
		catch (IOException e) {
			throw new RuntimeException("Failed persisting file", e);
		}
		finally {
			IOUtils.closeQuietly(in);
			this.closeFileDto(file);
		}

		return url;
	}

	FileDTO convertToPngIfGif(FileDTO fileDto) throws IOException {
		// convert GIF to PNG
		if (IMAGE_GIF.equals(fileDto.getContentType())) {
			FileDTO gifDto = fileDto;


			InputStream in = new FileInputStream(fileDto.getIn());
			FileOutputStream os = null;
			File newFile = File.createTempFile("scrudBeansUpload", "tmp");
			try {
				os = new FileOutputStream(newFile);
				// convert
				BufferedImage tmpImg = ImageIO.read(in);
				ImageIO.write(tmpImg, "PNG", os);
				// update FileDTO
				fileDto = new FileDTO.Builder()
						.contentLength(newFile.length())
						.contentType(IMAGE_PNG)
						.in(newFile)
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
	public void deleteFile(Field fileField, FileDTO file) {

		FilePersistence config = fileField.getAnnotation(FilePersistence.class);

		// delete file
		List<String> keys = new LinkedList<String>();
		keys.add(file.getPath());

		// generate previews?
		Map<String, FilePersistencePreview> previews = getPreviews(fileField);
		if (FilePersistenceService.isImage(file.getContentType()) && MapUtils.isNotEmpty(previews)) {
			for (String key : previews.keySet()) {
				keys.add(file.getPath() + "_" + key);
			}
		}

		deleteFiles(keys.toArray(new String[keys.size()]));

	}


	public String saveScaledImage(BufferedImage img, String contentType, int maxWidth, int maxHeight, String path) throws IOException {
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

	public Map<String, FilePersistencePreview> getPreviews(Field fileField) {
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

	public FileDTO scaleFile(BufferedImage img, String contentType, int maxWidth, int maxHeight) throws IOException {
		FileDTO scaledFile = null;
		FileOutputStream os = null;
		File newFile = File.createTempFile("scrudBeansUpload", "tmp");
		try {
			BufferedImage scaled = Scalr.resize(img,
					Scalr.Method.QUALITY,
					Scalr.Mode.FIT_TO_WIDTH,
					maxWidth,
					maxHeight,
					Scalr.OP_ANTIALIAS);
			os = new FileOutputStream(newFile);
			ImageIO.write(scaled, FilePersistenceService.getImageIoFormat(contentType), os);
			scaledFile = new FileDTO.Builder()
					.contentLength(newFile.length())
					.contentType(contentType)
					.in(newFile)
					.build();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			IOUtils.closeQuietly(os);
		}
		return scaledFile;
	}


	public String saveImageFile(BufferedImage img, long contentLength, String contentType, String path) throws IOException {
		String result = null;
		OutputStream os = null;
		try {
			File tmpFile = File.createTempFile("scrudBeansImage", "tmp");
			os = new FileOutputStream(tmpFile);
			ImageIO.write(img, FilePersistenceService.getImageIoFormat(contentType), os);
			result = saveFile(tmpFile, tmpFile.length(), contentType, path);
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			IOUtils.closeQuietly(os);
		}
		return result;
	}

}
