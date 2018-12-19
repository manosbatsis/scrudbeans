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
package com.github.manosbatsis.scrudbeans.jpa.fs;


import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.manosbatsis.scrudbeans.api.domain.UploadedFileModel;
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractSystemUuidPersistableModel;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Formula;

/**
 *
 * @author jdmr
 */
@Entity
@Table(name = "images")
public class BinaryFile extends AbstractSystemUuidPersistableModel implements UploadedFileModel {

	private String name;

	@Formula(value = " CONCAT(id, '_thumb.png') ")
	private String thumbnailFilename;

	@Formula(value = " CONCAT(id, '.', file_name_extention) ")
	private String newFilename;

	@Column(name = "file_name_extention")
	private String fileNameExtention;

	@Formula(value = " CONCAT(parent_path, '/', id, '.', file_name_extention) ")
	private String path;

	@Column(name = "parent_path")
	private String parentPath;

	private String contentType;

	@Column(name = "size_")
	private Long size;

	private Long thumbnailSize;

	@Transient
	private String url;

	@Transient
	private String thumbnailUrl;

	@Transient
	private String deleteUrl;

	@Transient
	private String deleteType;

	@Transient
	private List<String> initialPreviewConfig;

	@Transient
	private List<String> initialPreview;


	public BinaryFile() {
	}

	@Override
	public String getName() {
		return name;
	}


//	@Override
//	public String toString() {
//		return "BinaryFile [source=" + source + ", thumbnailFilename="
//				+ thumbnailFilename + ", newFilename=" + newFilename
//				+ ", fileNameExtention=" + fileNameExtention + ", pathFragment=" + pathFragment
//				+ ", parentPath=" + parentPath + ", contentType=" + contentType
//				+ ", size=" + size + ", thumbnailSize=" + thumbnailSize
//				+ ", url=" + url + ", thumbnailUrl=" + thumbnailUrl
//				+ ", deleteUrl=" + deleteUrl + ", deleteType=" + deleteType
//				+ ", getSource()=" + getSource() + ", getThumbnailFilename()="
//				+ getThumbnailFilename() + ", getNewFilename()="
//				+ getNewFilename() + ", getFileNameExtention()="
//				+ getFileNameExtention() + ", getPath()=" + getPath()
//				+ ", getParentPath()=" + getParentPath()
//				+ ", getContentType()=" + getContentType() + ", getSize()="
//				+ getSize() + ", getThumbnailSize()=" + getThumbnailSize()
//				+ ", getUrl()=" + getUrl() + ", getThumbnailUrl()="
//				+ getThumbnailUrl() + ", getDeleteUrl()=" + getDeleteUrl()
//				+ ", getDeleteType()=" + getDeleteType() + ", toString()="
//				+ super.toString() + ", getCreatedBy()=" + getCreatedBy()
//				+ ", getCreatedDate()=" + getCreatedDate()
//				+ ", getLastModifiedBy()=" + getLastModifiedBy()
//				+ ", getLastModifiedDate()=" + getLastModifiedDate()
//				+ ", getIdentifier()=" + getIdentifier() + ", isNew()=" + isNew()
//				+ ", hashCode()="
//				+ hashCode() + ", getClass()=" + getClass() + "]";
//	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getThumbnailFilename() {
		return thumbnailFilename;
	}

	@Override
	public void setThumbnailFilename(String thumbnailFilename) {
		this.thumbnailFilename = thumbnailFilename;
	}

	@Override
	public String getNewFilename() {
		return newFilename;
	}

	@Override
	public void setNewFilename(String newFilename) {
		this.newFilename = newFilename;
	}

	@Override
	public String getFileNameExtention() {
		return fileNameExtention;
	}

	@Override
	public void setFileNameExtention(String fileNameExtention) {
		this.fileNameExtention = fileNameExtention;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getParentPath() {
		return parentPath;
	}

	@Override
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public Long getSize() {
		return size;
	}

	@Override
	public void setSize(Long size) {
		this.size = size;
	}

	@Override
	public Long getThumbnailSize() {
		return thumbnailSize;
	}

	@Override
	public void setThumbnailSize(Long thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	@Override
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	@Override
	public String getDeleteUrl() {
		return deleteUrl;
	}

	@Override
	public void setDeleteUrl(String deleteUrl) {
		this.deleteUrl = deleteUrl;
	}

	@Override
	public String getDeleteType() {
		return deleteType;
	}

	@Override
	public void setDeleteType(String deleteType) {
		this.deleteType = deleteType;
	}

	@Override
	public List<String> getInitialPreviewConfig() {
		return initialPreviewConfig;
	}

	@Override
	public void setInitialPreviewConfig(List<String> initialPreviewConfig) {
		this.initialPreviewConfig = initialPreviewConfig;
	}

	@Override
	public List<String> getInitialPreview() {
		return initialPreview;
	}

	@Override
	public void setInitialPreview(List<String> initialPreview) {
		this.initialPreview = initialPreview;
	}

	@Override
	public boolean addInitialPreview(String initialPreview) {
		if (CollectionUtils.isEmpty(this.initialPreview)) {
			this.initialPreview = new LinkedList<String>();
		}
		return this.initialPreview.add(initialPreview);
	}

	@Override
	public boolean addInitialPreviewConfig(String initialPreviewConfig) {
		if (CollectionUtils.isEmpty(this.initialPreviewConfig)) {
			this.initialPreviewConfig = new LinkedList<String>();
		}
		return this.initialPreviewConfig.add(initialPreviewConfig);
	}

}
