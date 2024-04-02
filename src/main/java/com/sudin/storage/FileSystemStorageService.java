package com.sudin.storage;

import com.sudin.CommonService;
import com.sudin.Controller.FileUploadController;
import com.sudin.dto.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

	@Autowired
	private StorageProperties properties;

//	private final Path rootLocation;
//	private final Long maxFileSize;
//	private final Long maxRequestSize;
//
//	@Autowired
//	public FileSystemStorageService(StorageProperties properties) {
//
//        if(properties.getLocation().trim().length() == 0){
//            throw new StorageException("File upload location can not be Empty.");
//        }
//
//		this.rootLocation = properties.getRootLocation();
//        this.maxFileSize = properties.getMaxFileSize();
//        this.maxRequestSize = properties.getMaxRequestSize();
//	}

	@Override
	public void store(MultipartFile  file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			if (file.getSize() > properties.getMaxFileSize()) {
				throw new StorageException("Each file size must not exceed 300MB.");
			}
			MultipartFile newFile = createRenamedMultipartFile(file, cleanFileName(file.getOriginalFilename()));
			Path destinationFile = properties.getRootLocation().resolve(
					Paths.get(newFile.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(properties.getRootLocation().toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(properties.getRootLocation(), 1)
				.filter(path -> !path.equals(properties.getRootLocation()))
				.map(properties.getRootLocation()::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return properties.getRootLocation().resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(properties.getRootLocation().toFile());
	}

	@Override
	public List<FileDTO> getFiles(HttpServletRequest request) {
		List<String> linkFiles = loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
						"serveFile", path.getFileName().toString(), request).build().toUri().toString())
				.collect(Collectors.toList());
		List<FileDTO> files = new ArrayList<>();
		linkFiles.forEach(file -> {
			String[] farr = file.split("/");
			String fileName = farr[farr.length - 1];
			Path path = Paths.get(properties.getRootLocation().toString(), fileName);
			BasicFileAttributes attr = null;
			try {
				attr = Files.readAttributes(path, BasicFileAttributes.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileDTO fileDTO = new FileDTO();
			fileDTO.setName(fileName);
			fileDTO.setLink(file);
			fileDTO.setDate(CommonService.convertToDateTime(attr.creationTime().toString()));
			fileDTO.setSize(String.format("%s KB" ,attr.size()/1024));
			files.add(fileDTO);
		});
		return files;
	}

	// Hàm đổi tên file
	private String cleanFileName(String originalFileName) {
		return originalFileName.replaceAll("[^a-zA-Z0-9.-]", "");
	}

	public MultipartFile createRenamedMultipartFile(MultipartFile originalFile, String newFilename) {
		return new RenamedMultipartFile(originalFile, newFilename);
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(properties.getRootLocation());
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
