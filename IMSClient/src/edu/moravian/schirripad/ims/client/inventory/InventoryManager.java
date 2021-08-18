package edu.moravian.schirripad.ims.client.inventory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.gui.MainFrame;
import edu.moravian.schirripad.ims.client.tickets.CreateListingTicket;
import edu.moravian.schirripad.ims.logging.Logger;

public class InventoryManager {
	private Logger log = new Logger("IM");

	private Hashtable<String, Listing> listings = new Hashtable<String, Listing>();
	private Hashtable<Long, Listing> listingsByID = new Hashtable<Long, Listing>();
	private File invDir;

	public InventoryManager() {
		log.log("Starting InventoryManager...");
		if (!Main.confHasValue("invdir")) {
			invDir = new File(Main.home, "inv.d");
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
			if(listing.getName().startsWith(".")) {
				continue;
			}
			try {
				Listing l = new Listing(listing);
				this.listings.put(l.getListingName(), l);
				listingsByID.put(l.getID(), l);
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

	public Collection<Listing> getListings() {
		return listings.values();
	}

	public boolean removeListing(String listingName) {
		if (!listings.containsKey(listingName))
			return false;
		Listing l = listings.get(listingName);
		listings.remove(listingName);
		listingsByID.remove(l.getID());
		return true;
	}

	public boolean removeListing(long id) {
		if (!listingsByID.containsKey(id)) {
			return false;
		}
		Listing l = listingsByID.get(id);
		File f = new File(invDir, l.getListingName() + ".listing");
		if (!f.exists()) {
			log.err("Failed to delete " + f.getAbsolutePath() + "\nFile does not exist");
			return false;
		}
		if (!f.delete()) {
			log.err("Failed to delete " + f.getAbsolutePath() + "\nFile failed to delete");
			return false;
		}
		listingsByID.remove(id);
		listings.remove(l.getListingName());
		return true;
	}

	public Listing getListing(long id) {
		return listingsByID.get(id);
	}

	public void createListing(long id, String listingName, String description, int quantity, int price, int cost,
			boolean hasImage, boolean isSold, File image, String[] categories) throws ListingException {
		File f = new File(invDir, listingName + ".listing");
		try {
			if (f.exists()) {
				f.delete();
				f.createNewFile();
			}
			if (listings.containsKey(listingName)) {
				listings.remove(listingName);
			}

			PrintStream out = new PrintStream(new FileOutputStream(f));
			out.println("id:" + id);
			out.println("hasimage:" + hasImage);
			if(hasImage)
			out.println("imgfile:" + image.getPath());
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
			out.println("cost:" + cost);
			out.println("description:" + description);
			if (MainFrame.ch == null)
				out.println("new:" + true);
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
 
			if (MainFrame.ch != null) {
				CreateListingTicket clt = new CreateListingTicket(l);
				MainFrame.ch.addTicket(clt);
			}

			listings.put(lName, l);
			listingsByID.put(l.getID(), l);

		} catch (Exception e) {
			throw new ListingException(e);
		}
	}
	
	public void fireGlobalPropertyChange(String propId, String value) {
		Enumeration<Listing> lstngs = listingsByID.elements();
		while(lstngs.hasMoreElements()) {
			lstngs.nextElement().receiveGlobalPropertyChange(propId, value);
		}
	}

}
