package edu.moravian.schirripad.ims.server.inventory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;

import edu.moravian.schirripad.ims.server.Main;

public class Listing {
	private boolean hasImage, isSold;
	private int quantity, price, id;
	private File image, listingConf;
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
				if (parts.length < 2) {
					image = new File("/etc/ims/default.png");
					break;
				}
				image = new File(parts[1]);
				if (!image.exists())
					throw new FileNotFoundException(image.getAbsolutePath());
				break;
			case "name":
				listingName = parts[1];
				break;
			case "categories":
				String[] cats = parts[1].split(",");
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

	public File getImage() {
		if (image == null) {
			return Main.defImg;
		}
		return image;
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

	public int getPrice() {
		return price;
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

}
