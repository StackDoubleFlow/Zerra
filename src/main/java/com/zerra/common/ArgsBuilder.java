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
	private final AccountProcessor accountProcessor;

	public ArgsBuilder(boolean isServer, AccountProcessor accountProcessor) {
		this.isServer = isServer;
		this.isClient = !isServer;
		this.accountProcessor = accountProcessor;
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
				IOManager.init(new File("data\\"));
				return new ArgsBuilder(false, new AccountProcessor("null"));
			} else {
				LAUNCH.fatal("Missing required parameters");
				System.exit(CrashCodes.INVALID_ARGUMENTS);
			}
		}

		// default assignments
		boolean isServer = false;
        AccountProcessor accountProcessor = null;

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

				break;
			case "--id":
				if(!iterator.hasNext()) {
					throw new IllegalArgumentException("after --id an id should be specified");
				}
				String id = iterator.next();
                if (id.startsWith("--")) {
                    throw new IllegalArgumentException("after --id an id should be specified");
                }
                accountProcessor = new AccountProcessor(id);
				accountProcessor.process();
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
					throw new IllegalArgumentException("after --dir a directory should be specified");
				}
				if(!dataDirectory.exists()){
					throw new IllegalArgumentException("after --dir an existing directory should be specified");
				}
				IOManager.init(dataDirectory);
				//instead of saving it, we initialize the io manager before we start zerra
				break;
			default:
				break;
			}
		}
        if(accountProcessor == null){
            if (Launch.IS_DEVELOPMENT_BUILD) {
                accountProcessor = new AccountProcessor("null");
            } else {
                LAUNCH.fatal("Missing required parameters");
                System.exit(CrashCodes.INVALID_ARGUMENTS);
            }
        }
		return new ArgsBuilder(isServer, accountProcessor);
	}

	public boolean isClient() {
		return isClient;
	}

	public boolean isServer() {
		return isServer;
	}


}