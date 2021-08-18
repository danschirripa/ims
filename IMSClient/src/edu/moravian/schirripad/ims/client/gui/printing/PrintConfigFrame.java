package edu.moravian.schirripad.ims.client.gui.printing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.gui.MainFrame;
import edu.moravian.schirripad.ims.client.gui.MainMenuBar;

public class PrintConfigFrame extends JFrame {
	private JTextField textField;
	private JFrame t = this;

	public PrintConfigFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Print Settings");
		setSize(207, 92);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel("Label Column Count: ");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		NumberFormatter formatter = new NumberFormatter(intFormat);
		formatter.setValueClass(Integer.class);
		formatter.setAllowsInvalid(false);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);

		textField = new JFormattedTextField(formatter);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);
		
		JLabel printPriceLabel = new JLabel("Print Price:");
		GridBagConstraints gbc_printPriceLabel = new GridBagConstraints();
		gbc_printPriceLabel.insets = new Insets(0, 0, 5, 0);
		gbc_printPriceLabel.anchor = GridBagConstraints.EAST;
		gbc_printPriceLabel.gridx = 1;
		gbc_printPriceLabel.gridy = 2;
		getContentPane().add(printPriceLabel, gbc_printPriceLabel);
		
		JCheckBox printPriceBox = new JCheckBox();
		printPriceBox.setSelected(true);
		GridBagConstraints gbc_printPriceBox = new GridBagConstraints();
		gbc_printPriceBox.insets = new Insets(0, 0, 5, 0);
		gbc_printPriceBox.anchor = GridBagConstraints.WEST;
		gbc_printPriceBox.gridx = 2;
		gbc_printPriceBox.gridy = 2;
		getContentPane().add(printPriceBox, gbc_printPriceBox);
		

		JButton btnNewButton = new JButton("Apply");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 9;
		getContentPane().add(btnNewButton, gbc_btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int columnCount = Integer.parseInt(textField.getText());
					MainMenuBar.pQ.setColumnCount(columnCount);
					if(printPriceBox.isSelected()) {
						MainFrame.printPrice = true;
						Main.getIMS().fireGlobalPropertyChange("printPrice", "true");
					} else {
						MainFrame.printPrice = false;
						Main.getIMS().fireGlobalPropertyChange("printPrice", "false");
					}
					t.setVisible(false);
				} catch(Exception e1) {
					JOptionPane.showMessageDialog(null, "Please enter a valid number");
				}
			}
		});
	}

}
