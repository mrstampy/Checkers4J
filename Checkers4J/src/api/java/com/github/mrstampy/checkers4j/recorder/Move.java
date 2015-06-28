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
package com.github.mrstampy.checkers4j.recorder;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class Move.
 */
public class Move implements Serializable {

	private static final long serialVersionUID = -5366937236549160068L;

	private long gameId;

	private int pieceColour;

	private int pieceNumber;

	private int toPosition;

	/**
	 * Instantiates a new move.
	 *
	 * @param gameId
	 *          the game id
	 * @param pieceColour
	 *          the piece colour
	 * @param pieceNumber
	 *          the piece number
	 * @param toPosition
	 *          the to position
	 */
	public Move(long gameId, int pieceColour, int pieceNumber, int toPosition) {
		setGameId(gameId);
		setPieceColour(pieceColour);
		setPieceNumber(pieceNumber);
		setToPosition(toPosition);
	}

	/**
	 * Gets the piece colour.
	 *
	 * @return the piece colour
	 */
	public int getPieceColour() {
		return pieceColour;
	}

	/**
	 * Sets the piece colour.
	 *
	 * @param pieceColour
	 *          the new piece colour
	 */
	public void setPieceColour(int pieceColour) {
		this.pieceColour = pieceColour;
	}

	/**
	 * Gets the piece number.
	 *
	 * @return the piece number
	 */
	public int getPieceNumber() {
		return pieceNumber;
	}

	/**
	 * Sets the piece number.
	 *
	 * @param pieceNumber
	 *          the new piece number
	 */
	public void setPieceNumber(int pieceNumber) {
		this.pieceNumber = pieceNumber;
	}

	/**
	 * Gets the to position.
	 *
	 * @return the to position
	 */
	public int getToPosition() {
		return toPosition;
	}

	/**
	 * Sets the to position.
	 *
	 * @param toPosition
	 *          the new to position
	 */
	public void setToPosition(int toPosition) {
		this.toPosition = toPosition;
	}

	/**
	 * Gets the game id.
	 *
	 * @return the game id
	 */
	public long getGameId() {
		return gameId;
	}

	/**
	 * Sets the game id.
	 *
	 * @param gameId
	 *          the new game id
	 */
	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

}
