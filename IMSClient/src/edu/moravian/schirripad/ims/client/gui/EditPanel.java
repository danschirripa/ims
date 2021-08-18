package edu.moravian.schirripad.ims.client.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.inventory.Listing;
import edu.moravian.schirripad.ims.client.inventory.ListingException;

public class EditPanel extends JPanel {
	private EditPanel editor = this;
	private JTextField priceField;
	private JTextField quantityField;
	private JTextField catField;
	private File img = new File("");
	private JTextField costField;
	private boolean hasImg;

	/**
	 * Create the panel.
	 */
	public EditPanel(Listing l, ListingPanel lp) {
		hasImg = l.hasImage();
		img = l.getImageFile();

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblPrice = new JLabel("Price:");
		GridBagConstraints gbc_lblPrice = new GridBagConstraints();
		gbc_lblPrice.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrice.gridx = 0;
		gbc_lblPrice.gridy = 0;
		add(lblPrice, gbc_lblPrice);

		priceField = new JTextField(l.getPrice() + "");
		GridBagConstraints gbc_priceField = new GridBagConstraints();
		gbc_priceField.insets = new Insets(0, 0, 5, 0);
		gbc_priceField.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceField.gridx = 2;
		gbc_priceField.gridy = 0;
		add(priceField, gbc_priceField);
		priceField.setColumns(10);

		JLabel lblQuantity = new JLabel("Quantity:");
		GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
		gbc_lblQuantity.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuantity.gridx = 0;
		gbc_lblQuantity.gridy = 1;
		add(lblQuantity, gbc_lblQuantity);

		quantityField = new JTextField(l.getQuantity() + "");
		GridBagConstraints gbc_quantityField = new GridBagConstraints();
		gbc_quantityField.insets = new Insets(0, 0, 5, 0);
		gbc_quantityField.fill = GridBagConstraints.HORIZONTAL;
		gbc_quantityField.gridx = 2;
		gbc_quantityField.gridy = 1;
		add(quantityField, gbc_quantityField);
		quantityField.setColumns(10);

		JLabel lblCost = new JLabel("Cost:");
		GridBagConstraints gbc_lblCost = new GridBagConstraints();
		gbc_lblCost.insets = new Insets(0, 0, 5, 5);
		gbc_lblCost.gridx = 0;
		gbc_lblCost.gridy = 2;
		add(lblCost, gbc_lblCost);

		costField = new JTextField();
		costField.setText(l.getCost() + "");
		GridBagConstraints gbc_costField = new GridBagConstraints();
		gbc_costField.insets = new Insets(0, 0, 5, 0);
		gbc_costField.fill = GridBagConstraints.HORIZONTAL;
		gbc_costField.gridx = 2;
		gbc_costField.gridy = 2;
		add(costField, gbc_costField);
		costField.setColumns(10);

		JLabel lblCategories = new JLabel("Categories:");
		GridBagConstraints gbc_lblCategories = new GridBagConstraints();
		gbc_lblCategories.insets = new Insets(0, 0, 5, 5);
		gbc_lblCategories.gridx = 0;
		gbc_lblCategories.gridy = 3;
		add(lblCategories, gbc_lblCategories);

		String cats = "";
		for (String c : l.getCategories()) {
			cats += (c + ", ");
		}

		catField = new JTextField(cats);
		GridBagConstraints gbc_catField = new GridBagConstraints();
		gbc_catField.insets = new Insets(0, 0, 5, 0);
		gbc_catField.fill = GridBagConstraints.HORIZONTAL;
		gbc_catField.gridx = 2;
		gbc_catField.gridy = 3;
		add(catField, gbc_catField);
		catField.setColumns(10);

		JLabel lblDescription = new JLabel("Description:");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.anchor = GridBagConstraints.NORTH;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 4;
		add(lblDescription, gbc_lblDescription);

		JTextArea descField = new JTextArea(l.getDescription());
		GridBagConstraints gbc_descField = new GridBagConstraints();
		gbc_descField.insets = new Insets(0, 0, 5, 0);
		gbc_descField.fill = GridBagConstraints.BOTH;
		gbc_descField.gridx = 2;
		gbc_descField.gridy = 4;
		add(descField, gbc_descField);

		JButton btnSelectImage = new JButton("New Image");
		btnSelectImage.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setMultiSelectionEnabled(false);
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg"));
				int resp = fc.showOpenDialog(editor);
				if (resp == JFileChooser.APPROVE_OPTION) {
					img = fc.getSelectedFile();
					hasImg = true;
				}
			}

		});
		GridBagConstraints gbc_btnSelectImage = new GridBagConstraints();
		gbc_btnSelectImage.insets = new Insets(0, 0, 5, 5);
		gbc_btnSelectImage.gridx = 0;
		gbc_btnSelectImage.gridy = 6;
		add(btnSelectImage, gbc_btnSelectImage);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Create new/updated listing
				String catString = catField.getText();
				catString = catString.replace(" ", "");
				String[] cats = catString.split(",");
				try {
					int price, cost, quantity;
					String priceString = priceField.getText();
					String costString = costField.getText();
					String quantityString = quantityField.getText();
					try {
						price = Integer.parseInt(priceString);
						cost = Integer.parseInt(costString);
						quantity = Integer.parseInt(quantityString);
					} catch (Exception e) {
						priceString = priceString.replace("$", "").replace(".", "");
						costString = costString.replace("$", "").replace(".", "");
						quantity = Integer.parseInt(quantityString);
						try {
							price = Integer.parseInt(priceString);
							cost = Integer.parseInt(costString);
						} catch (Exception e1) {
							e.printStackTrace();
							e1.printStackTrace();
							JOptionPane.showMessageDialog(null,
									"Unable to Save: " + e.getLocalizedMessage() + ":" + e1.getLocalizedMessage());
							return;
						}
					}
					Main.getIMS().createListing(l.getID(), l.getListingName(), descField.getText(), quantity, price,
							cost, hasImg, l.isSold(), img, cats);
					lp.updateListing(Main.getIMS().getListing(l.getID()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ListingException e) {
					e.printStackTrace();
				}
			}

		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.insets = new Insets(0, 0, 0, 5);
		gbc_btnSave.gridx = 0;
		gbc_btnSave.gridy = 7;
		add(btnSave, gbc_btnSave);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null,
						"Delete \"" + l.getListingName() + "\" with SKU " + l.getID() + "?");
				if (confirm == JOptionPane.YES_OPTION) {
					if (!Main.getIMS().removeListing(l.getID())) {
						JOptionPane.showMessageDialog(null, "Deletion Failed!");
					}
					lp.updateListing(null);
				}
			}
		});
		btnDelete.setForeground(Color.RED);
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.anchor = GridBagConstraints.EAST;
		gbc_btnDelete.gridx = 2;
		gbc_btnDelete.gridy = 7;
		add(btnDelete, gbc_btnDelete);

	}

}
