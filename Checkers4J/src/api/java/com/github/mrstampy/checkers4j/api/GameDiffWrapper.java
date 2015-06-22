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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mrstampy.checkers4j.PieceState;

// TODO: Auto-generated Javadoc
/**
 * Wrapper for a game which if used for {@link #move(int, int, int)}s can return
 * only the diff from the move if {@link #isReturnDiff()} is true. If false then
 * {@link #move(int, int, int)} behaves as
 * {@link CheckerGame#move(int, int, int)}.
 * 
 * @author burton
 *
 */
public class GameDiffWrapper {

	private CheckerGame checkerGame;

	@SuppressWarnings("unchecked")
	private List<PieceState> currentState = Collections.EMPTY_LIST;

	private boolean returnDiff = true;

	/**
	 * Gets the checker game.
	 *
	 * @return the checker game
	 */
	public CheckerGame getCheckerGame() {
		return checkerGame;
	}

	/**
	 * Sets the checker game.
	 *
	 * @param checkerGame
	 *          the new checker game
	 */
	public void setCheckerGame(CheckerGame checkerGame) {
		this.checkerGame = checkerGame;
	}

	/**
	 * Returns the full current state. Has the advantage over
	 * {@link CheckerGame#getState()} implementations which generate the list on
	 * each call.
	 *
	 * @return the current state
	 */
	public List<PieceState> getCurrentState() {
		return currentState;
	}

	/**
	 * Checks if is return diff.
	 *
	 * @return true, if is return diff
	 */
	public boolean isReturnDiff() {
		return returnDiff;
	}

	/**
	 * Sets the return diff.
	 *
	 * @param returnDiff
	 *          the new return diff
	 */
	public void setReturnDiff(boolean returnDiff) {
		this.returnDiff = returnDiff;
	}

	/**
	 * Move.
	 *
	 * @param pieceColour
	 *          the piece colour
	 * @param pieceNumber
	 *          the piece number
	 * @param toPosition
	 *          the to position
	 * @return the list
	 */
	public List<PieceState> move(int pieceColour, int pieceNumber, int toPosition) {
		List<PieceState> newState = getCheckerGame().move(pieceColour, pieceNumber, toPosition);

		return setCurrentState(newState);
	}

	private List<PieceState> setCurrentState(List<PieceState> newState) {
		List<PieceState> diff = getDiff(newState, this.currentState);
		this.currentState = newState;
		return diff;
	}

	private List<PieceState> getDiff(List<PieceState> newState, List<PieceState> oldState) {
		if (!isReturnDiff()) return newState;

		List<PieceState> diff = new ArrayList<>();

		for (PieceState ps : newState) {
			PieceState old = getOld(ps, oldState);

			if (unchanged(ps, old)) continue;

			diff.add(ps);
		}

		return diff;
	}

	private boolean unchanged(PieceState ps, PieceState old) {
		return old.getPosition() == ps.getPosition() && old.isKinged() == ps.isKinged();
	}

	private PieceState getOld(PieceState ps, List<PieceState> oldState) {
		for (PieceState old : oldState) {
			if (samePiece(ps, old)) return old;
		}

		throw new IllegalStateException("Should never get here...");
	}

	private boolean samePiece(PieceState ps, PieceState old) {
		return old.getColour() == ps.getColour() && old.getNumber() == ps.getNumber();
	}

}
