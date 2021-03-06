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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	List<String> turnOrder = new ArrayList<String>();
	Map<String, Territory> territoryMap = new HashMap<String, Territory>();
	Map<String, PlayableCountry> playableCountryMap = new HashMap<String, PlayableCountry>();
	Map<String, Purchasable> purchasableMap = new HashMap<String, Purchasable>();
	PlayableCountry activePlayer;
	String version;
}
