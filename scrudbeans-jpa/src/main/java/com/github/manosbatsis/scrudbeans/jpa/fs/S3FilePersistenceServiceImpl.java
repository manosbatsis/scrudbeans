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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.github.manosbatsis.scrudbeans.api.mdd.service.FilePersistenceService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * An implementation of {@link FilePersistenceService} that uses Amazon S3 for
 * file storage. Configuration properties from dev.properties:
 *
 * build.aws_access_key_id=
 * build.aws_secret_access_key=
 * build.aws_namecard_bucket=:
 *
 * from bean config: awsAccessKey, awsSecretAccessKey, nameCardBucket
 *
 */
public class S3FilePersistenceServiceImpl extends AbstractFilePersistenceServiceImpl implements FilePersistenceService, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(S3FilePersistenceServiceImpl.class);

	@Value("${aws_namecard_bucket}")
	private String nameCardBucket;

	@Value("${aws_access_key_id}")
	private String awsAccessKey;

	@Value("${aws_secret_access_key}")
	private String awsSecretAccessKey;

	private AmazonS3Client s3Client;

	/**
	 * Invoked by the containing {@code BeanFactory} after it has set all bean properties
	 * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
	 * <p>This method allows the bean instance to perform validation of its overall
	 * configuration and final initialization when all bean properties have been set.
	 * @throws Exception in the event of misconfiguration (such as failure to set an
	 * essential property) or if initialization fails for any other reason
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// create S3 credentials
		BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretAccessKey);
		// setup client
		this.s3Client = new AmazonS3Client(credentials);
		LOGGER.debug("Created S3 client");
	}


	/**
	 * Save file in S3
	 * @see FilePersistenceService#saveFile(File, long, String, String)
	 */
	@Override
	public String saveFile(File file, long contentLength, String contentType, String path) {
		String url;
		// create metadata
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(contentLength);
		meta.setContentType(contentType);
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			// save to bucket
			PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest(nameCardBucket, path, in, meta)
					.withCannedAcl(CannedAccessControlList.PublicRead));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (Objects.nonNull(in)) {
				IOUtils.closeQuietly(in);
			}
		}
		// set the URL to return
		url = s3Client.getUrl(nameCardBucket, path).toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("File saved, url: {}, size: {}, contentType: {}", url, contentLength, contentType);
		}//
		return url;
	}

	/**
	 * Delete files from S3
	 *
	 * @see FilePersistenceService#deleteFiles(String...)
	 */
	@Override
	public void deleteFiles(String... paths) {
		// delete from bucket
		s3Client.deleteObjects(new DeleteObjectsRequest(nameCardBucket).withKeys(paths));
	}

}
