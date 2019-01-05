package com.zerra.common;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zerra.Launch;
import com.zerra.common.world.storage.IOManager;

/**
 * @author Tebreca Class that takes the String[] given as arg and deserializes it into an object holding the launch args vice versa
 */
public class ArgsBuilder {

	// TODO: make a toString to make this class able to produce args

	public static final Logger LAUNCH = LogManager.getLogger("Launch");

	private final boolean isServer;
	private final boolean isClient;
	private final String id;
	private final String workingDir;

	public ArgsBuilder(boolean isServer, String id, String workingDir) {
		this.isServer = isServer;
		this.isClient = !isServer;
		this.id = id;
		this.workingDir = workingDir;
	}

	/**
	 * @param args
	 *            the args given by the runtime environment
	 * @return an new instance of the {@link ArgsBuilder} which contains the values of the parsed args
	 */
	public static ArgsBuilder deserialize(String[] args) {
		// check if zerra is in a development environment
		if (Launch.IS_DEVELOPMENT_BUILD) {
			LAUNCH.info("Launching from development environment");
		}

		// checks if there are enough args, unless in development build, it'll exit with a negative exit code
		if (args.length == 0) {
			if (Launch.IS_DEVELOPMENT_BUILD) {
				IOManager.init(new File("data"));
				return new ArgsBuilder(false, "player", "null");
			} else {
				LAUNCH.fatal("Missing required parameters");
				System.exit(CrashCodes.INVALID_ARGUMENTS);
			}
		}
		// default assignments
		boolean isServer = false;
		String id = null, workingDir = null;
		Iterator<String> iterator = Arrays.asList(args).iterator();

		// iterating trough strings as args; to add args: just add another case statement to the switch.
		// then, when you need the next string, check if the iterator has a next arg, and that it doesn't start
		// with "--" for an example look at the case for loginKey, username and dir
		while (iterator.hasNext()) {
			String value = iterator.next();
			switch (value) {
			case "--server":
				isServer = true;
				break;
			case "--client":
				isServer = false;
				break;
			case "--id":
				if(!iterator.hasNext()) {
					throw new IllegalArgumentException("after --id a directory should be specified");
				}
				//AccountProcessor proc = new AccountProcessor(iterator.next());
				//proc.process();
				break;
			case "--dir":
				if (!iterator.hasNext()) {
					throw new IllegalArgumentException("after --dir a directory should be specified");
				}
				String path = iterator.next();
				if (path.startsWith("--")) {
					throw new IllegalArgumentException("after --dir a directory should be specified");
				}
				File dataDirectory = new File(path);
				if (!dataDirectory.isDirectory()) {
					throw new IllegalArgumentException("path (" + path + ") is not a directory!");
				}
				if(!dataDirectory.exists()){
					throw new IllegalArgumentException("directory specified (" + path + ") does not exist!");
				}
				IOManager.init(dataDirectory);
				//instead of saving it, we preinit the io manager before we start zerra
				break;
			default:
				break;
			}
		}

		// test if all args are set, if not, assigning the data but nly if IS_DEVELOPMENT_BUILD is true
		return new ArgsBuilder(isServer, id, workingDir);
	}

	public boolean isClient() {
		return isClient;
	}

	public boolean isServer() {
		return isServer;
	}

	public String getId() {
		return id;
	}

	public String getWorkingDirectory() {
		return workingDir;
	}
}