package edu.moravian.schirripad.ims.client.inventory;

import java.io.IOException;

public class ListingException extends IOException {

	public ListingException(String message) {
		super(message);
	}

	public ListingException(Exception e) {
		super(e);
	}

}
