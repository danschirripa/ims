package edu.moravian.schirripad.ims.client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Scanner;

import javax.swing.JOptionPane;

import edu.moravian.schirripad.ims.logging.Logger;

public class ConnectionHandler {
	private Socket s;
	private Hashtable<Boolean, PriorityQueue<Ticket>> tix = new Hashtable<Boolean, PriorityQueue<Ticket>>();
	private Logger log = new Logger("CH");

	public ConnectionHandler(Socket s) {
		this.s = s;
		Thread conHandler = new Thread(new Handler());
		tix.put(true, new PriorityQueue<Ticket>());
		tix.put(false, new PriorityQueue<Ticket>());
		conHandler.start();
	}

	public void addTicket(Ticket t) {
		tix.get(false).add(t);
	}

	public boolean isTicketComplete(Ticket t) throws TicketException {
		if (tix.get(false).contains(t))
			return false;
		if (tix.get(true).contains(t))
			return true;
		else
			throw new TicketException("Ticket not found!");
	}

	private class Handler implements Runnable {

		@Override
		public void run() {
			Scanner sc = null;
			PrintStream out = null;

			// Create event loop, send requests based on triggers
			while (true) {
				if (tix.get(false).isEmpty()) {
					try {
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(-1);
					}
				} else {
					Ticket t = tix.get(false).poll();
					boolean done = false;
					try {
						try {
							sc = new Scanner(s.getInputStream());
							out = new PrintStream(s.getOutputStream());
						} catch (IOException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, "Failed to connect to server: " + e.getMessage());
							System.exit(-1);
						}
						done = t.performAction(sc, out);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!done) {
						JOptionPane.showMessageDialog(null, "Ticket " + t.getID() + " failed!");
					}
					tix.get(true).add(t);
				}
			}
		}

	}

}
