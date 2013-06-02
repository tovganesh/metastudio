package net.falappa.imageio;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.imageio.spi.ImageReaderSpi;
import javax.swing.filechooser.FileFilter;

/**
 * A file filter based on an <code>ImageReaderSpi</code> object.
 * The file filter accepts directories and files ending with the suffixes
 * of the <code>ImageReaderSpi</code> object it is based on.
 * The file filter description is built according to the pattern below:
 * <blockquote><code>
 * <I>&lt;FORMAT NAME&gt;</I> image files (*.<I>&lt;FILE SUFFIX&gt;</I>;*.<I>&lt;FILE SUFFIX&gt;</I>;...)
 * </code></blockquote>
 * where &lt;FORMAT NAME&gt; is the first string between those returned by
 * <code>ImageReaderSpi.getFormatNames()</code> method and the &lt;FILE SUFFIX&gt;es
 * are those returned by <code>ImageReaderSpi.getFileSuffixes()</code> method.
 * Typical use is as follows:
 * <pre>
 * JFileChooser fc=new JFileChooser();
 * IIORegistry theRegistry=IIORegistry.getDefaultInstance();
 * Iterator it=theRegistry.getServiceProviders(ImageReaderSpi.class,false);
 * while(it.hasNext()){
 *   ImageReaderSpi reader=(ImageReaderSpi)it.next();
 *   ImageReaderSpiFileFilter ff=new ImageReaderSpiFileFilter(reader);
 *   fc.addChoosableFileFilter(ff);
 * }
 * </pre>
 * The class offers utility methods to test if a string is a valid filename
 * according to the <code>ImageReaderSpi</code> object and to add such a suffix.
 * @see javax.imageio.spi.ImageReaderSpi#getFormatNames()
 * @see javax.imageio.spi.ImageReaderSpi#getFileSuffixes()
 * @author Alessandro Falappa
 */
public class ImageReaderSpiFileFilter extends FileFilter {
  private String description;
  private String[] suffixes;
  private ImageReaderSpi reader;
  private static final ResourceBundle messagesBundle=
	  ResourceBundle.getBundle(
		  ImageViewerPanelSaveAction.class.getPackage().getName()
			  + ".res.ImageSpiFileFilters");

  /**
   * Create a file filter based on an image reader.
   * @param reader the ImageReaderSpi object the filter is based on
   */
  public ImageReaderSpiFileFilter(ImageReaderSpi reader){
    this.reader=reader;
    StringBuffer sb=new StringBuffer();
	String template= messagesBundle.getString("ImageSpiFileFilter.image_files"); //$NON-NLS-1$
	sb.append(
		MessageFormat.format(
			template, reader.getFormatNames()[0].toUpperCase()));
    suffixes=reader.getFileSuffixes();
    sb.append(" (*.").append(suffixes[0]); //$NON-NLS-1$
    for (int i = 1; i < suffixes.length; i++)
      sb.append(";*.").append(suffixes[i]); //$NON-NLS-1$
    sb.append(')');
    description=sb.toString();
  }
  // implements the method of the abstract base class
  public boolean accept(File f) {
    return f.isDirectory()||hasCorrectSuffix(f.getName());
  }
  // implements the method of the abstract base class
  public String getDescription() {
    return description;
  }
  /**
   * Tests if a string ends with one of the suffixes accepted by the image reader.
   * @param name the string to test
   * @return true if the string passes the test
   */
  public boolean hasCorrectSuffix(String name){
    boolean accepted=false;
    for (int i = 0; !accepted && i < suffixes.length; i++)
      accepted=accepted||name.endsWith(suffixes[i]);
    return accepted;
  }
  /**
   * Appends a suffix accepted by the image reader to the file name passed as argument.
   * @param fileName the file name to modify
   * @return a new file name
   */
  public String addSuffix(String fileName){
    return fileName+"."+suffixes[0]; //$NON-NLS-1$
  }
  /**
   * Returns the imagewriter corresponding to the filter
   * @return a ImageReaderSpi object
   */
  public ImageReaderSpi getImageReaderSpi(){
    return reader;
  }
}
