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

import java.util.List;

import com.github.mrstampy.checkers4j.AbstractCheckerGame;
import com.github.mrstampy.checkers4j.Piece;
import com.github.mrstampy.checkers4j.PieceState;
import com.github.mrstampy.checkers4j.api.CheckerRules;
import com.github.mrstampy.checkers4j.ex.CheckersStateException;
import com.github.mrstampy.checkers4j.ex.CheckersStateException.ErrorState;

// TODO: Auto-generated Javadoc
/**
 * The Class StandardCheckerGame's intended usage is as follows:<br>
 * <br>
 * 
 * 1. Instantiate the subclass, setting a unique {@link #setGameId(long)} if
 * required.<br>
 * 2. The initial state prior to play is available via {@link #getState()}.<br>
 * 3. Alternate between players by invoking {@link #beginTurn(int)},
 * {@link #move(int, int, int)} until no more moves, then {@link #endTurn(int)}.<br>
 * 4. The next player is available via {@link #getNextPlayer()} and the last
 * player via {@link #getLastPlayer()}.<br>
 * 5. {@link #hasTurn()} will return the player with a 'moves lock' on the game,
 * -1 if none.<br>
 */
public class StandardCheckerGame extends AbstractCheckerGame {
	private static final long serialVersionUID = -9117782259107654402L;

	/** The Constant GAME_NAME. */
	public static final String GAME_NAME = "Standard Checkers";

	private CheckerBoard board;

	/**
	 * Creates a standard checker game for a standard 8x8 checkerboard.
	 */
	public StandardCheckerGame() {
		this(new StandardCheckerRules());
	}

	/**
	 * Creates a standard checker game using the specified {@link CheckerRules}.
	 *
	 * @param checkerRules
	 *          the checker rules
	 */
	public StandardCheckerGame(CheckerRules checkerRules) {
		initialize(checkerRules);
	}

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
	 * @see
	 * com.github.mrstampy.checkers4j.AbstractCheckerGame#initialize(com.github
	 * .mrstampy.checkers4j.api.CheckerRules)
	 */
	@Override
	public void initialize(CheckerRules rules) {
		super.initialize(rules);

		board = new CheckerBoard(rules.getBoardWidth(), rules.getBoardHeight());

		getFullState().forEach(p -> addPieceToBoard(p));
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

		return list.stream().filter(p -> canMove(p)).findAny().isPresent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerGame#canMove(com.github.mrstampy
	 * .checkers4j.Piece)
	 */
	public boolean canMove(Piece piece) {
		if (piece.isJumped()) return false;

		return piece.isKinged() ? canMoveKing(piece) : canMove(piece, piece.getColour() == WHITE_NUM);
	}

	/**
	 * Convenience method to move a piece to a grid position specified by toX and
	 * toY.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @param pieceNumber
	 *          the piece number
	 * @param toX
	 *          the to x
	 * @param toY
	 *          the to y
	 * @return the list
	 * @throws CheckersStateException
	 *           the checkers state exception
	 */
	public List<PieceState> move(int pieceColour, int pieceNumber, int toX, int toY) throws CheckersStateException {
		return move(pieceColour, pieceNumber, getPositionFromGrid(toX, toY));
	}

	/**
	 * Helper method to convert a grid position to an absolute position. Numbering
	 * for grid and absolute position starts at zero.
	 *
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @return the position from grid
	 */
	public int getPositionFromGrid(int x, int y) {
		return getRules().getBoardHeight() * y + x;
	}

	/**
	 * Exposed, use sparingly.
	 *
	 * @return the board
	 */
	public CheckerBoard getBoard() {
		return board;
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

		state.forEach(p -> addPieceToBoard(p));
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
	 * @see
	 * com.github.mrstampy.checkers4j.AbstractCheckerGame#moveImpl(com.github.
	 * mrstampy.checkers4j.Piece, int)
	 */
	@Override
	protected void moveImpl(Piece piece, int toPosition) throws CheckersStateException {
		int y = getRules().getY(piece.getPosition());
		int x = getRules().getX(piece.getPosition());

		int toY = getRules().getY(toPosition);
		int toX = getRules().getX(toPosition);

		boolean jumped = evaluateMove(piece, toPosition, y, x, toY, toX);

		piece.setPosition(toPosition);
		board.setBoardPiece(piece, toX, toY);
		board.setBoardPiece(null, x, y);

		if (endingTurn(jumped, piece)) endTurn(piece.getColour());
	}

	private boolean evaluateMove(Piece piece, int toPosition, int y, int x, int toY, int toX)
			throws CheckersStateException {
		evaluateMoveTo(piece, toPosition, toY, toX);

		if (isJump(x, toX, y, toY)) {
			evaluateJump(piece, x, y, toPosition, toX, toY);
			return true;
		}

		if (isMove(x, toX, y, toY)) return false;

		throw new CheckersStateException(piece.getColour(), piece.getNumber(), toPosition, ErrorState.ILLEGAL_MOVE,
				"Cannot move " + piece + " to " + toPosition);
	}

	private void evaluateMoveTo(Piece piece, int toPosition, int toY, int toX) throws CheckersStateException {
		Piece toPiece = board.getBoardPiece(toX, toY);

		if (toPiece == null) return;

		throw new CheckersStateException(piece.getColour(), piece.getNumber(), toPosition, ErrorState.ILLEGAL_MOVE,
				"Cannot move " + piece + " to " + toPosition + " as " + toPiece + " already occupies it");
	}

	/**
	 * Returns true if the turn is over for the specified piece.
	 *
	 * @param jumped
	 *          the jumped
	 * @param piece
	 *          the piece
	 * @return true, if successful
	 */
	protected boolean endingTurn(boolean jumped, Piece piece) {
		return isAutoEndTurn() && (!jumped || !canJump(piece));
	}

	private boolean canJump(Piece piece) {
		return piece.isKinged() ? canKingJump(piece) : canJump(piece, piece.getColour() == WHITE_NUM);
	}

	private boolean canKingJump(Piece piece) {
		return canJump(piece, true) || canJump(piece, false);
	}

	private boolean canJump(Piece piece, boolean forward) {
		int y = getRules().getY(piece.getPosition());
		int x = getRules().getX(piece.getPosition());

		return board.canJump(forward, x, y);
	}

	private void addPieceToBoard(Piece piece) {
		if (piece.isJumped()) return;

		int y = getRules().getY(piece.getPosition());
		int x = getRules().getX(piece.getPosition());

		board.setBoardPiece(piece, x, y);
	}

	private void evaluateJump(Piece piece, int x, int y, int toPosition, int toX, int toY) throws CheckersStateException {
		int jumpX = splitDiff(x, toX);
		int jumpY = splitDiff(y, toY);

		Piece toJump = board.getBoardPiece(jumpX, jumpY);
		if (toJump == null) {
			throw new CheckersStateException(piece.getColour(), piece.getNumber(), toPosition, ErrorState.ILLEGAL_JUMP,
					"No piece at " + jumpX + ":" + jumpY + " to jump");
		}

		if (toJump.getColour() == piece.getColour()) {
			throw new CheckersStateException(piece.getColour(), piece.getNumber(), toPosition, ErrorState.ILLEGAL_JUMP,
					"Cannot jump over one's own piece: piece: " + piece + ", to jump: " + toJump);
		}

		toJump.jumped();
		board.setBoardPiece(null, jumpX, jumpY);
	}

	private boolean isJump(int x, int toX, int y, int toY) {
		return isMove(x, toX, y, toY, 2);
	}

	private boolean isMove(int x, int toX, int y, int toY) {
		return isMove(x, toX, y, toY, 1);
	}

	private boolean isMove(int x, int toX, int y, int toY, int factor) {
		int xDiff = x - toX;
		int yDiff = y - toY;

		return Math.abs(yDiff) == factor && Math.abs(xDiff) == factor;
	}

	private boolean canMoveKing(Piece piece) {
		if (canMove(piece, true)) return true;
		if (canMove(piece, false)) return true;

		return false;
	}

	private boolean canMove(Piece piece, boolean forward) {
		int y = getRules().getY(piece.getPosition());
		int x = getRules().getX(piece.getPosition());

		return board.canMoveOrJump(forward, x, y);
	}
}
