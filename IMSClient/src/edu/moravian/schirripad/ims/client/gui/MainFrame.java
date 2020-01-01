package edu.moravian.schirripad.ims.client.gui;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.moravian.schirripad.ims.client.ConnectionHandler;
import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.Ticket;
import edu.moravian.schirripad.ims.client.TicketException;
import edu.moravian.schirripad.ims.client.inventory.Listing;
import edu.moravian.schirripad.ims.client.inventory.ListingException;

public class MainFrame extends JFrame {
	public static ConnectionHandler ch;
	private JTextField txtSearch;
	private JPanel displayTab, editTab;
	private JList<Listing> list;
	private MainFrame t;
	private JTabbedPane listingPanel, displayPanel;
	private JFrame prog;
	private JLabel progUpdate;

	private DefaultListModel<Listing> listModel;

	public MainFrame() {
		t = this;
		setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());

		prog = new JFrame("Launching IMS");
		JProgressBar progBar = new JProgressBar();
		progBar.setIndeterminate(true);
		progBar.setVisible(true);

		progUpdate = new JLabel("Launching IMS...");
		prog.getContentPane().add(progBar, BorderLayout.SOUTH);
		prog.getContentPane().add(progUpdate, BorderLayout.NORTH);
		prog.setSize(200, 100);
		prog.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
		prog.setVisible(true);

		progUpdate.setText("Attempting Server connection...");
		try {
			String ip = Main.getConfValue("serverip");
			int port = Integer.parseInt(Main.getConfValue("serverport"));
			Socket s = new Socket();
			SocketAddress addr = new InetSocketAddress(ip, port);
			s.connect(addr, 2000);
			progUpdate.setText("Getting listings from server...");
			ch = new ConnectionHandler(s);
			Ticket invUpdate = new Ticket() {
				@Override
				public boolean action(Scanner sc, PrintStream out) throws TicketException {
					Main.setOffline(false);
					log.log("Retrieving listings...");
					out.println("list");
					String stringN = sc.nextLine();
					int n = 0;
					try {
						n = Integer.parseInt(stringN);
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.log("Server says there is " + n + " listings...");
					String[] listings = new String[n];
					for (int i = 0; i < n; i++) {
						listings[i] = sc.nextLine();
					}
					log.log("Got listing names: " + listings.length);

					// Create all Listings in IMS
					if (n > 0)
						for (String s : listings) {
							out.println("get");
							sc.nextLine();
							out.println(s);
							if (sc.hasNextLine() && sc.nextLine().equals("BAD:LISTINGNAME")) {
								JOptionPane.showMessageDialog(null,
										"Failed to get listing \"" + s + "\" due to bad name");
								continue;
							}
							out.println("all");
							String listing = sc.nextLine();
							log.log(listing);
							String[] parts = listing.split(";");

							String listingName = parts[0];
							boolean hasImage = Boolean.parseBoolean(parts[1]);
							File image = new File(parts[2]);
							String unparsedCats = parts[3];
							boolean isSold = Boolean.parseBoolean(parts[4]);
							int quantity = Integer.parseInt(parts[5]);
							int price = Integer.parseInt(parts[6]);
							String description = parts[7];
							int id = Integer.parseInt(parts[8]);

							String[] categories = unparsedCats.split(",");

							try {
								Main.getIMS().createListing(id, listingName, description, quantity, price, hasImage,
										isSold, image, categories);
							} catch (ListingException e) {
								JOptionPane.showMessageDialog(null, "Failed to create listing: " + e.getMessage());
								e.printStackTrace();
								log.log(e.getMessage());
							}
						}

					log.log("Done getting listings");
					return true;
					// Done getting listings
				}
			};

			ch.addTicket(invUpdate);
			while (!invUpdate.isDone()) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			progUpdate.setText("Starting in offline mode...");
			Main.setOffline(true);
		}

		progUpdate.setText("Constructing GUI...");
		setTitle("IMS");
		JPanel pane = new JPanel();
		getContentPane().add(pane);
		pane.setLayout(null);

		txtSearch = new JTextField();
		txtSearch.setBounds(120, 8, 114, 19);
		txtSearch.setText("Search...");
		pane.add(txtSearch);
		txtSearch.setColumns(10);

		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String search = txtSearch.getText();
				Listing l = Main.getIMS().getListing(search);
				if (l == null) {
					try {
						int id = Integer.parseInt(search);
						l = Main.getIMS().getListing(id);
					} catch (Exception e2) {
					}
					if (l == null) {
						JOptionPane.showMessageDialog(t, "No Search Results, please check capitalization!");
						return;
					}
				}
				list.setSelectedValue(l, true);
			}
		});
		btnSearch.setBounds(239, 5, 114, 25);
		btnSearch.setToolTipText("Search");
		pane.add(btnSearch);

		listingPanel = new JTabbedPane();
		listingPanel.setBounds(12, 39, (getWidth() / 2) - 20, getHeight() - 75);
		pane.add(listingPanel);

		JPanel panel = new JPanel();
		listingPanel.addTab("Listings", null, panel, null);
		panel.setLayout(null);

		JLabel lblSortBy = new JLabel("Sort By:");
		lblSortBy.setBounds(12, 12, 70, 15);
		panel.add(lblSortBy);

		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerListModel(new String[] { "Name", "Price", "Category" }));
		spinner.setBounds(71, 10, 81, 20);
		spinner.addChangeListener(new SpinnerListener());
		((DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
		panel.add(spinner);

		JPanel allListingPanel = new JPanel();
		allListingPanel.setBounds(12, 33, 175, 144);
		panel.add(allListingPanel);

		listModel = new DefaultListModel<Listing>();
		// Populate listings
		List<Listing> listings = new ArrayList<Listing>(Main.getIMS().getListings());
		// Complete default ordering
		Collections.sort(listings, new SortByName());
		for (Listing l : Main.getIMS().getListings())
			listModel.addElement(l);

		list = new JList<Listing>(listModel);
		list.addListSelectionListener(new ListChangeListener());
		JScrollPane listingScroller = new JScrollPane(list);
		allListingPanel.add(listingScroller);

		JButton btnNewListing = new JButton("New Listing");
		btnNewListing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Create new Listing
				int id;
				while (true)
					try {
						id = Integer.parseInt(JOptionPane.showInputDialog("Listing ID:"));
						break;
					} catch (Exception e1) {
						continue;
					}
				try {
					Main.getIMS().createListing(id, " ", " ", 0, 0, false, false, null, new String[1]);
				} catch (ListingException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failed to create new Listing: " + e1.getMessage());
				}
			}
		});
		btnNewListing.setBounds(12, 248, 117, 25);
		panel.add(btnNewListing);

		displayPanel = new JTabbedPane();
		displayPanel.setBounds(227, 39, (getWidth() / 2) - 10, getHeight() - 75);
		pane.add(displayPanel);

		displayTab = new JPanel();
		displayPanel.addTab("Listing", null, displayTab, "Listing Information");

		editTab = new JPanel();
		displayPanel.addTab("Edit", null, editTab, "Editing Listing");

		prog.setVisible(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponentListener(new MainFrameResizeListener());
		// TODO work on implementing GUI
	}

	private class SortByName implements Comparator<Listing> {

		@Override
		public int compare(Listing o1, Listing o2) {
			char[] n1 = o1.getListingName().toCharArray();
			char[] n2 = o2.getListingName().toCharArray();
			int n = 0;
			if (n1.length < n2.length)
				n = n1.length;
			else
				n = n2.length;

			for (int i = 0; i < n; i++) {
				System.out.println(n1[i] + ":" + n2[i]);
				if (n1[i] == n2[i])
					continue;
				if (n1[i] > n2[i])
					return -1;
				return 1;
			}

			return 0;
		}

	}

	private class SortByCategory implements Comparator<Listing> {

		@Override
		public int compare(Listing o1, Listing o2) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	private class SortByPrice implements Comparator<Listing> {

		@Override
		public int compare(Listing o1, Listing o2) {
			return o1.getPrice() - o2.getPrice();
		}

	}

	// TODO Fix listModel sorting
	private class SpinnerListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (!(e.getSource() instanceof JSpinner))
				return;
			JSpinner spinner = (JSpinner) e.getSource();
			String sortType = (String) spinner.getValue();
			listModel.clear();
			List<Listing> listings = new ArrayList<Listing>(Main.getIMS().getListings());

			if (sortType.equals("Name"))
				Collections.sort(listings, new SortByName());
			if (sortType.equals("Price"))
				Collections.sort(listings, new SortByPrice());
			if (sortType.equals("Category"))
				Collections.sort(listings, new SortByCategory());
			for (Listing l : Main.getIMS().getListings())
				listModel.addElement(l);
		}

	}

	private class ListChangeListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (list.getSelectedValue() == null)
				return;
			prog.setVisible(true);
			progUpdate.setText("Loading Listing");
			displayTab.removeAll();
			displayTab.add(new JScrollPane(new ListingPanel(list.getSelectedValue())));
			displayTab.repaint();
			editTab.removeAll();
			editTab.add(new EditPanel(list.getSelectedValue()));
			editTab.repaint();
			t.repaint();
			prog.setVisible(false);
		}

	}

	private class MainFrameResizeListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			listingPanel.setBounds(12, 39, (getWidth() / 2) - 20, getHeight() - 75);
			displayPanel.setBounds((getWidth() / 2) + 10, 39, (getWidth() / 2) - 20, getHeight() - 75);
		}
	}
}
