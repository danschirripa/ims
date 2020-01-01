package edu.moravian.schirripad.ims.logging;

import java.time.LocalDateTime;

public class Logger {
	private String name;

	public Logger(String name) {
		this.name = name;
	}

	public void log(String log) {
		print(log + "\n");
	}

	public void print(String log) {
		LocalDateTime now = LocalDateTime.now();
		System.out.print("[" + name + ":" + now.getHour() + "." + now.getMinute() + "." + now.getSecond() + "] " + log);
	}

	public void printErr(String err) {
		LocalDateTime now = LocalDateTime.now();
		System.err.print("[" + name + ":" + now.getHour() + "." + now.getMinute() + "." + now.getSecond() + "] " + err);
	}

	public void err(String err) {
		printErr(err + "\n");
	}
}
