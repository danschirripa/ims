package edu.moravian.schirripad.ims.client.inventory;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;

import javax.imageio.ImageIO;

import edu.moravian.schirripad.ims.client.ConnectionHandler;
import edu.moravian.schirripad.ims.client.Ticket;
import edu.moravian.schirripad.ims.client.TicketException;
import edu.moravian.schirripad.ims.client.gui.MainFrame;

public class Listing {
	private boolean hasImage, isSold;
	private int quantity, price, id;
	private File image, listingConf;
	private Image img;
	private String listingName, description;
	private HashSet<String> categories = new HashSet<String>();

	public Listing(File listingConf) throws FileNotFoundException, ArrayIndexOutOfBoundsException {
		this.listingConf = listingConf;
		Scanner sc = new Scanner(new FileInputStream(listingConf));
		while (sc.hasNext()) {
			String next = sc.nextLine();
			String[] parts = next.split(":");
			switch (parts[0]) {
			case "hasimage":
				hasImage = Boolean.parseBoolean(parts[1]);
				break;
			case "imgfile":
				image = new File(parts[1]);
				if (!image.exists())
					throw new FileNotFoundException(image.getAbsolutePath());
				break;
			case "name":
				listingName = parts[1];
				break;
			case "categories":
				String[] cats = parts[1].split(",");
				if (cats == null)
					break;
				for (String cat : cats) {
					if (cat != null)
						categories.add(cat);
				}
				break;
			case "description":
				description = parts[1];
				break;
			case "sold":
				isSold = Boolean.parseBoolean(parts[1]);
				break;
			case "quantity":
				quantity = Integer.parseInt(parts[1]);
				break;
			case "price":
				price = Integer.parseInt(parts[1]);
				break;
			case "id":
				id = Integer.parseInt(parts[1]);
				break;
			}
		}
		sc.close();
		sc = null;
	}

	public boolean hasImage() {
		return hasImage;
	}

	public Image getImage() {
		if (!hasImage) {
			return null;
		}
		if (img != null) {
			return img;
		}
		ConnectionHandler ch = MainFrame.ch;
		Ticket getImg = new Ticket() {

			@Override
			public boolean action(Scanner sc, PrintStream out) throws TicketException {
				// TODO Create ticket for getting image
				System.out.println("Getting img");
				out.println("get");
				sc.nextLine();
				out.println(getListingName());
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
		};
		ch.addTicket(getImg);
		while (!getImg.isDone()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return img;
	}

	public int getPrice() {
		return price;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public String getListingName() {
		return listingName;
	}

	public HashSet<String> getCategories() {
		return categories;
	}

	public void addCategory(String cat) {
		if (!categories.contains(cat))
			categories.add(cat);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSold() {
		return isSold;
	}

	public void setSold(boolean sold) {
		isSold = sold;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getID() {
		return id;
	}

	public boolean saveListing() throws ListingException {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(listingConf));
			out.println("hasImage:" + hasImage);
			out.println("imgFile:" + image.getPath());
			out.println("name:" + listingName);
			out.print("categories:");
			String allCats = "";
			for (String cat : categories) {
				allCats = allCats + cat + ",";
			}
			allCats.substring(0, allCats.length());
			out.println(allCats);
			out.println("sold:" + isSold);
			out.println("quantity:" + quantity);
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new ListingException(e.getMessage());
		}
		return true;
	}

	@Override
	public String toString() {
		return listingName;
	}

}
