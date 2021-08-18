package edu.moravian.schirripad.ims.client.gui.printing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import edu.moravian.schirripad.ims.client.inventory.Listing;

public class PrintQueue implements Printable {
	private Hashtable<Listing, Integer> queue = new Hashtable<Listing, Integer>();
	private int columnCount = 5;
	private ArrayList<BufferedImage> pages = null;

	public void queueListing(Listing l) {
		if (queue.containsKey(l)) {
			queue.put(l, queue.get(l) + 1);
		} else {
			queue.put(l, 1);
		}
	}

	public void queueListings(Listing l, int quantity) {
		if (queue.containsKey(l)) {
			queue.put(l, queue.get(l) + quantity);
		} else {
			queue.put(l, quantity);
		}
	}

	public void setListingQuantity(Listing l, int quantity) {
		queue.put(l, quantity);
	}

	public void queueListings(Listing... l) {
		for (Listing ls : l) {
			queueListing(ls);
		}
	}

	public void removeFromQueue(Listing l) {
		if (queue.contains(l)) {
			queue.remove(l);
		}
	}

	public void removeFromQueue(Listing l, int quantity) {
		if (queue.contains(l)) {
			int curN = queue.get(l);
			if (curN < quantity) {
				queue.remove(l);
			} else {
				queue.put(l, curN - quantity);
			}
		}
	}

	public Hashtable<Listing, Integer> getQueue() {
		return queue;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public void preparePrint(PageFormat pf) {
		System.out.println("Prepare print");
		Set<Listing> keySet = queue.keySet();

		double imgWidth = pf.getImageableWidth() * 3;
		double imgHeight = pf.getImageableHeight() * 3;
		System.out.println("W: " + imgWidth);
		System.out.println("H: " + imgHeight);

		int bcWidth = (int) ((imgWidth - (columnCount * 5)) / columnCount);
		int bcHeight = queue.keys().nextElement().getBarcode()
				.getScaledInstance(bcWidth, -1, BufferedImage.SCALE_SMOOTH).getHeight(null);

		int maxRowCount = (int) (imgHeight / bcHeight);

		int index = 0; // Index repeats at "columnCount"
		int row = 0; // Horizontal row count

		ArrayList<BufferedImage> pages = new ArrayList<BufferedImage>();
		BufferedImage page = new BufferedImage((int) imgWidth, (int) imgHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) page.getGraphics();

		for (Listing l : keySet) {
			int count = queue.get(l);
			System.out.println(count);
			System.out.println(l.getBarcode().getWidth() + " : " + l.getBarcode().getHeight());
			for (int i = 0; i < count; i++) {
				if (index == columnCount) {
					index = 0;
					row++;
				}
				if (row == maxRowCount) {
					pages.add(page);
					page = new BufferedImage((int) imgWidth, (int) imgHeight, BufferedImage.TYPE_INT_ARGB);
					g = (Graphics2D) page.getGraphics();
					row = 0;
					index = 0;
				}
				g.drawImage(l.getBarcode(), (index * bcWidth) + (index * 5) + 30, (row * bcHeight) + (row * 5) + 35,
						bcWidth - 15, bcHeight - 15, null);
				index++;
			}
		}
		pages.add(page);
		this.pages = pages;
		//System.out.println(pages.size());
	}

	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		if (pages == null)
			preparePrint(pf);
		if (pageIndex > pages.size()) {
			pages = null;
			return NO_SUCH_PAGE;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(pf.getImageableWidth() / pages.get(pageIndex).getWidth(),
				pf.getImageableHeight() / pages.get(pageIndex).getHeight());
		//System.out.println("Page: " + pageIndex);
		g2.drawImage(pages.get(pageIndex), (int) pf.getImageableX(), (int) pf.getImageableY(), null);
		return PAGE_EXISTS;
	}

	public void resetPrintCanvas() {
		pages = null;
	}

}
