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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

public interface GameModel extends Subject {
	List<PlayableCountry> getPlayableCountries();

	PlayableCountry getActivePlayer();

	void turnOver();

	void saveGame(String name);

	void loadGame(String name);

	void deleteGame(String game);

	void seizeTerritories(String seizingCountry, String... territoryNames);

	void purchase(String countryName, Collection<Purchase> purchases);

	SortedSet<Territory> getTerritories();

	void newGame();

	SortedSet<String> getAvailableGames();

	SortedSet<Purchasable> getPurchasables();

	Game getGame();

	void setGame(Game game);

	void setErrors(List<String> errors);

	List<String> getErrors();

	void undo();

	boolean isUndoAvailable();

	void setUndoGame(Game game);

	String getVersion();
}
