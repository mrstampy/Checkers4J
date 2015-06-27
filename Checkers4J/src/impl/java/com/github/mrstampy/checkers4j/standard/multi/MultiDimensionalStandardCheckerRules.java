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

import java.util.concurrent.atomic.AtomicInteger;

import com.github.mrstampy.checkers4j.standard.StandardCheckerRules;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiDimensionalStandardCheckerRules.
 */
public class MultiDimensionalStandardCheckerRules extends StandardCheckerRules {

	private static final long serialVersionUID = 3658691995074812524L;

	private AtomicInteger pieceNumberOffset = new AtomicInteger();
	private int numBoards;

	/**
	 * Instantiates a new multi dimensional standard checker rules.
	 *
	 * @param numBoards
	 *          the num boards
	 */
	public MultiDimensionalStandardCheckerRules(int numBoards) {
		this(StandardCheckerRules.STD_WIDTH, StandardCheckerRules.STD_HEIGHT);
	}

	/**
	 * Instantiates a new multi dimensional standard checker rules.
	 *
	 * @param boardWidth
	 *          the board width
	 * @param boardHeight
	 *          the board height
	 */
	public MultiDimensionalStandardCheckerRules(int boardWidth, int boardHeight) {
		this(boardWidth, boardHeight, 2);
	}

	/**
	 * Instantiates a new multi dimensional standard checker rules.
	 *
	 * @param numBoards
	 *          the num boards
	 * @param boardWidth
	 *          the board width
	 * @param boardHeight
	 *          the board height
	 */
	public MultiDimensionalStandardCheckerRules(int numBoards, int boardWidth, int boardHeight) {
		super(boardWidth, boardHeight);
		setNumBoards(numBoards);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.standard.StandardCheckerRules#
	 * getPieceNumberOffset()
	 */
	public int getPieceNumberOffset() {
		return pieceNumberOffset.getAndIncrement();
	}

	/**
	 * Gets the num boards.
	 *
	 * @return the num boards
	 */
	public int getNumBoards() {
		return numBoards;
	}

	/**
	 * Sets the num boards.
	 *
	 * @param numBoards
	 *          the new num boards
	 */
	public void setNumBoards(int numBoards) {
		assert numBoards > 1;

		this.numBoards = numBoards;
	}

}
