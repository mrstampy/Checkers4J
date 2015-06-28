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
package com.github.mrstampy.checkers4j.standard.multi;

import static com.github.mrstampy.checkers4j.standard.CheckerBoard.splitDiff;
import static com.github.mrstampy.checkers4j.standard.StandardCheckerRules.WHITE_NUM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.mrstampy.checkers4j.Piece;
import com.github.mrstampy.checkers4j.PieceState;
import com.github.mrstampy.checkers4j.api.CheckerGame;
import com.github.mrstampy.checkers4j.api.CheckerRules;
import com.github.mrstampy.checkers4j.ex.CheckersStateException;
import com.github.mrstampy.checkers4j.ex.CheckersStateException.ErrorState;
import com.github.mrstampy.checkers4j.standard.CheckerBoard;
import com.github.mrstampy.checkers4j.standard.StandardCheckerGame;
import com.github.mrstampy.checkers4j.standard.StandardCheckerRules;

// TODO: Auto-generated Javadoc
/**
 * This class is a state engine for 3D checkers using standard checker rules.
 * The pieces created have a unique piece number in the context of each 3D game.
 * State is given in terms of absolute position, defined as a sequential integer
 * starting at zero and increasing left to right, top to bottom, up to down ie:
 * the first board's first position is 0, the second board's first position is
 * 64, the third board's first position is 128 etc.<br>
 * <br>
 * Standard checker rules apply for movement on a single board. To facilitate
 * movement across boards here are the rules I made up:<br>
 * <br>
 * 1. All pieces can move across boards in either z direction.<br>
 * 2. Movement across boards mimics movement on a board, where a single move can
 * occur when the space at (abs(deltaX, deltaY, deltaZ) = 1) is unoccupied and a
 * jump can occur when (abs(deltaX, deltaY, deltaZ) = 1) is occupied, of the
 * other colour and (abs(deltaX, deltaY, deltaZ) = 2) is unoccupied in the same
 * diagonal direction.<br>
 * 3. Uncrowned pieces are limited to their y direction (white forward, black
 * reverse).<br>
 * 4. Kings can move in all valid directions and can be crowned on any board.
 */
public class ThreeDStandardCheckerGame implements CheckerGame {

	private static final long serialVersionUID = 6556554049093188540L;

	/** The Constant GAME_NAME. */
	public static final String GAME_NAME = "Three D Standard Checkers";

	private ThreeDStandardCheckerRules rules;
	private List<StandardCheckerGame> boards = new ArrayList<>();

	private int lastBoardIdx = -1;

	private long gameId;

	private GameState gameState = GameState.STATELESS;

	private long endTime;

	private long startTime;

	/**
	 * Instantiates a new threeD standard checker game with two boards.
	 */
	public ThreeDStandardCheckerGame() {
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
	public ThreeDStandardCheckerGame(int boardWidth, int boardHeight) {
		this(new ThreeDStandardCheckerRules(2, boardWidth, boardHeight));
	}

	/**
	 * Instantiates a new threeD standard checker game with the specified number
	 * of boards of standard dimensions (8x8).
	 *
	 * @param numBoards
	 *          the num boards
	 */
	public ThreeDStandardCheckerGame(int numBoards) {
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
	public ThreeDStandardCheckerGame(int numBoards, int boardWidth, int boardHeight) {
		this(new ThreeDStandardCheckerRules(numBoards, boardWidth, boardHeight));
	}

	/**
	 * Instantiates a new threeD standard checker game.
	 *
	 * @param rules
	 *          the rules
	 */
	protected ThreeDStandardCheckerGame(ThreeDStandardCheckerRules rules) {
		assert rules != null;

		this.rules = rules;

		initialize(rules);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerGame#initialize(com.github.mrstampy
	 * .checkers4j.api.CheckerRules)
	 */
	/**
	 * Initialize.
	 *
	 * @param rules
	 *          the rules
	 */
	@Override
	public void initialize(CheckerRules rules) {
		int nb = getNumBoards();

		for (int i = 0; i < nb; i++) {
			boards.add(new StandardCheckerGame(rules));
		}

		setGameState(GameState.INITIALIZED);
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
	 * Move.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @param pieceNumber
	 *          the piece number
	 * @param toPosition
	 *          the to position, must be absolute
	 * @return the {@link #getState()}
	 * @throws CheckersStateException
	 *           the checkers state exception
	 */
	@Override
	public List<PieceState> move(int pieceColour, int pieceNumber, int toPosition) throws CheckersStateException {
		assert getRules().isValidPieceColour(pieceColour);
		assert isValidPosition(toPosition);
		assert isValidPieceNumber(pieceNumber);

		beginTurn(pieceColour);

		if (GameState.STARTED != getGameState()) {
			throw new CheckersStateException(ErrorState.ILLEGAL_STATE, "Game " + getGameId() + " not started");
		}

		int toBoardIdx = getBoardIndex(toPosition);

		int boardIdx = getBoardIndexByNum(pieceNumber);

		Piece piece = checkPiece(pieceColour, pieceNumber, toPosition, boardIdx);

		boolean jumped = false;
		if (boardIdx == toBoardIdx) {
			boards.get(boardIdx).move(pieceColour, pieceNumber, getRelativePosition(toPosition, boardIdx));
		} else {
			jumped = moveAcrossBoards(pieceColour, pieceNumber, toPosition, boardIdx, toBoardIdx);
		}

		lastBoardIdx = toBoardIdx;

		if (getRules().isKingable(piece)) piece.setKinged(true);

		if (endingTurn(jumped, piece)) endTurn(pieceColour);

		endOfGameCheck(piece);

		return getState();
	}

	private Piece checkPiece(int pieceColour, int pieceNumber, int toPosition, int boardIdx)
			throws CheckersStateException {
		Piece piece = getPiece(pieceColour, pieceNumber, boardIdx);

		if (piece == null) {
			throw new CheckersStateException(pieceColour, pieceNumber, toPosition, ErrorState.ILLEGAL_STATE, "Piece "
					+ getRules().toColourName(pieceColour) + "-" + pieceNumber + " not found on board index " + boardIdx);
		}

		if (!piece.directionCheck(toPosition)) {
			throw new CheckersStateException(pieceColour, pieceNumber, toPosition, ErrorState.ILLEGAL_MOVE, "Cannot move "
					+ piece + " to " + toPosition);
		}

		return piece;
	}

	private boolean endingTurn(boolean jumped, Piece piece) {
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
		int boardIdx = getBoardIndex(piece);

		CheckerBoard board = boards.get(boardIdx).getBoard();

		return board.canJump(forward, x, y) || canJumpAcrossBoards(piece, forward, x, y, boardIdx);
	}

	private boolean canJumpAcrossBoards(Piece piece, boolean forward, int x, int y, int boardIdx) {
		return canJumpAcrossBoards(piece, forward, x, y, boardIdx, true)
				|| canJumpAcrossBoards(piece, forward, x, y, boardIdx, false);
	}

	private boolean canJumpAcrossBoards(Piece piece, boolean forward, int x, int y, int boardIdx, boolean down) {
		int jumpIdx = down ? boardIdx + 1 : boardIdx - 1;
		int toIdx = down ? boardIdx + 2 : boardIdx - 2;

		int jy = forward ? y + 1 : y - 1;
		int ty = forward ? y + 2 : y - 2;

		int c = piece.getColour();

		if (isValidJump(x + 1, jy, x + 2, ty, jumpIdx, toIdx, c)) return true;
		if (isValidJump(x - 1, jy, x - 2, ty, jumpIdx, toIdx, c)) return true;

		return false;
	}

	private boolean isValidJump(int jx, int jy, int tx, int ty, int jIdx, int tIdx, int pieceColour) {
		if (!(isValidX(jx) && isValidX(tx))) return false;
		if (!(isValidY(jy) && isValidY(ty))) return false;
		if (!(isValidZ(jIdx) && isValidZ(tIdx))) return false;

		return hasPieceToJump(jx, jy, jIdx, pieceColour) && isEmptyAt(tx, ty, tIdx);
	}

	private boolean isValidZ(int z) {
		return z >= 0 && z < getNumBoards();
	}

	private boolean isValidX(int x) {
		return x >= 0 && x < getRules().getBoardWidth();
	}

	private boolean isValidY(int y) {
		return y >= 0 && y < getRules().getBoardHeight();
	}

	private boolean isEmptyAt(int x, int y, int boardIdx) {
		return getBoardPiece(x, y, boardIdx) == null;
	}

	private boolean hasPieceToJump(int x, int y, int boardIdx, int pieceColour) {
		Piece p = getBoardPiece(x, y, boardIdx);

		return p != null && p.getColour() != pieceColour;
	}

	private Piece getBoardPiece(int x, int y, int boardIdx) {
		StandardCheckerGame scg = boards.get(boardIdx);

		CheckerBoard board = scg.getBoard();

		Piece p = board.getBoardPiece(x, y);
		return p;
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
	public List<PieceState> move(int pieceColour, int pieceNumber, int toX, int toY, int toBoardIdx)
			throws CheckersStateException {
		return move(pieceColour, pieceNumber, getAbsolutePosition(toX, toY, toBoardIdx));
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
	public int getAbsolutePosition(int x, int y, int z) {
		assert isValidX(x);
		assert isValidY(y);
		assert isValidZ(z);

		int w = getRules().getBoardWidth();
		int h = getRules().getBoardHeight();

		return z * w * h + y * w + x;
	}

	/**
	 * Returns the x,y,z coordinates specified by the absolute position.
	 *
	 * @param absolutePosition
	 *          the absolute position
	 * @return the coordinates
	 */
	public Coordinates getCoordinates(int absolutePosition) {
		assert isValidPosition(absolutePosition);

		int z = getBoardIndex(absolutePosition);

		int relativePosition = getRelativePosition(absolutePosition, z);
		int x = getRules().getX(relativePosition);
		int y = getRules().getY(relativePosition);

		return new Coordinates(x, y, z, absolutePosition);
	}

	private void endOfGameCheck(Piece piece) {
		if (otherColoursInPlay(piece.getColour())) return;

		setWinningColour(piece.getColour());
		setGameState(GameState.FINISHED);
	}

	private boolean otherColoursInPlay(int colour) {
		for (StandardCheckerGame scg : boards) {
			if (scg.getFullState().stream().filter(p -> evalPlayable(p, colour)).findAny().isPresent()) {
				return true;
			}
		}

		return false;
	}

	private boolean evalPlayable(Piece p, int colour) {
		return p.getColour() == colour ? false : canMove(p);
	}

	// returns whether or not a jump was performed
	private boolean moveAcrossBoards(int pieceColour, int pieceNumber, int toPosition, int boardIdx, int toBoardIdx)
			throws CheckersStateException {
		int diff = Math.abs(boardIdx - toBoardIdx);
		if (diff > 2) {
			throw new CheckersStateException(pieceColour, pieceNumber, toPosition, ErrorState.ILLEGAL_MOVE, "Cannot move to "
					+ toPosition + " as it is across " + diff + " boards");
		}

		Piece piece = getPiece(pieceColour, pieceNumber, boardIdx);

		switch (diff) {
		case 1:
			moveBoards(piece, boardIdx, toBoardIdx, getRelativePosition(toPosition, toBoardIdx));
			return false;
		case 2:
			jumpBoards(piece, boardIdx, toBoardIdx, getRelativePosition(toPosition, toBoardIdx));
			break;
		}

		return true;
	}

	private void jumpBoards(Piece piece, int boardIdx, int toBoardIdx, int relativePosition)
			throws CheckersStateException {
		StandardCheckerGame from = boards.get(boardIdx);
		StandardCheckerGame to = boards.get(toBoardIdx);

		evalMovable(to, relativePosition, toBoardIdx);

		int toX = getRules().getX(relativePosition);
		int toY = getRules().getY(relativePosition);

		int fromX = getRules().getX(piece.getPosition());
		int fromY = getRules().getY(piece.getPosition());

		int jX = splitDiff(fromX, toX);
		int jY = splitDiff(fromY, toY);

		Piece toJump = boards.get(splitDiff(boardIdx, toBoardIdx)).getBoard().getBoardPiece(jX, jY);

		if (toJump == null || toJump.getColour() == piece.getColour()) {
			throw new CheckersStateException(piece.getColour(), piece.getNumber(), ErrorState.ILLEGAL_JUMP,
					"Cannot jump piece " + toJump + " with " + piece + " from board " + boardIdx + " to " + toBoardIdx);
		}

		toJump.jumped();

		moveBoards(piece, relativePosition, from, to);
	}

	private void moveBoards(Piece piece, int boardIdx, int toBoardIdx, int relativePosition)
			throws CheckersStateException {
		StandardCheckerGame from = boards.get(boardIdx);
		StandardCheckerGame to = boards.get(toBoardIdx);

		evalMovable(to, relativePosition, toBoardIdx);

		moveBoards(piece, relativePosition, from, to);
	}

	private void moveBoards(Piece piece, int relativePosition, StandardCheckerGame from, StandardCheckerGame to) {
		List<Piece> fromList = from.getFullState();
		List<Piece> toList = to.getFullState();

		fromList.remove(piece);
		toList.add(piece);

		piece.setPosition(relativePosition);

		from.setState(fromList);
		to.setState(toList);
	}

	private void evalMovable(StandardCheckerGame to, int relativePosition, int toBoardIdx) throws CheckersStateException {
		Piece p = getPiece(to, relativePosition);

		if (p == null) return;

		throw new CheckersStateException(ErrorState.ILLEGAL_STATE, p + " occupies position " + relativePosition);
	}

	private Piece getPiece(StandardCheckerGame to, int relativePosition) {
		CheckerBoard cb = to.getBoard();

		int x = getRules().getX(relativePosition);
		int y = getRules().getY(relativePosition);

		return cb.getBoardPiece(x, y);
	}

	private Piece getPiece(int pieceColour, int pieceNumber, int boardIdx) {
		Optional<Piece> o = boards.get(boardIdx).getFullState().stream().filter(p -> isPiece(pieceColour, pieceNumber, p))
				.findAny();

		return o.isPresent() ? o.get() : null;
	}

	private boolean isPiece(int pieceColour, int pieceNumber, Piece p) {
		return p.getColour() == pieceColour && p.getNumber() == pieceNumber;
	}

	private boolean isValidPieceNumber(int pieceNumber) {
		return pieceNumber >= 0 && pieceNumber < getNumBoards() * getRules().getNumberOfPieces();
	}

	private boolean isValidPosition(int toPosition) {
		return toPosition >= 0 && toPosition < getZFactor(getNumBoards());
	}

	/**
	 * Gets the board index.
	 *
	 * @param absolutePosition
	 *          the absolute position
	 * @return the board index
	 */
	public int getBoardIndex(int absolutePosition) {
		assert absolutePosition >= 0;

		int boardIdx = absolutePosition / getNumBoards();
		assert isValidY(boardIdx);

		return boardIdx;
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
	 * Returns the state of the game for the specified colour. Piece positions are
	 * absolute.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @return the state
	 */
	@Override
	public List<PieceState> getState(int pieceColour) {
		Map<Integer, List<PieceState>> forBoards = new HashMap<>();

		for (int i = 0; i < getNumBoards(); i++) {
			forBoards.put(i, boards.get(i).getState(pieceColour));
		}

		return toAbsolutePositions(forBoards);
	}

	/**
	 * Returns the state of the game. Piece positions are absolute.
	 *
	 * @return the state
	 */
	@Override
	public List<PieceState> getState() {
		Map<Integer, List<PieceState>> forBoards = new HashMap<>();

		for (int i = 0; i < getNumBoards(); i++) {
			forBoards.put(i, boards.get(i).getState());
		}

		return toAbsolutePositions(forBoards);
	}

	private List<PieceState> toAbsolutePositions(Map<Integer, List<PieceState>> forBoards) {
		List<PieceState> state = new ArrayList<>();

		forBoards.entrySet().forEach(e -> e.getValue().forEach(p -> toAbsolutePosition(p, state, e.getKey())));

		return state;
	}

	private void toAbsolutePosition(PieceState p, List<PieceState> state, int boardIdx) {
		p.setPosition(getAbsolutePosition(p, boardIdx));

		state.add(p);
	}

	private int getAbsolutePosition(PieceState p, int boardIdx) {
		return p.getPosition() + getZFactor(boardIdx);
	}

	private int getRelativePosition(int absolutePosition, int boardIdx) {
		return absolutePosition - getZFactor(boardIdx);
	}

	private int getZFactor(int boardIdx) {
		return boardIdx * getRules().getBoardWidth() * getRules().getBoardHeight();
	}

	/**
	 * Sets the state for the game. Piece positions must be absolute.
	 *
	 * @param state
	 *          the new state
	 */
	@Override
	public void setState(List<Piece> state) {
		assert state != null && state.size() == getRules().getNumberOfPieces() * getNumBoards();

		Map<Integer, List<Piece>> forBoards = createForBoards(state);
		for (int i = 0; i < getNumBoards(); i++) {
			boards.get(i).setState(forBoards.get(i));
		}
	}

	/**
	 * Sets the state for the game. Piece positions must be relative to the board.
	 *
	 * @param state
	 *          the state
	 */
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

	private Map<Integer, List<Piece>> createForBoards(List<Piece> state) {
		Map<Integer, List<Piece>> forBoards = new HashMap<>();
		int numPieces = getRules().getNumberOfPieces() * getNumBoards();

		state.forEach(p -> addForBoards(p, forBoards, numPieces));

		return forBoards;
	}

	private void addForBoards(Piece p, Map<Integer, List<Piece>> forBoards, int numPieces) {
		int boardIdx = getBoardIndex(p.getPosition());

		int relativePosition = getRelativePosition(p.getPosition(), boardIdx);

		p.setPosition(relativePosition);

		List<Piece> list = forBoards.get(boardIdx);
		if (list == null) {
			list = new ArrayList<>();
			forBoards.put(boardIdx, list);
		}

		list.add(p);
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
	 * Gets the game name.
	 *
	 * @return the game name
	 */
	@Override
	public String getGameName() {
		return GAME_NAME;
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

	private boolean canMoveAcrossBoards(PieceState piece, int boardIdx) {
		if (piece.getPosition() == -1) return false;

		//@formatter:off
		return piece.isKinged() ? 
				canKingMoveAcrossBoards(piece, boardIdx) : 
				canMoveAcrossBoards(piece, boardIdx, piece.getColour() == StandardCheckerRules.WHITE_NUM);
		//@formatter:on
	}

	private boolean canKingMoveAcrossBoards(PieceState piece, int boardIdx) {
		return canMoveAcrossBoards(piece, boardIdx, true) || canMoveAcrossBoards(piece, boardIdx, false);
	}

	private boolean canMoveAcrossBoards(PieceState piece, int boardIdx, boolean b) {
		boolean single = canMoveAcrossBoards(piece, boardIdx, b, 1);
		boolean jump = !single && canMoveAcrossBoards(piece, boardIdx, b, 2);

		return single || jump;
	}

	private boolean canMoveAcrossBoards(PieceState piece, int boardIdx, boolean forward, int factor) {
		int plus = boardIdx + factor;
		int minus = boardIdx - factor;

		int x = getRules().getX(piece.getPosition());
		int y = getRules().getY(piece.getPosition()) + (forward ? factor : -factor);

		boolean plusMt = false;
		boolean minusMt = false;

		if (plus < getNumBoards()) {
			plusMt = isEmpty(plus, x + factor, y) && isEmpty(plus, x - factor, y);
		} else {
			plusMt = true;
		}

		if (minus >= 0) {
			minusMt = isEmpty(minus, x + factor, y) && isEmpty(minus, x - factor, y);
		} else {
			minusMt = true;
		}

		return !plusMt || !minusMt;
	}

	private boolean isEmpty(int boardIdx, int x, int y) {
		StandardCheckerGame scg = boards.get(boardIdx);

		return scg.getBoard().getBoardPiece(x, y) == null;
	}

	private int getBoardIndex(Piece piece) {
		return getBoardIndexByNum(piece.getNumber());
	}

	private int getBoardIndexByNum(int pieceNumber) {
		int idx = -1;

		for (int i = 0; i < boards.size(); i++) {
			List<PieceState> l = boards.get(i).getState();
			if (l.stream().filter(ps -> ps.getNumber() == pieceNumber).findAny().isPresent()) {
				idx = i;
				break;
			}
		}

		return idx;
	}

	/**
	 * Draw.
	 */
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
	public CheckerRules getRules() {
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
	public void setEndTime(long endTime) {
		this.endTime = endTime;

		boards.forEach(scg -> scg.setEndTime(endTime));
	}

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
