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
package com.github.mrstampy.checkers4j.standard;

import java.io.Serializable;

import com.github.mrstampy.checkers4j.Piece;

// TODO: Auto-generated Javadoc
/**
 * Variable size checker board corresponding to standard checker rules when
 * evaluating {@link #canMoveOrJump(boolean, int, int)}. Each square in the
 * board is sequentially numbered starting at zero and proceeding left to right,
 * top to bottom.
 * 
 * @author burton
 *
 */
public class CheckerBoard implements Serializable {

	private static final long serialVersionUID = -176765182830578438L;
	
	private Piece[][] board;
	private int width;
	private int height;

	/**
	 * Instantiates a new checker board.
	 *
	 * @param width
	 *          the width
	 * @param height
	 *          the height
	 */
	public CheckerBoard(int width, int height) {
		this.width = width;
		this.height = height;

		resetBoard();
	}

	/**
	 * Returns the board piece at the specified position.
	 *
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @return the board piece
	 */
	public Piece getBoardPiece(int x, int y) {
		return board[y][x];
	}

	/**
	 * Sets the board piece at the specified position.
	 *
	 * @param piece
	 *          the piece
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 */
	public void setBoardPiece(Piece piece, int x, int y) {
		board[y][x] = piece;
	}

	/**
	 * Clears the board. Use in conjunction with
	 * {@link #setBoardPiece(Piece, int, int)}.
	 */
	public void resetBoard() {
		board = new Piece[height][width];
	}

	/**
	 * Returns true if the piece identified by x & y has a potential move or jump
	 * to make.
	 *
	 * @param forward
	 *          true if evaluating forward moves, else false
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @return true, if successful
	 */
	public boolean canMoveOrJump(boolean forward, int x, int y) {
		return canMove(forward, x, y) || canJump(forward, x, y);
	}

	/**
	 * Returns true if the piece identified by x & y has a potential move to make.
	 *
	 * @param forward
	 *          true if evaluating forward moves, else false
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @return true, if successful
	 */
	public boolean canMove(boolean forward, int x, int y) {
		if (canMove(x, y, x + 1, forward)) return true;

		return canMove(x, y, x - 1, forward);
	}

	/**
	 * Returns true if the piece identified by x & y has a potential jump to make.
	 *
	 * @param forward
	 *          true if evaluating forward moves, else false
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @return true, if successful
	 */
	public boolean canJump(boolean forward, int x, int y) {
		if (canJump(x, y, x + 2, forward)) return true;

		return canJump(x, y, x - 2, forward);
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	private boolean canMove(int x, int y, int toX, boolean forward) {
		int toY = forward ? y + 1 : y - 1;

		if (!isValidGrid(toX, getWidth()) || !isValidGrid(toY, getHeight())) return false;

		return getBoardPiece(toX, toY) == null;
	}

	private boolean canJump(int x, int y, int toX, boolean forward) {
		int toY = forward ? y + 2 : y - 2;

		if (!isValidGrid(toX, getWidth()) || !isValidGrid(toY, getHeight())) return false;

		if (getBoardPiece(toX, toY) != null) return false;

		int jumpY = forward ? y + 1 : y - 1;
		int jumpX = splitDiff(x, toX);

		Piece toJump = getBoardPiece(jumpX, jumpY);
		Piece piece = getBoardPiece(x, y);

		return toJump != null && toJump.getColour() != piece.getColour();
	}

	private boolean isValidGrid(int i, int length) {
		return i >= 0 && i < length;
	}

	/**
	 * Returns the median integer between the given bounds.
	 *
	 * @param i
	 *          the i
	 * @param toI
	 *          the to i
	 * @return the int
	 */
	public static int splitDiff(int i, int toI) {
		int diff = i - toI;
		return Math.abs(diff / 2) + Math.min(i, toI);
	}
}
