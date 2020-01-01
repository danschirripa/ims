package edu.moravian.schirripad.ims.client.gui.files;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ImageFileFilter extends FileFilter {

	private final String[] ext = { ".jpg", ".png" };

	@Override
	public boolean accept(File f) {
		for (String ext : this.ext)
			if (f.getName().endsWith(ext))
				return true;
		return false;
	}

	@Override
	public String getDescription() {
		return null;
	}

}
