package au.com.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;

public class STMain extends Activity {

	private Handler mHandler;
	private LicenseCheckerCallback mLicenseCheckerCallback;
	private LicenseChecker mChecker;

	private final String BASE64_PUBLIC_KEY = "your public key in here";
	private static final byte[] SALT = new byte[] {0,0,0,0,0}; // Your Salt in here

	@SuppressWarnings("unused")
	private static boolean licensed = true;
	@SuppressWarnings("unused")
	private static boolean didCheck = false;
	@SuppressWarnings("unused")
	private static boolean checkingLicense = false;
	
	private final String INDEX = "index";
	private final int MAX_TRYS = 4;
	private int counter = 0;

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mHandler = new Handler();
		preferences = getSharedPreferences("preferences", Context.CONTEXT_IGNORE_SECURITY);
		editor = preferences.edit();

		counter = preferences.getInt(INDEX, 0);
		Toast.makeText(this, "Checking Application License", Toast.LENGTH_SHORT).show();

		checkLicense();
	}


	private void displayResult(final String result) {
		mHandler.post(new Runnable() {
			public void run() {
				setProgressBarIndeterminateVisibility(false);
			}
		});
	}


	protected void doCheck() {
		didCheck = false;
		checkingLicense = true;
		setProgressBarIndeterminateVisibility(true);
		mChecker.checkAccess(mLicenseCheckerCallback);
	}


	protected void checkLicense() {
		Log.e("LICENSE", "checkLicense");
		mHandler = new Handler();
		String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
		mLicenseCheckerCallback = new MyLicenseCheckerCallback();
		mChecker = new LicenseChecker(this, new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY);
		doCheck();
	}

	protected class MyLicenseCheckerCallback implements LicenseCheckerCallback {

		public void allow() {
			Log.e("LICENSE", "allow");
			if (isFinishing()) {
				return;
			}
			displayResult(getString(R.string.allow));
			licensed = true;
			checkingLicense = false;
			didCheck = true;
			editor.putInt(INDEX, 0);
			editor.commit();
			Intent myIntent = new Intent(STMain.this, EulaSTWp.class);
			startActivity(myIntent);
			finish();
		}


		@SuppressWarnings("deprecation")
		public void dontAllow() {
			Log.e("LICENSE", "dontAllow");
			if (isFinishing()) {
				return;
			}
			displayResult(getString(R.string.dont_allow));
			licensed = false;
			checkingLicense = false;
			didCheck = true;
			counter++;
			if (counter > MAX_TRYS) {
				showDialog(0);
			} else {
				editor.putInt(INDEX, counter);
				editor.commit();
				Intent myIntent = new Intent(STMain.this, EulaSTWp.class);
				startActivity(myIntent);
				finish();
			}
		}


		@Override
		public void applicationError(ApplicationErrorCode errorCode) {
			Log.e("LICENSE", "error: " + errorCode);
			if (isFinishing()) {
				return;
			}
			licensed = false;
			String result = String.format(getString(R.string.application_error), errorCode);
			checkingLicense = false;
			didCheck = true;
			displayResult(result);
			finish();
			// showDialog(0);
		}
	}


	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this).setTitle(R.string.unlicensed_dialog_title).setMessage(R.string.unlicensed_dialog_body)
				.setPositiveButton(R.string.buy_button, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
						startActivity(marketIntent);
						finish();
					}
				}).setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})

				.setCancelable(false).setOnKeyListener(new DialogInterface.OnKeyListener() {
					public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
						Log.e("License", "Key Listener");
						finish();
						return true;
					}
				}).create();

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mChecker != null) {
			Log.e("LIcense", "distroy checker");
			mChecker.onDestroy();
		}
	}

	
	
}