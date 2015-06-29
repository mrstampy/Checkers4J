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
package com.github.mrstampy.checkers4j.standard.threeD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mrstampy.checkers4j.Piece;
import com.github.mrstampy.checkers4j.PieceState;
import com.github.mrstampy.checkers4j.annotation.ExposeInternals;
import com.github.mrstampy.checkers4j.annotation.Writable;
import com.github.mrstampy.checkers4j.api.CheckerGame;
import com.github.mrstampy.checkers4j.ex.CheckersStateException;
import com.github.mrstampy.checkers4j.standard.StandardCheckerGame;
import com.github.mrstampy.checkers4j.standard.StandardCheckerRules;

// TODO: Auto-generated Javadoc
/**
 * Convenience superclass of 3D standard checker games.
 */
public abstract class AbstractThreeDStandardCheckerGame implements CheckerGame<ThreeDStandardCheckerRules> {

	private static final long serialVersionUID = -3722403586138663557L;

	/** The boards. */
	protected List<StandardCheckerGame> boards = new ArrayList<>();

	private ThreeDStandardCheckerRules rules;
	private int lastBoardIdx = -1;
	private long gameId;
	private GameState gameState = GameState.STATELESS;
	private long endTime;
	private long startTime;

	/**
	 * Instantiates a new threeD standard checker game with two boards.
	 */
	public AbstractThreeDStandardCheckerGame() {
		this(new ThreeDStandardCheckerRules(2));
	}

	/**
	 * Instantiates a new threeD standard checker game with two boards using the
	 * specified dimensions.
	 *
	 * @param boardWidth
	 *          the board width
	 * @param boardHeight
	 *          the board height
	 */
	public AbstractThreeDStandardCheckerGame(int boardWidth, int boardHeight) {
		this(new ThreeDStandardCheckerRules(2, boardWidth, boardHeight));
	}

	/**
	 * Instantiates a new threeD standard checker game with the specified number
	 * of boards of standard dimensions (8x8).
	 *
	 * @param numBoards
	 *          the num boards
	 */
	public AbstractThreeDStandardCheckerGame(int numBoards) {
		this(new ThreeDStandardCheckerRules(numBoards));
	}

	/**
	 * Instantiates a new threeD standard checker game.
	 *
	 * @param numBoards
	 *          the num boards
	 * @param boardWidth
	 *          the board width
	 * @param boardHeight
	 *          the board height
	 */
	public AbstractThreeDStandardCheckerGame(int numBoards, int boardWidth, int boardHeight) {
		this(new ThreeDStandardCheckerRules(numBoards, boardWidth, boardHeight));
	}

	/**
	 * Instantiates a new threeD standard checker game.
	 *
	 * @param rules
	 *          the rules
	 */
	protected AbstractThreeDStandardCheckerGame(ThreeDStandardCheckerRules rules) {
		assert rules != null;

		this.rules = rules;

		initialize(rules);
	}

	/**
	 * Begin turn.
	 *
	 * @param pieceColour
	 *          the piece colour
	 */
	@Override
	public void beginTurn(int pieceColour) {
		if (GameState.INITIALIZED == getGameState()) setGameState(GameState.STARTED);

		boards.forEach(scg -> scg.beginTurn(pieceColour));
	}

	/**
	 * Convenience method to move the specified piece to the x,y,z coordinates
	 * specified.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @param pieceNumber
	 *          the piece number
	 * @param toX
	 *          the to x
	 * @param toY
	 *          the to y
	 * @param toBoardIdx
	 *          the to board idx
	 * @return the list
	 * @throws CheckersStateException
	 *           the checkers state exception
	 */
	@Writable
	public List<PieceState> move(int pieceColour, int pieceNumber, int toX, int toY, int toBoardIdx)
			throws CheckersStateException {
		return move(pieceColour, pieceNumber, getAbsolutePosition(toX, toY, toBoardIdx));
	}

	/**
	 * Gets the board index.
	 *
	 * @param absolutePosition
	 *          the absolute position
	 * @return the board index
	 */
	public abstract int getBoardIndex(int absolutePosition);

	/**
	 * Sets the state for the game. Piece positions must be relative to the board.
	 *
	 * @param state
	 *          the state
	 */
	@Writable
	public void setState(Map<Integer, List<Piece>> state) {
		assert state != null && state.size() == getNumBoards();

		for (int i = 0; i < getNumBoards(); i++) {
			boards.get(i).setState(state.get(i));
		}
	}

	/**
	 * Gets the full state by board. Positions of pieces are relative to each
	 * board. Absolute position can be calculated by multiplying the board number
	 * (map key, z value) by the width and height of the boards and adding this
	 * value to the relative position.<br>
	 * <br>
	 * 
	 * This method exposes the pieces in play for direct inspection and
	 * manipulation. Use sparingly, or not at all.
	 *
	 * @return the state by board
	 * @see #getStateByBoard()
	 */
	@ExposeInternals
	public Map<Integer, List<Piece>> getFullStateByBoard() {
		Map<Integer, List<Piece>> map = new HashMap<>();

		for (int i = 0; i < getNumBoards(); i++) {
			map.put(i, boards.get(i).getFullState());
		}

		return map;
	}

	/**
	 * Gets the state by board. Positions of pieces are relative to each board.
	 * Absolute position can be calculated by multiplying the board number (map
	 * key, z value) by the width and height of the boards and adding this value
	 * to the relative position.
	 *
	 * @return the state by board
	 */
	public Map<Integer, List<PieceState>> getStateByBoard() {
		Map<Integer, List<PieceState>> map = new HashMap<>();

		for (int i = 0; i < getNumBoards(); i++) {
			map.put(i, boards.get(i).getState());
		}

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#endTurn(int)
	 */
	/**
	 * End turn.
	 *
	 * @param pieceColour
	 *          the piece colour
	 */
	@Override
	public void endTurn(int pieceColour) {
		boards.forEach(scg -> scg.endTurn(pieceColour));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#hasTurn()
	 */
	/**
	 * Checks for turn.
	 *
	 * @return the int
	 */
	@Override
	public int hasTurn() {
		return boards.get(0).hasTurn();
	}

	/**
	 * Convenience method to convert x,y,z coordinates to absolute position.
	 *
	 * @param x
	 *          the x
	 * @param y
	 *          the y
	 * @param z
	 *          the z
	 * @return the absolute position
	 */
	public abstract int getAbsolutePosition(int x, int y, int z);

	/**
	 * Returns the x,y,z coordinates specified by the absolute position.
	 *
	 * @param absolutePosition
	 *          the absolute position
	 * @return the coordinates
	 */
	public abstract Coordinates getCoordinates(int absolutePosition);

	/**
	 * Gets the last board idx.
	 *
	 * @return the last board idx
	 */
	public int getLastBoardIdx() {
		return lastBoardIdx;
	}

	/**
	 * Sets the last board idx.
	 *
	 * @param lastBoardIdx
	 *          the new last board idx
	 */
	protected void setLastBoardIdx(int lastBoardIdx) {
		this.lastBoardIdx = lastBoardIdx;
	}

	/**
	 * Draw.
	 */
	@Writable
	@Override
	public void draw() {
		boards.forEach(scg -> scg.draw());
	}

	/**
	 * Checks if is draw.
	 *
	 * @return true, if is draw
	 */
	@Override
	public boolean isDraw() {
		return boards.get(0).isDraw();
	}

	/**
	 * Gets the rules.
	 *
	 * @return the rules
	 */
	@Override
	public ThreeDStandardCheckerRules getRules() {
		return rules;
	}

	/**
	 * Gets the num boards.
	 *
	 * @return the num boards
	 */
	public int getNumBoards() {
		return rules.getNumBoards();
	}

	/**
	 * Gets the game id.
	 *
	 * @return the game id
	 */
	@Override
	public long getGameId() {
		return gameId;
	}

	/**
	 * Sets the game id.
	 *
	 * @param gameId
	 *          the new game id
	 */
	@Writable
	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	/**
	 * Checks if is auto end turn.
	 *
	 * @return true, if is auto end turn
	 */
	@Override
	public boolean isAutoEndTurn() {
		return boards.get(0).isAutoEndTurn();
	}

	/**
	 * Sets the auto end turn.
	 *
	 * @param autoEndTurn
	 *          the new auto end turn
	 */
	@Writable
	public void setAutoEndTurn(boolean autoEndTurn) {
		boards.forEach(scg -> scg.setAutoEndTurn(autoEndTurn));
	}

	/**
	 * Sets the game state.
	 *
	 * @param gameState
	 *          the new game state
	 */
	@SuppressWarnings("incomplete-switch")
	@Writable
	public void setGameState(GameState gameState) {
		this.gameState = gameState;

		if (gameState.ordinal() >= GameState.STARTED.ordinal()) boards.forEach(scg -> scg.setGameState(gameState));

		switch (gameState) {
		case FINISHED:
			setEndTime(System.currentTimeMillis());
			break;
		case STARTED:
			setStartTime(System.currentTimeMillis());
			break;
		}
	}

	/**
	 * Sets the start time.
	 *
	 * @param startTime
	 *          the new start time
	 */
	@Writable
	public void setStartTime(long startTime) {
		this.startTime = startTime;

		boards.forEach(scg -> scg.setStartTime(startTime));
	}

	/**
	 * Sets the end time.
	 *
	 * @param endTime
	 *          the new end time
	 */
	@Writable
	public void setEndTime(long endTime) {
		this.endTime = endTime;

		boards.forEach(scg -> scg.setEndTime(endTime));
	}

	/**
	 * Gets the next player.
	 *
	 * @return the next player
	 */
	@Override
	public int getNextPlayer() {
		return lastBoardIdx == -1 ? -1 : boards.get(lastBoardIdx).getNextPlayer();
	}

	/**
	 * Gets the last player.
	 *
	 * @return the last player
	 */
	@Override
	public int getLastPlayer() {
		return lastBoardIdx == -1 ? -1 : boards.get(lastBoardIdx).getLastPlayer();
	}

	/**
	 * Gets the winning colour.
	 *
	 * @return the winning colour
	 */
	@Override
	public int getWinningColour() {
		return boards.get(0).getWinningColour();
	}

	/**
	 * Sets the winning colour.
	 *
	 * @param winningColour
	 *          the new winning colour
	 */
	@Writable
	public void setWinningColour(int winningColour) {
		boards.forEach(scg -> scg.setWinningColour(winningColour));
	}

	/**
	 * Gets the start time.
	 *
	 * @return the start time
	 */
	@Override
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Gets the end time.
	 *
	 * @return the end time
	 */
	@Override
	public long getEndTime() {
		return endTime;
	}

	/**
	 * Gets the game state.
	 *
	 * @return the game state
	 */
	@Override
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * Can move.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @return true, if successful
	 */
	@Override
	public boolean canMove(int pieceColour) {
		if (boards.stream().filter(scg -> scg.canMove(pieceColour)).findAny().isPresent()) return true;

		return canMoveAcrossBoards(pieceColour);
	}

	private boolean canMoveAcrossBoards(int pieceColour) {
		for (int i = 0; i < boards.size(); i++) {
			StandardCheckerGame scg = boards.get(i);
			int idx = i;
			if (scg.getState().stream().filter(p -> canMoveAcrossBoards(p, idx)).findAny().isPresent()) return true;
		}
		return false;
	}

	/**
	 * Can move.
	 *
	 * @param piece
	 *          the piece
	 * @return true, if successful
	 */
	@Override
	public boolean canMove(Piece piece) {
		if (piece.isJumped()) return false;

		int boardIdx = getBoardIndex(piece);

		return boards.get(boardIdx).canMove(piece) ? true : canMoveAcrossBoards(piece, boardIdx);
	}

	/**
	 * Can move across boards.
	 *
	 * @param piece
	 *          the piece
	 * @param boardIdx
	 *          the board idx
	 * @return true, if successful
	 */
	protected boolean canMoveAcrossBoards(PieceState piece, int boardIdx) {
		if (piece.getPosition() == -1) return false;

		//@formatter:off
		return piece.isKinged() ? 
				canKingMoveAcrossBoards(piece, boardIdx) : 
				canMoveAcrossBoards(piece, boardIdx, piece.getColour() == StandardCheckerRules.WHITE_NUM);
		//@formatter:on
	}

	/**
	 * Can king move across boards.
	 *
	 * @param piece
	 *          the piece
	 * @param boardIdx
	 *          the board idx
	 * @return true, if successful
	 */
	protected boolean canKingMoveAcrossBoards(PieceState piece, int boardIdx) {
		return canMoveAcrossBoards(piece, boardIdx, true) || canMoveAcrossBoards(piece, boardIdx, false);
	}

	/**
	 * Can move across boards.
	 *
	 * @param piece
	 *          the piece
	 * @param boardIdx
	 *          the board idx
	 * @param b
	 *          the b
	 * @return true, if successful
	 */
	protected boolean canMoveAcrossBoards(PieceState piece, int boardIdx, boolean b) {
		boolean single = canMoveAcrossBoards(piece, boardIdx, b, 1);
		boolean jump = !single && canMoveAcrossBoards(piece, boardIdx, b, 2);

		return single || jump;
	}

	/**
	 * Implement to return true if the specified piece can move to an empty space
	 * on a board either below or above.
	 *
	 * @param piece
	 *          the piece
	 * @param boardIdx
	 *          the index of the board on which this piece exists
	 * @param forward
	 *          if true test for movability in the postive direction, else
	 *          negative direction
	 * @param factor
	 *          the amount to add and subtract from the board index to test piece
	 *          movability
	 * @return true, if successful
	 */
	protected abstract boolean canMoveAcrossBoards(PieceState piece, int boardIdx, boolean forward, int factor);

	/**
	 * Return the index of the board on which this piece resides.
	 *
	 * @param piece
	 *          the piece
	 * @return the board index
	 */
	protected abstract int getBoardIndex(Piece piece);

	/**
	 * The class Coordinates encapsulates the x,y,z coordinates specified by the
	 * absolute position.
	 */
	public static class Coordinates implements Serializable {

		private static final long serialVersionUID = -4879987415777690375L;

		/** The absolute position. */
		int x, y, z, absolutePosition;

		/**
		 * Instantiates a new coordinates.
		 *
		 * @param x
		 *          the x
		 * @param y
		 *          the y
		 * @param z
		 *          the z
		 * @param absolutePosition
		 *          the absolute position
		 */
		public Coordinates(int x, int y, int z, int absolutePosition) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.absolutePosition = absolutePosition;
		}

		/**
		 * Gets the x.
		 *
		 * @return the x
		 */
		public int getX() {
			return x;
		}

		/**
		 * Gets the y.
		 *
		 * @return the y
		 */
		public int getY() {
			return y;
		}

		/**
		 * Gets the z.
		 *
		 * @return the z
		 */
		public int getZ() {
			return z;
		}

		/**
		 * Gets the absolute position.
		 *
		 * @return the absolute position
		 */
		public int getAbsolutePosition() {
			return absolutePosition;
		}
	}

}
