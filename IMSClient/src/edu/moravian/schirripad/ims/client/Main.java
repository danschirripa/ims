package edu.moravian.schirripad.ims.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Scanner;

import edu.moravian.schirripad.ims.client.gui.MainFrame;
import edu.moravian.schirripad.ims.client.inventory.InventoryManager;
import edu.moravian.schirripad.ims.logging.Logger;

public class Main {
	private static final Logger log = new Logger("Main");
	private static Hashtable<String, String> configuration;
	private static InventoryManager ims;
	public static final File home = new File(System.getProperty("user.home"));
	private static boolean isOffline;

	public static void main(String[] args) {
		log.log("Starting IMS Client...");
		configuration = new Hashtable<String, String>();
		loadConf();
		log.log("Starting IMS...");
		ims = new InventoryManager();
		log.log("Launching Client");
		new MainFrame();
	}

	public static InventoryManager getIMS() {
		return ims;
	}

	private static void loadConf() {
		log.log("Loading configuration...");
		File conf = new File(home, "ims/ims.conf");
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
				out.println("invdir:" + home.getPath() + "/inv.d");
				out.println("imagedir:" + home.getPath() + "dat/img.d");
				out.println("serverip:thelicc.ignorelist.com");
				out.println("serverport:35");

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

	public static boolean getOffline() {
		return isOffline;
	}

	public static void setOffline(boolean setOffline) {
		isOffline = setOffline;
	}
}
