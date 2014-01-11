package au.com.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import au.com.constants.ConfigEnum;
import au.com.constants.Constants;
import au.com.constants.ReturnCodes;
import au.com.constants.ScreenHorientation;
import au.com.wp.util.Util;

public class GallerySettingST extends Activity implements SeekBar.OnSeekBarChangeListener {
	private EditText mProgressText;
	private Button toDefault;
	private SeekBar screenValue;
	private ToggleButton toggleLandscape;

	private int value_mSeekBar;
	private double max_mSeekBar;
	private double min_mSeekBar;
	private double start_mSeekBar;
	private int mValue;
	private float settedValue;
	private float returnValue;
	private String valueToReturn;
	private Intent settingsIntent;

	private static final float SCREEN_DEFAULT_VALUE = 65f;
	private static final float MAGIC_NUMBER_THREE = 3f;
	private static final int MAX_SCREEN_SIZE = 100;
//	private static final int ZERO = 0;
	
	private static Context ctx;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallerysettings);
		GallerySettingST.ctx = this.getApplicationContext();

		settingsIntent = getIntent();
		Util u = new Util(GallerySettingST.ctx);
		int orientation = Integer.parseInt(u.getParamValue(ConfigEnum.ORIENTATION));

		Bundle bundle = settingsIntent.getExtras();
		settedValue = bundle.getFloat(ConfigEnum.SIZE.getCode());

		setRequestedOrientation(orientation);

		@SuppressWarnings("unused")
		TextView textExplanation = (TextView) findViewById(R.id.gallerytextView1);

		screenValue = (SeekBar) findViewById(R.id.seekBar1);
		screenValue.setMax(MAX_SCREEN_SIZE);
		screenValue.setKeyProgressIncrement(1);

		mProgressText = (EditText) findViewById(R.id.gallerytextscreensize);
		mProgressText.setWidth(100);

		min_mSeekBar = 10; // Min value to return for mValue
		max_mSeekBar = 100; // Max value to return for mValue
		start_mSeekBar = settedValue; // Starting value within range [min_mSeekBar-max_mSeekBar]
		valueToReturn = settedValue + Constants.EMPTY;

		mValue = (int) start_mSeekBar; // That's the value that will change within the defined range

		double calc = ((start_mSeekBar - min_mSeekBar) / (max_mSeekBar - min_mSeekBar)) * 100;

		Log.i("Hub", "calc=" + calc);

		value_mSeekBar = (int) Math.round(calc);
		screenValue.setProgress(value_mSeekBar);

		Log.i("Hub", "value_mSeekBar[0-100]=" + value_mSeekBar);

		screenValue.setOnSeekBarChangeListener(this);
		mProgressText.setText(Double.toString(mValue));
//		if (mProgressText.getText().toString() != null && !mProgressText.getText().toString().equals(Constants.EMPTY)) {
//			String textNumber = checkRange(mProgressText.getText().toString());
//			mProgressText.setText(textNumber);
//		}

		toggleLandscape = (ToggleButton) findViewById(R.id.toggleButton1);
		if (orientation == ScreenHorientation.SCREEN_PORTRAIT.getIntCode()) {
			toggleLandscape.setChecked(false);
		} else {
			toggleLandscape.setChecked(true);
		}
		toggleLandscape.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Util u = new Util(GallerySettingST.ctx);
				if (toggleLandscape.isChecked()) {
					u.updateParamValue(ConfigEnum.ORIENTATION, ScreenHorientation.SCREEN_SENSOR.getStrCode() + Constants.EMPTY);
				} else {
					u.updateParamValue(ConfigEnum.ORIENTATION, ScreenHorientation.SCREEN_PORTRAIT.getStrCode() + Constants.EMPTY);
				}
				u.saveAll();
			}
		});

		toDefault = (Button) findViewById(R.id.button1);
		toDefault.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setDefaults();
			}
		});
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (valueToReturn != null && valueToReturn.trim().length() != 0) {
					returnValue = Float.parseFloat(valueToReturn);
				}
//				Intent myIntent = new Intent(this, GalleryST.class);
				Bundle bdl = settingsIntent.getExtras();
				bdl.putFloat(ConfigEnum.SIZE.getCode(), returnValue);

				Util u = new Util(GallerySettingST.ctx);
				String toInt = u.getParamValue(ConfigEnum.ORIENTATION);
				u.updateParamValue(ConfigEnum.SIZE, valueToReturn);
				u.saveAll();
				bdl.putInt(ConfigEnum.ORIENTATION.getCode(), Integer.parseInt(toInt));
				settingsIntent.putExtras(bdl);
				setResult(ReturnCodes.ACTIVITY_GALLERY_RESULT.getIntCode(), settingsIntent);
				finish();
				return true;

			default:
				return false;
		}
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		Log.i("Hub", "progress[0-100]=" + progress);
		mValue = (int) (((max_mSeekBar - min_mSeekBar) * (double) progress / 100) + min_mSeekBar);
		Log.i("Hub", "mValue=" + mValue);
		valueToReturn = Double.toString(mValue);
		mProgressText.setText(Double.toString(mValue));
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}


	private void setDefaults() {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("Confirm? ").setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						valueToReturn = SCREEN_DEFAULT_VALUE + Constants.EMPTY;
						int toSet = Math.round(SCREEN_DEFAULT_VALUE - MAGIC_NUMBER_THREE);
						GallerySettingST.this.screenValue.setProgress(toSet);
						Util u = new Util(GallerySettingST.ctx);
						u.resetToDefault();
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
				});
		AlertDialog alert = alt_bld.create();
		alert.setIcon(R.drawable.tng);
		alert.setTitle("Set to Default Settings");
		alert.show();
	}
	
//	private String checkRange(String editable) {
//		String strReturn = editable.toString() ;
//		float valueToCheck = Float.parseFloat(strReturn + "f");
//		if (valueToCheck < 10) {
//			strReturn = "10";
//		}
//		if (valueToCheck > 100) {
//			strReturn = "100";
//		}
//		mProgressText.setText(strReturn);
//		return strReturn;
//	}
}
