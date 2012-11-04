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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jdom.axis.and.allies.model.GameModel;
import com.jdom.axis.and.allies.model.PlayableCountry;
import com.jdom.axis.and.allies.model.Subject;
import com.jdom.axis.and.allies.model.Territory;
import com.jdom.axis.and.allies.view.ViewUtils.SelectedItemsRunner;

public class AxisAlliesToolsView extends AbstractView {

	private final GameModel model;

	private final AxisAndAlliesActivity activity;

	private final OnClickListener purchaseUnitsListener = new OnClickListener() {
		public void onClick(View v) {
			Intent i = new Intent(activity, PurchaseActivity.class);
			activity.lastSentIntent = i;
			activity.startActivityForResult(i, 0);
		}
	};

	public AxisAlliesToolsView(GameModel model, AxisAndAlliesActivity activity) {
		super(activity);

		this.model = model;
		this.activity = activity;
		this.model.registerObserver(this);
	}

	public void update(Subject subject) {
		PlayableCountry activePlayer = model.getActivePlayer();

		final List<PlayableCountry> countries = new ArrayList<PlayableCountry>(
				this.model.getPlayableCountries());

		for (int i = 0; i < countries.size(); i++) {
			final int idx = i;
			final PlayableCountry country = countries.get(idx);

			TextView countryNameView;
			TextView countryIpcsView;
			switch (idx) {
			case 0:
				countryNameView = (TextView) activity
						.findViewById(R.id.playerOneName);
				countryIpcsView = (TextView) activity
						.findViewById(R.id.playerOneIpcs);
				break;
			case 1:
				countryNameView = (TextView) activity
						.findViewById(R.id.playerTwoName);
				countryIpcsView = (TextView) activity
						.findViewById(R.id.playerTwoIpcs);
				break;
			case 2:
				countryNameView = (TextView) activity
						.findViewById(R.id.playerThreeName);
				countryIpcsView = (TextView) activity
						.findViewById(R.id.playerThreeIpcs);
				break;
			case 3:
				countryNameView = (TextView) activity
						.findViewById(R.id.playerFourName);
				countryIpcsView = (TextView) activity
						.findViewById(R.id.playerFourIpcs);
				break;
			case 4:
				countryNameView = (TextView) activity
						.findViewById(R.id.playerFiveName);
				countryIpcsView = (TextView) activity
						.findViewById(R.id.playerFiveIpcs);
				break;
			default:
				throw new IllegalArgumentException(
						"Found too many playable countries!");
			}

			countryIpcsView.setText("" + country.getIpcs());

			Typeface typefaceToSet = Typeface.DEFAULT;
			String countryNameToSet = country.toString();
			if (country == activePlayer) {
				typefaceToSet = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC);
				countryNameToSet = "*" + countryNameToSet;
			}

			countryNameView.setTypeface(typefaceToSet);
			countryIpcsView.setTypeface(typefaceToSet);
			countryNameView.setText(countryNameToSet);

		}

		TextView versionView = (TextView) activity.findViewById(R.id.version);
		versionView.setText(model.getVersion());

		final Button button = (Button) activity
				.findViewById(R.id.purchase_units_button);
		button.setOnClickListener(getPurchaseUnitsListener());
		final Button seizeTerritoryButton = (Button) activity
				.findViewById(R.id.seize_territory_button);
		seizeTerritoryButton.setOnClickListener(getSeizeTerritoryListener());

		final Button undoButton = (Button) activity
				.findViewById(R.id.undo_button);
		undoButton.setEnabled(model.isUndoAvailable());

		// Check for any errors
		List<String> errors = model.getErrors();
		if (errors != null && !errors.isEmpty()) {
			ErrorDialog errorDialog = new ErrorDialog(activity, errors.get(0));
			errorDialog.show();
		}
	}

	private OnClickListener getSeizeTerritoryListener() {
		final List<String> items = new ArrayList<String>();

		for (Territory territory : model.getTerritories()) {
			items.add(territory.toString());
		}

		SelectedItemsRunner<String> runner = new SelectedItemsRunner<String>() {

			public void run(Collection<String> selectedItems) {
				Toast.makeText(AxisAlliesToolsView.this.activity,
						"Seized: " + selectedItems, Toast.LENGTH_SHORT).show();
				model.seizeTerritories(model.getActivePlayer().toString(),
						selectedItems.toArray(new String[selectedItems.size()]));
			}
		};

		return ViewUtils.getOnMultiChoiceClickListenerForSelectableCollection(
				activity, "Seize", "OK", "Cancel", items, runner);
	}

	private OnClickListener getPurchaseUnitsListener() {
		return purchaseUnitsListener;
	}
}
