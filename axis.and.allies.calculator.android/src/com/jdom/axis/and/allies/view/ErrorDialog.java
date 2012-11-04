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

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ErrorDialog {
	private String message;
	private Context context;
	private Exception exception;

	public ErrorDialog(Context context, String message) {
		this.context = context;
		this.message = message;
	}

	public ErrorDialog(Context context, String message, Exception exception) {
		this(context, message);
		this.exception = exception;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public void show() {
		if (exception == null) {
			showWithoutException();
		} else {
			showWithException();
		}
	}

	private void showWithoutException() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Error");
		builder.setMessage(message);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showWithException() {
		AlertDialog.Builder builder;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.error, null);
		TextView text = (TextView) layout.findViewById(R.id.error_text);
		text.setMovementMethod(new ScrollingMovementMethod());
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		text.setText(sw.toString());
		builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		builder.setMessage(message);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
