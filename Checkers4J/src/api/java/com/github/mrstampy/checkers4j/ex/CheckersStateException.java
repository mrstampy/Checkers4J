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
package com.github.mrstampy.checkers4j.ex;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckersStateException.
 */
public class CheckersStateException extends Exception {

	private static final long serialVersionUID = -8967632739094953416L;

	/**
	 * The Enum ErrorState.
	 */
	public enum ErrorState {

		/** The illegal move. */
		ILLEGAL_MOVE,
		/** The illegal jump. */
		ILLEGAL_JUMP,
		/** The out of turn. */
		OUT_OF_TURN,
		/** The illegal value. */
		ILLEGAL_VALUE,
		/** The illegal state. */
		ILLEGAL_STATE;
	}

	private int colour;
	private int number;
	private int toPosition;
	private ErrorState errorState;

	/**
	 * Instantiates a new checkers state exception.
	 *
	 * @param errorState
	 *          the error state
	 * @param msg
	 *          the msg
	 */
	public CheckersStateException(ErrorState errorState, String msg) {
		this(-1, -1, errorState, msg);
	}

	/**
	 * Instantiates a new checkers state exception.
	 *
	 * @param colour
	 *          the colour
	 * @param number
	 *          the number
	 * @param errorState
	 *          the error state
	 * @param msg
	 *          the msg
	 */
	public CheckersStateException(int colour, int number, ErrorState errorState, String msg) {
		this(colour, number, -1, errorState, msg);
	}

	/**
	 * Instantiates a new checkers state exception.
	 *
	 * @param colour
	 *          the colour
	 * @param number
	 *          the number
	 * @param toPosition
	 *          the to position
	 * @param errorState
	 *          the error state
	 * @param msg
	 *          the msg
	 */
	public CheckersStateException(int colour, int number, int toPosition, ErrorState errorState, String msg) {
		super(msg);

		setColour(colour);
		setToPosition(toPosition);
		setErrorState(errorState);
	}

	/**
	 * Gets the to position.
	 *
	 * @return the to position
	 */
	public int getToPosition() {
		return toPosition;
	}

	/**
	 * Sets the to position.
	 *
	 * @param toPosition
	 *          the new to position
	 */
	public void setToPosition(int toPosition) {
		this.toPosition = toPosition;
	}

	/**
	 * Gets the error state.
	 *
	 * @return the error state
	 */
	public ErrorState getErrorState() {
		return errorState;
	}

	/**
	 * Sets the error state.
	 *
	 * @param errorState
	 *          the new error state
	 */
	public void setErrorState(ErrorState errorState) {
		this.errorState = errorState;
	}

	/**
	 * Gets the colour.
	 *
	 * @return the colour
	 */
	public int getColour() {
		return colour;
	}

	/**
	 * Sets the colour.
	 *
	 * @param colour
	 *          the new colour
	 */
	public void setColour(int colour) {
		this.colour = colour;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 *
	 * @param number
	 *          the new number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

}
