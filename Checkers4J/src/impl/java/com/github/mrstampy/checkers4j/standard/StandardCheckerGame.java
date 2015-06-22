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

import static com.github.mrstampy.checkers4j.standard.CheckerBoard.splitDiff;
import static com.github.mrstampy.checkers4j.standard.StandardCheckerRules.BLACK_NUM;
import static com.github.mrstampy.checkers4j.standard.StandardCheckerRules.WHITE_NUM;

import java.util.ArrayList;
import java.util.List;

import com.github.mrstampy.checkers4j.AbstractCheckerGame;
import com.github.mrstampy.checkers4j.Piece;
import com.github.mrstampy.checkers4j.api.CheckerRules;

// TODO: Auto-generated Javadoc
/**
 * The Class StandardCheckerGame.
 */
public class StandardCheckerGame extends AbstractCheckerGame {
	private static final long serialVersionUID = -9117782259107654402L;

	/** The Constant GAME_NAME. */
	public static final String GAME_NAME = "Standard Checkers";

	private CheckerBoard board;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getGameName()
	 */
	@Override
	public String getGameName() {
		return GAME_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#canMove(int)
	 */
	@Override
	public boolean canMove(int pieceColour) {
		List<Piece> list = byColour.get(pieceColour);

		if (list == null || list.isEmpty()) return false;

		for (Piece piece : list) {
			if (canMove(piece)) return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.AbstractCheckerGame#setStateImpl(java.util
	 * .List)
	 */
	@Override
	protected void setStateImpl(List<Piece> state) {
		board.resetBoard();

		for (Piece piece : state) {
			addPieceToBoard(piece);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.AbstractCheckerGame#setNextPlayer()
	 */
	@Override
	protected void setNextPlayer() {
		int nextPlayer = -1;

		switch (getLastPlayer()) {
		case WHITE_NUM:
			nextPlayer = BLACK_NUM;
			break;
		case BLACK_NUM:
			nextPlayer = WHITE_NUM;
			break;
		}

		setNextPlayer(nextPlayer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.AbstractCheckerGame#createPieces(int,
	 * com.github.mrstampy.checkers4j.api.CheckerRules)
	 */
	@Override
	protected List<Piece> createPieces(int pieceColour, CheckerRules rules) {
		if (board == null) board = new CheckerBoard(rules.getBoardWidth(), rules.getBoardHeight());

		List<Piece> pieces = new ArrayList<>();

		for (int i = 1; i <= 12; i++) {
			Piece piece = new Piece(rules, pieceColour, i);
			pieces.add(piece);

			addPieceToBoard(piece);
		}

		return pieces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.AbstractCheckerGame#moveImpl(com.github.
	 * mrstampy.checkers4j.Piece, int)
	 */
	@Override
	protected void moveImpl(Piece piece, int toPosition) {
		int y = getY(piece.getPosition());
		int x = getX(piece.getPosition(), y);

		int toY = getY(toPosition);
		int toX = getX(toPosition, toY);

		Piece toPiece = board.getBoardPiece(toX, toY);
		if (toPiece != null) {
			throw new IllegalStateException("Cannot move " + piece + " to " + toPosition + " as " + toPiece
					+ " already occupies it");
		}

		if (isJump(x, toX, y, toY)) {
			evaluateJump(piece, x, y, toPosition, toX, toY);
		} else {
			evaluate(piece, x, y, toPosition, toX, toY);
		}

		piece.setPosition(toPosition);
		board.setBoardPiece(piece, toX, toY);
		board.setBoardPiece(null, x, y);
	}

	private void addPieceToBoard(Piece piece) {
		if (piece.isJumped()) return;

		int y = getY(piece.getPosition());
		int x = getX(piece.getPosition(), y);

		board.setBoardPiece(piece, x, y);
	}

	private void evaluate(Piece piece, int x, int y, int toPosition, int toX, int toY) {
		int xDiff = x - toX;
		int yDiff = y - toY;
		if (Math.abs(xDiff) != 1 || Math.abs(yDiff) != 1) {
			throw new IllegalStateException("Illegal move: piece " + piece + " to " + toPosition);
		}
	}

	private void evaluateJump(Piece piece, int x, int y, int toPosition, int toX, int toY) {
		int jumpX = splitDiff(x, toX);
		int jumpY = splitDiff(y, toY);

		Piece toJump = board.getBoardPiece(jumpX, jumpY);
		if (toJump == null) throw new IllegalStateException("No piece at " + jumpX + ":" + jumpY + " to jump");

		if (toJump.getColour() == piece.getColour()) {
			throw new IllegalStateException("Cannot jump over one's own piece: piece: " + piece + ", to jump: " + toJump);
		}

		toJump.jumped();
		board.setBoardPiece(null, jumpX, jumpY);
	}

	private boolean isJump(int x, int toX, int y, int toY) {
		int xDiff = x - toX;
		int yDiff = y - toY;

		return Math.abs(yDiff) == 2 && Math.abs(xDiff) == 2;
	}

	private boolean canMove(Piece piece) {
		if (piece.isJumped()) return false;

		return piece.isKinged() ? canMoveKing(piece) : canMove(piece, piece.getColour() == WHITE_NUM);
	}

	private boolean canMoveKing(Piece piece) {
		if (canMove(piece, true)) return true;
		if (canMove(piece, false)) return true;

		return false;
	}

	private boolean canMove(Piece piece, boolean forward) {
		int y = getY(piece.getPosition());
		int x = getX(piece.getPosition(), y);

		return board.canMoveOrJump(forward, x, y);
	}

	private int getY(int position) {
		assert isValidPosition(position);

		return position / rules.getBoardHeight();
	}

	private int getX(int position, int y) {
		assert isValidPosition(position);

		return position - (y * rules.getBoardHeight());
	}

	private boolean isValidPosition(int position) {
		return rules.isValidPosition(position);
	}
}
