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

public interface Purchasable extends Comparable<Purchasable> {
	Purchasable INFANTRY = new PurchasableImpl("Infantry", 3);
	Purchasable ARTILLERY = new PurchasableImpl("Artillery", 4);
	Purchasable TANK = new PurchasableImpl("Tank", 5);
	Purchasable ANTIAIRCRAFT_GUN = new PurchasableImpl("Antiaircraft Gun", 6);
	Purchasable INDUSTRIAL_COMPLEX = new PurchasableImpl("Industrial Complex",
			15);
	Purchasable FIGHTER = new PurchasableImpl("Fighter", 10);
	Purchasable BOMBER = new PurchasableImpl("Bomber", 12);
	Purchasable BATTLESHIP = new PurchasableImpl("Battleship", 20);
	Purchasable AIRCRAFT_CARRIER = new PurchasableImpl("Aircraft Carrier", 14);
	Purchasable CRUISER = new PurchasableImpl("Cruiser", 12);
	Purchasable DESTROYER = new PurchasableImpl("Destroyer", 8);
	Purchasable SUBMARINE = new PurchasableImpl("Submarine", 6);
	Purchasable TRANSPORT = new PurchasableImpl("Transport", 7);

	int getIpcValue();

	static class PurchasableImpl implements Purchasable {
		private final String displayText;

		private final int ipcValue;

		PurchasableImpl(String displayText, int ipcValue) {
			this.displayText = displayText;
			this.ipcValue = ipcValue;
		}

		public int getIpcValue() {
			return ipcValue;
		}

		public int compareTo(Purchasable o) {
			return this.toString().compareTo(o.toString());
		}

		@Override
		public String toString() {
			return displayText;
		}
	}
}
