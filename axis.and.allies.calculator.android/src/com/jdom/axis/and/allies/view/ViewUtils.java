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
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class ViewUtils {

	public interface SelectedItemsRunner<TYPE> {
		void run(Collection<TYPE> selectedItems);
	}

	private static AlertDialog.Builder getAlertDialog(Activity activity,
			String title, String positiveText, String negativeText,
			DialogInterface.OnClickListener positiveButtonListener,
			DialogInterface.OnClickListener negativeButtonListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title);
		builder.setPositiveButton(positiveText, positiveButtonListener);
		builder.setNegativeButton(negativeText, negativeButtonListener);

		return builder;
	}

	public static AlertDialog getAlertDialog(
			Activity activity,
			String title,
			String positiveText,
			String negativeText,
			DialogInterface.OnClickListener positiveButtonListener,
			DialogInterface.OnClickListener negativeButtonListener,
			DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener,
			String[] items, boolean[] itemSelectedOrNot) {
		AlertDialog.Builder dialog = getAlertDialog(activity, title,
				positiveText, negativeText, positiveButtonListener,
				negativeButtonListener);
		dialog.setMultiChoiceItems(items, itemSelectedOrNot,
				onMultiChoiceClickListener);
		return dialog.create();
	}

	private static AlertDialog getAlertDialog(
			Activity activity,
			String title,
			String positiveText,
			String negativeText,
			android.content.DialogInterface.OnClickListener positiveButtonListener,
			android.content.DialogInterface.OnClickListener negativeButtonListener,
			android.content.DialogInterface.OnClickListener onSingleItemClickListener,
			String[] items, boolean[] itemSelectedOrNot) {
		AlertDialog.Builder dialog = getAlertDialog(activity, title,
				positiveText, negativeText, positiveButtonListener,
				negativeButtonListener);
		dialog.setSingleChoiceItems(items, -1, onSingleItemClickListener);
		return dialog.create();
	}

	public static <TYPE> OnClickListener getOnMultiChoiceClickListenerForSelectableCollection(
			final Activity activity, final String title,
			final String positiveText, final String negativeText,
			final Collection<TYPE> collection,
			final SelectedItemsRunner<TYPE> runner) {
		final String[] items = new String[collection.size()];

		Iterator<TYPE> iter = collection.iterator();
		int i = 0;

		while (iter.hasNext()) {
			items[i++] = iter.next().toString();
		}

		return new OnClickListener() {

			public void onClick(View v) {

				final Collection<TYPE> selectedUnits = new ArrayList<TYPE>();

				DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						runner.run(selectedUnits);
					}
				};

				DialogInterface.OnClickListener negativeButtonListener = new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						selectedUnits.clear();
					}
				};

				DialogInterface.OnMultiChoiceClickListener onMultipleItemsSelectedListener = new DialogInterface.OnMultiChoiceClickListener() {

					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						Iterator<TYPE> iter = collection.iterator();
						for (int i = 0; i < which; i++) {
							iter.next();
						}

						Collection<TYPE> selectedItems = selectedUnits;
						if (isChecked) {
							selectedItems.add(iter.next());
						} else {
							selectedItems.remove(iter.next());
						}

						Toast.makeText(activity.getApplicationContext(),
								selectedItems.toString(), Toast.LENGTH_SHORT)
								.show();
					}
				};

				AlertDialog alert = ViewUtils.getAlertDialog(activity, title,
						positiveText, negativeText, positiveButtonListener,
						negativeButtonListener,
						onMultipleItemsSelectedListener, items,
						new boolean[items.length]);
				alert.show();
			}
		};
	}

	public static <TYPE> OnClickListener getOnSingleChoiceClickListenerForSelectableCollection(
			final Activity activity, final String title,
			final String positiveText, final String negativeText,
			final Collection<TYPE> collection,
			final SelectedItemsRunner<TYPE> runner) {
		final String[] items = new String[collection.size()];

		Iterator<TYPE> iter = collection.iterator();
		int i = 0;

		while (iter.hasNext()) {
			items[i++] = iter.next().toString();
		}

		return new OnClickListener() {

			public void onClick(View v) {

				if (collection.isEmpty()) {
					android.app.AlertDialog.Builder noneNotification = new android.app.AlertDialog.Builder(
							activity);
					noneNotification.setMessage("None available.");
					noneNotification.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					noneNotification.show();
					return;
				}

				final Collection<TYPE> selectedUnits = new ArrayList<TYPE>();

				DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						runner.run(selectedUnits);
					}
				};

				DialogInterface.OnClickListener negativeButtonListener = new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						selectedUnits.clear();
					}
				};

				DialogInterface.OnClickListener onSingleItemClickListener = new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Iterator<TYPE> iter = collection.iterator();
						for (int i = 0; i < which; i++) {
							iter.next();
						}

						selectedUnits.clear();
						selectedUnits.add(iter.next());

						Toast.makeText(activity.getApplicationContext(),
								selectedUnits.toString(), Toast.LENGTH_SHORT)
								.show();
					}
				};

				AlertDialog alert = ViewUtils.getAlertDialog(activity, title,
						positiveText, negativeText, positiveButtonListener,
						negativeButtonListener, onSingleItemClickListener,
						items, new boolean[items.length]);
				alert.show();
			}
		};
	}

	public static <T> List<T> getChildrenOfType(ViewGroup viewGroup,
			Class<T> type) {
		List<T> children = new ArrayList<T>();

		int numberOfChildren = viewGroup.getChildCount();

		for (int i = 0; i < numberOfChildren; i++) {
			View child = viewGroup.getChildAt(i);

			if (child instanceof ViewGroup) {
				children.addAll(getChildrenOfType((ViewGroup) child, type));
			} else if (type.isAssignableFrom(child.getClass())) {
				children.add(type.cast(child));
			}
		}

		return children;
	}

	public static Builder getConfirmationDialog(Activity activity,
			String message, final Runnable runnable) {
		return new AlertDialog.Builder(activity)
				.setMessage(message)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								runnable.run();
							}
						}).setTitle("Confirm")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
	}
}
