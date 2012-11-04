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

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.jdom.axis.and.allies.model.Game;
import com.jdom.axis.and.allies.model.GameSerializer;
import com.jdom.axis.and.allies.model.PlayableCountry;
import com.jdom.axis.and.allies.model.Purchasable;
import com.jdom.axis.and.allies.model.Territory;
import com.jdom.axis.and.allies.model.PlayableCountry.PlayableCountryImpl;

public class GameSerializerTest {
	private static final GameSerializer SERIALIZER = new GameSerializer();

	private final Game game = GameUtil.getGame();

	public static void main(String[] args) {
		Game game = new Game();
		System.out.println(SERIALIZER.serialize(game));
	}

	@Test
	public void testSerializingAndDeserializingAGameKeepsStateOfATerritory() {
		Territory territory = game.territoryMap.get("Germany");
		// Add some damage
		territory.addDamage(4);

		Game deserialized = SERIALIZER.deserialize(SERIALIZER.serialize(game));
		Territory deserializedTerritory = deserialized.territoryMap
				.get("Germany");

		assertEquals(territory.getDamage(), deserializedTerritory.getDamage());
		assertEquals(territory.getIpcValue(),
				deserializedTerritory.getIpcValue());
		assertEquals(territory.toString(), deserializedTerritory.toString());
	}

	@Test
	public void testSerializingAndDeserializingAGameKeepsStateOfAPlayableCountry() {
		MockTerritory mockTerritory = new MockTerritory();
		game.territoryMap.put(mockTerritory.toString(), mockTerritory);

		PlayableCountry playableCountry = game.playableCountryMap
				.get("Germany");
		// Give it ipcs
		playableCountry.turnOver();
		playableCountry.turnOver();
		playableCountry.turnOver();
		Collection<Territory> ownedTerritories = playableCountry
				.getOwnedTerritories();

		ownedTerritories.add(mockTerritory);

		Game deserialized = SERIALIZER.deserialize(SERIALIZER.serialize(game));
		PlayableCountry deserializedPlayableCountry = deserialized.playableCountryMap
				.get("Germany");
		Collection<Territory> deserializedOwnedTerritories = deserializedPlayableCountry
				.getOwnedTerritories();

		assertEquals(ownedTerritories.size(),
				deserializedOwnedTerritories.size());
		assertEquals(playableCountry.getIpcs(),
				deserializedPlayableCountry.getIpcs());
		assertEquals(playableCountry.toString(),
				deserializedPlayableCountry.toString());
	}

	@Test
	public void testSerializingAndDeserializingAGameKeepsTurnOrder() {
		List<String> turnOrder = game.turnOrder;
		Game deserialized = SERIALIZER.deserialize(SERIALIZER.serialize(game));

		assertFalse(deserialized.turnOrder.isEmpty());
		Assert.assertArrayEquals(turnOrder.toArray(),
				deserialized.turnOrder.toArray());

	}

	@Test
	public void testSerializingAndDeserializingAGameKeepsActivePlayer() {
		game.activePlayer = game.playableCountryMap.get("Germany");
		Game deserialized = SERIALIZER.deserialize(SERIALIZER.serialize(game));

		assertEquals(game.activePlayer.toString(),
				deserialized.activePlayer.toString());
	}

	@Test
	public void testSerializingAndDeserializingAGameKeepsPurchasableState() {
		Purchasable purchasable = game.purchasableMap.get("Infantry");
		int original = purchasable.getIpcValue();

		assertEquals(original, SERIALIZER.deserialize(SERIALIZER
				.serialize(game)).purchasableMap.get("Infantry").getIpcValue());
	}

	@Test
	public void testSerializingAGameStoresTheCapital() {
		PlayableCountryImpl impl = (PlayableCountryImpl) game.playableCountryMap
				.get("Soviet Union");
		impl.capitals = new Territory[] { new MockTerritory() };

		assertTrue(
				"The capital doesn't seem to have been serialized!",
				SERIALIZER.serialize(game).contains(
						"Soviet\\ Union" + GameSerializer.CAPITALS_SUFFIX
								+ "=MockTerritory"));

	}

	@Test
	public void testDeserializingAGameWithoutCapitalsDefaultsThem() {
		Game deserialized = SERIALIZER
				.deserialize(new File(
						"src/test/resources/playable_countries_without_capitals.properties"));
		PlayableCountry soviets = deserialized.playableCountryMap
				.get("Soviet Union");
		assertTrue(soviets.getCapitalTerritories().length == 2);
	}

	@Test
	public void testDeserializingAGameWithCapitalDoesntDefaultThem() {
		Game deserialized = SERIALIZER
				.deserialize(new File(
						"src/test/resources/playable_countries_with_funky_capitals.properties"));
		PlayableCountry soviets = deserialized.playableCountryMap
				.get("Soviet Union");
		assertTrue(soviets.getCapitalTerritories().length == 1);
	}
}