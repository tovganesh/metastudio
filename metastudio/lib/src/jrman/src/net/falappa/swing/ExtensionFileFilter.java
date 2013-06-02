/*
 * Created on 26-ott-2003
 */
package net.falappa.swing;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

/**
 * A <code>JFileChooser</code> file filter based on an extension string.
 * The file filter accepts directories and files ending with the extension it is
 * built with.
 * 
 * @author Alessandro Falappa
 */
public class ExtensionFileFilter extends FileFilter {
	private String ext;
	private String desc;
	private static final ResourceBundle messagesBundle=
		ResourceBundle.getBundle(
			ExtensionFileFilter.class.getPackage().getName()
				+ ".res.ExtensionFileFilter");

	/**
	 * Constructs a <code>ExtensionFileFilter</code> object.
	 * 
	 * @param ext
	 *            the extension this filter will allow to select
	 */
	public ExtensionFileFilter(String ext) {
		if (ext == null)
			throw new NullPointerException("invalid extension"); //$NON-NLS-1$
		this.ext= ext;
		desc=
			MessageFormat.format(
				messagesBundle.getString("ExtensionFileFilter.description"), //$NON-NLS-1$
				ext.toUpperCase(), ext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(ext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return desc;
	}
}
