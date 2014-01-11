/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.main;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Displays an EULA ("End User License Agreement") that the user has to accept before using the application. Your
 * application should call {@link EulaSTWp#show(android.app.Activity)} in the onCreate() method of the first activity. If
 * the user accepts the EULA, it will never be shown again. If the user refuses, {@link android.app.Activity#finish()}
 * is invoked on your activity.
 */
public class EulaSTWp extends Activity {
	private static final String ASSET_EULA = "EULA";
	private static final String PREFERENCE_EULA_ACCEPTED = "eula.accepted";
	private static final String PREFERENCES_EULA = "eula";

	private static SharedPreferences preferences;

	private static Context context;

	/**
	 * callback to let the activity know when the user has accepted the EULA.
	 */
	public static interface OnEulaAgreedTo {

		/**
		 * Called when the user has accepted the eula and the dialog closes.
		 */
		void onEulaAgreedTo();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		show();
	}


	/**
	 * Displays the EULA if necessary. This method should be called from the onCreate() method of your main Activity.
	 * 
	 * @param activityResp
	 *            The Activity to finish if the user rejects the EULA.
	 * @return Whether the user has agreed already.
	 */
	public void show() { // (final Activity activityResp) {
		EulaSTWp.preferences = EulaSTWp.this.getSharedPreferences(PREFERENCES_EULA, Activity.MODE_PRIVATE);

//		preferences.edit().putBoolean(PREFERENCE_EULA_ACCEPTED, false).commit();

		if (EulaSTWp.preferences.getBoolean(PREFERENCE_EULA_ACCEPTED, false)) {
			onEulaAgreedTo();
		} else {
			// final AlertDialog.Builder builder = new AlertDialog.Builder(activityResp);
			AlertDialog.Builder builder = new AlertDialog.Builder(EulaSTWp.this);
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.eula_accept, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					accept(EulaSTWp.preferences);
					onEulaAgreedTo();
				}
			});
			builder.setNegativeButton(R.string.eula_refuse, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					refuse(EulaSTWp.this);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					refuse(EulaSTWp.this);
				}
			});
			builder.setMessage(readEula(EulaSTWp.this));
			AlertDialog alert = builder.create();
			alert.setTitle(R.string.eula_title);
			alert.show();
		}
	}


	private static void accept(SharedPreferences preferences) {
		preferences.edit().putBoolean(PREFERENCE_EULA_ACCEPTED, true).commit();
	}


	private static void refuse(Activity activity) {
		activity.finish();
	}


	private static CharSequence readEula(Activity activity) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(activity.getAssets().open(ASSET_EULA)));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = in.readLine()) != null)
				buffer.append(line).append('\n');
			return buffer;
		} catch (IOException e) {
			return "";
		} finally {
			closeStream(in);
		}
	}


	/**
	 * Closes the specified stream.
	 * 
	 * @param stream
	 *            The stream to close.
	 */
	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}


	public void onEulaAgreedTo() {
		EulaSTWp.context = this;
		Intent myIntent = new Intent(EulaSTWp.context, GalleryST.class);
		startActivity(myIntent);
		finish();
	}
}
