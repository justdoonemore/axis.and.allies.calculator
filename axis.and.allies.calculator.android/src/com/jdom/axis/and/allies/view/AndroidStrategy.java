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
package com.jdom.axis.and.allies.view;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import android.content.Context;

import com.jdom.axis.and.allies.model.Game;
import com.jdom.axis.and.allies.model.GameModelImpl.GameManagerStrategy;
import com.jdom.axis.and.allies.model.GameSerializer;

public class AndroidStrategy implements GameManagerStrategy {

	private static final String PROPERTIES_EXTENSION = ".properties";

	private final Context context;

	private final GameSerializer serializer = new GameSerializer();

	public AndroidStrategy(Context context) {
		this.context = context;
	}

	public Game loadGame(String name) {
		return serializer.deserialize(new File(context.getFilesDir(), name
				+ PROPERTIES_EXTENSION));
	}

	public void saveGame(Game game, String name) {
		File file = new File(context.getFilesDir(), name + PROPERTIES_EXTENSION);
		GameSerializer serializer = new GameSerializer();
		String fileContents = serializer.serialize(game);

		try {
			FileUtils.write(file, fileContents);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public SortedSet<String> getAvailableGames() {
		SortedSet<String> available = new TreeSet<String>();
		for (File file : context.getFilesDir().listFiles()) {
			if (file.isFile()) {
				available.add(file.getName().replaceAll(PROPERTIES_EXTENSION,
						""));
			}
		}
		return available;
	}

	public void deleteGame(String name) {
		File file = new File(context.getFilesDir(), name + PROPERTIES_EXTENSION);
		file.delete();
	}

}
