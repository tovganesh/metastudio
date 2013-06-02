/*
 * ProgressObservable.java Copyright (C) 2004 Alessandro Falappa
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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents an object making some lenghty computation and notifying
 * some <code>ProgressObserver</code> objects of its progress.
 * This class manages a list of <code>ProgressObserver</code> objects
 * allowing to add and remove observers and provides utility methods to
 * notify those objects of progress events.
 * @see org.jrman.ui.ProgressObserver
 * 
 * @author Alessandro Falappa
 * @version $Revision: 1.1 $
 */
public class ProgressObservable {
	protected ArrayList<ProgressObserver> observers= new ArrayList<ProgressObserver>(4);

	public void addProgressObserver(ProgressObserver observer) {
		if (observer == null)
			throw new NullPointerException("Can't add a null ProgressObserver");
		observers.add(observer);
	}

	public void removeProgressObserver(ProgressObserver observer) {
		if (observer == null)
			throw new NullPointerException("Can't remove a null ProgressObserver");
		observers.remove(observer);
	}

	protected void fireProgressStart(int startProgressValue, int endProgressValue) {
		Iterator i= observers.iterator();
		while (i.hasNext())
			 ((ProgressObserver)i.next()).start(startProgressValue, endProgressValue);
	}

	protected void fireProgress(int progressValue,String message) {
		Iterator i= observers.iterator();
		while (i.hasNext())
			 ((ProgressObserver)i.next()).setProgress(progressValue,message);
	}

	protected void fireProgressEnd() {
		Iterator i= observers.iterator();
		while (i.hasNext())
			 ((ProgressObserver)i.next()).end();
	}
}
