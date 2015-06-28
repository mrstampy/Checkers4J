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

import java.util.List;

import com.github.mrstampy.checkers4j.PieceState;
import com.github.mrstampy.checkers4j.api.recorder.CheckerGameRecorder;
import com.github.mrstampy.checkers4j.api.recorder.Move;
import com.github.mrstampy.checkers4j.api.recorder.RecordableCheckerGame;
import com.github.mrstampy.checkers4j.ex.CheckersStateException;

// TODO: Auto-generated Javadoc
/**
 * A {@link ThreeDStandardCheckerGame} which records moves, made available via
 * {@link RecordableCheckerGame#getMoves()}.
 */
public class RecordableThreeDStandardCheckerGame extends ThreeDStandardCheckerGame implements
		RecordableCheckerGame<ThreeDStandardCheckerRules> {

	private static final long serialVersionUID = -3454607901440192044L;

	/** The recorder. */
	protected CheckerGameRecorder recorder = new CheckerGameRecorder();

	/**
	 * Instantiates a new recordable three d standard checker game.
	 */
	public RecordableThreeDStandardCheckerGame() {
		super();
	}

	/**
	 * Instantiates a new recordable three d standard checker game.
	 *
	 * @param boardWidth
	 *          the board width
	 * @param boardHeight
	 *          the board height
	 */
	public RecordableThreeDStandardCheckerGame(int boardWidth, int boardHeight) {
		super(boardWidth, boardHeight);
	}

	/**
	 * Instantiates a new recordable three d standard checker game.
	 *
	 * @param numBoards
	 *          the num boards
	 */
	public RecordableThreeDStandardCheckerGame(int numBoards) {
		super(numBoards);
	}

	/**
	 * Instantiates a new recordable three d standard checker game.
	 *
	 * @param numBoards
	 *          the num boards
	 * @param boardWidth
	 *          the board width
	 * @param boardHeight
	 *          the board height
	 */
	public RecordableThreeDStandardCheckerGame(int numBoards, int boardWidth, int boardHeight) {
		super(numBoards, boardWidth, boardHeight);
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
	 * @see
	 * com.github.mrstampy.checkers4j.api.recorder.RecordableCheckerGame#getMoves
	 * ()
	 */
	@Override
	public List<Move> getMoves() {
		return recorder.getMoves();
	}

}
