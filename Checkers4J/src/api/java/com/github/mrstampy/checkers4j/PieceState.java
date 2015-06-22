/*
 * Checkers4J Copyright (C) 2015 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.checkers4j;

import java.io.Serializable;

import com.github.mrstampy.checkers4j.api.CheckerRules;

// TODO: Auto-generated Javadoc
/**
 * Superclass of {@link Piece}, suitable for serialization to XML or JSON. A
 * minimum of information is included to uniquely identify a piece and its
 * current state.
 * 
 * @author burton
 *
 */
public class PieceState implements Serializable {
	private static final long serialVersionUID = 6705826871367585979L;

	private boolean kinged;

	private int number;

	private int colour;

	private int position;

	/**
	 * Returns true if this piece is kinged.
	 *
	 * @return true, if is kinged
	 */
	public boolean isKinged() {
		return kinged;
	}

	/**
	 * Sets the kinged state of the piece.
	 *
	 * @param kinged
	 *          the new kinged
	 */
	public void setKinged(boolean kinged) {
		this.kinged = kinged;
	}

	/**
	 * Returns the piece number. Piece numbers start at one and are sequential.
	 *
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the piece number. Piece numbers start at one and are sequential.
	 *
	 * @param number
	 *          the new number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Returns the number representing the colour of the piece.
	 *
	 * @return the colour
	 * @see CheckerRules#getValidColours()
	 */
	public int getColour() {
		return colour;
	}

	/**
	 * Sets the number representing the colour of the piece.
	 *
	 * @param colour
	 *          the new colour
	 * @see CheckerRules#getValidColours()
	 */
	public void setColour(int colour) {
		this.colour = colour;
	}

	/**
	 * Returns the current position of the piece, -1 if the piece has been removed
	 * from play (jumped).
	 *
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the current position. Use -1 to indicate a jumped piece.
	 *
	 * @param position
	 *          the new position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

}
