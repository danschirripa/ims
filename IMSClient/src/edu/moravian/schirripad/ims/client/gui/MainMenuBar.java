package edu.moravian.schirripad.ims.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.moravian.schirripad.ims.client.Main;
import edu.moravian.schirripad.ims.client.gui.MainFrame.NewListingListener;
import edu.moravian.schirripad.ims.client.gui.printing.PrintConfigFrame;
import edu.moravian.schirripad.ims.client.gui.printing.PrintQueue;
import edu.moravian.schirripad.ims.client.gui.printing.PrintQueuePanel;

public class MainMenuBar extends JMenuBar {
	public static PrintQueue pQ = new PrintQueue();

	public MainMenuBar(MainFrame mf) {
		JMenu file = new JMenu("File");
		JMenuItem connect = new JMenuItem("Connect");

		JMenu listings = new JMenu("Listings");
		JMenuItem createListing = new JMenuItem("New");
		createListing.addActionListener(mf.getNewListingListener());
		JMenuItem deleteListing = new JMenuItem("Remove");
		JMenuItem editListing = new JMenuItem("Edit");
		JMenuItem searchListings = new JMenuItem("Search");

		JMenu printing = new JMenu("Printing");
		JMenuItem config = new JMenuItem("Configure");
		PrintConfigFrame confFrame = new PrintConfigFrame();
		confFrame.setSize(200, 75);
		config.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confFrame.setVisible(true);
			}
		});
		JMenuItem print = new JMenuItem("Print");
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrinterJob job = PrinterJob.getPrinterJob();
				PageFormat pf = job.pageDialog(job.defaultPage());
				HashPrintRequestAttributeSet atrrSet = new HashPrintRequestAttributeSet();
				PrinterResolution pr = new PrinterResolution(300, 300, ResolutionSyntax.DPI);
				atrrSet.add(pr);
				boolean dialog = job.printDialog();
				if (!dialog)
					return;
				job.setPrintable(pQ, pf);
				try {
					job.print(atrrSet);
					pQ.resetPrintCanvas();
				} catch (PrinterException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Failed to send to printer! Please check printer and try again.");
				}
			}
		});
		JMenuItem printQueue = new JMenuItem("Queue");
		printQueue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame pqFrame = new JFrame("Print Queue");
				pqFrame.setContentPane(new PrintQueuePanel(pQ));
				pqFrame.setSize(450, 300);
				pqFrame.setVisible(true);
			}
		});

		file.add(connect);
		listings.add(createListing);
		listings.add(deleteListing);
		listings.add(editListing);
		listings.add(searchListings);
		file.add(listings);
		printing.add(config);
		printing.add(printQueue);
		printing.add(print);

		this.add(file);
		this.add(printing);

	}

}
