package com.sudin.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties("storage")
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	@Value("${location}")
	private String location;

	@Value("${myapp.upload.max-file-size}")
	private String maxFileSize;

	@Value("${myapp.upload.max-request-size}")
	private String maxRequestSize;

	private Path rootLocation;

	public String getLocation() {
		return location;
	}

	public Long getMaxFileSize() {
		return parseSize(maxFileSize);
	}

	public Long getMaxRequestSize() {
		return parseSize(maxRequestSize);
	}

	public Path getRootLocation() {
		return rootLocation;
	}

	public void setRootLocation(Path rootLocation) {
		this.rootLocation = rootLocation;
	}

	private long parseSize(String size) {
		long multiplier = 1;
		size = size.toUpperCase();

		if (size.endsWith("MB")) {
			multiplier = 1024 * 1024; // 1 MB = 1024 KB * 1024 bytes
			size = size.substring(0, size.length() - 2);
		} else if (size.endsWith("KB")) {
			multiplier = 1024; // 1 KB = 1024 bytes
			size = size.substring(0, size.length() - 2);
		}

		return Long.parseLong(size) * multiplier;
	}
}
