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
package com.github.mrstampy.checkers4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.mrstampy.checkers4j.api.CheckerGame;
import com.github.mrstampy.checkers4j.api.CheckerRules;
import com.github.mrstampy.checkers4j.ex.CheckersStateException;
import com.github.mrstampy.checkers4j.ex.CheckersStateException.ErrorState;
import com.github.mrstampy.checkers4j.recorder.CheckerGameRecorder;
import com.github.mrstampy.checkers4j.recorder.Move;
import com.github.mrstampy.checkers4j.recorder.RecordableCheckerGame;

// TODO: Auto-generated Javadoc
/**
 * Convenience superclass for {@link CheckerGame} implementations. Its intended
 * usage is as follows:<br>
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
 * 
 * @author burton
 *
 */
public abstract class AbstractCheckerGame implements RecordableCheckerGame {
	private static final long serialVersionUID = -3616896688205945975L;

	private long gameId = -1;

	private CheckerRules rules;

	/** The by colour. */
	protected Map<Integer, List<Piece>> byColour = new HashMap<>();

	/** The state. */
	protected List<Piece> state = new ArrayList<>();

	private int winningColour = -1;

	private GameState gameState = GameState.STATELESS;

	private long startTime = -1;

	private long endTime = -1;

	private Map<Integer, Boolean> turns = new HashMap<>();

	private int lastPlayer = -1;

	private int nextPlayer = -1;

	private boolean draw = false;

	private boolean autoEndTurn = true;

	/** The recorder. */
	protected CheckerGameRecorder recorder = new CheckerGameRecorder();

	/**
	 * Returns the underlying list of all pieces. Use {@link #getState()} if only
	 * interested in state information; this method facilitates direct piece
	 * manipulation.
	 *
	 * @return the full state
	 */
	public List<Piece> getFullState() {
		return state;
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
		assert rules != null;

		this.rules = rules;

		for (int colour : rules.getValidColours()) {
			List<Piece> pieces = createPieces(colour, rules);
			state.addAll(pieces);
			byColour.put(colour, pieces);
			turns.put(colour, Boolean.FALSE);
		}

		setGameStateInternal(GameState.INITIALIZED);
	}

	private List<Piece> createPieces(int pieceColour, CheckerRules rules) {
		List<Piece> pieces = new ArrayList<>();

		int pno = 1 + rules.getPieceNumberOffset() * rules.getNumberOfPieces();
		AtomicInteger ai = new AtomicInteger(1);
		for (int i = pno; i - pno < rules.getNumberOfPieces(); i++) {
			pieces.add(new Piece(rules, pieceColour, i, ai.getAndIncrement()));
		}

		return pieces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#beginTurn(int)
	 */
	@Override
	public void beginTurn(int pieceColour) {
		rules.isValidPieceColour(pieceColour);

		if (claimable(pieceColour)) claim(pieceColour);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getLastPlayer()
	 */
	public int getLastPlayer() {
		return lastPlayer;
	}

	private void claim(int pieceColour) {
		beginStateCheck();
		turns.put(pieceColour, Boolean.TRUE);
		lastPlayer = pieceColour;
		setNextPlayer();
	}

	/**
	 * Sets the next player.
	 */
	protected abstract void setNextPlayer();

	private boolean claimable(int pieceColour) {
		int hasTurn = hasTurn();
		if (hasTurn >= 0 && hasTurn != pieceColour) {
			throw new IllegalStateException("Cannot claim turn for " + pieceColour + "; " + hasTurn + " claims the turn");
		}

		return -1 == hasTurn && lastPlayer != pieceColour;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#endTurn(int)
	 */
	@Override
	public void endTurn(int pieceColour) {
		rules.isValidPieceColour(pieceColour);
		turns.put(pieceColour, Boolean.FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#hasTurn()
	 */
	public int hasTurn() {
		for (Entry<Integer, Boolean> entry : turns.entrySet()) {
			if (entry.getValue() == Boolean.TRUE) return entry.getKey();
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#move(int, int, int)
	 */
	@Override
	public List<PieceState> move(int pieceColour, int pieceNumber, int toPosition) throws CheckersStateException {
		recorder.addMove(getGameId(), pieceColour, pieceNumber, toPosition);

		beginTurn(pieceColour);

		if (GameState.STARTED != getGameState()) {
			throw new CheckersStateException(ErrorState.ILLEGAL_STATE, "Game " + getGameId() + " not started");
		}

		if (!rules.isValidPosition(toPosition)) {
			throw new CheckersStateException(pieceColour, pieceNumber, toPosition, ErrorState.ILLEGAL_VALUE,
					"Illegal position " + toPosition);
		}

		Piece piece = pieceCheck(pieceColour, pieceNumber);

		if (!piece.directionCheck(toPosition)) {
			throw new CheckersStateException(pieceColour, pieceNumber, toPosition, ErrorState.ILLEGAL_MOVE, "Cannot move "
					+ piece + " to " + toPosition);
		}

		moveImpl(piece, toPosition);

		if (rules.isKingable(piece)) piece.setKinged(true);

		endOfGameCheck(piece);

		return getState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.ReplayableCheckerGame#getMoves()
	 */
	@Override
	public List<Move> getMoves() {
		return recorder.getMoves();
	}

	/**
	 * Implement to move the piece to the intended position, throwing runtime
	 * exceptions if such a move would put the game into an undefined state. At
	 * this point the {@link #getGameState()}, the validity of the toPosition and
	 * whether the piece is still in play (not jumped) have been successfully
	 * evaluated.
	 *
	 * @param piece
	 *          the piece
	 * @param toPosition
	 *          the to position
	 * @throws CheckersStateException
	 *           the checkers state exception
	 */
	protected abstract void moveImpl(Piece piece, int toPosition) throws CheckersStateException;

	/**
	 * End of game check.
	 *
	 * @param piece
	 *          the piece
	 */
	protected void endOfGameCheck(Piece piece) {
		if (otherColoursInPlay(piece.getColour())) return;

		setWinningColour(piece.getColour());
		setGameStateInternal(GameState.FINISHED);
	}

	/**
	 * Other colours in play.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @return true, if successful
	 */
	protected boolean otherColoursInPlay(int pieceColour) {
		boolean others = false;

		for (Entry<Integer, List<Piece>> entry : byColour.entrySet()) {
			if (entry.getKey() == pieceColour) continue;

			if (!others && playing(entry.getValue())) others = canMove(entry.getKey());
		}

		return others;
	}

	/**
	 * Playing.
	 *
	 * @param value
	 *          the value
	 * @return true, if successful
	 */
	public static boolean playing(List<Piece> value) {
		for (Piece piece : value) {
			if (!piece.isJumped()) return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private Piece pieceCheck(int pieceColour, int pieceNumber) throws CheckersStateException {
		List<Piece> pieces = byColour.containsKey(pieceColour) ? byColour.get(pieceColour) : Collections.EMPTY_LIST;

		if (pieces.isEmpty()) {
			throw new CheckersStateException(pieceColour, pieceNumber, ErrorState.ILLEGAL_STATE, "No pieces for colour "
					+ pieceColour);
		}

		Piece piece = getPiece(pieces, pieceNumber);

		if (piece == null) {
			throw new CheckersStateException(pieceColour, pieceNumber, ErrorState.ILLEGAL_STATE, "No piece " + pieceNumber
					+ " for colour " + pieceColour);
		}

		if (piece.isJumped()) {
			throw new CheckersStateException(pieceColour, pieceNumber, ErrorState.ILLEGAL_STATE, piece + " has been jumped");
		}

		return piece;
	}

	private Piece getPiece(List<Piece> pieces, int pieceNumber) {
		for (Piece p : pieces) {
			if (p.getNumber() == pieceNumber) return p;
		}

		return null;
	}

	@SuppressWarnings("incomplete-switch")
	private void beginStateCheck() {
		switch (getGameState()) {
		case FINISHED:
			throw new IllegalStateException("Game " + getGameId() + " is finished");
		case STATELESS:
			throw new IllegalStateException("Game " + getGameId() + " has not been initialized");
		case INITIALIZED:
			setGameStateInternal(GameState.STARTED);
			break;
		}
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
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getState()
	 */
	@Override
	public List<PieceState> getState() {
		return createState(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.api.CheckerGame#setState(java.util.List)
	 */
	@Override
	public void setState(List<Piece> state) {
		assert state != null && state.size() == getRules().getNumberOfPieces() * 2;

		this.state = state;

		byColour.clear();

		state.forEach(p -> addByColour(p));

		setStateImpl(state);
	}

	private void addByColour(Piece piece) {
		List<Piece> list = byColour.get(piece.getColour());
		if (list == null) {
			list = new ArrayList<>();
			byColour.put(piece.getColour(), list);
		}
		list.add(piece);
	}

	/**
	 * Implement in subclasses to capture state change from
	 * {@link #setState(List)}.
	 *
	 * @param state
	 *          the new state impl
	 */
	protected abstract void setStateImpl(List<Piece> state);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getState(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PieceState> getState(int pieceColour) {
		return byColour.containsKey(pieceColour) ? createState(byColour.get(pieceColour)) : Collections.EMPTY_LIST;
	}

	private List<PieceState> createState(List<Piece> list) {
		List<PieceState> state = new ArrayList<>();

		list.forEach(p -> addToState(p, state));

		return state;
	}

	private void addToState(Piece p, List<PieceState> list) {
		PieceState ps = new PieceState();

		ps.setColour(p.getColour());
		ps.setKinged(p.isKinged());
		ps.setPosition(p.getPosition());
		ps.setNumber(p.getNumber());

		list.add(ps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getWinningColour()
	 */
	@Override
	public int getWinningColour() {
		return winningColour;
	}

	/**
	 * Sets the winning colour.
	 *
	 * @param winningColour
	 *          the new winning colour
	 */
	public void setWinningColour(int winningColour) {
		this.winningColour = winningColour;
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

	/**
	 * Sets the start time.
	 *
	 * @param startTime
	 *          the new start time
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
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

	/**
	 * Sets the end time.
	 *
	 * @param endTime
	 *          the new end time
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
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

	/**
	 * Sets the game state.
	 *
	 * @param gameState
	 *          the new game state
	 */
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	/**
	 * Sets the game's state, applying any state switching logic required. On
	 * {@link GameState#FINISHED} the {@link #setEndTime(long)} is set and on
	 * {@link GameState#STARTED} {@link #setStartTime(long)} is invoked.
	 *
	 * @param gameState
	 *          the new game state internal
	 */
	@SuppressWarnings("incomplete-switch")
	protected void setGameStateInternal(GameState gameState) {
		setGameState(gameState);

		switch (gameState) {
		case FINISHED:
			setEndTime(System.currentTimeMillis());
			break;
		case STARTED:
			setStartTime(System.currentTimeMillis());
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#getNextPlayer()
	 */
	@Override
	public int getNextPlayer() {
		return nextPlayer;
	}

	/**
	 * Sets the next player.
	 *
	 * @param nextPlayer
	 *          the new next player
	 */
	public void setNextPlayer(int nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#isDraw()
	 */
	@Override
	public boolean isDraw() {
		return draw;
	}

	/**
	 * Sets the draw.
	 *
	 * @param draw
	 *          the new draw
	 */
	public void setDraw(boolean draw) {
		this.draw = draw;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.api.CheckerGame#draw()
	 */
	@Override
	public void draw() {
		setGameStateInternal(GameState.FINISHED);
		setDraw(true);
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
	 * True by default.
	 *
	 * @return true, if is auto end turn
	 */
	@Override
	public boolean isAutoEndTurn() {
		return autoEndTurn;
	}

	/**
	 * Sets the auto end turn.
	 *
	 * @param autoEndTurn
	 *          the new auto end turn
	 */
	public void setAutoEndTurn(boolean autoEndTurn) {
		this.autoEndTurn = autoEndTurn;
	}

}
