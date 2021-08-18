package edu.moravian.schirripad.ims.client.inventory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.BarcodeException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import edu.moravian.schirripad.ims.client.ConnectionHandler;
import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.gui.MainFrame;
import edu.moravian.schirripad.ims.client.tickets.GetImageTicket;

public class Listing {
	private boolean hasImage, isSold, isNew = false;
	private int quantity, price, cost;
	private long upcNum;
	private File image, listingConf;
	private Image img;
	private BufferedImage upc;
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
				hasImage = true;
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
			case "cost":
				cost = Integer.parseInt(parts[1]);
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
				upcNum = Long.parseLong(parts[1]);
				break;
			case "new":
				isNew = Boolean.parseBoolean(parts[1]);
			}
		}
		sc.close();
		sc = null;
		try {
			generateBarcode();
		} catch (ConfigurationException | BarcodeException | IOException e) {
			e.printStackTrace();
		}
	}

	public Listing(long id, String listingName, String description, int quantity, int price, int cost, boolean hasImage,
			boolean isSold, File image, String[] categories) {
		this.upcNum = id;
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

	public File getImageFile() {
		return image;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isNew() {
		return isNew;
	}

	public Image getImage() {
		if (Main.getOffline()) {
			try {
				return ImageIO.read(image);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to load Image: " + e.getLocalizedMessage());
			}
		}
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

	public int getCost() {
		return cost;
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

	public long getID() {
		return upcNum;
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

	private void generateBarcode() throws ConfigurationException, BarcodeException, IOException {
		/*
		 * DefaultConfiguration defConf = new DefaultConfiguration("barcode");
		 * DefaultConfiguration child = new DefaultConfiguration("upc-a");
		 * DefaultConfiguration text = new DefaultConfiguration("human-readable");
		 * DefaultConfiguration placement = new DefaultConfiguration("placement");
		 * placement.setValue("bottom"); text.addChild(placement); child.addChild(text);
		 * defConf.addChild(child);
		 * 
		 * BarcodeGenerator gen =
		 * BarcodeUtil.getInstance().createBarcodeGenerator(defConf);
		 * BitmapCanvasProvider canvas = new BitmapCanvasProvider(1080,
		 * BufferedImage.TYPE_INT_ARGB, true, 0);
		 */
		String upcString = "" + upcNum;
		if (upcString.length() < 12) {
			for (int i = upcString.length(); i < 12; i++) {
				upcString = "0" + upcString;
			}
		}
		if (upcString.length() > 12) {
			upcString = upcString.substring(0, 12);
		}

		String priceString = "" + price;
		if (priceString.length() < 2)
			priceString = "$0.0" + priceString;
		else {
			priceString = "$" + priceString.substring(0, priceString.length() - 2) + "."
					+ priceString.substring(priceString.length() - 2);
		}

		try {
			MultiFormatWriter writer = new MultiFormatWriter();
			BitMatrix bM = writer.encode(upcString, BarcodeFormat.CODE_128, 520, 400);
			upc = MatrixToImageWriter.toBufferedImage(bM);
			Graphics2D g2 = upc.createGraphics();
			g2.setColor(Color.WHITE);
			if (MainFrame.printPrice)
				g2.fillRect(0, 0, (priceString.length() * 25) + 10, 50);
			g2.fillRect(0, upc.getHeight() - 100, 520, 100);
			g2.setFont(new Font(Font.SERIF, Font.BOLD, 85));
			g2.setColor(Color.BLACK);
			g2.drawString(upcString, 5, upc.getHeight() - 20);
			g2.setFont(new Font(Font.SERIF, Font.BOLD, 50));
			if (MainFrame.printPrice)
				g2.drawString(priceString, 10, 40);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getBarcode() {
		return upc;
	}

	public void receiveGlobalPropertyChange(String propId, String value) {
		switch (propId) {
		case "printPrice": {
			try {
				generateBarcode();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		}

	}

}
