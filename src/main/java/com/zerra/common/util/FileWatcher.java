package com.zerra.common.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public abstract class FileWatcher implements Runnable {

	private final WatchService watcher;

	public FileWatcher(final Path folderPath) throws IOException {
		watcher = FileSystems.getDefault().newWatchService();
		folderPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	}

	@Override
	public void run() {
		try {
			WatchKey key;
			while((key = watcher.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					if(event.kind() == OVERFLOW) {
						continue;
					}
					Path filePath = (Path) event.context();
					onEvent(event, filePath);
				}
				key.reset();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private final void onEvent(WatchEvent<?> event, Path filePath) {
		if(event == ENTRY_CREATE) {
			onEntryCreate(filePath);
		} else if(event == ENTRY_DELETE) {
			onEntryDelete(filePath);
		} else if(event == ENTRY_MODIFY) {
			onEntryModified(filePath);
		}
	}
	
	public abstract void onEntryCreate(Path filePath);
	public abstract void onEntryDelete(Path filePath);
	public abstract void onEntryModified(Path filePath);
}
