/** 
 *  Copyright (C) 2012  Just Do One More
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jdom.axis.and.allies.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jdom.axis.and.allies.model.Game;
import com.jdom.axis.and.allies.model.GameModelImpl;
import com.jdom.axis.and.allies.model.GameSerializer;
import com.jdom.axis.and.allies.model.Observer;
import com.jdom.axis.and.allies.model.PlayableCountry;
import com.jdom.axis.and.allies.model.Purchasable;
import com.jdom.axis.and.allies.model.Purchase;
import com.jdom.axis.and.allies.model.GameModelImpl.GameManagerStrategy;

@RunWith(JMock.class)
public class GameModelImplTest {
	private final Mockery mockery = new JUnit4Mockery();
	private final Game game = GameUtil.getGame();
	private final GameModelImpl model = new GameModelImpl(game, null,
			new GameSerializer().serialize(game));

	@Test
	public void testGettingPlayableCountries() {
		List<PlayableCountry> playableCountries = model.getPlayableCountries();

		assertNotNull(playableCountries);
		assertFalse(playableCountries.isEmpty());
	}

	@Test
	public void testGetActivePlayerReturnsOrderInSet() {
		List<String> turnOrder = game.turnOrder;

		for (String currentTurn : turnOrder) {
			assertEquals(currentTurn, model.getActivePlayer().toString());
			model.turnOver();
		}
	}

	@Test
	public void testTurnOverWillLoopActivePlayerBackToBeginning() {
		List<String> turnOrder = game.turnOrder;

		for (int i = 0; i < turnOrder.size(); i++) {
			model.turnOver();
		}

		assertEquals(game.playableCountryMap.get(turnOrder.get(0)),
				game.activePlayer);
	}

	@Test
	public void testSavingGameCallsStrategy() {
		final String name = "testName";
		final GameManagerStrategy mockStrategy = mockery
				.mock(GameManagerStrategy.class);
		model.gameManagerStrategy = mockStrategy;

		mockery.checking(new Expectations() {
			{
				oneOf(mockStrategy).saveGame(model.game, name);

			}
		});

		model.saveGame(name);
	}

	@Test
	public void testLoadingGameNotifiesObservers() {
		final String name = "testName";
		final GameManagerStrategy mockGameStrategy = mockery
				.mock(GameManagerStrategy.class);
		final Observer observer = mockery.mock(Observer.class);
		model.gameManagerStrategy = mockGameStrategy;

		mockery.checking(new Expectations() {
			{
				oneOf(mockGameStrategy).loadGame(name);
				exactly(2).of(observer).update(model);
			}
		});

		model.registerObserver(observer);
		model.loadGame(name);
	}

	@Test
	public void testTurnOverNotifiesObservers() {
		final Observer observer = mockery.mock(Observer.class);

		mockery.checking(new Expectations() {
			{
				exactly(2).of(observer).update(model);
			}
		});

		model.registerObserver(observer);
		model.turnOver();
	}

	@Test
	public void testPurchaseUnitsNotifiesObservers() {
		final Observer observer = mockery.mock(Observer.class);

		mockery.checking(new Expectations() {
			{
				exactly(2).of(observer).update(model);
			}
		});

		model.registerObserver(observer);
		model.turnOver();
	}

	@Test
	public void testSeizingTerritoriesActsUponPlayableCountries() {
		PlayableCountry currentOwner = model.game.playableCountryMap
				.get("United States");
		assertTrue(currentOwner.getOwnedTerritories().contains(
				game.territoryMap.get("United States")));
		PlayableCountry newOwner = model.game.playableCountryMap.get("Germany");
		assertFalse(newOwner.getOwnedTerritories().contains(
				game.territoryMap.get("United States")));

		model.seizeTerritories("Germany", "United States");

		assertTrue(newOwner.getOwnedTerritories().contains(
				game.territoryMap.get("United States")));
		assertFalse(currentOwner.getOwnedTerritories().contains(
				game.territoryMap.get("United States")));
	}

	@Test
	public void testPurchasingUnitsActsUponPlayableCountries() {
		PlayableCountry currentOwner = model.game.playableCountryMap
				.get("Germany");
		int currentIpcs = currentOwner.getIpcs();

		model.purchase("Germany",
				Arrays.asList(new Purchase(Purchasable.INFANTRY, 2)));

		assertEquals(currentIpcs - Purchasable.INFANTRY.getIpcValue()
				- Purchasable.INFANTRY.getIpcValue(), currentOwner.getIpcs());
	}

	@Test
	public void testGettingPurchasablesReturnsGamePurchasables() {
		final Purchasable purchasable = mockery.mock(Purchasable.class, "one");
		final Purchasable purchasable2 = mockery.mock(Purchasable.class, "two");

		mockery.checking(new Expectations() {
			{
				allowing(purchasable);
				allowing(purchasable2);
			}
		});

		model.game.purchasableMap.put("one", purchasable);
		model.game.purchasableMap.put("two", purchasable);

		SortedSet<Purchasable> purchasables = model.getPurchasables();

		assertTrue(purchasables.contains(purchasable));
		assertTrue(purchasables.contains(purchasable2));
	}

	@Test
	public void testNewGameStartsANewGame() {
		Game originalGame = model.game;
		model.newGame();
		assertNotSame("The game does not appear to be new!", originalGame,
				model.game);
	}

	@Test
	public void testSettingErrorsNotifiesObservers() {
		final Observer observer = mockery.mock(Observer.class);

		mockery.checking(new Expectations() {
			{
				exactly(2).of(observer).update(model);
			}
		});

		model.registerObserver(observer);
		model.setErrors(Arrays.asList("One error.", "Two error."));
	}

	@Test
	public void testUndoNotifiesObservers() {
		final Observer observer = mockery.mock(Observer.class);

		mockery.checking(new Expectations() {
			{
				exactly(2).of(observer).update(model);
			}
		});

		model.undoGame = new GameSerializer().serialize(game);
		model.registerObserver(observer);
		model.undo();
	}

	@Test
	public void testRestoresStateWhenUndoIsCalled() {
		model.turnOver();

		PlayableCountry currentPlayer = model.getActivePlayer();

		model.newGame();

		assertFalse(currentPlayer.toString().equals(
				model.getGame().activePlayer.toString()));

		model.undo();

		assertTrue(currentPlayer.toString().equals(
				model.getGame().activePlayer.toString()));
	}

	@Test
	public void testUndoIsUnavailableOnNewGame() {
		assertFalse(model.isUndoAvailable());
	}

	@Test
	public void testUndoIsAvailableOnActionOccuring() {
		model.seizeTerritories("Germany", "United States");

		assertTrue(model.isUndoAvailable());
	}

	@Test
	public void testUndoRevertsLastAction() {
		PlayableCountry germany = model.game.playableCountryMap.get("Germany");
		int originalNumberOfTerritoriesOwned = germany.getOwnedTerritories()
				.size();

		model.seizeTerritories(germany.toString(), "United States");

		assertTrue(germany.getOwnedTerritories().size() > originalNumberOfTerritoriesOwned);

		model.undo();

		assertFalse(model.game.playableCountryMap.get("Germany")
				.getOwnedTerritories().size() > originalNumberOfTerritoriesOwned);
	}

	@Test
	public void testGettingErrorsClearsErrors() {
		model.setErrors(Arrays.asList("One error.", "Two error."));
		assertEquals(2, model.getErrors().size());
		assertTrue(model.getErrors().isEmpty());
	}
}
