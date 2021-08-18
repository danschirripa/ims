package edu.moravian.schirripad.ims.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.NumberFormatter;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.inventory.Listing;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class PointOfSaleFrame extends JPanel {
	private JTable table;
	private JTextField textFieldSKU;
	private JTextField textFieldQuantity;
	private POSTableModel tblMdl;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;

	public PointOfSaleFrame() {
		setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(6, 6, 438, 238);
		add(panel);
		panel.setLayout(null);

		
		tblMdl = new POSTableModel();
		table = new JTable(tblMdl);
		table.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBounds(6, 6, 430, 238);
		table.setShowGrid(true);
		panel.add(table);
		
		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter longFormatter = new NumberFormatter(format);
		longFormatter.setValueClass(Long.class);
		longFormatter.setAllowsInvalid(false);
		longFormatter.setMinimum(0l);
		longFormatter.setMaximum(Long.MAX_VALUE);
		
		textFieldSKU = new JFormattedTextField(longFormatter);
		textFieldSKU.setBounds(41, 259, 130, 26);
		add(textFieldSKU);
		textFieldSKU.setColumns(10);
		
		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		NumberFormatter formatter = new NumberFormatter(intFormat);
		formatter.setValueClass(Integer.class);
		formatter.setAllowsInvalid(false);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		
		textFieldQuantity = new JFormattedTextField(formatter);
		textFieldQuantity.setText("0");
		textFieldQuantity.setBounds(211, 259, 64, 26);
		add(textFieldQuantity);
		textFieldQuantity.setColumns(10);
		
		JButton btnNewButton = new JButton("Post");
		btnNewButton.setBounds(312, 256, 117, 29);
		btnNewButton.setMnemonic(KeyEvent.VK_ENTER);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String skuString = textFieldSKU.getText().replace(",", "");
				long sku = Long.parseLong(skuString);
				String quantString = textFieldQuantity.getText().replace(",", "");
				int quant = Integer.parseInt(quantString);
				Listing l = Main.getIMS().getListing(sku);
				if(l != null) {
					tblMdl.addItemListing(l, quant);
					table.setModel(tblMdl);
				} else {
					JOptionPane.showMessageDialog(null, "Item not found");
				}
			}
		});
		add(btnNewButton);
		
		lblNewLabel = new JLabel("SKU");
		lblNewLabel.setBounds(94, 244, 61, 16);
		add(lblNewLabel);
		
		lblNewLabel_1 = new JLabel("Quantity");
		lblNewLabel_1.setBounds(214, 244, 61, 16);
		add(lblNewLabel_1);

	}

	private class POSTableModel implements TableModel {
		ArrayList<Listing> items;
		Hashtable<Long, Integer> quantities;
		ArrayList<TableModelListener> listeners;

		public POSTableModel() {
			items = new ArrayList<Listing>();
			quantities = new Hashtable<Long, Integer>();
			listeners = new ArrayList<TableModelListener>();
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 4;
		}
		
		public void addItemListing(Listing l) {
			if(quantities.containsKey(l.getID())) {
				quantities.put(l.getID(), quantities.get(l.getID()) + 1);
				return;
			}
			items.add(l);
			quantities.put(l.getID(), 1);
			fireTableChange();
		}
		
		public void addItemListing(Listing l, int quantity) {
			if(quantities.containsKey(l.getID())) {
				quantities.put(l.getID(), quantities.get(l.getID()) + quantity);
				return;
			}
			items.add(l);
			quantities.put(l.getID(), quantity);
			fireTableChange();
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return "SKU";
			case 1:
				return "Name";
			case 2:
				return "Price";
			case 3:
				return "Quantity";
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Integer.class;
			case 1:
				return String.class;
			case 2:
				return Integer.class;
			case 3:
				return Integer.class;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 3)
				return true;
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Listing l = items.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return l.getID();
			case 1:
				return l.getListingName();
			case 2:
				return l.getPrice();
			case 3:
				return quantities.get(l.getID());

			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex != 3)
				return;
			int quant = (int) aValue;
			if(quant == 0) {
				quantities.remove(items.get(columnIndex).getID());
				items.remove(columnIndex);
				return;
			}
			quantities.put(items.get(columnIndex).getID(), quant);
			fireTableChange();
		}
		
		public void fireTableChange() {
			for(TableModelListener l : listeners) {
				l.tableChanged(new TableModelEvent(this, TableModelEvent.ALL_COLUMNS));
			}
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
			listeners.add(l);
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			listeners.remove(l);
		}

	}
}
