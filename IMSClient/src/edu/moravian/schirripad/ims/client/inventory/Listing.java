package edu.moravian.schirripad.ims.client.inventory;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import edu.moravian.schirripad.ims.client.ConnectionHandler;
import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.gui.MainFrame;
import edu.moravian.schirripad.ims.client.tickets.GetImageTicket;

public class Listing {
	private boolean hasImage, isSold, isNew = false;
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
			case "new":
				isNew = Boolean.parseBoolean(parts[1]);
			}
		}
		sc.close();
		sc = null;
	}

	public Listing(int id, String listingName, String description, int quantity, int price, boolean hasImage,
			boolean isSold, File image, String[] categories) {
		this.id = id;
		this.listingName = listingName;
		this.description = description;
		this.quantity = quantity;
		this.price = price;
		this.hasImage = hasImage;
		this.isSold = isSold;
		this.image = image;
		this.categories = new HashSet<String>(Arrays.asList(categories));
	}

	public boolean hasImage() {
		return hasImage;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isNew() {
		return isNew;
	}

	public Image getImage() {
		if (!hasImage) {
			return null;
		}
		if (img != null) {
			return img;
		}
		ConnectionHandler ch = MainFrame.ch;
		if (ch != null) {
			GetImageTicket getImg = new GetImageTicket(this);
			ch.addTicket(getImg);
			while (!getImg.isDone()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			img = getImg.getImage();
			return img;
		}
		return Main.defImg;
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
			out.println("new:" + isNew);
			out.println("description:" + description);
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
