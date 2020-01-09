package edu.moravian.schirripad.ims.server.transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import edu.moravian.schirripad.ims.logging.Logger;
import edu.moravian.schirripad.ims.server.Main;
import edu.moravian.schirripad.ims.server.inventory.Listing;

public class TransactionHandler {
	private static Logger log = new Logger("TH");

	public static void handleSocketConnection(Socket s) {
		Thread t = new Thread(new Handler(s));
		t.setName("TransactionHandler-" + s.getInetAddress().toString());
		t.start();
	}

	private static class Handler implements Runnable {
		private Socket s;

		public Handler(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			Scanner sc = null;
			PrintStream out = null;
			try {
				sc = new Scanner(s.getInputStream());
				out = new PrintStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			while (sc.hasNext()) {
				try {
					// <name>;<<hasImg>;<imgfile>;<cats>;<isSold>;<quantity>;<desc>
					String next = sc.nextLine();
					if (next.equals("create")) {
						out.println("READY");
						String listing = sc.nextLine();
						out.println("PARSE");
						String[] parts = listing.split(";");
						if (parts.length < 7) {
							out.println("BAD");
							continue;
						}
						out.println("GOOD");
						String listingName = parts[0];
						boolean hasImage = Boolean.parseBoolean(parts[1]);
						File image;
						try {
							image = new File(parts[2]);
						} catch (NullPointerException e) {
							hasImage = false;
							image = new File("assets/imagenotfound.png");
						}
						String unparsedCats = parts[3];
						boolean isSold = Boolean.parseBoolean(parts[4]);
						int quantity = Integer.parseInt(parts[5]);
						int price = Integer.parseInt(parts[6]);
						String description = parts[7];

						String[] categories = unparsedCats.split(",");

						Main.getIMS().createListing(listingName, description, quantity, price, hasImage, isSold, image,
								categories);
					} else if (next.equals("get")) {
						out.println("READY");
						String listingName = sc.nextLine();
						if (Main.getIMS().getListing(listingName) == null) {
							out.println("BAD:LISTINGNAME");
							continue;
						}
						Listing l = Main.getIMS().getListing(listingName);
						next = sc.nextLine();
						switch (next) {
						case "quantity":
							out.println(l.getQuantity());
							break;
						case "hasImage":
							out.println(l.hasImage());
							break;
						case "image":
							if (!l.hasImage()) {
								out.println("NOIMAGE");
								break;
							}
							out.println("SENDING");
							File img = l.getImage();
							FileInputStream fin = new FileInputStream(img);
							out.println(img.length());
							int nextB = 0;
							while ((nextB = fin.read()) != -1) {
								out.println(nextB);
							}
							out.println("DONE");
							break;
						case "isSold":
							out.println(l.isSold());
							break;
						case "categories":

							String categories = "";
							for (String cat : l.getCategories())
								categories = categories + cat + ",";
							out.println(categories.substring(0, categories.length()));
							break;
						case "description":
							out.println(l.getDescription());
							break;
						case "id":
							out.println(l.getID());
							break;
						case "all":
							// <name>;<<hasImg>;<imgfile>;<cats>;<isSold>;<quantity>;<desc>
							String listing = "";
							listing = l.getListingName() + ";";
							listing += l.hasImage() + ";";
							listing += l.getImage().getPath() + ";";
							HashSet<String> cats = l.getCategories();
							for (String s : cats) {
								listing += s + ",";
							}
							listing = listing.substring(0, listing.length());
							listing += ";";
							listing += l.isSold() + ";";
							listing += l.getQuantity() + ";";
							listing += l.getPrice() + ";";
							listing += l.getDescription() + ";";
							listing += l.getID() + ";";

							out.println(listing);
						}

					} else if (next.equals("remove")) {
						out.println("READY");
						String listingName = sc.nextLine();
						if (Main.getIMS().removeListing(listingName)) {
							out.println("DONE:SUCCESS");
						} else {
							out.println("DONE:FAIL");
						}
					} else if (next.equals("modify")) {

					} else if (next.equals("list")) {
						Set<String> listings = Main.getIMS().getListingNames();
						out.println(listings.size());
						for (String listing : listings) {
							out.println(listing);
						}
					}

					out.println("DONE");
				} catch (Exception e) {
					e.printStackTrace();
					out.println("EXCEPTION: " + e.getMessage());
				}
			}
			sc.close();

		}

	}

}
