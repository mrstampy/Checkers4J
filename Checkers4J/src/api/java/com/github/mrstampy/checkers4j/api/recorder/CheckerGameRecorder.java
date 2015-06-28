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
package com.github.mrstampy.checkers4j.api.recorder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Convenience class to record moves.
 * 
 * @see RecordableCheckerGame
 */
public class CheckerGameRecorder implements Serializable {

	private static final long serialVersionUID = -3178801099705113885L;

	private List<Move> moves = new ArrayList<>();

	/**
	 * Adds the move.
	 *
	 * @param gameId
	 *          the game id
	 * @param pieceColour
	 *          the piece colour
	 * @param pieceNumber
	 *          the piece number
	 * @param toPosition
	 *          the to position
	 */
	public void addMove(long gameId, int pieceColour, int pieceNumber, int toPosition) {
		moves.add(new Move(gameId, pieceColour, pieceNumber, toPosition));
	}

	/**
	 * Gets the moves.
	 *
	 * @return the moves
	 */
	public List<Move> getMoves() {
		return moves;
	}

	/**
	 * Clear.
	 */
	public void clear() {
		moves.clear();
	}
}
