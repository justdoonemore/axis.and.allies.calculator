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

import java.util.Collection;
import java.util.SortedSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.jdom.axis.and.allies.model.GameModel;

public class AxisAndAlliesActivity extends Activity {

	private AxisAndAlliesApplication application;

	private AxisAlliesToolsView view;

	Intent lastSentIntent;

	private GameModel gameModel;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		application = (AxisAndAlliesApplication) getApplication();
		gameModel = application.getGameModel();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (view == null) {
			view = new AxisAlliesToolsView(gameModel, this);
		} else {
			gameModel.setGame(gameModel.getGame());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		application.saveGame();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.new_game:

			Runnable runnable = new Runnable() {

				public void run() {
					gameModel.newGame();
				}
			};

			ViewUtils
					.getConfirmationDialog(
							this,
							"Are you sure you wish to lose all progress on the current game?",
							runnable).show();

			return true;
		case R.id.save_game:

			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.save_game_dialog, null);
			final EditText editText = (EditText) textEntryView
					.findViewById(R.id.save_game_entry);
			new AlertDialog.Builder(AxisAndAlliesActivity.this)
					.setTitle("Save Game")
					.setView(textEntryView)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int whichButton) {
									String name = editText.getText().toString()
											.trim();
									if (!"".equals(name)) {
										gameModel.saveGame(name);
									}
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).show();
			return true;
		case R.id.load_game:
			final SortedSet<String> availableGames = gameModel
					.getAvailableGames();

			ViewUtils.SelectedItemsRunner<String> runner = new ViewUtils.SelectedItemsRunner<String>() {

				public void run(Collection<String> selectedItems) {
					gameModel.loadGame(selectedItems.iterator().next());
				}

			};

			// Click the listener to display the dialog
			ViewUtils.getOnSingleChoiceClickListenerForSelectableCollection(
					this, "Load Game", "OK",
					"Cancel", availableGames, runner)
					.onClick(this.getCurrentFocus());
			return true;
		case R.id.delete_game:
			final SortedSet<String> availableGames2 = gameModel
					.getAvailableGames();

			ViewUtils.SelectedItemsRunner<String> runner2 = new ViewUtils.SelectedItemsRunner<String>() {

				public void run(Collection<String> selectedItems) {
					gameModel.deleteGame(selectedItems.iterator().next());
				}
			};

			// Click the listener to display the dialog
			ViewUtils.getOnSingleChoiceClickListenerForSelectableCollection(
					this, "Delete Game", "OK",
					"Cancel", availableGames2,
					runner2).onClick(this.getCurrentFocus());

			return true;
		case R.id.settings:
			Intent i = new Intent(this, SettingsActivity.class);
			this.lastSentIntent = i;
			this.startActivity(i);
			return true;
		case R.id.more:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://search?q=pub:Just Do One More"));
			startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void turnOver(View view) {
		this.gameModel.turnOver();
	}

	public void undo(View view) {
		this.gameModel.undo();
	}

	public void details(View view) {
		Intent i = new Intent(this, DetailsViewActivity.class);
		this.lastSentIntent = i;
		this.startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}
}