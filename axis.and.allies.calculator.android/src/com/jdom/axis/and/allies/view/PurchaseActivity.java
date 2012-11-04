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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jdom.axis.and.allies.model.GameModel;
import com.jdom.axis.and.allies.model.Observer;
import com.jdom.axis.and.allies.model.Purchasable;
import com.jdom.axis.and.allies.model.Purchase;
import com.jdom.axis.and.allies.model.Subject;

public class PurchaseActivity extends Activity implements Observer {

	private GameModel model;

	private final Map<CharSequence, Integer> numberSelected = new HashMap<CharSequence, Integer>();

	public Intent returnIntent;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);

		AxisAndAlliesApplication application = (AxisAndAlliesApplication) getApplication();

		model = application.getGameModel();
		model.registerObserver(this);
	}

	public void update(Subject subject) {
		TableLayout tableLayout = (TableLayout) findViewById(R.id.purchasables_table);
		tableLayout.removeAllViews();

		final SortedSet<Purchasable> purchasables = model.getPurchasables();

		for (Purchasable purchasable : purchasables) {
			TableRow tableRow = new TableRow(this);
			final String string = purchasable.toString();
			// start with -1 so the first click sets it to 0
			numberSelected.put(string, -1);
			final TextView textView = new TextView(this);
			OnClickListener addListener = new OnClickListener() {

				public void onClick(View v) {
					Integer currentNumber = numberSelected.get(string);
					currentNumber++;
					numberSelected.put(string, currentNumber);
					textView.setText(string + " (" + currentNumber + ")");
				}
			};
			OnClickListener subtractListener = new OnClickListener() {

				public void onClick(View v) {
					Integer currentNumber = numberSelected.get(string);
					if (currentNumber > 0) {
						currentNumber--;
						numberSelected.put(string, currentNumber);
						textView.setText(string + " (" + currentNumber + ")");
					}
				}
			};
			// Click it once to initialize its text
			addListener.onClick(tableLayout);

			Button addButton = new Button(this);
			addButton.setText("+");
			addButton.setOnClickListener(addListener);

			Button subtractButton = new Button(this);
			subtractButton.setText("-");
			subtractButton.setOnClickListener(subtractListener);

			tableRow.addView(textView);
			tableRow.addView(addButton);
			tableRow.addView(subtractButton);
			tableLayout.addView(tableRow);
		}
	}

	public void confirmPurchase(View view) {
		finishPurchase(numberSelected);
	}

	public void cancelPurchase(View view) {
		ViewUtils.getConfirmationDialog(this,
				"Are you sure you wish to cancel this purchase?",
				new Runnable() {
					public void run() {
						PurchaseActivity.this.finish();
					}
				}).show();
	}

	private void finishPurchase(final Map<CharSequence, Integer> toPurchase) {
		Collection<Purchase> purchases = new ArrayList<Purchase>();
		for (Purchasable purchasable : model.getPurchasables()) {
			int selected = toPurchase.get(purchasable.toString());

			if (selected == 0) {
				continue;
			}

			purchases.add(new Purchase(purchasable, selected));
		}

		try {
			model.purchase(model.getActivePlayer().toString(), purchases);
			returnIntent = getReturnIntent(Activity.RESULT_OK);
		} catch (IllegalArgumentException iae) {
			model.setErrors(Arrays.asList(iae.getMessage()));
			returnIntent = getReturnIntent(Activity.RESULT_CANCELED);
		}

		finish();
	}

	Intent getReturnIntent(int result) {
		Intent intent = new Intent();
		setResult(result, intent);
		return intent;
	}
}
