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

import org.junit.Test;

import com.jdom.axis.and.allies.model.Territory.TerritoryImpl;


public class TerritoryImplTest
{
    @Test
    public void testTerritoryCannotBeDamagedMoreThanTwiceItsIpcValue()
    {
        TerritoryImpl territory = new TerritoryImpl("Test", 3);
        assertEquals(3, territory.getIpcValue());

        territory.addDamage(9);

        assertEquals(3, territory.getIpcValue());
        assertEquals(6, territory.damage);
    }

    @Test
    public void testTerritoryCanBeDamagedLessThanTwiceItsIpcValue()
    {
        TerritoryImpl territory = new TerritoryImpl("Test", 3);
        assertEquals(3, territory.getIpcValue());

        territory.addDamage(5);

        assertEquals(3, territory.getIpcValue());
        assertEquals(5, territory.damage);
    }

    @Test
    public void testTerritoryCanBeRepairedForLessThanItsDamageValue()
    {
        TerritoryImpl territory = new TerritoryImpl("Test", 3);
        territory.addDamage(5);
        assertEquals(5, territory.damage);

        territory.repairDamage(3);
        assertEquals(2, territory.damage);
    }

    @Test
    public void testTerritoryCannotBeRepairedForMoreThanItsDamageValue()
    {
        TerritoryImpl territory = new TerritoryImpl("Test", 3);
        territory.addDamage(5);
        assertEquals(5, territory.damage);

        territory.repairDamage(6);
        assertEquals(0, territory.damage);
    }
}
