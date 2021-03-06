package au.com.main;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import au.com.bean.ImageFieldText;
import au.com.constants.ConfigEnum;
import au.com.constants.Constants;
import au.com.constants.ReturnCodes;
import au.com.main.R.drawable;
import au.com.wp.util.Util;

@SuppressWarnings("deprecation")
public class GalleryST extends Activity {

	private SpinnerAdapter spinAdpt;
	private Gallery gallery;
	private static Context ctx;
	private static float screenPorcentageSize;
	private int screenOrientation;
	private AudioManager audio;

	private int[] thumbIds;
	private boolean[] thumbsSelected;

	protected int galItemBg;
	protected static int clikedPos;
	protected static int imageId;

	protected int maxHeight = 0;
	protected int maxWidth = 0;
	protected int variableW = 0;
	protected int variableH = 0;

	protected Integer[] ids = null;
	protected List<ImageFieldText> listEntries = new ArrayList<ImageFieldText>();
	protected SoftReference<List<ImageFieldText>> directoryEntries = new SoftReference<List<ImageFieldText>>(listEntries);
	protected Bitmap bmp;
	protected BitmapFactory.Options bmpOptions;
	protected Resources mResource;


	// List<ImageFieldText> directoryEntries = new ArrayList<ImageFieldText>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.galery);

		GalleryST.ctx = this.getApplicationContext();
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mResource = getResources();

		if (savedInstanceState == null) {
			MediaPlayer mp = MediaPlayer.create(GalleryST.ctx, R.raw.tostransporter);
			mp.start();
			mp.release();

			savedInstanceState = new Bundle();
			Util u = new Util(GalleryST.ctx);
			GalleryST.screenPorcentageSize = Float.parseFloat(u.getParamValue(ConfigEnum.SIZE));
			screenOrientation = Integer.parseInt(u.getParamValue(ConfigEnum.ORIENTATION));
			setRequestedOrientation(screenOrientation);
			savedInstanceState.putString(Constants.FIRST, Constants.NOT_FIRST);
		}

		gallery = (Gallery) findViewById(R.id.examplegallery);
		if (spinAdpt == null) {
			spinAdpt = new AddImgAdp(this);
		}
		gallery.setAdapter(spinAdpt);

		gallery.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				GalleryST.imageId = GalleryST.this.listEntries.get(position).getmNumberImg();
				AlertDialog.Builder alt_bld = new AlertDialog.Builder(GalleryST.this);
				alt_bld.setMessage("Set it as wallpaper?").setCancelable(false)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								GalleryST.this.updatePicture();
							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								return;
							}
						});
				AlertDialog alert = alt_bld.create();
				alert.setIcon(R.drawable.tng);
				alert.setTitle("Wallpaper update");
				alert.show();
			}
		});

	}


	@Override
	public void onLowMemory() {
		super.onLowMemory();
		spinAdpt = null;
		System.gc();
		spinAdpt = new AddImgAdp(GalleryST.this);
	}


	public void updatePicture() {
		new UpdateWallpaper().execute();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				MediaPlayer mp = MediaPlayer.create(GalleryST.ctx, R.raw.tosturboliftdoor);
				mp.start();
				System.gc();
				finish();
				return true;

			case KeyEvent.KEYCODE_VOLUME_UP:
				audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				return true;

			case KeyEvent.KEYCODE_VOLUME_DOWN:
				audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
				return true;

			default:
				return false;
		}
	}

	public class AddImgAdp extends BaseAdapter {
		private Context ctx;
		private Class<drawable> rDrawable;
		protected Util u = new Util(GalleryST.this);
		protected Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		protected Point p = u.getDisplaySize(d);
		protected int numberOfImages = 0;


		public AddImgAdp(Context c) {
			ctx = c;
			if (rDrawable == null) {
				rDrawable = R.drawable.class; // getDrawable(rClass);
			}
			if (GalleryST.this.listEntries == null || GalleryST.this.listEntries.size() == 0) {
				GalleryST.this.listEntries = GalleryST.this.directoryEntries.get();
			}

			if (GalleryST.this.listEntries == null || GalleryST.this.listEntries.size() == 0) {
				Util u = new Util(GalleryST.ctx);
				GalleryST.this.listEntries = u.getListReady(rDrawable);
			}

			if (GalleryST.this.listEntries != null && GalleryST.this.listEntries.size() > 0) {
				numberOfImages = GalleryST.this.listEntries.size();
			}
			setValuesWhenOrientationHasChanged();
		}


		private void setBitmapList() {
			if (ids == null || ids.length == 0) {
				Util u = new Util(GalleryST.this);
				ids = u.loadUpImagesIds();
			}
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#finalize()
		 */
		@Override
		protected void finalize() throws Throwable {
			rDrawable = null;
			System.gc();
			super.finalize();
		}


		public int getCount() {
			return numberOfImages;
		}


		public Object getItem(int position) {
			return position;
		}


		public long getItemId(int position) {
			return position;
		}


		private void setValuesWhenOrientationHasChanged() {
			u = new Util(GalleryST.this);
			d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			p = u.getDisplaySize(d);
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imgView = new ImageView(ctx);
			float x = 0f;
			float y = 0f;

			if (d.getRotation() == 0) {
				if (maxHeight == 0 || maxWidth == 0) {
					setValuesWhenOrientationHasChanged();
					maxHeight = p.y;
					maxWidth = p.x;
					setBitmapList();
					List<Float> coordXY = getPorcentagePortrait(GalleryST.screenPorcentageSize, maxWidth, maxHeight);
					x = coordXY.get(Constants.Y_SIZE);
					y = coordXY.get(Constants.X_SIZE);
					variableH = (int) x;
					variableW = (int) y;
				}
				// Portrait
			} else {
				if (maxHeight == 0 || maxWidth == 0) {
					setValuesWhenOrientationHasChanged();
					maxHeight = p.y;
					maxWidth = p.x;
					setBitmapList();
					List<Float> coordXY = getPorcentageLandscapet(GalleryST.screenPorcentageSize, maxWidth, maxHeight);
					x = coordXY.get(Constants.Y_SIZE);
					y = coordXY.get(Constants.X_SIZE);
					variableH = (int) x;
					variableW = (int) y;
				}
				// Landscape
			}
			if (GalleryST.this.listEntries == null || GalleryST.this.listEntries.size() == 0) {
				GalleryST.this.listEntries = GalleryST.this.directoryEntries.get();
			}
			// Bitmap bmp = BitmapFactory.decodeResource(GalleryST.this.getResources(), ids[position]);
			// Bitmap finalBmp = Bitmap.createScaledBitmap(bmp, maxWidth / 2, maxHeight / 2, true);

			// imgView.setImageBitmap(Bitmap.createScaledBitmap(bmp, maxWidth / 4, maxHeight / 4, true));
			try {
				bmpOptions = new BitmapFactory.Options();
				bmpOptions.inJustDecodeBounds = true;
				bmpOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
				BitmapFactory.decodeResource(mResource, ids[position], bmpOptions);
				bmp = Bitmap.createBitmap(bmpOptions.outWidth, bmpOptions.outHeight, Bitmap.Config.ARGB_8888);

				bmpOptions = new BitmapFactory.Options();
				bmpOptions.inJustDecodeBounds = false;
				bmpOptions.inPurgeable = true;
				bmpOptions.inSampleSize = 3;
				bmpOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bmp = BitmapFactory.decodeResource(mResource, ids[position], bmpOptions);

				imgView.setImageBitmap(bmp);

				// bmp.recycle();
				// bmp = null;
				System.gc();
				imgView.setLayoutParams(new Gallery.LayoutParams(variableW, variableH));
				imgView.setScaleType(ImageView.ScaleType.FIT_XY);
			} catch (Exception e) {
				Toast.makeText(GalleryST.this, "An error has occoured, please try it again", Toast.LENGTH_LONG).show();
			}

			return imgView;
		}
	}


	/**
	 * Returns startX, startY, endX, endY in a List<Integer>
	 * 
	 * @param porcentage
	 * @param xMax
	 * @param yMax
	 * @return
	 */
	public List<Float> getPorcentagePortrait(float porc, int xMax, int yMax) {

		float porcX = porc;
		float porcY = porcX / Constants.RATIO;

		// Get screen centre
		float xM = xMax;
		float yM = yMax;

		// Get the porcentage asked in px from screen
		float porcentageX = (xM / 100) * porcX;
		float porcentageY = (yM / 100) * porcY;

		List<Float> coordXY = new ArrayList<Float>();
		coordXY.add(porcentageX);
		coordXY.add(porcentageY);
		return coordXY;
	}


	public List<Float> getPorcentageLandscapet(float porc, int xMax, int yMax) {

		float porcX = porc / Constants.RATIO;
		float porcY = porc;

		// Get screen centre
		float xM = xMax;
		float yM = yMax;

		// Get the porcentage asked in px from screen
		float porcentageX = (xM / 100) * porcX;
		float porcentageY = (yM / 100) * porcY;

		List<Float> coordXY = new ArrayList<Float>();
		coordXY.add(porcentageX);
		coordXY.add(porcentageY);
		return coordXY;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		Util u = new Util(GalleryST.ctx);
		if (u.checkForServiceProject()) {
			menu.getItem(0).setEnabled(true);
			menu.getItem(0).setVisible(true);
		} else {
			menu.getItem(0).setEnabled(false);
			menu.getItem(0).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.about: {
				callAbout();
				break;
			}

			case R.id.gallery: {
				Intent myIntent = new Intent(GalleryST.ctx, GallerySettingST.class);
				Bundle b = new Bundle();
				b.putFloat(ConfigEnum.SIZE.getCode(), GalleryST.screenPorcentageSize);
				b.putInt(ConfigEnum.ORIENTATION.getCode(), screenOrientation);
				myIntent.putExtras(b);
				startActivityForResult(myIntent, ReturnCodes.REQUEST_OK.getIntCode());
				break;
			}

			case R.id.service: {
				Intent myIntent = new Intent(GalleryST.ctx, ServiceST.class);
				Bundle b = new Bundle();
				b.putIntArray(Constants.IMAGES_IDS, thumbIds);
				b.putBooleanArray(Constants.IDS_SELECTED, thumbsSelected);
				myIntent.putExtras(b);
				startActivityForResult(myIntent, ReturnCodes.ACTIVITY_SERVICE_RESULT.getIntCode());
				break;
			}
		}
		return super.onContextItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case (Constants.ACTIVITY_GALLERY_RESULT): {
				Bundle b = data.getExtras();
				GalleryST.screenPorcentageSize = b.getFloat(ConfigEnum.SIZE.getCode());
				GalleryST.this.setRequestedOrientation(b.getInt(ConfigEnum.ORIENTATION.getCode()));
				((BaseAdapter) gallery.getAdapter()).notifyDataSetChanged();
				break;
			}

			case (Constants.ACTIVITY_SERVICE_RESULT): {
				Bundle b = data.getExtras();
				thumbIds = b.getIntArray(Constants.IMAGES_IDS);
				thumbsSelected = b.getBooleanArray(Constants.IDS_SELECTED);
				break;
			}
		}
	}


	public void callAbout() {
		final String nl = System.getProperty("line.separator");
		final String message = "Star Trek Wallpapers" + nl + nl + "By Klaus Villaca" + nl + "Janyary 2013";
		AlertDialog.Builder about = new AlertDialog.Builder(this);
		about.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		AlertDialog alert = about.create();
		alert.setTitle("About");
		alert.setIcon(R.drawable.tng);
		alert.show();
	}

	class UpdateWallpaper extends AsyncTask<Void, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(GalleryST.this);


		@Override
		protected Void doInBackground(Void... param) {
			Bitmap wallpaper = null;
			try {
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				int height = displayMetrics.heightPixels;
				int width = displayMetrics.widthPixels << 1;

				BitmapFactory.Options options = new Options();
				options.inScaled = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				options.inDither = false;
				options.inJustDecodeBounds = true;

				BitmapFactory.decodeResource(GalleryST.this.getResources(), GalleryST.imageId, options);
				options.inSampleSize = calculateInSampleSize(options, width, height);
				options.inJustDecodeBounds = false;

				wallpaper = BitmapFactory.decodeResource(GalleryST.this.getResources(), GalleryST.imageId, options);
				WallpaperManager wManager = WallpaperManager.getInstance(GalleryST.this);
				wManager.setBitmap(wallpaper);
				wallpaper.recycle();
				wallpaper = null;
				System.gc();
				return null;
			} catch (IllegalArgumentException e) {
				Log.e(Constants.TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(Constants.TAG, e.getMessage());
			} catch (SecurityException e) {
				Log.e(Constants.TAG, e.getMessage());
			}
			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			Toast.makeText(GalleryST.ctx, "Applied with success", Toast.LENGTH_SHORT).show();
		}


		@Override
		protected void onCancelled() {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}


		@Override
		protected void onPreExecute() {
			dialog.setCancelable(true);
			dialog.setMessage("Applying wallpaper...");
			dialog.setIndeterminate(true);
			dialog.show();
		}
	}


	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	class SoundWallpaper extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... param) {
			MediaPlayer mp = MediaPlayer.create(GalleryST.ctx, R.raw.tostransporter);
			mp.start();
			mp.release();
			mp = null;
			System.gc();
			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
		}


		@Override
		protected void onPreExecute() {
		}
	}

	class LoadAllWallpapers extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialog = new ProgressDialog(GalleryST.this);


		@Override
		protected void onPostExecute(Void result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}


		@Override
		protected void onCancelled() {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}


		@Override
		protected void onPreExecute() {
			dialog.setCancelable(true);
			dialog.setMessage("Loading wallpapers...");
			dialog.setIndeterminate(true);
			dialog.show();
		}


		@Override
		protected Void doInBackground(Void... params) {

			return null;
		}

	}
}