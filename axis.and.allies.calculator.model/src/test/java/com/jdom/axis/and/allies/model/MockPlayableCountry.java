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

import com.jdom.axis.and.allies.model.Territory;
import com.jdom.axis.and.allies.model.PlayableCountry.PlayableCountryImpl;
import com.jdom.axis.and.allies.model.Territory.TerritoryImpl;

public class MockPlayableCountry extends PlayableCountryImpl {
	private static int counter = 0;

	public MockPlayableCountry() {
		this("Mock", new TerritoryImpl("MockTerritory" + counter++, 1));
	}

	public MockPlayableCountry(Territory territory) {
		this("Mock", territory);
	}

	public MockPlayableCountry(String string, Territory territory) {
		super(string, territory);
	}

	public MockPlayableCountry(String displayText, int ipcs,
			Territory[] capitals, Territory... territories) {
		super(displayText, ipcs, capitals, territories);
	}

}
