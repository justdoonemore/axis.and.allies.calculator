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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.jdom.axis.and.allies.model.GameModel;
import com.jdom.axis.and.allies.model.GameModelImpl;
import com.jdom.axis.and.allies.model.PlayableCountry;
import com.jdom.axis.and.allies.model.Purchasable;
import com.jdom.axis.and.allies.model.Purchase;
import com.jdom.axis.and.allies.model.Territory;
import com.jdom.axis.and.allies.model.PlayableCountry.PlayableCountryImpl;
import com.jdom.axis.and.allies.model.Territory.TerritoryImpl;

public class PlayableCountryImplTest {
	public class MockGameModel extends GameModelImpl {

		private final List<PlayableCountry> list;

		public MockGameModel(List<PlayableCountry> list2) {
			super(null, null, null);

			this.list = list2;
		}

		@Override
		public List<PlayableCountry> getPlayableCountries() {
			return list;
		}
	}

	@Test
	public void testMovingOwnedTerritoryToDifferentCountryTransfersOwnership() {
		Territory territory = new MockTerritory();
		PlayableCountry originalOwner = new MockPlayableCountry(territory);
		PlayableCountry newOwner = new MockPlayableCountry();

		assertTrue(originalOwner.getOwnedTerritories().contains(territory));
		assertFalse(newOwner.getOwnedTerritories().contains(territory));

		newOwner.seizeTerritory(originalOwner, territory);

		assertFalse(originalOwner.getOwnedTerritories().contains(territory));
		assertTrue(newOwner.getOwnedTerritories().contains(territory));
	}

	@Test
	public void testSeizingTerritoryTransfersIpcPoints() {
		Territory territory = new MockTerritory();
		PlayableCountryImpl originalOwner = new MockPlayableCountry(territory);
		PlayableCountryImpl newOwner = new MockPlayableCountry();

		int ipcsForOriginalOwner = originalOwner.getIpcs();
		int ipcsForNewOwner = newOwner.getIpcs();
		// Set both ipc values to zero, then call turn over on both since
		// territory
		// IPCs are already added up at the end of each turn
		originalOwner.ipcs = 0;
		newOwner.ipcs = 0;

		newOwner.seizeTerritory(originalOwner, territory);
		originalOwner.turnOver();
		newOwner.turnOver();

		assertEquals(ipcsForOriginalOwner - territory.getIpcValue(),
				originalOwner.getIpcs());
		assertEquals(ipcsForNewOwner + territory.getIpcValue(),
				newOwner.getIpcs());
	}

	@Test
	public void testSeizingCapitalTerritoryTransfersIpcPointsInHand() {
		Territory territory = new MockTerritory();
		PlayableCountryImpl originalOwner = new MockPlayableCountry(territory);
		originalOwner.capitals = new Territory[] { territory };
		PlayableCountryImpl newOwner = new MockPlayableCountry();

		int ipcsForOriginalOwner = originalOwner.getIpcs();
		int ipcsForNewOwner = newOwner.getIpcs();

		newOwner.seizeTerritory(originalOwner, territory);

		assertEquals(0, originalOwner.getIpcs());
		assertEquals(ipcsForNewOwner + ipcsForOriginalOwner, newOwner.getIpcs());
	}

	@Test
	public void testGetIpcsReturnsSumOfCarryOverIpcsAndTerritories() {
		Territory territory = new MockTerritory();
		PlayableCountryImpl country = new MockPlayableCountry(territory);
		int startIpcs = country.ipcs;

		country.turnOver();

		assertEquals(startIpcs + territory.getIpcValue(), country.getIpcs());
	}

	@Test
	public void testPurchasingUnitsSubtractsTheirIpcValue() {
		Territory territory = new MockTerritory();
		PlayableCountryImpl country = new PlayableCountryImpl("Test", territory);
		country.ipcs = 18;

		int currentIpcs = country.getIpcs();

		country.purchaseUnits(Arrays.asList(new Purchase(
				Purchasable.INDUSTRIAL_COMPLEX, 1), new Purchase(
				Purchasable.INFANTRY, 1)));

		assertEquals(currentIpcs - Purchasable.INDUSTRIAL_COMPLEX.getIpcValue()
				- Purchasable.INFANTRY.getIpcValue(), country.getIpcs());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPurchasingUnitsWithoutEnoughIpcsThrowsException() {
		PlayableCountryImpl country = new MockPlayableCountry();
		country.ipcs = 17;

		country.purchaseUnits(Arrays.asList(new Purchase(
				Purchasable.INDUSTRIAL_COMPLEX, 1), new Purchase(
				Purchasable.INFANTRY, 1)));
	}

	@Test
	public void testStrategicBombingRaidOnTerritoryDoesNotLowersItsIpcValue() {
		Territory territoryToBomb = new TerritoryImpl("Test", 3);

		PlayableCountryImpl country = new PlayableCountryImpl("Test",
				new TerritoryImpl("Test", 1));

		int originalIpcs = territoryToBomb.getIpcValue();

		country.strategicBombingRaid(territoryToBomb, 3);

		assertEquals(originalIpcs, territoryToBomb.getIpcValue());
	}

	@Test
	public void testNotOwningCapitalForcesNoIpcsToBeGainedOnTurnOver() {
		TerritoryImpl territory = new TerritoryImpl("Test", 1);
		PlayableCountryImpl country = new PlayableCountryImpl("Test", 0,
				new Territory[] { territory }, new TerritoryImpl(
						"SomeOtherTerritory", 1));

		country.turnOver();

		assertEquals(0, country.getIpcs());
	}

	@Test
	public void testThatAlliesAreCorrectlyDetermined() {
		assertTrue(new PlayableCountryImpl("Soviet Union", new MockTerritory())
				.isAlly());
		assertTrue(new PlayableCountryImpl("United States", new MockTerritory())
				.isAlly());
		assertTrue(new PlayableCountryImpl("United Kingdom",
				new MockTerritory()).isAlly());
	}

	@Test
	public void testThatAxisAreCorrectlyDetermined() {
		assertFalse(new PlayableCountryImpl("Germany", new MockTerritory())
				.isAlly());
		assertFalse(new PlayableCountryImpl("Japan", new MockTerritory())
				.isAlly());
	}

	@Test
	public void testThatReclaimingATeammatesCapitalGoesBackToHim() {

		MockTerritory mockTerritory = new MockTerritory("Karelia S.S.R");
		MockPlayableCountry soviets = new MockPlayableCountry("Soviet Union",
				0, new Territory[] { mockTerritory }, mockTerritory);
		MockPlayableCountry germany = new MockPlayableCountry("Germany",
				new TerritoryImpl("SomePlace", 0));
		MockPlayableCountry usa = new MockPlayableCountry("United States",
				new TerritoryImpl("usa", 1));

		GameModel mockGameModel = new MockGameModel(
				Arrays.<PlayableCountry> asList(soviets, germany, usa));
		germany.setGameModel(mockGameModel);

		germany.setGameModel(mockGameModel);
		germany.seizeTerritory(soviets, mockTerritory);
		assertFalse(soviets.getOwnedTerritories().contains(mockTerritory));
		assertTrue(germany.getOwnedTerritories().contains(mockTerritory));

		usa.setGameModel(mockGameModel);
		usa.seizeTerritory(germany, mockTerritory);

		assertTrue(soviets.getOwnedTerritories().contains(mockTerritory));
		assertFalse(germany.getOwnedTerritories().contains(mockTerritory));
		assertFalse(usa.getOwnedTerritories().contains(mockTerritory));
	}

	// TODO: Fix this test
	// @Test
	// public void testThatReclaimingATeammatesTerritoryGoesBackToHim() {
	//
	// MockTerritory mockTerritory = new MockTerritory("Karelia S.S.R");
	// MockPlayableCountry soviets = new MockPlayableCountry("Soviet Union",
	// 0, new Territory[] { new TerritoryImpl("SomePlace", 0) },
	// new Territory[0]);
	// MockPlayableCountry germany = new MockPlayableCountry("Germany",
	// mockTerritory);
	// MockPlayableCountry usa = new MockPlayableCountry("United States",
	// new TerritoryImpl("usa", 1));
	//
	// GameModel mockGameModel = new MockGameModel(
	// Arrays.<PlayableCountry> asList(soviets, germany, usa));
	// germany.setGameModel(mockGameModel);
	//
	// usa.setGameModel(mockGameModel);
	// usa.seizeTerritory(germany, mockTerritory);
	//
	// assertTrue(soviets.getOwnedTerritories().contains(mockTerritory));
	// assertFalse(germany.getOwnedTerritories().contains(mockTerritory));
	// assertFalse(usa.getOwnedTerritories().contains(mockTerritory));
	// }
}
