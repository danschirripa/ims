package edu.moravian.schirripad.ims.server.inventory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Set;

import edu.moravian.schirripad.ims.logging.Logger;
import edu.moravian.schirripad.ims.server.Main;

public class InventoryManager {
	private Logger log = new Logger("IM");

	private Hashtable<String, Listing> listings = new Hashtable<String, Listing>();
	private File invDir;

	public InventoryManager() {
		log.log("Starting InventoryManager...");
		if (!Main.confHasValue("invdir")) {
			invDir = new File("/etc/ims/inv.d");
		} else {
			invDir = new File(Main.getConfValue("invdir"));
		}

		if (!invDir.exists()) {
			log.log("Inventory listings directory non-existent, creating...");
			try {
				invDir.mkdir();
				if (!invDir.exists()) {
					throw new IOException("Failed to create inventory listings directory");
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.err("FAILED TO CREATE DIRECTORY, EXITING");
				System.exit(-1);
			}
		}

		File[] listings = invDir.listFiles();
		for (File listing : listings) {
			try {
				Listing l = new Listing(listing);
				this.listings.put(l.getListingName(), l);
			} catch (Exception e) {
				e.printStackTrace();
				log.err("BAD LISTING @ " + listing.getAbsolutePath());
				continue;
			}
		}
	}

	public Listing getListing(String listingName) {
		return listings.get(listingName);
	}

	public boolean removeListing(String listingName) {
		if (!listings.containsKey(listingName))
			return false;
		listings.remove(listingName);
		return true;
	}

	public Set<String> getListingNames() {
		return listings.keySet();
	}

	public void createListing(String listingName, String description, int quantity, int price, boolean hasImage,
			boolean isSold, File image, String[] categories) throws ListingException {
		File f = new File(invDir, listingName + ".listing");
		try {
			if (f.exists()) {
				f.delete();
				f.createNewFile();
			}
			PrintStream out = new PrintStream(new FileOutputStream(f));
			out.println("hasImage:" + hasImage);
			out.println("imgFile:" + image.getPath());
			out.println("name:" + listingName);
			out.print("categories:");
			String allCats = "";
			for (String cat : categories) {
				allCats = allCats + "," + cat;
			}
			out.println(allCats);
			out.println("sold:" + isSold);
			out.println("quantity:" + quantity);
			out.println("price:" + price);
			out.flush();
			out.close();

			Listing l = new Listing(f);
			String lName = l.getListingName();
			int n = 0;

			while (listings.containsKey(lName)) {
				if (n != 0)
					lName.substring(0, lName.length());
				lName = lName + n;
				n++;
			}

			listings.put(lName, l);

		} catch (Exception e) {
			throw new ListingException(e.getMessage());
		}
	}

}
