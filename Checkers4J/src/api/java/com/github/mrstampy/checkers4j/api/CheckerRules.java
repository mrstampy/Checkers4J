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
package com.github.mrstampy.checkers4j.api;

import java.io.Serializable;

import com.github.mrstampy.checkers4j.Piece;

// TODO: Auto-generated Javadoc
/**
 * The Interface CheckerRules.
 */
public interface CheckerRules extends Serializable {

	/**
	 * Returns true if the specified position is a valid board position.
	 *
	 * @param position
	 *          the position
	 * @return true, if is valid position
	 */
	boolean isValidPosition(int position);

	/**
	 * Returns true if the specified number has been assigned a colour.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @return true, if is valid piece colour
	 */
	boolean isValidPieceColour(int pieceColour);

	/**
	 * Returns the string representation of the colour number.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @return the string
	 */
	String toColourName(int pieceColour);

	/**
	 * Returns the colour number from the string representation.
	 *
	 * @param name
	 *          the name
	 * @return the int
	 */
	int fromColourName(String name);

	/**
	 * Returns true if the number specified is a valid piece number. Piece numbers
	 * start at 1 and are sequential.
	 *
	 * @param pieceNumber
	 *          the piece number
	 * @return true, if is valid piece number
	 */
	boolean isValidPieceNumber(int pieceNumber);

	/**
	 * Returns the start position for the specified piece.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @param pieceNumber
	 *          the piece number
	 * @return the start position
	 */
	int getStartPosition(int pieceColour, int pieceNumber);

	/**
	 * Returns an array of valid colour numbers.
	 *
	 * @return the valid colours
	 */
	int[] getValidColours();

	/**
	 * Returns true if the specified piece can move in the direction specified by
	 * the position, false otherwise.
	 *
	 * @param piece
	 *          the piece
	 * @param toPosition
	 *          the to position
	 * @return true, if successful
	 */
	boolean directionCheck(Piece piece, int toPosition);

	/**
	 * Return the board width.
	 *
	 * @return the board width
	 */
	int getBoardWidth();

	/**
	 * Return the board height.
	 *
	 * @return the board height
	 */
	int getBoardHeight();

	/**
	 * Returns true if the piece is kingable in its current state.
	 *
	 * @param piece
	 *          the piece
	 * @return true, if is kingable
	 */
	boolean isKingable(Piece piece);

	/**
	 * Returns the number of pieces for each colour in the game.
	 *
	 * @return the number of pieces
	 */
	int getNumberOfPieces();

	/**
	 * Returns the grid X position of the specified absolute position, starting at
	 * 0 and increasing to {@link #getBoardWidth()} - 1;.
	 *
	 * @param position
	 *          the position
	 * @return the x
	 */
	int getX(int position);

	/**
	 * Returns the grid Y position of the specified absolute position, starting at
	 * 0 and increasing to {@link #getBoardHeight()} - 1;.
	 *
	 * @param position
	 *          the position
	 * @return the y
	 */
	int getY(int position);

	/**
	 * Gets the piece number offset.
	 *
	 * @return the piece number offset
	 */
	int getPieceNumberOffset();
}
