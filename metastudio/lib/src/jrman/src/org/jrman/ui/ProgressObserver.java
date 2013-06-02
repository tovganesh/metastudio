/*
 * ProgressObserver.java Copyright (C) 2004 Alessandro Falappa
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

/**
 * Identifies an object interested in observing the progress of the
 * computation made by another object.
 * 
 * @author Alessandro Falappa
 * @version $Revision: 1.1 $
 */
public interface ProgressObserver {
	/**
	 * Called when computation begins
	 * @param startProgressValue the initial progress value
	 * @param endProgressValue the last progress value
	 */
	void start(int startProgressValue, int endProgressValue);
	/**
	 * Called periodically during the computation to indicate
	 * the computation progress.
	 * @param value the amount of progress, should go from 
	 * <code>startProgressValue</code> inclusive to <code>endProgressValue</code>
	 * inclusive
	 * @param message a descriptive message about the current step of the
	 * computation, can be <code>null</code>
	 * @see #start(int, int)
	 */
	void setProgress(int value,String message);
	/**
	 * Called when computation ends
	 */
	void end();
}
