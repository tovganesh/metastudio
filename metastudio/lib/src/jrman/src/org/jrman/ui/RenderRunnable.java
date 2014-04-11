/*
 * RenderRunnable.java Copyright (C) 2004 Alessandro Falappa
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

import java.io.File;

import javax.swing.DefaultListModel;

import org.jrman.main.JRMan;

class RenderRunnable extends ProgressObservable implements Runnable {
	private DefaultListModel queue;
	private String optionsString;
	private boolean canceled;

	// (non-Javadoc)
	// @see java.lang.Runnable#run()
	//
	public void run() {
		canceled= false;
		if (queue != null && optionsString != null) {
			// begin rendering of queue
			fireProgressStart(0, queue.size());
			StringBuffer sb= new StringBuffer(optionsString.length() + 20);
			for (int i= 0; i < queue.size(); i++) {
				// prepare command line arguments
				String fileName= ((File)queue.elementAt(i)).getAbsolutePath();
				sb.setLength(0);
				sb.append(optionsString).append(' ').append(fileName);
				// launch renderer passing the arguments
				String[] tmp=sb.toString().trim().split("\\s+");
				JRMan.main(tmp);
				// signal file rendering completion and check for cancellation
				fireProgress(i + 1, fileName);
				if (canceled)
					break;
			}
			fireProgressEnd();
		}
	}

	/**
	 * @param b
	 */
	public void setCanceled(boolean b) {
		canceled= b;
	}

	/**
	 * @param model
	 */
	public void setQueue(DefaultListModel model) {
		queue= model;
	}

	/**
	 * @param string
	 */
	public void setOptionsString(String string) {
		optionsString= string;
	}

}