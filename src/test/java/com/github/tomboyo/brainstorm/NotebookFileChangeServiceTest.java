package com.github.tomboyo.brainstorm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.CamelSpringTest;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class NotebookFileChangeServiceTest {
	
	@TempDir
	Path directory;

	@Autowired
	@Qualifier("mockFileEventEndpoint")
	MockEndpoint mockFileEvent;

	@Autowired
	ThreadPoolTaskExecutor executor;

	@Autowired
	@Qualifier("mockFileEventProducerTemplate")
	ProducerTemplate producer;

	// TODO: @Profile + @ActiveProfile to autowire this, a la Elixir
	private NotebookFileChangeService subject;

	@BeforeEach
	public void setup() throws Exception {
		subject = new NotebookFileChangeService(
			directory, Set.of("ext"), executor, producer);
		subject.start();
	}

	@AfterEach
	public void teardown() {
		subject.shutdown();
	}

	@Test
	public void emitsEventsWhenFilesCreated() throws Exception {
		assertTrue(mockFileEvent.isStarted());
		assertTrue(mockFileEvent.getCamelContext().isStarted());
		assertTrue(producer.getCamelContext().isStarted());

		var newPath = directory.resolve("new.ext").toAbsolutePath();

		mockFileEvent.expectedBodiesReceived(newPath);

		Files.write(newPath, new byte[]{});

		mockFileEvent.assertIsSatisfied();
	}
}
