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
package com.restdude.domain;

import java.util.List;

/**
 * Created by manos on 4/2/2017.
 */
public interface UploadedFileModel {
	String getName();

	void setName(String name);

	String getThumbnailFilename();

	void setThumbnailFilename(String thumbnailFilename);

	String getNewFilename();

	void setNewFilename(String newFilename);

	String getFileNameExtention();

	void setFileNameExtention(String fileNameExtention);

	String getPath();

	void setPath(String path);

	String getParentPath();

	void setParentPath(String parentPath);

	String getContentType();

	void setContentType(String contentType);

	Long getSize();

	void setSize(Long size);

	Long getThumbnailSize();

	void setThumbnailSize(Long thumbnailSize);

	String getUrl();

	void setUrl(String url);

	String getThumbnailUrl();

	void setThumbnailUrl(String thumbnailUrl);

	String getDeleteUrl();

	void setDeleteUrl(String deleteUrl);

	String getDeleteType();

	void setDeleteType(String deleteType);

	List<String> getInitialPreviewConfig();

	void setInitialPreviewConfig(List<String> initialPreviewConfig);

	List<String> getInitialPreview();

	void setInitialPreview(List<String> initialPreview);

	boolean addInitialPreview(String initialPreview);

	boolean addInitialPreviewConfig(String initialPreviewConfig);
}
