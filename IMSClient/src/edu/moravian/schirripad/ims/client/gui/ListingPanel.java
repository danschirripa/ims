package edu.moravian.schirripad.ims.client.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.inventory.Listing;

public class ListingPanel extends JPanel {
	private MainFrame mf;

	/**
	 * Create the panel.
	 */
	public ListingPanel(Listing l, MainFrame mf) {
		this.mf = mf;
		processListing(l);
	}

	public void updateListing(Listing l) {
		this.removeAll();
		processListing(l);
		mf.updateList();
	}

	private void processListing(Listing l) {
		if (l == null) {
			return;
		}
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 205, 0, 40, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
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
				imgIcon = new ImageIcon(l.getImage().getScaledInstance(mf.getWidth() / 4,
						(int) ((mf.getHeight() / 2) * ratio), Image.SCALE_AREA_AVERAGING));
			} else {
				ratio = x / y;
				imgIcon = new ImageIcon(l.getImage().getScaledInstance((int) ((mf.getWidth() / 4) * ratio),
						mf.getHeight() / 2, Image.SCALE_AREA_AVERAGING));
			}
			icon = new JLabel(imgIcon);
		} else {
			icon = new JLabel();
		}

		GridBagConstraints gbc_icon = new GridBagConstraints();
		gbc_icon.gridwidth = 3;
		gbc_icon.gridheight = 11;
		gbc_icon.gridx = 3;
		gbc_icon.gridy = 0;
		add(icon, gbc_icon);

		JLabel lblSKU = new JLabel("SKU: " + l.getID());
		GridBagConstraints gbc_lblSKU = new GridBagConstraints();
		gbc_lblSKU.anchor = GridBagConstraints.WEST;
		gbc_lblSKU.insets = new Insets(0, 0, 5, 5);
		gbc_lblSKU.gridx = 0;
		gbc_lblSKU.gridy = 0;
		add(lblSKU, gbc_lblSKU);

		// Convert price int to a string
		int prPrice = l.getPrice();
		int centsPrice = prPrice % 100;
		int dollarPrice = prPrice / 100;

		JLabel lblBarcode = new JLabel(
				new ImageIcon(l.getBarcode().getScaledInstance(-1, 50, BufferedImage.SCALE_SMOOTH)));
		GridBagConstraints gbc_lblBarcode = new GridBagConstraints();
		gbc_lblBarcode.insets = new Insets(0, 0, 5, 5);
		gbc_lblBarcode.gridx = 0;
		gbc_lblBarcode.gridy = 1;
		add(lblBarcode, gbc_lblBarcode);

		JLabel lblPrice = new JLabel("Price:");
		GridBagConstraints gbc_lblPrice = new GridBagConstraints();
		gbc_lblPrice.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrice.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblPrice.gridx = 0;
		gbc_lblPrice.gridy = 2;
		add(lblPrice, gbc_lblPrice);

		JLabel price = new JLabel("$" + dollarPrice + "." + (centsPrice < 10 ? "0" + centsPrice : centsPrice));
		GridBagConstraints gbc_price = new GridBagConstraints();
		gbc_price.insets = new Insets(0, 0, 5, 5);
		gbc_price.gridx = 2;
		gbc_price.gridy = 2;
		add(price, gbc_price);

		// Convert categories array to string
		HashSet<String> cat = l.getCategories();
		String cats = "";
		for (String c : cat) {
			cats += c + ", ";
		}
		cats = cats.substring(2, cats.length() - 2);

		int prCost = l.getCost();
		int centsCost = prCost % 100;
		int dollarCost = prCost / 100;
		JLabel lblCost = new JLabel("Cost:");
		lblCost.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblCost = new GridBagConstraints();
		gbc_lblCost.anchor = GridBagConstraints.WEST;
		gbc_lblCost.insets = new Insets(0, 0, 5, 5);
		gbc_lblCost.gridx = 0;
		gbc_lblCost.gridy = 3;
		add(lblCost, gbc_lblCost);

		JLabel cost = new JLabel("$" + dollarCost + "." + (centsCost < 10 ? "0" + centsCost : centsCost));
		GridBagConstraints gbc_cost = new GridBagConstraints();
		gbc_cost.insets = new Insets(0, 0, 5, 5);
		gbc_cost.gridx = 2;
		gbc_cost.gridy = 3;
		add(cost, gbc_cost);

		JLabel lblSuggestedCost = new JLabel("Suggested Cost:");
		// TODO Generate suggested cost based on markup setting
		GridBagConstraints gbc_lblSuggestedCost = new GridBagConstraints();
		gbc_lblSuggestedCost.anchor = GridBagConstraints.WEST;
		gbc_lblSuggestedCost.insets = new Insets(0, 0, 5, 5);
		gbc_lblSuggestedCost.gridx = 0;
		gbc_lblSuggestedCost.gridy = 4;
		add(lblSuggestedCost, gbc_lblSuggestedCost);

		JLabel suggestedCost = new JLabel("$0.0");
		GridBagConstraints gbc_suggestedCost = new GridBagConstraints();
		gbc_suggestedCost.insets = new Insets(0, 0, 5, 5);
		gbc_suggestedCost.gridx = 2;
		gbc_suggestedCost.gridy = 4;
		add(suggestedCost, gbc_suggestedCost);

		JLabel lblQuantity = new JLabel("Quantity:");
		GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
		gbc_lblQuantity.anchor = GridBagConstraints.WEST;
		gbc_lblQuantity.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantity.gridx = 0;
		gbc_lblQuantity.gridy = 5;
		add(lblQuantity, gbc_lblQuantity);

		JLabel quantity = new JLabel(l.getQuantity() + "");
		GridBagConstraints gbc_quantity = new GridBagConstraints();
		gbc_quantity.insets = new Insets(0, 0, 5, 5);
		gbc_quantity.gridx = 2;
		gbc_quantity.gridy = 5;
		add(quantity, gbc_quantity);

		JLabel lblCategories = new JLabel("Categories:");
		GridBagConstraints gbc_lblCategories = new GridBagConstraints();
		gbc_lblCategories.anchor = GridBagConstraints.WEST;
		gbc_lblCategories.insets = new Insets(0, 0, 5, 5);
		gbc_lblCategories.gridx = 0;
		gbc_lblCategories.gridy = 6;
		add(lblCategories, gbc_lblCategories);

		JLabel categories = new JLabel(cats);
		GridBagConstraints gbc_categories = new GridBagConstraints();
		gbc_categories.insets = new Insets(0, 0, 5, 5);
		gbc_categories.gridx = 2;
		gbc_categories.gridy = 6;
		add(categories, gbc_categories);

		JLabel lblDescription = new JLabel("Description:");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 7;
		add(lblDescription, gbc_lblDescription);

		JLabel description = new JLabel(l.getDescription());
		GridBagConstraints gbc_description = new GridBagConstraints();
		gbc_description.gridwidth = 3;
		gbc_description.insets = new Insets(0, 0, 5, 5);
		gbc_description.gridx = 0;
		gbc_description.gridy = 8;
		add(description, gbc_description);

		JButton btnPntLbl = new JButton("Print Label");
		GridBagConstraints gbc_btnPntLbl = new GridBagConstraints();
		gbc_btnPntLbl.insets = new Insets(0, 0, 5, 5);
		gbc_btnPntLbl.gridx = 0;
		gbc_btnPntLbl.gridy = 10;
		add(btnPntLbl, gbc_btnPntLbl);
		btnPntLbl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				while (true)
					try {
						int count = Integer.parseInt(JOptionPane.showInputDialog("Count:"));
						MainMenuBar.pQ.queueListings(l, count);
						break;
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Please enter a number");
					}
			}
		});

		/*
		 * if (l.hasImage()) { System.out.println("Image Found"); JLabel lblImageTitle =
		 * new JLabel("Image"); GridBagConstraints gbc_lblImageTitle = new
		 * GridBagConstraints(); gbc_lblImageTitle.anchor = GridBagConstraints.EAST;
		 * gbc_lblImageTitle.insets = new Insets(0, 0, 5, 5); gbc_lblImageTitle.gridx =
		 * 0; gbc_lblImageTitle.gridy = 9; add(lblImageTitle, gbc_lblImageTitle);
		 * 
		 * JLabel lblImage = new JLabel(new ImageIcon(l.getImage())); GridBagConstraints
		 * gbc_lblImage = new GridBagConstraints(); gbc_lblImage.insets = new Insets(0,
		 * 0, 5, 5); gbc_lblImage.gridx = 2; gbc_lblImageTitle.gridy = 9; add(lblImage,
		 * gbc_lblImage); }
		 */
	}

}
