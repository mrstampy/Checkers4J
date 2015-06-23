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

	/** The Constant WHITE_KING_LIMIT. */
	public static final int WHITE_KING_LIMIT = 56;

	/** The Constant BLACK_KING_LIMIT. */
	public static final int BLACK_KING_LIMIT = 8;

	/** The Constant BOARD_WIDTH. */
	public static final int BOARD_WIDTH = 8;

	/** The Constant BOARD_HEIGHT. */
	public static final int BOARD_HEIGHT = 8;

	/** The Constant WHITE_NUM. */
	public static final int WHITE_NUM = 0;

	/** The Constant BLACK_NUM. */
	public static final int BLACK_NUM = 1;

	/** The Constant WHITE. */
	public static final String WHITE = "WHITE";

	/** The Constant BLACK. */
	public static final String BLACK = "BLACK";

	/** The number of pieces per colour. */
	public static final int NUM_PIECES = 12;

	private static final int[] VALID_COLOURS = { WHITE_NUM, BLACK_NUM };

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#isValidPosition(int)
	 */
	public boolean isValidPosition(int position) {
		switch (position) {
		case -1: // jumped
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
		case 14:
		case 17:
		case 19:
		case 21:
		case 23:
		case 24:
		case 26:
		case 28:
		case 30:
		case 33:
		case 35:
		case 37:
		case 39:
		case 40:
		case 42:
		case 44:
		case 46:
		case 49:
		case 51:
		case 53:
		case 55:
		case 56:
		case 58:
		case 60:
		case 62:
			return true;
		}

		return false;
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
		if (pieceNumber < 1 || pieceNumber > 12) {
			throw new IllegalArgumentException(getIllegalNumberMsg(pieceNumber));
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getStartPosition(int,
	 * int)
	 */
	public int getStartPosition(int pieceColour, int pieceNumber) {
		switch (pieceColour) {
		case WHITE_NUM:
			return getWhiteStartPosition(pieceNumber);
		case BLACK_NUM:
			return getBlackStartPosition(pieceNumber);
		}

		throw new IllegalArgumentException(getIllegalColourMsg(pieceColour));
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
		return BOARD_WIDTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getBoardHeight()
	 */
	@Override
	public int getBoardHeight() {
		return BOARD_HEIGHT;
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
		return BLACK_NUM == piece.getColour() ? pos < BLACK_KING_LIMIT : pos >= WHITE_KING_LIMIT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerRules#getNumberOfPieces()
	 */
	@Override
	public int getNumberOfPieces() {
		return NUM_PIECES;
	}

	private int getWhiteStartPosition(int pieceNumber) {
		switch (pieceNumber) {
		case 1:
			return 1;
		case 2:
			return 3;
		case 3:
			return 5;
		case 4:
			return 7;
		case 5:
			return 8;
		case 6:
			return 10;
		case 7:
			return 12;
		case 8:
			return 14;
		case 9:
			return 17;
		case 10:
			return 19;
		case 11:
			return 21;
		case 12:
			return 23;
		}

		throw new IllegalArgumentException(getIllegalNumberMsg(pieceNumber));
	}

	private int getBlackStartPosition(int pieceNumber) {
		switch (pieceNumber) {
		case 1:
			return 40;
		case 2:
			return 42;
		case 3:
			return 44;
		case 4:
			return 46;
		case 5:
			return 49;
		case 6:
			return 51;
		case 7:
			return 53;
		case 8:
			return 55;
		case 9:
			return 56;
		case 10:
			return 58;
		case 11:
			return 60;
		case 12:
			return 62;
		}

		throw new IllegalArgumentException(getIllegalNumberMsg(pieceNumber));
	}

	private String getIllegalNumberMsg(int pieceNumber) {
		return "Illegal piece number " + pieceNumber + ", must be one of 1 thru 12 inclusive";
	}

	private String getIllegalColourMsg(int pieceColour) {
		return "Illegal piece colour " + pieceColour + " must be either 0 (WHITE) or 1 (BLACK)";
	}

}
