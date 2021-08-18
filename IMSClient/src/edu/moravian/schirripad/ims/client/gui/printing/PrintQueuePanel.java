package edu.moravian.schirripad.ims.client.gui.printing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.NumberFormatter;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.gui.MainMenuBar;
import edu.moravian.schirripad.ims.client.inventory.Listing;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class PrintQueuePanel extends JPanel {
	private JTextField skuField;
	private JTextField quantityField;

	//private PrintQueue p;

	public PrintQueuePanel(PrintQueue p) {
		//this.p = p;
		setLayout(null);

		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter longFormatter = new NumberFormatter(format);
		longFormatter.setValueClass(Long.class);
		longFormatter.setAllowsInvalid(false);
		longFormatter.setMinimum(0l);
		longFormatter.setMaximum(Long.MAX_VALUE);

		skuField = new JFormattedTextField(longFormatter);
		skuField.setBounds(40, 249, 130, 26);
		add(skuField);
		skuField.setColumns(10);

		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		NumberFormatter formatter = new NumberFormatter(intFormat);
		formatter.setValueClass(Integer.class);
		formatter.setAllowsInvalid(false);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);

		quantityField = new JFormattedTextField(formatter);
		quantityField.setBounds(255, 249, 40, 26);
		add(quantityField);
		quantityField.setColumns(10);

		JButton btnNewButton = new JButton("Add");
		btnNewButton.setBounds(307, 249, 117, 29);
		add(btnNewButton);

		JLabel lblNewLabel = new JLabel("SKU:");
		lblNewLabel.setBounds(6, 254, 28, 16);
		add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Quantity:");
		lblNewLabel_1.setBounds(186, 254, 61, 16);
		add(lblNewLabel_1);
		
		JPanel tablePanel = new JPanel();
		tablePanel.setBounds(0, 224, 453, -224);

		PrintQueueTableModel model = new PrintQueueTableModel();
		tablePanel.setLayout(null);
		
		//JScrollPane scrollPane = new JScrollPane(printQueue);
		//scrollPane.setBounds(0, 225, 450, -225);
		//add(scrollPane);
		add(tablePanel);
		JTable printQueue = new JTable(model);
		printQueue.setBounds(8, 12, 436, 225);
		add(printQueue);
		printQueue.setCellSelectionEnabled(true);
		printQueue.setBorder(new LineBorder(new Color(0, 0, 0)));
		printQueue.setShowGrid(true);
		printQueue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add queue action
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String skuString = skuField.getText();
				String quantityString = quantityField.getText();

				try {
					long sku = Long.parseLong(skuString);
					int quantity = Integer.parseInt(quantityString);
					Listing l = Main.getIMS().getListing(sku);
					if (l == null) {
						JOptionPane.showMessageDialog(null, "Item not found!");
						return;
					}
					model.queueListings(l, quantity);
					model.fireTableChange();
					printQueue.setModel(model);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Invalid syntax");
					e1.printStackTrace();
				}
			}
		});
	}

	private class PrintQueueTableModel implements TableModel {
		HashSet<TableModelListener> listeners = new HashSet<TableModelListener>();

		@Override
		public int getRowCount() {
			return MainMenuBar.pQ.getQueue().keySet().size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		public void queueListing(Listing l) {
			MainMenuBar.pQ.queueListing(l);
			fireTableChange();
		}

		public void queueListings(Listing l, int q) {
			MainMenuBar.pQ.queueListings(l, q);
			fireTableChange();
		}

		public void queueListings(Listing... l) {
			MainMenuBar.pQ.queueListings(l);
			fireTableChange();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Listing l = getListingAt(rowIndex);
			if (columnIndex == 0)
				return l.getID();
			return MainMenuBar.pQ.getQueue().get(l);
		}

		private Listing getListingAt(int index) {
			Hashtable<Listing, Integer> q = MainMenuBar.pQ.getQueue();
			Enumeration<Listing> keys = q.keys();
			Listing l = null;
			for (int i = 0; i < (index + 1); i++) {
				l = keys.nextElement();
			}
			return l;
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) {
				return "Item";
			}
			return "Quantity";
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0)
				return Long.class;
			return Integer.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 1)
				return true;
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 1) {
				try {
					int quantity = (int) aValue;
					Listing l = getListingAt(rowIndex);
					MainMenuBar.pQ.setListingQuantity(l, quantity);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			fireTableChange();

		}

		private void fireTableChange() {
			for (TableModelListener l : listeners)
				l.tableChanged(new TableModelEvent(this, TableModelEvent.ALL_COLUMNS));
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
	// TODO KEEP IMPLEMENTING TABLEMODEL, ADD TABLE TO GUI
}
