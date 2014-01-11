package au.com.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import au.com.constants.ConfigEnum;
import au.com.constants.Constants;
import au.com.constants.OnOffEnum;
import au.com.constants.ReturnCodes;
import au.com.constants.TimeIntervalEnum;
import au.com.wp.util.EnumHelperClass;
import au.com.wp.util.Util;

public class ServiceST extends Activity {

	private static Context ctx;
	@SuppressWarnings("unused")
	private TextView firstText;
	@SuppressWarnings("unused")
	private TextView timeIntervalText;
	private CheckBox enableService;
	private Button choosePictures;
	private Spinner timeFrame;
	private String interval;
	private long intervalTime;

	private Intent intentWPService;
	private int[] imagesId = null;
	private boolean[] thumbsSelected = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		ServiceST.ctx = this.getApplicationContext();
		intentWPService = getIntent();

		firstText = (TextView) findViewById(R.id.settingsphrase);
		enableService = (CheckBox) findViewById(R.id.checkBox1);
		enableService.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Util u = new Util(ServiceST.ctx);
				if (!enableService.isChecked()) {
					u.updateParamValue(ConfigEnum.SERVICE, OnOffEnum.ON.getCode());
				} else {
					u.updateParamValue(ConfigEnum.SERVICE, OnOffEnum.OFF.getCode());
				}
				u.saveAll();
				checkIfEnabled();
			}
		});

		choosePictures = (Button) findViewById(R.id.button1);
		choosePictures.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				chooseWallpapers();
			}
		});

		timeIntervalText = (TextView) findViewById(R.id.choosetxt2);
		timeFrame = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timestochange,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timeFrame.setAdapter(adapter);
		timeFrame.setOnItemSelectedListener(new MyOnItemSelectedListener());

		Util u = new Util(ServiceST.ctx);
		String enabledStr = u.getParamValue(ConfigEnum.SERVICE);
		if (enabledStr == null || enabledStr.equals(OnOffEnum.OFF.getCode())) {
			enableService.setChecked(false);
			choosePictures.setEnabled(false);
			timeFrame.setEnabled(false);
		} else {
			enableService.setChecked(true);
			choosePictures.setEnabled(true);
			timeFrame.setEnabled(true);
		}
	}


	private void checkIfEnabled() {
		if (!enableService.isChecked()) {
			choosePictures.setEnabled(false);
			timeFrame.setEnabled(false);
		} else {
			choosePictures.setEnabled(true);
			timeFrame.setEnabled(true);
		}
	}


	private void chooseWallpapers() {
		// new ChooseThumbnails().execute();
		Intent myIntent = new Intent(ServiceST.ctx, GallerySelect.class);
		Bundle b = ServiceST.this.intentWPService.getExtras();
		if (thumbsSelected == null || thumbsSelected.length == 0) {
			thumbsSelected = new boolean[208];
		}
		b.putBooleanArray(Constants.IDS_SELECTED, ServiceST.this.thumbsSelected);
		myIntent.putExtras(b);
		startActivityForResult(myIntent, ReturnCodes.REQUEST_OK.getIntCode());
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Util u = new Util(ServiceST.ctx);
			Intent intent = new Intent(Intent.ACTION_RUN);
			intent.setComponent(new ComponentName("au.com.services.main",
					"au.com.services.main.StarTrekServiceActivity"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			final PackageManager packageManager = ctx.getPackageManager();
			List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
					PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
			
			if (ServiceST.this.enableService.isChecked() && list.size() > 0
					&& getOnlySelectedOnes(ServiceST.this.imagesId, ServiceST.this.thumbsSelected) != null
					&& getOnlySelectedOnes(ServiceST.this.imagesId, ServiceST.this.thumbsSelected).length > 1) {
				Toast.makeText(this, "Star Trek Wallpaper Service Starting Up", Toast.LENGTH_LONG);
				Log.i("Log", "Statring service installed." + list.size());
				// Update the file to enable the service, and pass all bitmaps ids on it.
				int[] idSelecteds = getOnlySelectedOnes(ServiceST.this.imagesId, ServiceST.this.thumbsSelected);
				u.createNewFile(intervalTime, 0, idSelecteds, true);
				u.updateParamValue(ConfigEnum.SERVICE, OnOffEnum.ON.getCode());
			} else {
				Toast.makeText(this, "Star Trek Wallpaper Service Stopping", Toast.LENGTH_LONG);
				int[] idSelecteds = { 0 };
				u.createNewFile(intervalTime, 0, idSelecteds, false);
				u.updateParamValue(ConfigEnum.SERVICE, OnOffEnum.OFF.getCode());
			}
			u.saveAll();
			ServiceST.ctx.startActivity(intent);
			setResult(ReturnCodes.ACTIVITY_SERVICE_RESULT.getIntCode(), ServiceST.this.intentWPService);
			finish();
			return true;
		}
		return false;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case (Constants.ACTIVITY_GALLERY_RESULT): {
				Bundle b = data.getExtras();
				imagesId = b.getIntArray(Constants.IMAGES_IDS);
				thumbsSelected = b.getBooleanArray(Constants.IDS_SELECTED);
				intentWPService.putExtras(b);
				break;
			}
		}
	}


	@SuppressWarnings("unused")
	private Intent updateIntentForService(Intent oldIntent) {
		ServiceST.this.imagesId = getOnlySelectedOnes(ServiceST.this.imagesId, ServiceST.this.thumbsSelected);
		Bundle b = oldIntent.getExtras();
		if (b == null) {
			b = new Bundle();
		}
		b.putIntArray(Constants.IDS_SELECTED, ServiceST.this.imagesId);
		b.putLong(Constants.INTERVAL_TIME, ServiceST.this.intervalTime);
		oldIntent.putExtras(b);
		return oldIntent;
	}


	private int[] getOnlySelectedOnes(int[] ids, boolean[] selected) {
		List<Integer> selectedIds = new ArrayList<Integer>();
		for (int i = 0; i < ids.length; i++) {
			if (selected[i]) {
				selectedIds.add(ids[i]);
			}
		}
		Util u = new Util(ServiceST.ctx);
		int[] idsToReturn = u.getIntArrayFromIntegerList(selectedIds);
		return idsToReturn;
	}


	// Class that will capture the selection.
	class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			interval = parent.getItemAtPosition(pos).toString();
			TimeIntervalEnum enumType = getEnumTypeFromString(interval);
			ServiceST.this.setIntervalasLong(enumType);
			// Toast.makeText(parent.getContext(), "The interval selected is " + interval, Toast.LENGTH_LONG).show();
		}


		@SuppressWarnings("rawtypes")
		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}


	private TimeIntervalEnum getEnumTypeFromString(String intervalStr) {
		TimeIntervalEnum[] enumValues = TimeIntervalEnum.values();
		TimeIntervalEnum returnEnum = TimeIntervalEnum.MIN_5;
		for (TimeIntervalEnum timeIntervalEnum : enumValues) {
			if (timeIntervalEnum.getStrCode().equals(intervalStr.trim())) {
				returnEnum = timeIntervalEnum;
				break;
			}
		}
		return returnEnum;
	}


	private void setIntervalasLong(TimeIntervalEnum intervaEnum) {
		ServiceST.this.intervalTime = EnumHelperClass.getMilliseconds(intervaEnum);
	}

}
