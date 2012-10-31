/*
Property List Real number - LGPL 3.0 licensed
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

/**
 * <p>Represents an real number of 64 bits in size.</p>
 * 
 * <p>In this implementation, a primitive <code>double</code> is used to represent the {@link NSReal}.</p>
 * @see Double
 */
public final class NSReal extends NSNumber {

	private final double theDouble;
	
	/**
	 * Constructor.
	 * @param theDouble value of the new object
	 */
	public NSReal(double theDouble) {
		this.theDouble = theDouble;
	}
	
	/**
	 * {@inheritDoc}
	 * @see #toDouble()
	 */
	@Override
	public Double getValue() {
		return toNumber();
	}
	/**
	 * Get the contents of this object as a <code>double</code>.
	 * @return the double
	 */
	@Override
	public double toDouble() {
		return theDouble;
	}
	/** {@inheritDoc} */
	@Override
	public Double toNumber() {
		return new Double(theDouble);
	}
	
	/** {@inheritDoc} */
	@Override
	public long toLong() {
		return Math.round(theDouble);
	}
	
	/** {@inheritDoc} */
	@Override
	public byte[] toBytes() {
		return longToByteArray(Double.doubleToLongBits(toDouble()));
	}

}
