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

import com.github.mrstampy.checkers4j.api.CheckerRules;

// TODO: Auto-generated Javadoc
/**
 * Represents a piece and its current state.
 */
public class Piece extends PieceState {

	private static final long serialVersionUID = 8571918472139040675L;

	private CheckerRules rules;

	private int order;

	/**
	 * Creates a coloured, numbered piece using the specified rules.
	 *
	 * @param rules
	 *          the rules
	 * @param colour
	 *          the colour
	 * @param number
	 *          the number, sequential starting at 1, unique to a game
	 * @param order
	 *          the order, sequential starting at 1, unique to a board and colour
	 */
	public Piece(CheckerRules rules, int colour, int number, int order) {
		setRules(rules);
		setColour(colour);
		setNumber(number);
		setOrder(order);

		setPosition(rules.getStartPosition(colour, order));
	}

	/**
	 * Returns true if the piece can move in the direction from its current
	 * position to the value specified. Direction validity only.
	 *
	 * @param toPosition
	 *          the to position
	 * @return true, if successful
	 */
	public boolean directionCheck(int toPosition) {
		return getRules().directionCheck(this, toPosition);
	}

	/**
	 * Sets the piece's position to -1, out of play.
	 */
	public void jumped() {
		setPosition(-1);
	}

	/**
	 * Returns true if jumped.
	 *
	 * @return true, if is jumped
	 */
	public boolean isJumped() {
		return getPosition() == -1;
	}

	/**
	 * Sets this piece's number.
	 *
	 * @param number
	 *          the new number
	 */
	public void setNumber(int number) {
		assert getRules().isValidPieceNumber(number);

		super.setNumber(number);
	}

	/**
	 * Returns the colour name of this piece.
	 *
	 * @return the colour name
	 */
	public String getColourName() {
		return getRules().toColourName(getColour());
	}

	/**
	 * Sets the colour of this piece.
	 *
	 * @param colour
	 *          the new colour
	 */
	public void setColour(int colour) {
		assert getRules().isValidPieceColour(colour);

		super.setColour(colour);
	}

	/**
	 * Sets the position of this piece.
	 *
	 * @param position
	 *          the new position
	 */
	public void setPosition(int position) {
		assert getRules().isValidPosition(position);

		super.setPosition(position);
	}

	/**
	 * Returns the rules this class was instantiated with.
	 *
	 * @return the rules
	 */
	public CheckerRules getRules() {
		return rules;
	}

	/**
	 * Sets the rules.
	 *
	 * @param rules
	 *          the new rules
	 */
	protected void setRules(CheckerRules rules) {
		assert rules != null;

		this.rules = rules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getColourName() + "-" + getNumber() + (isJumped() ? " jumped" : " at position " + getPosition());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.checkers4j.PieceState#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof Piece ? super.equals(o) : false;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order.
	 *
	 * @param order
	 *          the new order
	 */
	public void setOrder(int order) {
		this.order = order;
	}

}
