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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class GameModelImpl extends AbstractModel implements GameModel {

	private static final GameSerializer GAME_SERIALIZER = new GameSerializer();

	Game game;

	String undoGame;

	private final String serializedOriginal;

	GameManagerStrategy gameManagerStrategy;

	private List<String> errors;

	public GameModelImpl(Game game, GameManagerStrategy gameStrategy,
			String serializedOriginal) {
		this.game = game;
		this.serializedOriginal = serializedOriginal;
		this.gameManagerStrategy = gameStrategy;
	}

	public List<PlayableCountry> getPlayableCountries() {
		List<PlayableCountry> playableCountries = new ArrayList<PlayableCountry>();

		for (String playable : this.game.turnOrder) {
			playableCountries.add(game.playableCountryMap.get(playable));
		}

		return Collections.unmodifiableList(playableCountries);
	}

	public SortedSet<Territory> getTerritories() {
		return new TreeSet<Territory>(game.territoryMap.values());
	}

	public SortedSet<Purchasable> getPurchasables() {
		return new TreeSet<Purchasable>(game.purchasableMap.values());
	}

	public PlayableCountry getActivePlayer() {
		return game.activePlayer;
	}

	public void turnOver() {
		this.undoGame = GAME_SERIALIZER.serialize(game);
		game.activePlayer.turnOver();

		int indexOfNextPlayer = game.turnOrder.indexOf(game.activePlayer
				.toString()) + 1;
		if (indexOfNextPlayer == game.turnOrder.size()) {
			indexOfNextPlayer = 0;
		}

		game.activePlayer = game.playableCountryMap.get(game.turnOrder
				.get(indexOfNextPlayer));

		setGame(game);
	}

	public void saveGame(String name) {
		gameManagerStrategy.saveGame(game, name);
	}

	public void loadGame(String name) {
		Game game = gameManagerStrategy.loadGame(name);
		setGame(game);
	}

	public void deleteGame(String name) {
		gameManagerStrategy.deleteGame(name);
		setGame(game);
	}

	public static interface GameManagerStrategy {
		Game loadGame(String name);

		void saveGame(Game game, String name);

		void deleteGame(String name);

		SortedSet<String> getAvailableGames();
	}

	public void seizeTerritories(String seizingCountry,
			String... territoryNames) {
		this.undoGame = GAME_SERIALIZER.serialize(game);
		Map<String, PlayableCountry> playableCountryMap = game.playableCountryMap;
		PlayableCountry newOwner = playableCountryMap.get(seizingCountry);
		for (String territoryName : territoryNames) {
			Territory territory = game.territoryMap.get(territoryName);

			PlayableCountry foundOwner = null;
			for (PlayableCountry possibleCurrentOwner : playableCountryMap
					.values()) {
				if (possibleCurrentOwner.getOwnedTerritories().contains(
						territory)) {
					foundOwner = possibleCurrentOwner;
					break;
				}
			}

			if (foundOwner == null) {
				throw new IllegalStateException(
						"Could not find current owner of [" + territoryName
								+ "]!");
			} else {
				newOwner.seizeTerritory(foundOwner, territory);

				checkForTerritoryMoving();
			}
		}

		setGame(game);
	}

	private void checkForTerritoryMoving() {
		for (PlayableCountry country : this.getPlayableCountries()) {
			List<PlayableCountry> countriesToCheck = (country.isAlly()) ? this
					.getAllyPlayers() : this.getAxisPlayers();

			Iterator<Territory> iter = country.getOwnedTerritories().iterator();
			while (iter.hasNext()) {
				Territory territory = iter.next();
				for (PlayableCountry friend : countriesToCheck) {
					if (country.toString().equals(friend.toString())) {
						continue;
					}
				
					for (String friendTerritoryName : friend
							.getOriginalTerritoryNames()) {
						if (friendTerritoryName.equals(territory.toString())) {
							if (friend.isCapitalsAvailable()) {
								friend.getOwnedTerritories().add(territory);
								iter.remove();
								break;
							}
						}
					}
				}
			}
		}
	}

	private List<PlayableCountry> getAllyPlayers() {
		List<PlayableCountry> players = new ArrayList<PlayableCountry>();
		for (PlayableCountry country : this.getPlayableCountries()) {
			if (country.isAlly()) {
				players.add(country);
			}
		}
		return players;
	}

	private List<PlayableCountry> getAxisPlayers() {
		List<PlayableCountry> players = new ArrayList<PlayableCountry>();
		for (PlayableCountry country : this.getPlayableCountries()) {
			if (!country.isAlly()) {
				players.add(country);
			}
		}
		return players;
	}

	public void purchase(String countryName, Collection<Purchase> purchases) {
		this.undoGame = GAME_SERIALIZER.serialize(game);
		Map<String, PlayableCountry> playableCountryMap = game.playableCountryMap;
		PlayableCountry country = playableCountryMap.get(countryName);
		country.purchaseUnits(purchases);

		setGame(game);
	}

	public void newGame() {
		this.undoGame = GAME_SERIALIZER.serialize(game);
		setGame(GAME_SERIALIZER.deserialize(serializedOriginal));
	}

	public SortedSet<String> getAvailableGames() {
		return gameManagerStrategy.getAvailableGames();
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;

		if (game != null) {
			List<PlayableCountry> playableCountries = this
					.getPlayableCountries();
			if (playableCountries != null) {
				for (PlayableCountry country : playableCountries) {
					country.setGameModel(this);
				}
			}
		}

		updateObservers();
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;

		updateObservers();
	}

	public List<String> getErrors() {
		List<String> errors = this.errors;
		this.errors = Collections.emptyList();
		return errors;
	}

	public void undo() {
		String toUndo = undoGame;
		// Set to null to disable any more undos
		this.undoGame = null;
		setGame(GAME_SERIALIZER.deserialize(toUndo));
	}

	public boolean isUndoAvailable() {
		return undoGame != null;
	}

	public void setUndoGame(Game game) {
		this.undoGame = GAME_SERIALIZER.serialize(game);

	}

	public String getVersion() {
		return game.version;
	}
}
