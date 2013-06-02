/*
 * TextAreaPrintStream.java Copyright (C) 2004 Alessandro Falappa
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.jrman.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 * A <code>PrintStream</code> that prints appending text into a <code>JTextArea</code>.
 * 
 * @author Alessandro Falappa
 * @version $Revision: 1.1 $
 */
public class TextAreaPrintStream extends PrintStream{
	private JTextArea area;
	
	private static class NullOutputStream extends OutputStream{
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			// do nothing
		}
	}

	/**
	 * Constructs and initializes a TextAreaPrintStream object.
	 * @param textArea the <code>JTextArea</code> object this stream will append text to 
	 */
	public TextAreaPrintStream(JTextArea textArea) {
		super(new NullOutputStream());
		if(textArea==null)
		throw new NullPointerException("textArea can't be null!");
		this.area=textArea;
	}
	/* (non-Javadoc)
	 * @see java.io.PrintStream#close()
	 */
	public void close(){
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#flush()
	 */
	public void flush(){
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(boolean)
	 */
	public void print(boolean b) {
		area.append(String.valueOf(b));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(char)
	 */
	public void print(char c) {
		area.append(String.valueOf(c));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(char[])
	 */
	public void print(char[] s) {
		area.append(String.valueOf(s));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(double)
	 */
	public void print(double d) {
		area.append(String.valueOf(d));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(float)
	 */
	public void print(float f) {
		area.append(String.valueOf(f));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(int)
	 */
	public void print(int i) {
		area.append(String.valueOf(i));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(java.lang.Object)
	 */
	public void print(Object obj) {
		area.append(String.valueOf(obj));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(java.lang.String)
	 */
	public void print(String s) {
		area.append(s);
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#print(long)
	 */
	public void print(long l) {
		area.append(String.valueOf(l));
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println()
	 */
	public void println() {
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(boolean)
	 */
	public void println(boolean x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(char)
	 */
	public void println(char x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(char[])
	 */
	public void println(char[] x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(double)
	 */
	public void println(double x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(float)
	 */
	public void println(float x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(int)
	 */
	public void println(int x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(java.lang.Object)
	 */
	public void println(Object x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(java.lang.String)
	 */
	public void println(String x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

	/* (non-Javadoc)
	 * @see java.io.PrintStream#println(long)
	 */
	public void println(long x) {
		area.append(String.valueOf(x));
		area.append("\n");
	}

}
