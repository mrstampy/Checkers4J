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

import static com.github.mrstampy.checkers4j.standard.StandardCheckerRules.BLACK_NUM;
import static com.github.mrstampy.checkers4j.standard.StandardCheckerRules.WHITE_NUM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import com.github.mrstampy.checkers4j.Piece;
import com.github.mrstampy.checkers4j.PieceState;
import com.github.mrstampy.checkers4j.api.CheckerGame;
import com.github.mrstampy.checkers4j.api.CheckerRules;

// TODO: Auto-generated Javadoc
/**
 * The Class StandardCheckerGameTest.
 */
public class StandardCheckerGameTest {

	private StandardCheckerGame game;
	private List<PieceState> initial;

	private Random rand = new Random(System.nanoTime());

	private ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();

	/**
	 * Before.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Before
	public void before() throws Exception {
		game = new StandardCheckerGame();
		initial = game.getState();
		validateInitialState();
	}

	private void validateInitialState() {
		initial.stream().forEach(e -> validateInitialState(e, game.getRules()));
	}

	private void validateInitialState(PieceState ps, CheckerRules rules) {
		int expected = rules.getStartPosition(ps.getColour(), ps.getNumber());

		assertEquals(expected, ps.getPosition());
	}

	/**
	 * Test end of turn.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testEndOfTurn() throws Exception {
		List<Piece> pieces = game.getFullState();

		setEndOfTurnState(pieces);
		game.setState(pieces);

		game.move(BLACK_NUM, 1, 24);
		assertEquals(BLACK_NUM, game.hasTurn());

		game.move(BLACK_NUM, 1, 10);
		assertEquals(-1, game.hasTurn());
	}

	/*
	 * white 1 @ 2,1 white 2 @ 4,1 black 1 kinged @ 5,2
	 */
	private void setEndOfTurnState(List<Piece> pieces) {
		pieces.forEach(p -> setEndOfTurnState(p));
	}

	private void setEndOfTurnState(Piece p) {
		switch (p.getColour()) {
		case WHITE_NUM:
			switch (p.getNumber()) {
			case 1:
				p.setPosition(17);
				break;
			case 2:
				p.setPosition(33);
				break;
			default:
				p.jumped();
				break;
			}
			break;
		case BLACK_NUM:
			switch (p.getNumber()) {
			case 1:
				p.setPosition(42);
				p.setKinged(true);
				break;
			default:
				p.jumped();
				break;
			}
			break;
		}
	}

	/**
	 * Test start.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testStart() throws Exception {
		assertEquals(-1, game.getWinningColour());
		assertEquals(-1, game.getLastPlayer());
		assertEquals(-1, game.hasTurn());

		game.setAutoEndTurn(false);

		for (int i = 0; i < 63; i++) {
			failedMove(WHITE_NUM, 1, i, "Illegal move");
		}

		assertPlayers(WHITE_NUM, BLACK_NUM);
		assertEquals(WHITE_NUM, game.hasTurn());

		failedMove(BLACK_NUM, 1, 33, "Illegal claim");

		List<PieceState> state = game.move(WHITE_NUM, 9, 24);

		PieceState ps = getPieceState(state, 9, 24);
		assertNotNull(ps);

		failedMove(BLACK_NUM, 1, 33, "Illegal claim");

		assertEquals(WHITE_NUM, game.hasTurn());
		game.endTurn(WHITE_NUM);
		assertEquals(-1, game.hasTurn());
		assertPlayers(WHITE_NUM, BLACK_NUM);

		failedMove(BLACK_NUM, 1, 32, "Illegal to position");
		assertPlayers(BLACK_NUM, WHITE_NUM);
		assertEquals(BLACK_NUM, game.hasTurn());

		state = game.move(BLACK_NUM, 1, 33);

		ps = getPieceState(state, 1, 33);
		assertNotNull(ps);
		assertPlayers(BLACK_NUM, WHITE_NUM);
	}

	/**
	 * Test10 x10.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void test10X10() throws Exception {
		StandardCheckerGame scg = new StandardCheckerGame(new StandardCheckerRules(10, 10));

		initial = scg.getState();
		initial.stream().forEach(e -> validateInitialState(e, scg.getRules()));

		testGame(scg);
	}

	/**
	 * Test random game.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void testRandomGame() throws Exception {
		for (int i = 0; i < 10; i++) {
			testGame(game);
			before();
		}
	}

	private void testGame(CheckerGame game) {
		AtomicBoolean b = new AtomicBoolean(true);

		Future<?> f = svc.schedule(() -> b.set(false), 2, TimeUnit.SECONDS);

		int colour = BLACK_NUM;
		while (CheckerGame.GameState.FINISHED != game.getGameState() && b.get()) {
			// System.out.println("Making move for colour " + colour + ", game state "
			// + game.getGameState());
			List<PieceState> list = makeMove(colour, game);
			// list.stream().forEach(e -> print(e));
			game.endTurn(colour);
			colour = game.getNextPlayer();
		}

		if (b.get()) {
			f.cancel(true);
			System.out.println("Game won by " + game.getRules().toColourName(game.getWinningColour()));
		} else {
			game.draw();
			System.out.println("Draw");
		}

		assertTrue(game.getStartTime() > 0);
		assertTrue(game.getEndTime() > 0);
	}

	private void print(PieceState e, CheckerGame game) {
		System.out.println(game.getRules().toColourName(e.getColour()) + ":" + e.getNumber() + ", pos=" + e.getPosition()
				+ ", kinged=" + e.isKinged());
	}

	private List<PieceState> makeMove(int pieceColour, CheckerGame game) {
		while (true) {
			int pieceNumber = rand.nextInt(game.getRules().getNumberOfPieces()) + 1;

			for (int i = 0; i < game.getRules().getBoardHeight() * game.getRules().getBoardWidth() - 1; i++) {
				try {
					return game.move(pieceColour, pieceNumber, i);
				} catch (Exception expected) {
				}
			}
		}
	}

	private void assertPlayers(int lastPlayer, int nextPlayer) {
		assertEquals(lastPlayer, game.getLastPlayer());
		assertEquals(nextPlayer, game.getNextPlayer());
	}

	private void failedMove(int pieceColour, int pieceNumber, int toPosition, String failMsg) {
		try {
			game.move(pieceColour, pieceNumber, toPosition);
			fail(failMsg);
		} catch (Exception expected) {
		}
	}

	private PieceState getPieceState(List<PieceState> state, int number, int position) {
		for (PieceState ps : state) {
			if (ps.getNumber() == number && ps.getPosition() == position) return ps;
		}

		return null;
	}
}
