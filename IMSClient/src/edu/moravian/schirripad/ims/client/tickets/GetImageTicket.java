package edu.moravian.schirripad.ims.client.tickets;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.Ticket;
import edu.moravian.schirripad.ims.client.TicketException;
import edu.moravian.schirripad.ims.client.inventory.Listing;

public class GetImageTicket extends Ticket {
	private Listing l;
	private Image img = Main.defImg;

	public GetImageTicket(Listing l) {
		this.l = l;
	}

	@Override
	public boolean action(Scanner sc, PrintStream out) throws TicketException {
		// TODO Create ticket for getting image
		System.out.println("Getting img");
		out.println("get");
		sc.nextLine();
		out.println(l.getListingName());
		out.println("image");
		String next = sc.nextLine();
		if (next.equalsIgnoreCase("NOIMAGE")) {
			return true;
		}
		next = sc.nextLine();
		int n = Integer.parseInt(next);
		byte[] imgByte = new byte[n];
		n = 0;
		while (!(next = sc.nextLine()).equals("DONE")) {
			imgByte[n] = (byte) Integer.parseInt(next);
			n++;
		}
		ByteArrayInputStream bin = new ByteArrayInputStream(imgByte);
		try {
			img = ImageIO.read(bin);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Image getImage() {
		return img;
	}
}
