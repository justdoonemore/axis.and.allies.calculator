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

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


import com.jdom.axis.and.allies.model.GameModel;
import com.jdom.axis.and.allies.model.PlayableCountry;

public class DetailsViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.details);

		GameModel gameModel = getGameModel();

		for (PlayableCountry playableCountry : gameModel.getPlayableCountries()) {
			int countriesViewId = R.id.unitedStatesCountries;
			if ("Soviet Union".equals(playableCountry.toString())) {
				countriesViewId = R.id.sovietsCountries;
			} else if ("Germany".equals(playableCountry.toString())) {
				countriesViewId = R.id.germanyCountries;
			} else if ("United Kingdom".equals(playableCountry.toString())) {
				countriesViewId = R.id.unitedKingdomCountries;
			} else if ("Japan".equals(playableCountry.toString())) {
				countriesViewId = R.id.japanCountries;
			}

			writeOutCountries(playableCountry, countriesViewId);
		}

	}

	@SuppressWarnings("unchecked")
	private void writeOutCountries(PlayableCountry playableCountry,
			int countriesViewId) {
		TextView textView = (TextView) findViewById(countriesViewId);
		textView.setText(StringUtils.join(playableCountry.getOwnedTerritories()));
	}

	private GameModel getGameModel() {
		GameModel gameModel = ((AxisAndAlliesApplication) this.getApplication())
				.getGameModel();
		return gameModel;
	}

}
