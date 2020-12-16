package io.github.fourfantastics.standby.service;

import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.service.exceptions.InvalidExtensionException;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;

public interface FileService {
	public void init();

	public String save(MultipartFile file) throws InvalidExtensionException, RuntimeException;

	public Resource load(String filename);

	public void deleteAll();

	public Stream<Path> loadAll();
}