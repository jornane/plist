/*
Property List Date - LGPL 3.0 licensed
Copyright (C) 2012  Yørn de Jong

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3.0 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

File is part of the Property List project.
Project page on http://plist.sf.net/
*/
package net.sf.plist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>Represents a date and time.</p>
 * 
 * <p>In this implementation, a {@link Date} is used to represent the {@link NSDate}.</p>
 * @see Date
 */
public final class NSDate extends NSObject {

	/** Epoch constant, used to calculate dates in binary Property List files */
	static long EPOCH = 978307200000L;
	
	/**
	 * Get the format used for dates in Property List files
	 * @return the date format
	 */
	public static SimpleDateFormat getFormatter() {
		SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		result.setTimeZone(TimeZone.getTimeZone("GMT"));
		return result;
	}
	
	private final Date theDate;
	
	/**
	 * Constructor.
	 * @param theDate value of the new object
	 */
	public NSDate(Date theDate) {
		this.theDate = theDate;
	}
	
	/**
	 * Constructor for {@link Double} values.
	 * Use this constructor for values returned by the {@link #toDouble} method.
	 */
	public NSDate(double theDouble) {
		this(new Date((long)(EPOCH + 1000D*theDouble)));
	}
	
	/**
	 * {@inheritDoc}
	 * @see #toDate()
	 */
	@Override
	public Date getValue() {
		return toDate();
	}
	/**
	 * Get the {@link Date} represented by this object.
	 * @return the {@link Date}
	 */
	@Override
	public Date toDate() {
		// Don't return theDate because Date is mutable
		return new Date(getTime());
	}
	/** @see Date#getTime() */
	public long getTime() {
		return theDate.getTime();
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isTrue() {
		return true;
	}
	
	/** {@inheritDoc} */
	@Override
	public long toLong() {
		return getTime();
	}
	
	/**
	 * Generate a double value which represents the
	 * {@link Date} contained within the object.
	 * This value is used by the binary Property List format. 
	 */
	@Override
	public double toDouble() {
		return (double)(getTime()-EPOCH)/1000D;
	}

}
