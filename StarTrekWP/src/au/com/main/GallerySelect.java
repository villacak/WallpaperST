package au.com.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import au.com.bean.DataObject;
import au.com.bean.ImageFieldText;
import au.com.constants.Constants;
import au.com.constants.ReturnCodes;
import au.com.main.R.drawable;
import au.com.wp.util.Util;

public class GallerySelect extends Activity {

	protected static List<ImageFieldText> directoryEntries = new ArrayList<ImageFieldText>();
	private static Context ctx;
	private GridView gv;
	private Button selectBtn;

	private static boolean[] thumbnailsselection;
	private static Bitmap[] thumbnails;
	private static Integer[] mThumbIds;
	private static ImageAdapter adapter;
	protected static List<Map<String, Object>> mapDraws = new ArrayList<Map<String, Object>>();

	private Intent gallerySelectIntent;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.galleryselect2);
		GallerySelect.ctx = this.getApplicationContext();

		gallerySelectIntent = getIntent();
		if (gallerySelectIntent.getExtras().getBooleanArray(Constants.IDS_SELECTED) == null && GallerySelect.mapDraws == null ) {
			GallerySelect.thumbnailsselection = new boolean[208];
		} else {
			GallerySelect.thumbnailsselection = gallerySelectIntent.getExtras().getBooleanArray(Constants.IDS_SELECTED);
		}
		if (GallerySelect.mapDraws == null || GallerySelect.mapDraws.size() == 0) {
			loadUpImages();
		}
		new LoadThumbnails().execute();

		DataObject data = (DataObject) getLastNonConfigurationInstance();
		if (data == null) {
			data = populateData();
		} else {
			populateVariables(data);
		}


		// GallerySelect.adapter = new ImageAdapter();

		gv = (GridView) findViewById(R.id.phoneImageGrid);
		gv.setAdapter(GallerySelect.adapter);

		selectBtn = (Button) findViewById(R.id.selectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final int len = GallerySelect.thumbnailsselection.length;
				final int maxNumberAllowed = 10;
				int cnt = 0;
				String selectImages = "";
				for (int i = 0; i < len; i++) {
					if (GallerySelect.thumbnailsselection[i]) {
						cnt++;
					}
				}
				if (cnt <= 1) {
					Toast.makeText(getApplicationContext(), "You must select at least two images", Toast.LENGTH_LONG)
							.show();
				} else if (cnt > maxNumberAllowed) {
					Toast.makeText(getApplicationContext(),
							"You've selected Total of " + cnt + " image(s), when max allowed is 10.", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(getApplicationContext(), "You've selected Total " + cnt + " image(s).",
							Toast.LENGTH_LONG).show();
					returnToPreviousActivity();
				}
				Log.d("SelectedImages", selectImages);
			}
		});
	}


	@Override
	protected void onStart() {
		super.onStart();
	}


	private DataObject populateData() {
		DataObject dObject = new DataObject();
		dObject.setMapDraws(GallerySelect.mapDraws);
		dObject.setmThumbIds(GallerySelect.mThumbIds);
		dObject.setThumbnails(GallerySelect.thumbnails);
		dObject.setThumbnailsselection(GallerySelect.thumbnailsselection);
		return dObject;
	}


	private void populateVariables(DataObject data) {
		GallerySelect.mapDraws = data.getMapDraws();
		GallerySelect.mThumbIds = data.getmThumbIds();
		GallerySelect.thumbnails = data.getThumbnails();
		GallerySelect.thumbnailsselection = data.getThumbnailsselection();
	}


	@Override
	public Object getLastNonConfigurationInstance() {
		final DataObject data = populateData();
		return data;
		// return super.getLastNonConfigurationInstance();
	}


	private void loadUpImages() {
		Class<drawable> rDrawable = null;
		Util u = new Util(GallerySelect.ctx);

		if (rDrawable == null) {
			rDrawable = R.drawable.class;
		}
		if (GallerySelect.mapDraws == null || GallerySelect.mapDraws.size() == 0) {
			GallerySelect.mapDraws = u.getMapReady(rDrawable);
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MediaPlayer mp = MediaPlayer.create(GallerySelect.ctx, R.raw.tosturboliftdoor);
			mp.start();
			System.gc();
			returnToPreviousActivity();
			return true;
		}
		return false;
	}


	private void returnToPreviousActivity() {
		Util u = new Util(GallerySelect.ctx);
		List<Integer> toConvert = new ArrayList<Integer>();
		Collections.addAll(toConvert, GallerySelect.mThumbIds);
		int[] intArrayIds = u.getIntArrayFromIntegerList(toConvert);

		Bundle b = gallerySelectIntent.getExtras(); // Bundle();
		b.putIntArray(Constants.IMAGES_IDS, intArrayIds);
		b.putBooleanArray(Constants.IDS_SELECTED, GallerySelect.thumbnailsselection);
		gallerySelectIntent.putExtras(b);
		setResult(ReturnCodes.ACTIVITY_GALLERY_RESULT.getIntCode(), gallerySelectIntent);
		finish();
	}


	private class LoadThumbnails extends AsyncTask<Void, Void, Void> {
		protected ProgressDialog progressDialog = new ProgressDialog(GallerySelect.this);;


		@Override
		protected Void doInBackground(Void... arg0) {
			if (GallerySelect.thumbnails == null || GallerySelect.thumbnails.length == 0) {
				GallerySelect.adapter = new ImageAdapter();
			}
			return null;
		}


		@Override
		protected void onCancelled() {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}


		@Override
		protected void onPostExecute(Void result) {
			if (progressDialog.isShowing()) {
				GallerySelect.this.gv.setAdapter(new ImageAdapter());
				progressDialog.dismiss();
			}
		}


		@Override
		protected void onPreExecute() {
			this.progressDialog.setTitle("Loading thumbnails");
			this.progressDialog.setMessage("Please wait for one or two minutes.");
			this.progressDialog.setIndeterminate(true);
			this.progressDialog.setCancelable(false);
			this.progressDialog.show();
		}
	}


	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;


		public ImageAdapter() {
			this.mInflater = (LayoutInflater) GallerySelect.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			Util u = new Util(GallerySelect.ctx);
			if (GallerySelect.mThumbIds == null || GallerySelect.mThumbIds.length == 0) {
				GallerySelect.mThumbIds = u.loadImageArray(GallerySelect.mapDraws);
			}

			if (GallerySelect.thumbnails == null || GallerySelect.thumbnails.length == 0) {
				GallerySelect.thumbnails = u.copyIdToBitmap(GallerySelect.mThumbIds);
			}
			if (GallerySelect.thumbnailsselection == null || GallerySelect.thumbnailsselection.length == 0) {
				GallerySelect.thumbnailsselection = new boolean[GallerySelect.thumbnails.length];
			}
		}


		public int getCount() {
			return GallerySelect.mThumbIds.length;
		}


		public Object getItem(int position) {
			return null;
		}


		public long getItemId(int position) {
			return 0;
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.galleryitem, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
				holder.imageview.setScaleType(ScaleType.MATRIX);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.imageview.setScaleType(ScaleType.MATRIX);
			holder.imageview.setAdjustViewBounds(true);
			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (GallerySelect.thumbnailsselection[id]) {
						cb.setChecked(false);
						GallerySelect.thumbnailsselection[id] = false;
					} else {
						cb.setChecked(true);
						GallerySelect.thumbnailsselection[id] = true;
					}
				}
			});
			holder.imageview.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					// int id = v.getId();
					// Intent intent = new Intent();
					// intent.setAction(Intent.ACTION_VIEW);
					// intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
					// startActivity(intent);
				}
			});
			holder.imageview.setImageBitmap(GallerySelect.thumbnails[position]);
			holder.checkbox.setChecked(GallerySelect.thumbnailsselection[position]);
			holder.id = position;
			return convertView;
		}


		public boolean[] getThumbnailsselection() {
			return GallerySelect.thumbnailsselection;
		}


		public void setThumbnailsselection(boolean[] thumbnailsselection) {
			GallerySelect.thumbnailsselection = thumbnailsselection;
		}


		public Bitmap[] getThumbnails() {
			return GallerySelect.thumbnails;
		}


		public void setThumbnails(Bitmap[] thumbnails) {
			GallerySelect.thumbnails = thumbnails;
		}

	}


	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		int id;
	}
}
