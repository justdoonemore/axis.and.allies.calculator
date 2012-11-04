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

import java.io.File;

import com.jdom.axis.and.allies.model.Game;
import com.jdom.axis.and.allies.model.GameSerializer;


public class GameUtil {
    private static final GameSerializer SERIALIZER = new GameSerializer();

    public static Game getGame() {
        return SERIALIZER.deserialize(new File("src/test/resources/test_game.properties"));
    }
}
