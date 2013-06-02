/* =================================================
 * JWorkbook : data export from Java to spreadsheets
 * =================================================
 *
 * Project Info:  http://www.jfree.org/jworkbook/index.html;
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * --------------------
 * SpreadsheetDate.java
 * --------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SpreadsheetDate.java,v 1.2 2003/06/05 08:07:07 mungady Exp $
 *
 * Changes
 * -------
 * 08-Jul-2003 : Version 1 copied from JCommon (DG);
 *
 */

package org.jfree.workbook.date;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a date using an integer, in a similar fashion to the
 * implementation in Microsoft Excel.  The range of dates supported is
 * 1-Jan-1900 to 31-Dec-9999.
 * <P>
 * Be aware that there is a deliberate bug in Excel that recognises the year
 * 1900 as a leap year when in fact it is not a leap year. You can find more
 * information on the Microsoft website in article Q181370:
 * <P>
 * http://support.microsoft.com/support/kb/articles/Q181/3/70.asp
 * <P>
 * Excel uses the convention that 1-Jan-1900 = 1.  This class uses the
 * convention 1-Jan-1900 = 2.
 * The result is that the day number in this class will be different to the
 * Excel figure for January and February 1900...but then Excel adds in an extra
 * day (29-Feb-1900 which does not actually exist!) and from that point forward
 * the day numbers will match.
 *
 * @author David Gilbert
 */

public class SpreadsheetDate extends SerialDate implements Serializable {

    /** The day number (1-Jan-1900 = 2, 2-Jan-1900 = 3, ..., 31-Dec-9999 = 2958465). */
    private int serial;

    /** The day of the month (1 to 28, 29, 30 or 31 depending on the month). */
    private int day;

    /** The month of the year (1 to 12). */
    private int month;

    /** The year (1900 to 9999). */
    private int year;

    /** An optional description for the date. */
    private String description;

    /**
     * Constructs a new spreadsheet date.
     *
     * @param day  the day.
     * @param month  the month (1-12).
     * @param year  the year (in the range 1900 to 9999).
     */
    public SpreadsheetDate(int day, int month, int year) {

        if ((year >= 1900) && (year <= 9999)) {
            this.year = year;
        }
        else {
            throw new IllegalArgumentException(
                "SpreadsheetDate: Year must be in range 1900 to 9999.");
        }

        if ((month >= SerialDate.JANUARY) && (month <= SerialDate.DECEMBER)) {
            this.month = month;
        }
        else {
            throw new IllegalArgumentException("SpreadsheetDate: Invalid month.");
        }

        if ((day >= 1) && (day <= SerialDate.lastDayOfMonth(month, year))) {
            this.day = day;
        }
        else {
            throw new IllegalArgumentException("SpreadsheetDate: Invalid day.");
        }

        // the serial number needs to be synchronised with the day-month-year...
        this.serial = calcSerial(day, month, year);

        this.description = null;

    }

    /**
     * Standard constructor - creates a new date object representing the
     * specified day number (which should be in the range 2 to 2958465.
     *
     * @param serial  the serial number for the day (range: 2 to 2958465).
     */
    public SpreadsheetDate(int serial) {

        if ((serial >= SERIAL_LOWER_BOUND) && (serial <= SERIAL_UPPER_BOUND)) {
            this.serial = serial;
        }
        else {
            throw new IllegalArgumentException(
                "SpreadsheetDate: Serial must be in range 2 to 2958465.");
        }

        // the day-month-year needs to be synchronised with the serial number...
        calcDayMonthYear();

    }

    /**
     * Returns the description that is attached to the date.
     * <P>
     * It is not required that a date have a description, but for some applications it is useful.
     *
     * @return the description that is attached to the date.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description for the date.
     *
     * @param description  the description for this date.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the serial number for the date, where 1 January 1900 = 2
     * (this corresponds, almost, to the numbering system used in Microsoft
     * Excel for Windows and Lotus 1-2-3).
     *
     * @return the serial number of this date.
     */
    public int toSerial() {
        return this.serial;
    }

    /**
     * Returns a java.util.Date equivalent to this date.
     *
     * @return this as <code>java.util.Date</code>.
     */
    public Date toDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(getYYYY(), getMonth() - 1, getDayOfMonth(), 0, 0, 0);
        return calendar.getTime();

    }

    /**
     * Returns the year (assume a valid range of 1900 to 9999).
     *
     * @return the year.
     */
    public int getYYYY() {
        return this.year;
    }

    /**
     * Returns the month (January = 1, February = 2, March = 3).
     *
     * @return the month of the year.
     */
    public int getMonth() {
        return this.month;
    }

    /**
     * Returns the day of the month.
     *
     * @return the day of the month.
     */
    public int getDayOfMonth() {
        return this.day;
    }

    /**
     * Returns a code representing the day of the week.
     * <P>
     * The codes are defined in the SerialDate class as: SUNDAY, MONDAY,
     * TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY.
     *
     * @return a code representing the day of the week.
     */
    public int getDayOfWeek() {
        return (this.serial + 6) % 7 + 1;
    }

    /**
     * Tests the equality of this SpreadsheetDate with an arbitrary Object.
     * <P>
     * This method will return true ONLY if the object is an instance of the
     * SerialDate base class, and it represents the same day as this
     * SpreadsheetDate.
     *
     * @param object  the object to compare.
     *
     * @return <code>true</code> if this is equal to object.
     */
    public boolean equals(Object object) {

        if (object instanceof SerialDate) {
            SerialDate s = (SerialDate) object;
            return (s.toSerial() == this.toSerial());
        }
        else {
            return false;
        }

    }

    /**
     * Returns the difference (in days) between this date and the specified 'other' date.
     *
     * @param other  the date being compared to.
     *
     * @return the difference (in days) between this date and the specified 'other' date.
     */
    public int compare(SerialDate other) {
        return this.serial - other.toSerial();
    }

    /**
     * Returns true if this SerialDate represents the same date as the
     * specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return <code>true</code> if this SerialDate represents the same date as the
     *         specified SerialDate.
     */
    public boolean isOn(SerialDate other) {
        return (this.serial == other.toSerial());
    }

    /**
     * Returns true if this SerialDate represents an earlier date compared to
     * the specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return <code>true</code> if this SerialDate represents an earlier date
     *         compared to the specified SerialDate.
     */
    public boolean isBefore(SerialDate other) {
        return (this.serial < other.toSerial());
    }

    /**
     * Returns true if this SerialDate represents the same date as the
     * specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return <code>true</code> if this SerialDate represents the same date
     *         as the specified SerialDate.
     */
    public boolean isOnOrBefore(SerialDate other) {
        return (this.serial <= other.toSerial());
    }

    /**
     * Returns true if this SerialDate represents the same date as the
     * specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return <code>true</code> if this SerialDate represents the same date
     *         as the specified SerialDate.
     */
    public boolean isAfter(SerialDate other) {
        return (this.serial > other.toSerial());
    }

    /**
     * Returns true if this SerialDate represents the same date as the
     * specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return <code>true</code> if this SerialDate represents the same date as
     *         the specified SerialDate.
     */
    public boolean isOnOrAfter(SerialDate other) {
        return (this.serial >= other.toSerial());
    }

    /**
     * Returns true if this SerialDate is within the specified range
     * (INCLUSIVE).  The order of d1 d2 is not important.
     *
     * @param d1  one boundary date for the range.
     * @param d2  a second boundary date for the range.
     *
     * @return <code>true</code> if this SerialDate is within the specified range.
     */
    public boolean isInRange(SerialDate d1, SerialDate d2) {
        return false;
    }

    /**
     * Returns true if this SerialDate is within the specified range (caller
     * specifies whether or not the end-points are included).  The order of d1
     * and d2 is not important.
     *
     * @param d1  one boundary date for the range.
     * @param d2  a second boundary date for the range.
     * @param include   if <code>true</code> include d2 in the search.
     *
     * @return <code>true</code> if this SerialDate is within the specified range.
     */
    public boolean isInRange(SerialDate d1, SerialDate d2, int include) {
        return false;
    }

    /**
     * Calculate the serial number from the day, month and year.
     * <P>
     * 1-Jan-1900 = 2.
     *
     * @param day  the day.
     * @param month  the month.
     * @param year  the year.
     *
     * @return the serial number from the day, month and year.
     */
    private int calcSerial(int day, int month, int year) {

        int y = ((year - 1900) * 365) + SerialDate.leapYearCount(year - 1);
        int m = SerialDate.AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH[month];
        if (month > SerialDate.FEBRUARY) {
            if (SerialDate.isLeapYear(year)) {
                m = m + 1;
            }
        }
        int d = day;
        return y + m + d + 1;

    }

    /**
     * Calculate the day, month and year from the serial number.
     */
    private void calcDayMonthYear() {

        // get the year from the serial date
        int days = this.serial - SERIAL_LOWER_BOUND;
        // overestimated because we ignored leap days
        int overestimatedYYYY = 1900 + (days / 365);
        int leaps = SerialDate.leapYearCount(overestimatedYYYY);
        int nonleapdays = days - leaps;
        // underestimated because we overestimated years
        int underestimatedYYYY = 1900 + (nonleapdays / 365);

        if (underestimatedYYYY == overestimatedYYYY) {
            this.year = underestimatedYYYY;
        }
        else {
            int ss1 = calcSerial(1, 1, underestimatedYYYY);
            while (ss1 <= this.serial) {
                underestimatedYYYY = underestimatedYYYY + 1;
                ss1 = calcSerial(1, 1, underestimatedYYYY);
            }
            this.year = underestimatedYYYY - 1;
        }

        int ss2 = calcSerial(1, 1, this.year);

        int[] daysToEndOfPrecedingMonth = AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;

        if (isLeapYear(this.year)) {
            daysToEndOfPrecedingMonth = LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
        }

        // get the month from the serial date
        int mm = 1;
        int sss = ss2 + daysToEndOfPrecedingMonth[mm] - 1;
        while (sss < this.serial) {
            mm = mm + 1;
            sss = ss2 + daysToEndOfPrecedingMonth[mm] - 1;
        }
        this.month = mm - 1;

        // what's left is d(+1);
        this.day = this.serial - ss2 - daysToEndOfPrecedingMonth[this.month] + 1;

    }

}
