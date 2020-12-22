package io.github.fourfantastic.standby.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.repository.FileRepository;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = StandbyApplication.class)
public class FileRepositoryTest {
	final static Path root = Paths.get("testFolder");
	
	@Autowired
	FileRepository fileRepository;
	
	@BeforeAll
	public static void createRoot() {
		if (Files.notExists(root)) {
			assertDoesNotThrow(() -> {
				Files.createDirectory(root);
			});
		}
	}
	
	@BeforeEach
	public void setup() {
		assertTrue(Files.exists(root));
		assertDoesNotThrow(() -> {
			Iterator<Path> it = Files.list(root).iterator();
			while (it.hasNext()) {
				it.next().toFile().delete();
			}
			assertThat(Files.list(root).count()).isEqualTo(0L);
		});
	}
	
	@Test
	public void getFileExtensionTest() {
		final String fileName1 = "example.mp4";
		final String fileName2 = "C:\\Users\\Fourfantastics\\text.txt";
		final String fileName3 = "/home/fourfantastics/music-with_slashs-and_underlines.ogg";
		final String fileName4 = "F:\\Users/fourfantastics\\folder/anotherFolder\\Folder/otherFolder\\So long, lonesome.txt.mp4";
		final String fileName5 = "";
		final String fileName6 = "C:\\Users";
		final String fileName7 = "C:\\Users.sad.sad.asdasd.sad.exm";
		final String fileName8 = "asdas.";
		
		assertThat(fileRepository.getFileExtension(fileName1)).isEqualTo(".mp4");
		assertThat(fileRepository.getFileExtension(fileName2)).isEqualTo(".txt");
		assertThat(fileRepository.getFileExtension(fileName3)).isEqualTo(".ogg");
		assertThat(fileRepository.getFileExtension(fileName4)).isEqualTo(".mp4");
		assertThat(fileRepository.getFileExtension(fileName5)).isEqualTo(null);
		assertThat(fileRepository.getFileExtension(fileName6)).isEqualTo(null);
		assertThat(fileRepository.getFileExtension(fileName7)).isEqualTo(".exm");
		assertThat(fileRepository.getFileExtension(fileName8)).isEqualTo(null);
	}
	
	@Test
	public void getFileExtensionFromMultipartFileTest() {
		final MultipartFile mockFile = new MockMultipartFile("mockFile", "mockFile.mp4", "text/plain", "This is an example".getBytes());
		
		assertThat(fileRepository.getFileExtension(mockFile)).isEqualTo(".mp4");
	}
	
	@Test
	public void createDirectoryTest() {
		final String folderName = "thisIsATestFolder";
		final Path folderPath = root.resolve(folderName);
		
		assertTrue(Files.notExists(folderPath));
		
		assertTrue(fileRepository.createDirectory(folderPath));
		
		assertTrue(Files.exists(folderPath));
		assertTrue(Files.isDirectory(folderPath));
		assertTrue(Files.isReadable(folderPath));
		assertTrue(Files.isWritable(folderPath));
		assertDoesNotThrow(() -> {
			assertThat(Files.list(folderPath).count()).isEqualTo(0L);
		});
	}
	
	@Test
	public void saveFileTest() {
		final byte[] content = "This is an example".getBytes();
		final MultipartFile mockFile = new MockMultipartFile("mockFile", "mockFile.txt", "text/plain", content);
		final String fileName = "theNameOfTheFileWhenSaved.txt";
		final Path filePath = root.resolve(fileName);
		
		assertTrue(Files.notExists(filePath));
		
		assertTrue(fileRepository.saveFile(mockFile, filePath));
		
		assertTrue(Files.exists(filePath));
		assertFalse(Files.isDirectory(filePath));
		assertTrue(Files.isReadable(filePath));
		assertTrue(Files.isWritable(filePath));
		assertDoesNotThrow(() -> {
			File savedFile = filePath.toFile();
			FileInputStream inputStream = new FileInputStream(savedFile);
			assertThat(inputStream.readAllBytes()).isEqualTo(content);
			inputStream.close();
		});
	}
	
	@Test
	public void getFileTest() {
		final String fileName = "fileToGet";
		final Path filePath = root.resolve(fileName);
		assertDoesNotThrow(() -> {
			assertTrue(filePath.toFile().createNewFile());
		});
		
		assertTrue(Files.exists(filePath));
		Optional<Resource> optionalResource = fileRepository.getFile(filePath);
		assertTrue(optionalResource.isPresent());
		assertDoesNotThrow(() -> {
			assertThat(optionalResource.get().contentLength()).isEqualTo(0L);
		});
	}
	
	@Test
	public void getFileNotFoundTest() {
		final String fileName = "nonexistentFile";
		final Path filePath = root.resolve(fileName);
		
		assertTrue(Files.notExists(filePath));
		assertTrue(fileRepository.getFile(filePath).isEmpty());
	}
	
	@AfterAll
	public static void cleanUp() {
		assertTrue(Files.exists(root));
		assertDoesNotThrow(() -> {
			Iterator<Path> it = Files.list(root).iterator();
			while (it.hasNext()) {
				it.next().toFile().delete();
			}
			assertThat(Files.list(root).count()).isEqualTo(0L);
			Files.delete(root);	
		});
		assertTrue(Files.notExists(root));
	}
}