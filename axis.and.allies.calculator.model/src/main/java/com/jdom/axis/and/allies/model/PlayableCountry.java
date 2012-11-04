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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public interface PlayableCountry extends Comparable<PlayableCountry>, Cloneable {
	Collection<Territory> getOwnedTerritories();

	void seizeTerritory(PlayableCountry oldOwner, Territory territory);

	int getIpcs();

	void purchaseUnits(Collection<Purchase> purchases);

	void turnOver();

	void strategicBombingRaid(Territory territory, int value);

	Territory[] getCapitalTerritories();

	String[] getOriginalTerritoryNames();

	PlayableCountry clone();

	void clearAllIpcs();

	void setGameModel(GameModel game);

	boolean isAlly();

	boolean isCapitalsAvailable();

	class PlayableCountryImpl implements PlayableCountry {
		private static final Map<String, String[]> ORIGINAL_TERRITORIES_MAP = new HashMap<String, String[]>();
		{
			ORIGINAL_TERRITORIES_MAP.put("Soviet Union", new String[] {
					"Russia", "Karelia S.S.R", "Archangel", "Caucasus",
					"Kazakh S.S.R", "Novosibirsk", "Evenki National Okrug",
					"Yukut S.S.R", "Buryatia S.S.R", "Soviet Far East" });
			ORIGINAL_TERRITORIES_MAP.put("Germany", new String[] { "Germany",
					"Western Europe", "Norway", "Southern Europe",
					"Eastern Europe", "Balkans", "Belorussia", "Ukraine S.S.R",
					"West Russia", "Algeria", "Libya", "Balkans" });
			ORIGINAL_TERRITORIES_MAP.put("United Kingdom", new String[] {
					"United Kingdom", "Eastern Canada", "Western Canada",
					"New Zealand", "Australia", "India", "Persia",
					"Trans-Jordan", "Anglo-Egypt Sudan",
					"Union of South Africa", "French Madagascar", "Rhodesia",
					"Belgian Congo", "French Equatorial Africa",
					"French West Africa", "Italian East Africa" });
			ORIGINAL_TERRITORIES_MAP.put("Japan",
					new String[] { "Japan", "Manchuria", "Kwangtung",
							"French Indo-China", "East Indies", "Borneo",
							"Philippine Islands", "New Guinea" });
			ORIGINAL_TERRITORIES_MAP.put("United States", new String[] {
					"Western United States", "Central United States",
					"Eastern United States", "Alaska", "Hawaiian Islands",
					"China", "Sinkiang", "Brazil", "Panama", "West Indies" });
		}
		private Collection<Territory> ownedTerritories = new TreeSet<Territory>();
		private final String displayText;
		int ipcs;
		Territory[] capitals;
		private GameModel game;

		PlayableCountryImpl(String displayText,
				Territory... startingTerritories) {
			this(displayText, 0, new Territory[0], startingTerritories);

			// Only in a non-serialization constructor do we add starting IPCs
			for (Territory territory : startingTerritories) {
				this.ipcs += territory.getIpcValue();
			}
		}

		PlayableCountryImpl(String displayText, int ipcs, Territory[] capitals,
				Territory... territories) {
			this.displayText = displayText;
			this.ipcs = ipcs;
			this.capitals = capitals;

			for (Territory territory : territories) {
				ownedTerritories.add(territory);
			}
		}

		public Collection<Territory> getOwnedTerritories() {
			return ownedTerritories;
		}

		public void seizeTerritory(PlayableCountry oldOwner, Territory territory) {
			if (!oldOwner.getOwnedTerritories().remove(territory)) {
				throw new IllegalArgumentException(this + " does not own "
						+ territory + "!");
			}
			for (Territory capital : oldOwner.getCapitalTerritories()) {
				if (capital.equals(territory)) {
					this.ipcs += oldOwner.getIpcs();
					oldOwner.clearAllIpcs();
					break;
				}
			}

			boolean addToSelf = true;
			if (game != null) {
				for (PlayableCountry other : game.getPlayableCountries()) {
					for (Territory capital : other.getCapitalTerritories()) {
						if (capital.toString().equals(territory.toString())) {
							if (other.isAlly() == this.isAlly()) {
								other.getOwnedTerritories().add(territory);
								addToSelf = false;
								return;
							}
						}
					}
				}
			}
			if (addToSelf) {
				this.getOwnedTerritories().add(territory);
			}
		}

		public int getIpcs() {
			return ipcs;
		}

		public int compareTo(PlayableCountry o) {
			return this.toString().compareTo(o.toString());
		}

		@Override
		public String toString() {
			return displayText;
		}

		@Override
		public PlayableCountryImpl clone() {
			try {
				PlayableCountryImpl clone = (PlayableCountryImpl) super.clone();
				clone.ownedTerritories = new TreeSet<Territory>(
						this.ownedTerritories);

				return clone;
			} catch (CloneNotSupportedException e) {
				throw new IllegalStateException(e);
			}
		}

		public void purchaseUnits(Collection<Purchase> purchases) {
			int currentIpcs = getIpcs();
			int costForUnits = 0;

			for (Purchase purchase : purchases) {
				Purchasable purchasable = purchase.getPurchasable();
				costForUnits += (purchasable.getIpcValue() * purchase
						.getQuantity());
			}

			if (currentIpcs < costForUnits) {
				throw new IllegalArgumentException("The specified items cost "
						+ costForUnits + " ipcs but " + this + " only has "
						+ currentIpcs + "!");
			}

			this.ipcs -= costForUnits;
		}

		public void turnOver() {
			// Skip adding if a capital isn't owned by us
			if (!isCapitalsAvailable()) {
				return;
			}

			for (Territory territory : ownedTerritories) {
				ipcs += territory.getIpcValue();
			}
		}

		public void strategicBombingRaid(Territory territory, int value) {
		}

		public Territory[] getCapitalTerritories() {
			return this.capitals;
		}

		public void clearAllIpcs() {
			this.ipcs = 0;
		}

		public boolean isAlly() {
			// This should be moved out to the config file
			return this.toString().equals("Soviet Union")
					|| this.toString().equals("United Kingdom")
					|| this.toString().equals("United States");
		}

		public void setGameModel(GameModel game) {
			this.game = game;
		}

		public String[] getOriginalTerritoryNames() {
			return ORIGINAL_TERRITORIES_MAP.get(this.toString());
		}

		public boolean isCapitalsAvailable() {
			for (Territory capital : capitals) {
				if (!ownedTerritories.contains(capital)) {
					return false;
				}
			}

			return true;
		}
	}

}
