package io.github.fourfantastics.standby.repository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class FileRepository {
	public String getFileExtension(MultipartFile file) {
		return getFileExtension(file.getOriginalFilename());
	}
	
	public String getFileExtension(String fileName) {
		String[] fileSplit = fileName.split("\\.");
		if (fileSplit.length < 2) {
			return null;
		}

		String extension = fileSplit[fileSplit.length - 1];
		if (extension.isEmpty()) {
			return null;
		}
		return "." + extension;
	}
	
	public boolean createDirectory(Path path) {
		try {
			Files.createDirectory(path);
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}
	
	public boolean saveFile(MultipartFile file, Path path) {
		return saveFile(file.getResource(), path);
	}
	
	public boolean saveFile(Resource file, Path path) {
		try {
			Files.copy(file.getInputStream(), path);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public Optional<Resource> getFile(Path path) {
		try {
			Resource resource = new UrlResource(path.toUri());
			if (resource.exists()) {
				return Optional.of(resource);
			}
		} catch (MalformedURLException e) {
		}
		return Optional.empty();
	}
}
