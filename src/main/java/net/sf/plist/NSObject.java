/*
Property List Object - LGPL 3.0 licensed
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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>Every Property List contains one or more {@link NSObject}s.</p>
 * <p>Every different extension holds a certain kind of value,
 * for example the {@link NSString} holds a {@link String} and the {@link NSDate} holds a {@link java.util.Date}.</p>
 * <p>Some {@link NSObject}s can hold other {@link NSObject}s, thus making a tree.<br />
 * Those are {@link NSDictionary} and {@link NSArray}.</p>
 * 
 * <p>The value of this object can be requested through the {@link #getValue()} method.</p>
 * 
 * <p>The {@link #hashCode()}, {@link #equals(Object)} and {@link #toString()} methods are overridden to use the respective functions of the value object.</p> 
 */
public abstract class NSObject {

	NSObject() {/*not directly extendable outside this package*/}
	
	/**
	 * Get the unmodifiable value of this object.
	 * @return the unmodifiable value of this object
	 */
	public abstract Object getValue();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		NSObject other = (NSObject) obj;
		if (this.getValue() == null) {
			if (other.getValue() != null)
				return false;
		} else if (!this.getValue().equals(other.getValue()))
			return false;
		return true;
	}
	/**
	 * Get the String representation of this objects value.
	 * @return the {@link java.lang.String} representation for this object
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		Object val = getValue();
		return val == null ? "null" : val.toString();
	}
	
	/**
	 * Get a {@link ByteArrayInputStream} in which encapsulates the result of {@link #toBytes()}.
	 * @return the {@link ByteArrayInputStream}
	 */
	public ByteArrayInputStream toStream() {
		return new ByteArrayInputStream(toBytes());
	}
	
	/**
	 * Try to retrieve the list contained in this {@link NSObject}.
	 * This method will return the list if the object is a {@link NSArray}
	 * and will return a list containing all the values (but without the keys)
	 * if this object is a {@link NSDictionary}. Otherwise an <b>empty</b> list is returned.
	 * @return	the list
	 */
	public List<NSObject> toList() {
		return new ArrayList<NSObject>();
	}
	
	/**
	 * <p>Return whether or not the value evaluates to true.</p>
	 *
	 * <p>A value returns to true if and only if</p>
	 * <ul>
	 * <li>it is the {@link NSBoolean} type {@link NSBoolean#TRUE}</li>
	 * <li>It is an {@link NSString} which has a length greater than zero and does not equal "NO" or "False" (case insensitive)</li>
	 * <li>It is an {@link NSNumber} greater than 0</li>
	 * <li>It is an {@link NSCollection} which contains more than zero elements</li>
	 * <li>It is an {@link NSDate}</li>
	 * <li>It is an {@link NSData} with a length greater than 0</li>
	 * </ul>
	 * @return	the value evaluates to true
	 */
	public abstract boolean isTrue();
	/** @see #isTrue() */
	public final Boolean toBoolean() {
		return Boolean.valueOf(isTrue());
	}
	
	/**
	 * Get the raw data of the value.
	 * @return	the data
	 */
	public byte[] toBytes() {
		return new byte[0];
	}
	
	/**
	 * Get the value as a date.
	 * This will only work if this object is a {@link NSDate},
	 * otherwise the current date is returned.
	 * @return	the date or current date
	 */
	public Date toDate() {
		return new Date();
	}
	
	/**
	 * Try to retrieve the {@link SortedMap} contained in this {@link NSObject}.
	 * This method will return the {@link SortedMap} if the object is a {@link NSDictionary}
	 * and will return a {@link SortedMap} containing incremental keys and all the values
	 * if this object is a {@link NSArray}. Otherwise an <b>empty</b> list is returned.
	 * @return	the list
	 */
	public SortedMap<String, NSObject> toMap() {
		return new TreeMap<String,NSObject>();
	}
	
	/**
	 * <p>Return the value as a number.
	 * If this is not a {@link NSNumber}
	 * the number is derived from the value.</p>
	 * 
	 * <p>The number is derived in the following way</p>
	 * <ul>
	 * <li>For {@link NSNumber}: the actual number value</li>
	 * <li>For {@link NSCollection}: the amount of children</li>
	 * <li>For {@link NSData}: the length in bytes</li>
	 * <li>For {@link NSBoolean}: always byte; 0 for false, 1 for true</li>
	 * <li>For {@link NSString}: If the String is numeric, return the number in the string. Otherwise return 0<sup>*</sup></li>
	 * </ul>
	 * 
	 * <p>If you need to be sure about the result (e.g. you need to know if the result
	 * is user intended or a guess) you should do instanceof checks yourself.</p>
	 * <p><sup>*</sup>This behaviour is exactly as the library works
	 * and even though one could argue that the method should return null instead of 0,
	 * the method returns 0 and will never return null. There are a couple reasons for this:
	 * <ul>
	 * <li><b>The methods {@link #toLong()} and {@link #toDouble()} use {@link #toNumber()};</b><br />
	 * 	returning null would cause {@link NullPointerException}s.</li>
	 * <li><b>Just calling {@link #toNumber()} on any {@link NSObject} is dirty anyway.</b><br />
	 * 	If you want to be sure that your number is really a number,
	 * 	check if your {@link NSObject} is an {@link NSNumber}.</li>
	 * <li><b>If the implementation would fail on non-numeric Strings,
	 * 	the implementing programmer would need to check for these failures.</b><br />
	 * 	By not failing explicitly, the programmer can still conduct some checks,
	 * 	but is not forced to do so if these checks are not important.</li>
	 * </ul>
	 * @return	the number
	 */
	public Number toNumber() {
		return new Long(toLong());
	}
	
	/**
	 * Return the value as a long.
	 * If this is not a {@link NSInteger}
	 * the long value is derived from the actual value.
	 * @return	the long
	 */
	public abstract long toLong();
	
	/**
	 * Return the value as an int.
	 * This value is found by cutting bytes off from {@link #toLong()}.
	 * @return	the int
	 */
	public final int toInteger() {
		return (int) toLong();
	}
	
	/**
	 * Return the value as a double.
	 * If this is not a {@link NSReal}
	 * the double value is derived from the actual value.
	 * @return	the double
	 */
	public abstract double toDouble();

}
