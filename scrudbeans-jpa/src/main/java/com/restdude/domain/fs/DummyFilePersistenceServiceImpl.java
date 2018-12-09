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
package com.restdude.domain.fs;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import com.restdude.mdd.service.FilePersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dummy implementation of {@link FilePersistenceService} that does not save files
 */
public class DummyFilePersistenceServiceImpl implements FilePersistenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DummyFilePersistenceServiceImpl.class);


	@PostConstruct
	public void postConstruct() {
		LOGGER.debug("Created dummy impl bean for FilePersistenceService");
	}


	/**
	 * Non-action implementation, does not persist files
	 * @see FilePersistenceService#saveFile(java.io.InputStream, long, java.lang.String, java.lang.String)
	 */
	@Override
	public String saveFile(InputStream in, long contentLength, String contentType, String path) {
		LOGGER.warn("File not saved, please configure another bean for id FilePersistenceService to save: " + path + ", size: " + contentLength + ", contentType: " + contentType);
		return path;
	}

	public void deleteFiles(String... path) {
		LOGGER.warn("File not deleted, please configure another bean for id FilePersistenceService to delete: " + path);
	}

}