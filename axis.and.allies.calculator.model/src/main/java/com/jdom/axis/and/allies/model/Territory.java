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

public interface Territory {

	int getIpcValue();

	void repairDamage(int damage);

	class TerritoryImpl implements Territory, Comparable<TerritoryImpl> {
		private final String displayText;
		private final int ipcValue;
		int damage;

		TerritoryImpl(String displayText, int ipcValue) {
			this(displayText, ipcValue, 0);
		}

		TerritoryImpl(String displayText, int ipcValue, int damage) {
			this.displayText = displayText;
			this.ipcValue = ipcValue;
			this.damage = damage;
		}

		public int getIpcValue() {
			return ipcValue;
		}

		public int compareTo(TerritoryImpl o) {
			return this.displayText.compareTo(o.displayText);
		}

		@Override
		public String toString() {
			return displayText;
		}

		public void addDamage(int value) {
			this.damage = Math.min(damage + value, ipcValue * 2);
		}

		public void repairDamage(int damage) {
			this.damage = Math.max(this.damage - damage, 0);
		}

		public int getDamage() {
			return damage;
		}
	}

	void addDamage(int value);

	int getDamage();
}
