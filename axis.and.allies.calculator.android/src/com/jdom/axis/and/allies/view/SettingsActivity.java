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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.jdom.axis.and.allies.model.GameModel;
import com.jdom.axis.and.allies.model.GameSerializer;

public class SettingsActivity extends Activity {

	private static final String ARE_YOU_SURE_YOU_WISH_TO_CHANGE_VERSIONS = "Are you sure you wish to change versions?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		RadioButton button1942 = (RadioButton) findViewById(R.id.version_1942);
		RadioButton buttonRevised = (RadioButton) findViewById(R.id.version_revised);

		GameModel gameModel = getGameModel();
		button1942.setChecked(gameModel.getVersion().endsWith("1942"));
		buttonRevised.setChecked(gameModel.getVersion().endsWith("Revised"));
	}

	public void ok(View view) {
		finish();
	}

	public void cancel(View view) {
		finish();
	}

	public void set1942Edition(View view) {
		Runnable runnable = new Runnable() {
			public void run() {
				setGameEdition(R.raw.aa_1942);
				finish();
			}
		};

		ViewUtils.getConfirmationDialog(this,
				ARE_YOU_SURE_YOU_WISH_TO_CHANGE_VERSIONS, runnable).show();
	}

	public void setRevisedEdition(View view) {
		Runnable runnable = new Runnable() {
			public void run() {
				setGameEdition(R.raw.aa_revised);
				finish();
			}
		};

		ViewUtils.getConfirmationDialog(this,
				ARE_YOU_SURE_YOU_WISH_TO_CHANGE_VERSIONS, runnable).show();
	}

	private void setGameEdition(int editionReference) {
		InputStream rawRes = null;
		String serialized = null;
		try {
			rawRes = getResources().openRawResource(editionReference);
			serialized = IOUtils.toString(rawRes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(rawRes);
		}

		if (serialized != null) {
			GameModel gameModel = getGameModel();
			gameModel.setGame(new GameSerializer().deserialize(serialized));
		}
	}

	private GameModel getGameModel() {
		GameModel gameModel = ((AxisAndAlliesApplication) this.getApplication())
				.getGameModel();
		return gameModel;
	}
}
