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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.jdom.axis.and.allies.model.Observer;

public abstract class AbstractView implements Observer {

	protected final Activity activity;

	public AbstractView(Activity activity) {
		this.activity = activity;
	}

	public void okAlert(String alertMessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(alertMessage).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public void promptAndHandleSelection(String title, final String[] items,
			ItemSelectionListener listener) {

		SingleItemClickListener itemSelectedListener = new SingleItemClickListener(
				listener, items);

		final Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(title);
		alert.setSingleChoiceItems(items, -1, itemSelectedListener);

		alert.setNegativeButton("Cancel", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
			}
		});

		alert.show();
	}

	private class SingleItemClickListener implements OnClickListener {

		private final ItemSelectionListener listener;

		private final String[] items;

		private SingleItemClickListener(ItemSelectionListener listener,
				String[] items) {
			this.listener = listener;
			this.items = items;
		}

		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			listener.handleSelectedItem(items[which]);
		}
	}

	public static interface ItemSelectionListener {
		void handleSelectedItem(String item);
	}
}
