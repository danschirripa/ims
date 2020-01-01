package edu.moravian.schirripad.ims.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.moravian.schirripad.ims.logging.Logger;
import edu.moravian.schirripad.ims.server.transaction.TransactionHandler;

public class Server {
	private final Logger log = new Logger("Server");

	public Server(int port) throws IOException {
		ServerSocket ss = new ServerSocket(port);
		while (!ss.isClosed()) {
			Socket s = ss.accept();
			log.log("Accepted connection from " + s.getInetAddress());
			TransactionHandler.handleSocketConnection(s);
		}
	}

}
