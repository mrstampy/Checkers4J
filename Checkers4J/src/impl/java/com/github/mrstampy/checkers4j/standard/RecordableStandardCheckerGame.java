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

import java.util.List;

import com.github.mrstampy.checkers4j.PieceState;
import com.github.mrstampy.checkers4j.api.recorder.CheckerGameRecorder;
import com.github.mrstampy.checkers4j.api.recorder.Move;
import com.github.mrstampy.checkers4j.api.recorder.RecordableCheckerGame;
import com.github.mrstampy.checkers4j.ex.CheckersStateException;

// TODO: Auto-generated Javadoc
/**
 * A {@link StandardCheckerGame} which records moves, made available by
 * {@link RecordableCheckerGame#getMoves()}.
 */
public class RecordableStandardCheckerGame extends StandardCheckerGame implements
		RecordableCheckerGame<StandardCheckerRules> {

	private static final long serialVersionUID = 5238272702671148986L;

	/** The recorder. */
	protected CheckerGameRecorder recorder = new CheckerGameRecorder();

	/**
	 * Instantiates a new recordable standard checker game.
	 */
	public RecordableStandardCheckerGame() {
		super();
	}

	/**
	 * Instantiates a new recordable standard checker game.
	 *
	 * @param checkerRules
	 *          the checker rules
	 */
	public RecordableStandardCheckerGame(StandardCheckerRules checkerRules) {
		super(checkerRules);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.AbstractCheckerGame#move(int, int, int)
	 */
	@Override
	public List<PieceState> move(int pieceColour, int pieceNumber, int toPosition) throws CheckersStateException {
		recorder.addMove(getGameId(), pieceColour, pieceNumber, toPosition);

		return super.move(pieceColour, pieceNumber, toPosition);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.checkers4j.standard.StandardCheckerGame#getGameName()
	 */
	@Override
	public String getGameName() {
		return "Recordable " + GAME_NAME;
	}

}
