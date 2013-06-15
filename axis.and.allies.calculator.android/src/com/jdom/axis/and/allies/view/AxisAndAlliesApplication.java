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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jdom.axis.and.allies.model.Game;
import com.jdom.axis.and.allies.model.GameModel;
import com.jdom.axis.and.allies.model.GameModelImpl;
import com.jdom.axis.and.allies.model.GameSerializer;
import com.jdom.logging.android.AndroidLoggerFactory;
import com.jdom.logging.api.LogFactory;
import com.jdom.logging.api.Logger;

public final class AxisAndAlliesApplication extends Application {
	private static final Logger log;
	static {
		LogFactory.setFactory(AndroidLoggerFactory.class);
		log = LogFactory.getLogger(AxisAndAlliesApplication.class);
	}

	private final String SAVED_STATE_KEY = "state";

	private final GameSerializer gameSerializer = new GameSerializer();

	private GameModel gameModel;

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences preferences = getSharedPreferences(SAVED_STATE_KEY,
				MODE_PRIVATE);

		String serializedGame = null;
		if (preferences != null) {
			serializedGame = preferences.getString(SAVED_STATE_KEY, null);
		}

		String serializedOriginal = null;
		InputStream rawRes = null;
		try {
			rawRes = getResources().openRawResource(R.raw.aa_1942);
			serializedOriginal = IOUtils.toString(rawRes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(rawRes);
		}

		if (serializedGame == null) {
			serializedGame = serializedOriginal;
		}

		Game game = gameSerializer.deserialize(serializedGame);

		AndroidStrategy strategy = new AndroidStrategy(this);
		gameModel = new GameModelImpl(game, strategy, serializedOriginal);
		// gameModel.setGame(game);
	}

	public void saveGame() {
		SharedPreferences preferences = getSharedPreferences(SAVED_STATE_KEY,
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(SAVED_STATE_KEY,
				gameSerializer.serialize(gameModel.getGame()));
		editor.commit();
	}

	public GameModel getGameModel() {
		return gameModel;
	}

	public GameSerializer getGameSerializer() {
		return gameSerializer;
	}
}
