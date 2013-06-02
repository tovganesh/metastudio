package net.falappa.imageio;

import javax.swing.filechooser.*;
import javax.imageio.spi.ImageWriterSpi;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * A file filter based on an <code>ImageWriterSpi</code> object.
 * The file filter accepts directories and files ending with the suffixes
 * of the <code>ImageWriterSpi</code> object it is based on.
 * The file filter description is built according to the pattern below:
 * <blockquote><code>
 * <I>&lt;FORMAT NAME&gt;</I> image files (*.<I>&lt;FILE SUFFIX&gt;</I>;*.<I>&lt;FILE SUFFIX&gt;</I>;...)
 * </code></blockquote>
 * where &lt;FORMAT NAME&gt; is the first string between those returned by
 * <code>ImageWriterSpi.getFormatNames()</code> method and the &lt;FILE SUFFIX&gt;es
 * are those returned by <code>ImageWriterSpi.getFileSuffixes()</code> method.
 * Typical use is as follows:
 * <pre>
 * JFileChooser fc=new JFileChooser();
 * IIORegistry theRegistry=IIORegistry.getDefaultInstance();
 * Iterator it=theRegistry.getServiceProviders(ImageWriterSpi.class,false);
 * while(it.hasNext()){
 *   ImageWriterSpi writer=(ImageWriterSpi)it.next();
 *   ImageWriterSpiFileFilter ff=new ImageWriterSpiFileFilter(writer);
 *   fc.addChoosableFileFilter(ff);
 * }
 * </pre>
 * The class offers utility methods to test if a string is a valid filename
 * according to the <code>ImageWriterSpi</code> object and to add such a suffix.
 * @see javax.imageio.spi.ImageWriterSpi#getFormatNames()
 * @see javax.imageio.spi.ImageWriterSpi#getFileSuffixes()
 * @author Alessandro Falappa
 */
public class ImageWriterSpiFileFilter extends FileFilter {
	private String description;
	private String[] suffixes;
	private ImageWriterSpi writer;
	private static final ResourceBundle messagesBundle=
		ResourceBundle.getBundle(
			ImageWriterSpiFileFilter.class.getPackage().getName()
				+ ".res.ImageSpiFileFilters");

	/**
	 * Create a file filter based on an image writer.
	 * @param writer the ImageWriterSpi object the filter is based on
	 */
	public ImageWriterSpiFileFilter(ImageWriterSpi writer) {
		this.writer= writer;
		StringBuffer sb= new StringBuffer();
		String template= messagesBundle.getString("ImageSpiFileFilter.image_files"); //$NON-NLS-1$
		sb.append(
			MessageFormat.format(
				template, writer.getFormatNames()[0].toUpperCase()));
		suffixes= writer.getFileSuffixes();
		sb.append(" (*.").append(suffixes[0]); //$NON-NLS-1$
		for (int i= 1; i < suffixes.length; i++)
			sb.append(";*.").append(suffixes[i]); //$NON-NLS-1$
		sb.append(')');
		description= sb.toString();
	}
	// implements the method of the abstract base class
	public boolean accept(File f) {
		return f.isDirectory() || hasCorrectSuffix(f.getName());
	}
	// implements the method of the abstract base class
	public String getDescription() {
		return description;
	}
    
	/**
	 * Tests if a string ends with one of the suffixes accepted by the image writer.
	 * @param name the string to test
	 * @return true if the string passes the test
	 */
	public boolean hasCorrectSuffix(String name) {
		boolean accepted= false;
		for (int i= 0; !accepted && i < suffixes.length; i++)
			accepted= accepted || name.endsWith(suffixes[i]);
		return accepted;
	}
	/**
	 * Appends a suffix accepted by the image writer to the file name passed as argument.
	 * @param fileName the file name to modify
	 * @return a new file name
	 */
	public String addSuffix(String fileName) {
		return fileName + "." + suffixes[0]; //$NON-NLS-1$
	}
	/**
	 * Returns the imagewriter corresponding to the filter
	 * @return a ImageWriterSpi object
	 */
	public ImageWriterSpi getImageWriterSpi() {
		return writer;
	}
}
