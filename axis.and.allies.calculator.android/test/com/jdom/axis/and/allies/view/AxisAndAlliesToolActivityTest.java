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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowAlertDialog;
import com.xtremelabs.robolectric.tester.android.view.TestMenuItem;

//NOTE: IF YOU START GETTING NULL EXCEPTIONS FOR ROBOTIC YOU PROBABLY HAVE A BAD XML FILE!
// NOTE: NoSuchFieldError: NULL means there is a pre 4.5 version of JUnit jar on the classpath!
@RunWith(RobolectricTestRunner.class)
public class AxisAndAlliesToolActivityTest {
	private static final String SOME_GAME_NAME = "SomeName";
	private static final int PLAYER_ONE_STARTING_IPCS = 24;
	private static final int PLAYER_TWO_STARTING_IPCS = 40;
	private static final int PLAYER_THREE_STARTING_IPCS = 30;
	private static final int PLAYER_FOUR_STARTING_IPCS = 30;
	private static final int PLAYER_FIVE_STARTING_IPCS = 42;
	private final AxisAndAlliesActivity axisAndAlliesActivity = new AxisAndAlliesActivity();

	@Test
	public void testCountryTextsHaveAppropriateStartingIpcs() throws Exception {
		setupActivity();

		List<Integer> ipcs = getPlayerIpcs();

		assertEquals(PLAYER_ONE_STARTING_IPCS, (int) ipcs.get(0));
		assertEquals(PLAYER_TWO_STARTING_IPCS, (int) ipcs.get(1));
		assertEquals(PLAYER_THREE_STARTING_IPCS, (int) ipcs.get(2));
		assertEquals(PLAYER_FOUR_STARTING_IPCS, (int) ipcs.get(3));
		assertEquals(PLAYER_FIVE_STARTING_IPCS, (int) ipcs.get(4));
	}

	private void setupActivity() {
		axisAndAlliesActivity.getApplication().onCreate();
		axisAndAlliesActivity.onCreate(null);
		axisAndAlliesActivity.onResume();
	}

	@Test
	public void testCountryIpcIsLoweredByPurchasingAUnit() throws Exception {
		setupActivity();

		purchaseSomething();

		assertWhetherPurchaseHasBeenMade(true);
	}

	@Test
	public void testPurchasingTooMuchDisplaysError() throws Exception {
		setupActivity();

		purchaseSomething(10, Activity.RESULT_CANCELED);

		ShadowAlertDialog alertDialog = Robolectric.shadowOf(ShadowAlertDialog
				.getLatestAlertDialog());
		assertEquals("Error", alertDialog.getTitle());
		assertEquals(
				"Did not find the expected error message in the dialog!",
				"The specified items cost 60 ipcs but Soviet Union only has 24!",
				alertDialog.getMessage());
		assertEquals("The positive button text was not as expected!", "OK",
				alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).getText());
	}

	@Test
	public void testCountryIpcIsNotLoweredWhenCancellingPurchasingAUnit()
			throws Exception {
		setupActivity();

		Button purchaseUnitsButton = (Button) axisAndAlliesActivity
				.findViewById(R.id.purchase_units_button);
		purchaseUnitsButton.performClick();

		decideNotToPurchaseSomething();

		assertWhetherPurchaseHasBeenMade(false);
	}

	@Test
	public void testEndingTurnChangesIpcsOfActiveCountry() throws Exception {
		setupActivity();

		Button endTurnButton = (Button) axisAndAlliesActivity
				.findViewById(R.id.turn_over_button);

		endTurnButton.performClick();

		assertWhetherPurchaseHasBeenMade(true);
	}

	@Test
	public void testClickingNewGameStartsNewGameIfConfirmed() {
		setupActivity();

		purchaseSomething();

		assertWhetherPurchaseHasBeenMade(true);

		MenuItem item = new TestMenuItem() {
			@Override
			public int getItemId() {
				return R.id.new_game;
			}
		};

		axisAndAlliesActivity.onOptionsItemSelected(item);

		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		assertWhetherPurchaseHasBeenMade(false);
	}

	@Test
	public void testUndoingActionRevertsLastAction() {
		setupActivity();

		purchaseSomething();

		assertWhetherPurchaseHasBeenMade(true);

		Button undoButton = (Button) axisAndAlliesActivity
				.findViewById(R.id.undo_button);
		undoButton.performClick();

		assertWhetherPurchaseHasBeenMade(false);
	}

	@Test
	public void testUndoingActionDisablesUndoButton() {
		setupActivity();

		Button undoButton = (Button) axisAndAlliesActivity
				.findViewById(R.id.undo_button);
		assertFalse(
				"Undo button should not be enabled until there is something to undo!",
				undoButton.isEnabled());

		purchaseSomething();

		assertTrue(
				"Undo button should be enabled when there is something to undo!",
				undoButton.isEnabled());

		undoButton.performClick();

		assertFalse(
				"Undo button should not be enabled until there is something to undo!",
				undoButton.isEnabled());
	}

	@Test
	public void testClickingNewGameDoesntStartNewGameIfNotConfirmed() {
		setupActivity();

		purchaseSomething();

		assertWhetherPurchaseHasBeenMade(true);

		MenuItem item = new TestMenuItem() {
			@Override
			public int getItemId() {
				return R.id.new_game;
			}
		};

		axisAndAlliesActivity.onOptionsItemSelected(item);

		AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();

		assertWhetherPurchaseHasBeenMade(true);
	}

	@Test
	public void testDeletingGameMakesItUnavailableToLoadGameDialog() {
		setupActivity();

		MenuItem item = new TestMenuItem() {
			@Override
			public int getItemId() {
				return R.id.save_game;
			}
		};

		axisAndAlliesActivity.onOptionsItemSelected(item);

		// This is the save game dialog, enter a name
		ShadowAlertDialog dialog = Robolectric.shadowOf(ShadowAlertDialog
				.getLatestAlertDialog());
		EditText view = (EditText) dialog.findViewById(R.id.save_game_entry);
		view.setText(SOME_GAME_NAME);
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		verifyGameIsAvailable(SOME_GAME_NAME);

		item = new TestMenuItem() {
			@Override
			public int getItemId() {
				return R.id.delete_game;
			}
		};

		axisAndAlliesActivity.onOptionsItemSelected(item);

		dialog = Robolectric.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
		assertEquals("Delete Game", dialog.getTitle());

		ShadowAlertDialog alertDialog = Robolectric.shadowOf(ShadowAlertDialog
				.getLatestAlertDialog());
		assertEquals(SOME_GAME_NAME, alertDialog.getItems()[0]);
	}

	@Test
	public void testSavingGameMakesItAvailableToLoadGameDialog() {
		setupActivity();

		purchaseSomething();

		assertWhetherPurchaseHasBeenMade(true);

		int firstPlayerIpcs = getPlayerIpc(R.id.playerOneIpcs);

		MenuItem item = new TestMenuItem() {
			@Override
			public int getItemId() {
				return R.id.save_game;
			}
		};

		axisAndAlliesActivity.onOptionsItemSelected(item);

		// This is the save game dialog, enter a name
		ShadowAlertDialog dialog = Robolectric.shadowOf(ShadowAlertDialog
				.getLatestAlertDialog());
		assertEquals("Save Game", dialog.getTitle());

		EditText view = (EditText) dialog.findViewById(R.id.save_game_entry);
		view.setText(SOME_GAME_NAME);
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		// Now make some changes to verify the display text changes
		purchaseSomething();

		assertFalse("The display text should have changed!",
				firstPlayerIpcs == getPlayerIpc(R.id.playerOneIpcs));

		verifyGameIsAvailable(SOME_GAME_NAME);

		ShadowAlertDialog alertDialog = Robolectric.shadowOf(ShadowAlertDialog
				.getLatestAlertDialog());
		alertDialog.clickOnItem(0);
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

		// Now that the game has been loaded the text should have been restored
		assertTrue("The display text should have been restored!",
				firstPlayerIpcs == getPlayerIpc(R.id.playerOneIpcs));
	}

	private void verifyGameIsAvailable(String string) {
		// Look it up in the load game dialog
		MenuItem loadGameItem = new TestMenuItem() {
			@Override
			public int getItemId() {
				return R.id.load_game;
			}
		};

		axisAndAlliesActivity.onOptionsItemSelected(loadGameItem);

		ShadowAlertDialog alertDialog = Robolectric.shadowOf(ShadowAlertDialog
				.getLatestAlertDialog());
		assertEquals("Load Game", alertDialog.getTitle());
		assertEquals(string, alertDialog.getItems()[0]);
	}

	private void purchaseSomething() {
		purchaseSomething(1, Activity.RESULT_OK);
	}

	private void purchaseSomething(int quantityOfItemToPurchase, int result) {
		Button purchaseUnitsButton = (Button) axisAndAlliesActivity
				.findViewById(R.id.purchase_units_button);
		purchaseUnitsButton.performClick();

		Intent originalIntent = axisAndAlliesActivity.lastSentIntent;

		PurchaseActivity activity = new PurchaseActivity();
		activity.setIntent(originalIntent);
		activity.onCreate(null);

		TableLayout tableLayout = (TableLayout) activity
				.findViewById(R.id.purchasables_table);
		Button addOneItemButton = (Button) ((TableRow) tableLayout
				.getChildAt(1)).getChildAt(1);

		for (int i = 0; i < quantityOfItemToPurchase; i++) {
			addOneItemButton.performClick();
		}

		Button makePurchaseButton = (Button) activity
				.findViewById(R.id.make_purchase_button);
		makePurchaseButton.performClick();

		Robolectric.shadowOf(axisAndAlliesActivity).receiveResult(
				originalIntent, result, activity.returnIntent);
	}

	private void decideNotToPurchaseSomething() {
		Button purchaseUnitsButton = (Button) axisAndAlliesActivity
				.findViewById(R.id.purchase_units_button);
		purchaseUnitsButton.performClick();

		Intent originalIntent = axisAndAlliesActivity.lastSentIntent;

		PurchaseActivity activity = new PurchaseActivity();
		activity.setIntent(originalIntent);
		activity.onCreate(null);

		TableLayout tableLayout = (TableLayout) activity
				.findViewById(R.id.purchasables_table);
		Button addOneItemButton = (Button) ((TableRow) tableLayout
				.getChildAt(1)).getChildAt(1);
		addOneItemButton.performClick();

		Button cancelPurchaseButton = (Button) activity
				.findViewById(R.id.cancel_purchase_button);
		cancelPurchaseButton.performClick();
	}

	private void assertWhetherPurchaseHasBeenMade(
			final boolean whetherPurchaseShouldHaveBeenMade) {
		int firstPlayerIpcs = getPlayerIpc(R.id.playerOneIpcs);
		assertEquals("The IPCs for the first country were not as expected!",
				whetherPurchaseShouldHaveBeenMade,
				PLAYER_ONE_STARTING_IPCS != firstPlayerIpcs);
	}

	private List<Integer> getPlayerIpcs() {
		return Arrays.asList(getPlayerIpc(R.id.playerOneIpcs),
				getPlayerIpc(R.id.playerTwoIpcs),
				getPlayerIpc(R.id.playerThreeIpcs),
				getPlayerIpc(R.id.playerFourIpcs),
				getPlayerIpc(R.id.playerFiveIpcs));

	}

	private Integer getPlayerIpc(int id) {
		return Integer.parseInt((String) ((TextView) axisAndAlliesActivity
				.findViewById(id)).getText());
	}

}
