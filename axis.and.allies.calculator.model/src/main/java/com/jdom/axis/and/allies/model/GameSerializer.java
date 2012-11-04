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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.jdom.axis.and.allies.model.PlayableCountry.PlayableCountryImpl;
import com.jdom.axis.and.allies.model.Purchasable.PurchasableImpl;
import com.jdom.axis.and.allies.model.Territory.TerritoryImpl;

public class GameSerializer {
	private static final String PLAYABLE_COUNTRY_NAMES = "playableCountryNames";
	private static final String TERRITORY_NAMES = "territoryNames";
	private static final String ACTIVE_PLAYER_NAME = "activePlayerName";
	private static final String PURCHASABLE_NAMES = "purchasableNames";

	private static final char SEPARATOR = ',';
	private static final String TERRITORIES = ".territories";
	private static final String DAMAGE = ".damage";
	private static final String IPCS = ".ipcs";
	private static final String IPC_VALUES = ".ipcValues";
	private static final String VERSION = "version";
	private static final String DEFAULT_VERSION = "Axis & Allies: 1942";
	static final String CAPITALS_SUFFIX = ".capitals";
	private static final Hashtable<String, String> DEFAULT_CAPITAL_MAP = new Hashtable<String, String>();
	{
		DEFAULT_CAPITAL_MAP.put("Soviet Union", "Russia,Karelia S.S.R");
		DEFAULT_CAPITAL_MAP.put("United States",
				"Western United States,Eastern United States");
		DEFAULT_CAPITAL_MAP.put("United Kingdom", "United Kingdom,India");
		DEFAULT_CAPITAL_MAP.put("Germany",
				"Germany,Western Europe,Southern Europe");
		DEFAULT_CAPITAL_MAP.put("Japan", "Japan,Philippine Islands,Kwangtung");
	}

	public String serialize(Game game) {
		Properties properties = new Properties();
		// Required for backwards compatibility
		if (game.version == null) {
			game.version = DEFAULT_VERSION;
		}
		properties.setProperty(VERSION, game.version);

		Set<String> territoryNames = game.territoryMap.keySet();
		properties.put(TERRITORY_NAMES,
				StringUtils.join(territoryNames, SEPARATOR));
		properties.put(ACTIVE_PLAYER_NAME, game.activePlayer.toString());

		for (String territoryName : territoryNames) {
			Territory territory = game.territoryMap.get(territoryName);
			properties.put(territoryName + IPC_VALUES,
					"" + territory.getIpcValue());
			properties.put(territoryName + DAMAGE, "" + territory.getDamage());
		}

		List<String> countryNames = game.turnOrder;
		properties.put(PLAYABLE_COUNTRY_NAMES,
				StringUtils.join(countryNames, ','));

		for (String countryName : countryNames) {
			PlayableCountry playableCountry = game.playableCountryMap
					.get(countryName);

			properties.put(countryName + IPCS, "" + playableCountry.getIpcs());
			properties.put(countryName + TERRITORIES,
					convertCollectionToString(playableCountry
							.getOwnedTerritories()));

			if (playableCountry.getCapitalTerritories() != null) {
				properties.put(countryName + CAPITALS_SUFFIX, StringUtils.join(
						Arrays.asList(playableCountry.getCapitalTerritories()),
						','));

			}

		}

		Set<String> purchasableNames = game.purchasableMap.keySet();
		properties.put(PURCHASABLE_NAMES,
				StringUtils.join(purchasableNames, SEPARATOR));
		for (String purchasableName : purchasableNames) {
			Purchasable purchasable = game.purchasableMap.get(purchasableName);

			properties.put(purchasableName + IPC_VALUES,
					"" + purchasable.getIpcValue());
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			properties.store(os, null);
		} catch (IOException e) {
			// Should never happen
		} finally {
			IOUtils.closeQuietly(os);
		}
		return os.toString();
	}

	private String convertCollectionToString(Collection<?> collection) {
		return StringUtils.join(collection, SEPARATOR);
	}

	public Game deserialize(File file) {
		try {
			String serializedGame = FileUtils.readFileToString(file);
			return deserialize(serializedGame);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Game deserialize(String serialized) {
		Game game = new Game();

		game.territoryMap = new HashMap<String, Territory>();

		Properties properties = new Properties();
		try {
			properties.load(new ByteArrayInputStream(serialized.getBytes()));
		} catch (IOException e) {
		} finally {

		}

		game.version = properties.getProperty(VERSION);
		// Required for backwards compatibility
		if (game.version == null) {
			game.version = DEFAULT_VERSION;
		}
		String[] territoryNames = StringUtils.split(
				properties.getProperty(TERRITORY_NAMES), SEPARATOR);

		for (String territoryName : territoryNames) {
			game.territoryMap.put(
					territoryName,
					new TerritoryImpl(territoryName, getInteger(properties,
							territoryName + IPC_VALUES), getInteger(properties,
							territoryName + DAMAGE)));
		}

		String[] playableCountryNames = StringUtils.split(
				properties.getProperty(PLAYABLE_COUNTRY_NAMES), SEPARATOR);

		game.playableCountryMap = new HashMap<String, PlayableCountry>();
		for (String playableCountryName : playableCountryNames) {
			game.turnOrder.add(playableCountryName);
			String[] ownedTerritoryNames = StringUtils.split(
					properties.getProperty(playableCountryName + TERRITORIES),
					SEPARATOR);
			Territory[] territories = new Territory[ownedTerritoryNames.length];

			for (int i = 0; i < territories.length; i++) {
				Territory territory = game.territoryMap
						.get(ownedTerritoryNames[i]);
				territories[i] = territory;
			}

			String capitalNamesLine = properties
					.getProperty(playableCountryName + CAPITALS_SUFFIX);

			// Required for backwards compatibility
			if (capitalNamesLine == null) {
				capitalNamesLine = DEFAULT_CAPITAL_MAP.get(playableCountryName);
			}

			String[] capitalNames = StringUtils.split(capitalNamesLine, ",");
			Territory[] capitals = new Territory[capitalNames.length];
			for (int i = 0; i < capitalNames.length; i++) {
				capitals[i] = game.territoryMap.get(capitalNames[i]);
			}

			game.playableCountryMap.put(
					playableCountryName,
					new PlayableCountryImpl(playableCountryName, getInteger(
							properties, playableCountryName + IPCS), capitals,
							territories));
		}

		String[] purchasableNames = StringUtils.split(
				properties.getProperty(PURCHASABLE_NAMES), SEPARATOR);

		for (String purchasableName : purchasableNames) {
			int ipcValue = getInteger(properties, purchasableName + IPC_VALUES);
			game.purchasableMap.put(purchasableName, new PurchasableImpl(
					purchasableName, ipcValue));
		}

		game.activePlayer = game.playableCountryMap.get(properties
				.get(ACTIVE_PLAYER_NAME));
		return game;
	}

	private int getInteger(Properties properties, String key) {
		return Integer.parseInt(properties.getProperty(key));
	}
}
