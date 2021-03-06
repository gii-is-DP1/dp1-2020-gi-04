package io.github.fourfantastic.standby.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.repository.FileRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class FileRepositoryTest {
	final static Path root = Paths.get("testFolder");

	@Autowired
	FileRepository fileRepository;

	@BeforeAll
	public static void createRoot() throws IOException {
		if (Files.notExists(root)) {
			Files.createDirectory(root);
		}
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
		final String fileName9 = "dasdasd.     ";

		assertThat(fileRepository.getFileExtension(fileName1)).isEqualTo(".mp4");
		assertThat(fileRepository.getFileExtension(fileName2)).isEqualTo(".txt");
		assertThat(fileRepository.getFileExtension(fileName3)).isEqualTo(".ogg");
		assertThat(fileRepository.getFileExtension(fileName4)).isEqualTo(".mp4");
		assertThat(fileRepository.getFileExtension(fileName5)).isEqualTo(null);
		assertThat(fileRepository.getFileExtension(fileName6)).isEqualTo(null);
		assertThat(fileRepository.getFileExtension(fileName7)).isEqualTo(".exm");
		assertThat(fileRepository.getFileExtension(fileName8)).isEqualTo(null);
		assertThat(fileRepository.getFileExtension(fileName9)).isEqualTo(null);
	}

	@Test
	public void getFileExtensionFromMultipartFileTest() {
		final MultipartFile mockFile = new MockMultipartFile("mockFile", "mockFile.mp4", "text/plain",
				"This is an example".getBytes());

		assertThat(fileRepository.getFileExtension(mockFile)).isEqualTo(".mp4");
	}
	
	@Test
	public void createDirectoryTest() {
		final String directoryName = "wakala";
		final Path directoryPath = root.resolve(directoryName);
		
		assertTrue(Files.notExists(directoryPath));
		
		fileRepository.createDirectory(directoryPath);
		
		assertTrue(Files.exists(directoryPath));
	}
	
	@Test
	public void createRepeatedDirectoryTest() {
		final String directoryName = "wakala2";
		final Path directoryPath = root.resolve(directoryName);
		
		assertTrue(Files.notExists(directoryPath));
		
		fileRepository.createDirectory(directoryPath);
		
		assertTrue(Files.exists(directoryPath));
		
		assertFalse(fileRepository.createDirectory(directoryPath));
	}

	@Test
	public void saveMultipartFileTest() {
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
			assertThat(Files.readAllBytes(filePath)).isEqualTo(content);
		});
	}

	@Test
	public void saveRepeatedMultipartFileTest() {
		final byte[] content = "This is an example".getBytes();
		final MultipartFile mockFile = new MockMultipartFile("mockFile2", "mockFile2.txt", "text/plain", content);
		final String fileName = "theNameOfTheFileWhenSaved2.txt";
		final Path filePath = root.resolve(fileName);

		assertTrue(Files.notExists(filePath));

		assertTrue(fileRepository.saveFile(mockFile, filePath));

		assertTrue(Files.exists(filePath));
		assertFalse(Files.isDirectory(filePath));
		assertTrue(Files.isReadable(filePath));
		assertTrue(Files.isWritable(filePath));
		assertDoesNotThrow(() -> {
			assertThat(Files.readAllBytes(filePath)).isEqualTo(content);
		});
		
		assertFalse(fileRepository.saveFile(mockFile, filePath));
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
		assertFalse(fileRepository.getFile(filePath).isPresent());
	}

	@AfterAll
	public static void cleanUp() throws IOException {
		File[] files = root.toFile().listFiles();
		for (File file : files) {
			file.delete();
		}
		root.toFile().delete();
	}
}
