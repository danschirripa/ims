package edu.moravian.schirripad.ims.client.tickets;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.JOptionPane;

import edu.moravian.schirripad.ims.client.Ticket;
import edu.moravian.schirripad.ims.client.TicketException;
import edu.moravian.schirripad.ims.client.inventory.Listing;

public class CreateListingTicket extends Ticket {
	private HashSet<Listing> listings = new HashSet<Listing>();

	public CreateListingTicket(Listing... listings) {
		this.listings = new HashSet<Listing>(Arrays.asList(listings));
	}

	public void addListing(Listing l) {
		listings.add(l);
	}

	public void removeListing(Listing l) {
		listings.remove(l);
	}

	@Override
	public boolean action(Scanner sc, PrintStream out) throws TicketException {

		for (Listing l : listings) {
			out.println("create");
			sc.nextLine();
			String listing = l.getListingName() + ";" + l.hasImage() + ";" + "img" + ";";
			for (String s : l.getCategories()) {
				listing += s + ",";
			}
			listing = listing.substring(0, listing.length() - 1);
			listing += ";" + l.isSold() + ";" + l.getQuantity() + ";" + l.getPrice() + ";" + l.getDescription();
			out.println(listing);
			sc.nextLine();
			if (sc.nextLine().equalsIgnoreCase("BAD")) {
				JOptionPane.showMessageDialog(null, "Failed to create/update listing on remote server");
				throw new TicketException("Got BAD response from server, failed to create/update listing on remote!");
			}

		}

		return true;
	}

}
