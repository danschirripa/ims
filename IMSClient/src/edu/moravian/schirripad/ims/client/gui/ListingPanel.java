package edu.moravian.schirripad.ims.client.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.inventory.Listing;

public class ListingPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public ListingPanel(Listing l) {
		if (l == null) {
			JOptionPane.showMessageDialog(null, "NULL LISTING");
			System.exit(-1);
		}
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 205, 0, 40, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 15, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		// TODO Deal with getting image icon, and displaying properly - Check
		// Listing.getImage()
		ImageIcon imgIcon = null;
		JLabel icon;
		if (l.hasImage()) {
			Image img = l.getImage();
			if (img == null) {
				img = Main.defImg;
			}
			double ratio = 1;
			double x = img.getWidth(null);
			double y = img.getHeight(null);
			if (x > y) {
				ratio = y / x;
				imgIcon = new ImageIcon(
						l.getImage().getScaledInstance(50, (int) (50 * ratio), Image.SCALE_AREA_AVERAGING));
			} else {
				ratio = x / y;
				imgIcon = new ImageIcon(
						l.getImage().getScaledInstance((int) (50 * ratio), 50, Image.SCALE_AREA_AVERAGING));
			}
			icon = new JLabel(imgIcon);
		} else {
			icon = new JLabel();
		}

		GridBagConstraints gbc_icon = new GridBagConstraints();
		gbc_icon.gridwidth = 3;
		gbc_icon.gridheight = 7;
		gbc_icon.gridx = 3;
		gbc_icon.gridy = 0;
		add(icon, gbc_icon);

		JLabel lblPrice = new JLabel("Price:");
		GridBagConstraints gbc_lblPrice = new GridBagConstraints();
		gbc_lblPrice.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrice.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblPrice.gridx = 0;
		gbc_lblPrice.gridy = 1;
		add(lblPrice, gbc_lblPrice);

		// Convert price int to a string
		int pr = l.getPrice();
		int cents = pr % 100;
		int dollar = pr / 100;

		JLabel price = new JLabel("$" + dollar + "." + cents);
		GridBagConstraints gbc_price = new GridBagConstraints();
		gbc_price.insets = new Insets(0, 0, 5, 5);
		gbc_price.gridx = 2;
		gbc_price.gridy = 1;
		add(price, gbc_price);

		JLabel lblQuantity = new JLabel("Quantity:");
		GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
		gbc_lblQuantity.anchor = GridBagConstraints.WEST;
		gbc_lblQuantity.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantity.gridx = 0;
		gbc_lblQuantity.gridy = 2;
		add(lblQuantity, gbc_lblQuantity);

		JLabel quantity = new JLabel(l.getQuantity() + "");
		GridBagConstraints gbc_quantity = new GridBagConstraints();
		gbc_quantity.insets = new Insets(0, 0, 5, 5);
		gbc_quantity.gridx = 2;
		gbc_quantity.gridy = 2;
		add(quantity, gbc_quantity);

		JLabel lblCategories = new JLabel("Categories:");
		GridBagConstraints gbc_lblCategories = new GridBagConstraints();
		gbc_lblCategories.anchor = GridBagConstraints.WEST;
		gbc_lblCategories.insets = new Insets(0, 0, 5, 5);
		gbc_lblCategories.gridx = 0;
		gbc_lblCategories.gridy = 3;
		add(lblCategories, gbc_lblCategories);

		// Convert categories array to string
		HashSet<String> cat = l.getCategories();
		String cats = "";
		for (String c : cat) {
			cats += c + ", ";
		}
		cats = cats.substring(2, cats.length() - 2);

		JLabel categories = new JLabel(cats);
		GridBagConstraints gbc_categories = new GridBagConstraints();
		gbc_categories.insets = new Insets(0, 0, 5, 5);
		gbc_categories.gridx = 2;
		gbc_categories.gridy = 3;
		add(categories, gbc_categories);

		JLabel lblDescription = new JLabel("Description:");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 5;
		add(lblDescription, gbc_lblDescription);

		JLabel description = new JLabel(l.getDescription());
		GridBagConstraints gbc_description = new GridBagConstraints();
		gbc_description.gridwidth = 3;
		gbc_description.insets = new Insets(0, 0, 0, 5);
		gbc_description.gridx = 0;
		gbc_description.gridy = 6;
		add(description, gbc_description);

	}

}
