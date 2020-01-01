package edu.moravian.schirripad.ims.client;

import java.io.PrintStream;
import java.util.Scanner;

import edu.moravian.schirripad.ims.logging.Logger;

public abstract class Ticket implements Comparable<Ticket> {
	private static int idN = 0;
	private final int id = idN++;
	protected final Logger log = new Logger(id + "");
	private boolean isDone = false;

	public final boolean performAction(Scanner sc, PrintStream out) throws TicketException {
		boolean ret = action(sc, out);
		isDone = true;
		return ret;
	}

	public abstract boolean action(Scanner sc, PrintStream out) throws TicketException;

	public final int getID() {
		return id;
	}

	protected Logger getLogger() {
		return log;
	}

	@Override
	public int compareTo(Ticket t) {
		if (t.id < id)
			return 1;
		if (t.id > id)
			return -1;
		return 0;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setIsDone(boolean isDone) {
		this.isDone = isDone;
	}

}
