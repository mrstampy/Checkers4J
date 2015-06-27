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
 * The Class MultiDimensionStandardCheckerGame.
 */
public class ThreeDStandardCheckerGame implements CheckerGame {

	private static final long serialVersionUID = 6556554049093188540L;

	/** The Constant GAME_NAME. */
	public static final String GAME_NAME = "Multi Dimensional Standard Checkers";

	private ThreeDStandardCheckerRules rules;
	private List<StandardCheckerGame> boards = new ArrayList<>();

	private int lastBoardIdx = -1;

	private long gameId;

	private GameState gameState = GameState.STATELESS;

	private long endTime;

	private long startTime;

	/**
	 * Instantiates a new multi dimension standard checker game.
	 */
	public ThreeDStandardCheckerGame() {
		this(new ThreeDStandardCheckerRules(2));
	}

	/**
	 * Instantiates a new multi dimension standard checker game.
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
	 * Instantiates a new multi dimension standard checker game.
	 *
	 * @param numBoards
	 *          the num boards
	 */
	public ThreeDStandardCheckerGame(int numBoards) {
		this(new ThreeDStandardCheckerRules(numBoards));
	}

	/**
	 * Instantiates a new multi dimension standard checker game.
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
	 * Instantiates a new multi dimension standard checker game.
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
	@Override
	public void initialize(CheckerRules rules) {
		int nb = getNumBoards();

		for (int i = 0; i < nb; i++) {
			StandardCheckerGame scg = new StandardCheckerGame(rules);
			scg.setAutoEndTurn(false);
			boards.add(scg);
		}

		setGameState(GameState.INITIALIZED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#beginTurn(int)
	 */
	@Override
	public void beginTurn(int pieceColour) {
		if (GameState.INITIALIZED == getGameState()) setGameState(GameState.STARTED);

		boards.forEach(scg -> scg.beginTurn(pieceColour));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#move(int, int, int)
	 */
	@Override
	public List<PieceState> move(int pieceColour, int pieceNumber, int toPosition) throws CheckersStateException {
		assert rules.isValidPieceColour(pieceColour);
		assert isValidPosition(toPosition);
		assert isValidPieceNumber(pieceNumber);

		beginTurn(pieceColour);

		if (GameState.STARTED != getGameState()) {
			throw new CheckersStateException(ErrorState.ILLEGAL_STATE, "Game " + getGameId() + " not started");
		}

		int toBoardIdx = getBoardIndex(toPosition);

		int boardIdx = getBoardIndexByNum(pieceNumber);

		Piece piece = getPiece(pieceColour, pieceNumber, boardIdx);
		if (piece == null) {
			throw new CheckersStateException(pieceColour, pieceNumber, toPosition, ErrorState.ILLEGAL_STATE, "Piece "
					+ getRules().toColourName(pieceColour) + "-" + pieceNumber + " not found on board index " + boardIdx);
		}

		if (boardIdx == toBoardIdx) {
			boards.get(boardIdx).move(pieceColour, pieceNumber, getRelativePosition(toPosition, boardIdx));
		} else {
			moveAcrossBoards(pieceColour, pieceNumber, toPosition, boardIdx, toBoardIdx);
		}

		lastBoardIdx = toBoardIdx;

		if (rules.isKingable(piece)) piece.setKinged(true);

		endOfGameCheck(piece);

		return getState();
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

	private void moveAcrossBoards(int pieceColour, int pieceNumber, int toPosition, int boardIdx, int toBoardIdx)
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
			break;
		case 2:
			jumpBoards(piece, boardIdx, toBoardIdx, getRelativePosition(toPosition, toBoardIdx));
			break;
		}
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
		return pieceNumber >= 0 && pieceNumber < getNumBoards() * rules.getNumberOfPieces();
	}

	private boolean isValidPosition(int toPosition) {
		return toPosition >= 0 && toPosition < getNumBoards() * rules.getBoardWidth() * rules.getBoardHeight();
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
		assert boardIdx < getNumBoards();

		return boardIdx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#endTurn(int)
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
	@Override
	public int hasTurn() {
		return boards.get(0).hasTurn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getState(int)
	 */
	@Override
	public List<PieceState> getState(int pieceColour) {
		Map<Integer, List<PieceState>> forBoards = new HashMap<>();

		for (int i = 0; i < getNumBoards(); i++) {
			forBoards.put(i, boards.get(i).getState(pieceColour));
		}

		return toAbsolutePositions(forBoards);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getState()
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
		return p.getPosition() + (boardIdx * getRules().getBoardWidth() * getRules().getBoardHeight());
	}

	private int getRelativePosition(int absolutePosition, int boardIdx) {
		return absolutePosition - (boardIdx * getRules().getBoardWidth() * getRules().getBoardHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerGame#setState(java.util.List)
	 */
	@Override
	public void setState(List<Piece> state) {
		Map<Integer, List<Piece>> forBoards = createForBoards(state);
		for (int i = 0; i < getNumBoards(); i++) {
			boards.get(i).setState(forBoards.get(i));
		}
	}

	/**
	 * Gets the state by board.
	 *
	 * @return the state by board
	 */
	public Map<Integer, List<PieceState>> getStateByBoard() {
		Map<Integer, List<PieceState>> map = new HashMap<>();

		for (int i = 0; i < getNumBoards(); i++) {
			int idx = i;

			StandardCheckerGame scg = boards.get(i);
			List<PieceState> l = scg.getState();
			l.stream().forEach(p -> p.setPosition(getAbsolutePosition(p, idx)));

			map.put(i, l);
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

		List<Piece> list = forBoards.get(boardIdx);
		if (list == null) {
			list = new ArrayList<>();
			forBoards.put(boardIdx, list);
		}

		list.add(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getWinningColour()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getStartTime()
	 */
	@Override
	public long getStartTime() {
		return startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getEndTime()
	 */
	@Override
	public long getEndTime() {
		return endTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getGameState()
	 */
	@Override
	public GameState getGameState() {
		return gameState;
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
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getNextPlayer()
	 */
	@Override
	public int getNextPlayer() {
		return lastBoardIdx == -1 ? -1 : boards.get(lastBoardIdx).getNextPlayer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getLastPlayer()
	 */
	@Override
	public int getLastPlayer() {
		return lastBoardIdx == -1 ? -1 : boards.get(lastBoardIdx).getLastPlayer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#canMove(int)
	 */
	@Override
	public boolean canMove(int pieceColour) {
		for (StandardCheckerGame scg : boards) {
			if (scg.canMove(pieceColour)) return true;
		}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerGame#canMove(com.github.mrstampy
	 * .checkers4j.Piece)
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

		int x = rules.getX(piece.getPosition());
		int y = rules.getY(piece.getPosition()) + (forward ? factor : -factor);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#draw()
	 */
	@Override
	public void draw() {
		boards.forEach(scg -> scg.draw());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#isDraw()
	 */
	@Override
	public boolean isDraw() {
		return boards.get(0).isDraw();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getRules()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getGameId()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#isAutoEndTurn()
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

}
