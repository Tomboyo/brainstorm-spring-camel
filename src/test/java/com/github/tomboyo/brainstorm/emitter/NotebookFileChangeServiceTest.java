package com.github.tomboyo.brainstorm.emitter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.github.tomboyo.brainstorm.configuration.TemporaryDirectory;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(properties = "notebook.file.extensions=ext")
@ContextConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public final class NotebookFileChangeServiceTest {

	@Autowired @Qualifier("fileEventEndpoint")
	protected MockEndpoint endpoint;

	@Autowired
	protected NotebookFileChangeService subject;

	@Test
	public void emitsEventsWhenFilesCreated() throws Exception {
		assertTrue(endpoint.isStarted());
		assertTrue(endpoint.getCamelContext().isStarted());

		var newPath = subject.getNotebookDirecotry()
			.resolve("new.ext")
			.toAbsolutePath();

		endpoint.expectedBodiesReceived(newPath);

		Files.write(newPath, new byte[]{});

		endpoint.assertIsSatisfied();
	}
}
