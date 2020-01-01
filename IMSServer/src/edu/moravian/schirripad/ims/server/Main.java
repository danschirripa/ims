package edu.moravian.schirripad.ims.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Scanner;

import edu.moravian.schirripad.ims.logging.Logger;
import edu.moravian.schirripad.ims.server.inventory.InventoryManager;

public class Main {
	private static final Logger log = new Logger("Main");
	private static Hashtable<String, String> configuration;
	private static InventoryManager ims;
	// public static final int PORT = 35;

	public static void main(String[] args) {
		log.log("Starting IMS Server...");
		configuration = new Hashtable<String, String>();
		loadConf();
		log.log("Starting IMS...");
		ims = new InventoryManager();
		log.log("Launching Server...");
		String stringPort = getConfValue("port");
		if (stringPort == null) {
			stringPort = "35";
		}
		int port;
		try {
			port = Integer.parseInt(stringPort);
		} catch (Exception e) {
			port = 35;
		}
		try {
			new Server(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static InventoryManager getIMS() {
		return ims;
	}

	private static void loadConf() {
		log.log("Loading configuration...");
		File conf = new File("/etc/ims/ims.conf");
		if (!conf.exists()) {
			log.log("Configuration non-existent, creating...");
			try {
				conf.getParentFile().mkdirs();
				if (!conf.getParentFile().exists()) {
					throw new IOException("Failed to create configuration directory");
				}
				conf.createNewFile();
				if (!conf.exists()) {
					throw new IOException("Failed to create configuration file");
				}
				log.log("Created conf file, populating with defaults...");

				PrintStream out = new PrintStream(new FileOutputStream(conf));
				// Write Defaults...
				out.println("port:35");
				out.println("invdir:/etc/ims/inv.d");
				out.println("imagedir:/etc/ims/dat/img.d");

				log.log("Finished populating, cleaning up...");
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				e.printStackTrace();
				log.err("UNABLE TO CREATE CONFIGURATION FILE, EXITING");
				System.exit(-1);
			}
		}

		// Read/Parse conf
		try {
			Scanner sc = new Scanner(new FileInputStream(conf));
			while (sc.hasNext()) {
				String next = sc.nextLine();
				String[] parts = next.split(":");
				if (parts.length < 2) {
					log.err("Invalid configuration line: " + next);
					continue;
				}
				configuration.put(parts[0], parts[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.err("UNABLE TO FIND CONFIGURATION FILE, EXITING");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			log.err("IOEXCEPTION, EXITING");
			System.exit(-1);
		}
	}

	public static String getConfValue(String key) {
		return configuration.get(key);
	}

	public static boolean confHasValue(String key) {
		return configuration.containsKey(key);
	}
}
