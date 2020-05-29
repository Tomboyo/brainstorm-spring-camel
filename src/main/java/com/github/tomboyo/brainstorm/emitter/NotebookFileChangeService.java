package com.github.tomboyo.brainstorm.emitter;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.Set;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class NotebookFileChangeService {
	private static final Logger logger =
		LoggerFactory.getLogger(NotebookFileChangeService.class);

	private final Path notebookDirectory;
	private final Set<String> extensions;
	private final ThreadPoolTaskExecutor executor;
	private final ProducerTemplate producer;
	private final WatchService watcher;
	
	private Future<?> watchTask;

	@Autowired
	public NotebookFileChangeService(
		@Qualifier("notebookDirectory") Path notebookDirectory,
		@Qualifier("notebookFileExtensions") Set<String> extensions,
		ThreadPoolTaskExecutor executor,
		@Qualifier("fileEventProducer") ProducerTemplate producer
	) throws IOException {
		this.notebookDirectory = notebookDirectory;
		this.extensions = extensions;
		this.executor = executor;
		this.producer = producer;
		watcher = FileSystems.getDefault().newWatchService();
	}

	@PostConstruct
	public void start() throws IOException {
		logger.info("Watching {} for changes to files with extensions {}",
			notebookDirectory, extensions);
		notebookDirectory.register(
			watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		watchTask = executor.submit(this::watch);
	}

	public Path getNotebookDirecotry() {
		return notebookDirectory;
	}

	/**
	 * Do not close any autowired resources. Let the container do that.
	 */
	@PreDestroy
	public void shutdown() {
		watchTask.cancel(true);

		try {
			watcher.close();
		} catch (IOException e) {
			logger.warn("Failed to close watch service", e);
		}
	}

	private void watch() {
		try {
			while (true) {
				var key = watcher.take();
				key.pollEvents().stream()
					.filter(event -> {
						return true;
						// var file = ((Path) event.context()).toFile();
						// return file.isFile() && hasExtension(file, extensions);
					})
					.forEach(event -> emitWatchEvent(event));
				key.reset();
			}
		} catch (InterruptedException e) {
			// When we cancel the watch task during shutdown, it will interrupt
			// the running thread.
		} catch (ClosedWatchServiceException e) {
			// If we *don't* cancel the watch task, the watch service will close
			// before the executor is shut down and raise this exception. We
			// don't expect that to happen, so we warn.
			logger.warn("Watch service closed while watching for changes", e);
		}
	}

	private static boolean hasExtension(File file, Set<String> extensions) {
		var name = file.getName();
		var index = name.lastIndexOf(".");
		if (index == -1) return false;
		return extensions.contains(name.substring(index + 1));
	}

	private void emitWatchEvent(WatchEvent<?> event) {
		var modified = (Path) event.context();
		try {
			producer.sendBody(modified.toAbsolutePath());
			logger.debug("Emitted update event for file {}", modified);
		} catch (CamelExecutionException e) {
			logger.error("Failed to send body for file {}", modified);
		}
	}
}
