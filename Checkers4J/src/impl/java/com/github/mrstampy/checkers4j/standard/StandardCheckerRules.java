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

import com.github.mrstampy.checkers4j.Piece;
import com.github.mrstampy.checkers4j.api.CheckerRules;

// TODO: Auto-generated Javadoc
/**
 * {@link #WHITE} pieces are initially placed at the top half of the board,
 * {@link #BLACK} on the bottom. Pieces are initially positioned sequentially
 * according to the value returned by {@link Piece#getNumber()}, left to right &
 * top to bottom. Board position is uniquely identified by a sequential integer
 * starting at zero, increasing left to right and top to bottom.
 * 
 * @author burton
 *
 */
public class StandardCheckerRules implements CheckerRules {
	private static final long serialVersionUID = 9186550493157881658L;

	/** The Constant STD_WIDTH. */
	public static final int STD_WIDTH = 8;

	/** The Constant STD_HEIGHT. */
	public static final int STD_HEIGHT = 8;

	/** The Constant WHITE_NUM. */
	public static final int WHITE_NUM = 0;

	/** The Constant BLACK_NUM. */
	public static final int BLACK_NUM = 1;

	/** The Constant WHITE. */
	public static final String WHITE = "WHITE";

	/** The Constant BLACK. */
	public static final String BLACK = "BLACK";

	private static final int[] VALID_COLOURS = { WHITE_NUM, BLACK_NUM };

	private int boardWidth;
	private int boardHeight;
	private int numberOfPieces;

	/**
	 * Creates rules for a standard 8x8 checkerboard.
	 */
	public StandardCheckerRules() {
		this(STD_WIDTH, STD_HEIGHT);
	}

	/**
	 * Creates rules for a checkerboard of the specified dimensions. Board width
	 * and height must be > 2 and must be even numbers. The final board will have
	 * pieces on all but the opponent-facing row.
	 *
	 * @param boardWidth
	 *          the board width
	 * @param boardHeight
	 *          the board height
	 */
	public StandardCheckerRules(int boardWidth, int boardHeight) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		calculateNumberOfPieces();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#isValidPosition(int)
	 */
	public boolean isValidPosition(int position) {
		if (position == -1) return true;
		if (position < 0 || position >= getBoardWidth() * getBoardHeight()) return false;

		boolean evenRow = isEven(getY(position));
		boolean evenPos = isEven(position);

		return evenRow ? !evenPos : evenPos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerRules#isValidPieceColour(int)
	 */
	public boolean isValidPieceColour(int pieceColour) {
		switch (pieceColour) {
		case WHITE_NUM:
		case BLACK_NUM:
			return true;
		}

		throw new IllegalArgumentException(getIllegalColourMsg(pieceColour));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#toColourName(int)
	 */
	public String toColourName(int pieceColour) {
		switch (pieceColour) {
		case WHITE_NUM:
			return WHITE;
		case BLACK_NUM:
			return BLACK;
		}

		throw new IllegalArgumentException(getIllegalColourMsg(pieceColour));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerRules#fromColourName(java.lang
	 * .String)
	 */
	public int fromColourName(String name) {
		switch (name) {
		case WHITE:
			return WHITE_NUM;
		case BLACK:
			return BLACK_NUM;
		}

		throw new IllegalArgumentException("Illegal piece colour " + name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerRules#isValidPieceNumber(int)
	 */
	public boolean isValidPieceNumber(int pieceNumber) {
		return pieceNumber > 0 && pieceNumber <= getNumberOfPieces();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getX(int)
	 */
	@Override
	public int getX(int position) {
		assert isValidPosition(position);

		int y = getY(position);

		return position - (y * getBoardHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getY(int)
	 */
	@Override
	public int getY(int position) {
		return position / getBoardHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getStartPosition(int,
	 * int)
	 */
	public int getStartPosition(int pieceColour, int pieceNumber) {
		assert isValidPieceColour(pieceColour);
		assert isValidPieceNumber(pieceNumber);

		int ppr = getPiecesPerRow();
		int rows = getRows();

		int factor = pieceColour == WHITE_NUM ? 0 : getBoardHeight() / 2 + 1;

		int row = -1;
		for (int i = 0; i < rows; i++) {
			if ((i + 1) * ppr >= pieceNumber) {
				row = i + factor;
				break;
			}
		}

		int idx = -1;
		for (int i = 0; i < ppr; i++) {
			if (((row - factor) * ppr) + i + 1 == pieceNumber) {
				idx = i;
				break;
			}
		}

		return convertToPosition(row, idx);
	}

	/**
	 * Convert to position.
	 *
	 * @param row
	 *          the row
	 * @param idx
	 *          the idx
	 * @return the int
	 */
	protected int convertToPosition(int row, int idx) {
		int rowPos = row * getBoardWidth();
		int factor = isEven(row) ? 1 : 0;

		return rowPos + (idx * 2) + factor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getValidColours()
	 */
	@Override
	public int[] getValidColours() {
		return VALID_COLOURS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerRules#directionCheck(com.github
	 * .mrstampy.checkers4j.Piece, int)
	 */
	@Override
	public boolean directionCheck(Piece piece, int toPosition) {
		if (piece.getPosition() == toPosition) {
			throw new IllegalArgumentException("Piece " + piece + " already at position " + toPosition);
		}

		if (piece.isKinged()) return true;

		return piece.getColour() == BLACK_NUM ? toPosition < piece.getPosition() : toPosition > piece.getPosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getBoardWidth()
	 */
	@Override
	public int getBoardWidth() {
		return boardWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getBoardHeight()
	 */
	@Override
	public int getBoardHeight() {
		return boardHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerRules#isKingable(com.github.mrstampy
	 * .checkers4j.Piece)
	 */
	@Override
	public boolean isKingable(Piece piece) {
		if (piece.isKinged()) return false;

		int pos = piece.getPosition();
		return BLACK_NUM == piece.getColour() ? pos < getBlackKingLimit() : pos >= getWhiteKingLimit();
	}

	/**
	 * Gets the black king limit.
	 *
	 * @return the black king limit
	 */
	protected int getBlackKingLimit() {
		return getBoardWidth();
	}

	/**
	 * Gets the white king limit.
	 *
	 * @return the white king limit
	 */
	protected int getWhiteKingLimit() {
		int width = getBoardWidth();
		int height = getBoardHeight();

		return width * height - width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getNumberOfPieces()
	 */
	@Override
	public int getNumberOfPieces() {
		return numberOfPieces;
	}

	private String getIllegalColourMsg(int pieceColour) {
		return "Illegal piece colour " + pieceColour + " must be either 0 (WHITE) or 1 (BLACK)";
	}

	private void calculateNumberOfPieces() {
		assert getBoardWidth() > 2 && isEven(getBoardWidth());
		assert getBoardHeight() > 2 && isEven(getBoardHeight());

		this.numberOfPieces = getPiecesPerRow() * getRows();
	}

	/**
	 * Checks if is even.
	 *
	 * @param i
	 *          the i
	 * @return true, if is even
	 */
	protected boolean isEven(int i) {
		return i % 2 == 0;
	}

	private int getPiecesPerRow() {
		return getBoardWidth() / 2;
	}

	private int getRows() {
		return (getBoardHeight() - 2) / 2;
	}

}
