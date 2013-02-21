import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class Watcher {
	public static void main(String[] args) {
		Path this_dir = Paths.get(".");
		System.out.println("Now watching the current directory ...");

		try {
			WatchService watcher = this_dir.getFileSystem().newWatchService();
			this_dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

			WatchKey watckKey = watcher.take();

			List<WatchEvent<?>> events = watckKey.pollEvents();
			for (WatchEvent event : events) {
				System.out.println("Someone just created the file '"
						+ event.context().toString() + "'.");
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}
}