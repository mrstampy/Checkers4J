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

import com.github.mrstampy.checkers4j.api.CheckerGame;
import com.github.mrstampy.checkers4j.api.CheckerRules;

// TODO: Auto-generated Javadoc
/**
 * Defines the interface for games which record moves.
 *
 * @author burton
 * @param <RULES>
 *          the generic type
 * @see CheckerGameRecorder
 */
public interface RecordableCheckerGame<RULES extends CheckerRules> extends CheckerGame<RULES>, Recordable {
}
