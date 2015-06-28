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
import java.util.List;

import com.github.mrstampy.checkers4j.Piece;
import com.github.mrstampy.checkers4j.PieceState;
import com.github.mrstampy.checkers4j.ex.CheckersStateException;

// TODO: Auto-generated Javadoc
/**
 * The Interface CheckerGame.
 */
public interface CheckerGame<RULES extends CheckerRules> extends Serializable {

	/**
	 * Id for uniquely identifying a specific game.
	 *
	 * @return the game id
	 */
	long getGameId();

	/**
	 * Must be invoked before play can begin.
	 *
	 * @param rules
	 *          the rules
	 */
	void initialize(RULES rules);

	/**
	 * Must be invoked prior to {@link #move(int, int, int)}ing.
	 *
	 * @param pieceColour
	 *          the piece colour
	 */
	void beginTurn(int pieceColour);

	/**
	 * Moves the specified piece to the new position. Should fail hard by throwing
	 * {@link RuntimeException}s if the move is in error.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @param pieceNumber
	 *          the piece number
	 * @param toPosition
	 *          the to position
	 * @return the list
	 * @throws CheckersStateException
	 *           the checkers state exception
	 */
	List<PieceState> move(int pieceColour, int pieceNumber, int toPosition) throws CheckersStateException;

	/**
	 * Must be invoked after all {@link #move(int, int, int)}s have been performed
	 * to allow the {@link #getNextPlayer()} to have their turn.
	 *
	 * @param pieceColour
	 *          the piece colour
	 */
	void endTurn(int pieceColour);

	/**
	 * Returns the colour number of the player who has invoked
	 * {@link #beginTurn(int)} and has yet to invoke {@link #endTurn(int)}.
	 * Returns -1 otherwise.
	 *
	 * @return the int
	 */
	int hasTurn();

	/**
	 * Returns the current state of play.
	 *
	 * @return the state
	 */
	List<PieceState> getState();

	/**
	 * Sets the state to that specified.
	 *
	 * @param state
	 *          the new state
	 */
	void setState(List<Piece> state);

	/**
	 * Returns the current state of the specified colour.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @return the state
	 */
	List<PieceState> getState(int pieceColour);

	/**
	 * Returns the number of the winning colour, -1 otherwise.
	 *
	 * @return the winning colour
	 */
	int getWinningColour();

	/**
	 * Returns the start time of the game, -1 if not started.
	 *
	 * @return the start time
	 */
	long getStartTime();

	/**
	 * Returns the end time of the game, -1 if not finished.
	 *
	 * @return the end time
	 */
	long getEndTime();

	/**
	 * Game State enum.
	 *
	 * @author burton
	 */
	enum GameState {

		/** The stateless. */
		STATELESS,
		/** The initialized. */
		INITIALIZED,
		/** The started. */
		STARTED,
		/** The finished. */
		FINISHED;
	}

	/**
	 * Returns the current state of the game play, internally managed in the
	 * implementation.
	 *
	 * @return the game state
	 */
	GameState getGameState();

	/**
	 * Returns the name of the checkers implementation.
	 *
	 * @return the game name
	 */
	String getGameName();

	/**
	 * Returns the next player.
	 *
	 * @return the next player
	 */
	int getNextPlayer();

	/**
	 * Returns the last player. Will equal {@link #hasTurn()} if
	 * {@link #beginTurn(int)} has been called prior to {@link #endTurn(int)}.
	 *
	 * @return the last player
	 */
	int getLastPlayer();

	/**
	 * Returns true if the specified colour can currently move. Invoked on turn
	 * end for all other colours. If false then the colour which just moved is the
	 * winner.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @return true, if successful
	 */
	boolean canMove(int pieceColour);

	/**
	 * Returns true if the specified piece is able to make a move.
	 *
	 * @param piece
	 *          the piece
	 * @return true, if successful
	 */
	boolean canMove(Piece piece);

	/**
	 * Invoked when pieces can move but no end to the game can be had. Must be
	 * agreed by both players before invoking.
	 */
	void draw();

	/**
	 * Returns true if {@link #draw()} was invoked.
	 *
	 * @return true, if is draw
	 */
	boolean isDraw();

	/**
	 * Returns the rules for this game.
	 *
	 * @return the rules
	 */
	RULES getRules();

	/**
	 * Returns true if a player's turn is to automatically end after a move. If
	 * true then the game will automatically end a turn when no more moves can be
	 * instantiator.
	 *
	 * @return true, if is auto end turn
	 */
	boolean isAutoEndTurn();
}
