package au.com.wp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import au.com.bean.ImageFieldText;
import au.com.constants.ConfigEnum;
import au.com.constants.Constants;
import au.com.constants.ImageMap;
import au.com.constants.OnOffEnum;
import au.com.constants.ScreenHorientation;
import au.com.main.R;
import au.com.main.R.drawable;

public class Util {

	private Context ctx;
	private static final String SPLIT_CHARS = ":";
	private static final int EMPTY_LIST = 0;
	private static final int VALUE_LIST = 1;
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final String TAG = "APP_TAG";
	private final String pathForSerivces = "/StarTrekService";
	private final String serviceFileName = "wlIds.ids";
	private final String slashPath = "/";
	private final String deactivate = "deactive";

	private List<String> fileData = new ArrayList<String>();


	public Util(Context ctx) {
		this.ctx = ctx;
		checkFile();
	}


	public void checkFile() {
		File checkfile = ctx.getFileStreamPath(Constants.FILE_NAME_CFG);
		if (!checkfile.exists()) {
			createDefaultFile();
		}
		readAll();
	}


	public void readAll() {
		try {
			fileData = new ArrayList<String>();
			FileInputStream in = ctx.openFileInput(Constants.FILE_NAME_CFG);
			InputStreamReader inputreader = new InputStreamReader(in);
			BufferedReader buffer = new BufferedReader(inputreader);
			String line = null;
			while ((line = buffer.readLine()) != null) {
				fileData.add(line);
			}
			buffer.close();
			inputreader.close();
			in.close();
		} catch (FileNotFoundException e) {
			Log.i("Util.getScreenOrientation ", e.getMessage());
		} catch (IOException e) {
			Log.i("Util.getScreenOrientation ", e.getMessage());
		}
	}


	public void saveAll() {
		try {
			ctx.deleteFile(Constants.FILE_NAME_CFG);
			FileOutputStream out = ctx.openFileOutput(Constants.FILE_NAME_CFG, Context.MODE_PRIVATE);
			for (String strElement : fileData) {
				strElement = strElement + NEW_LINE;
				out.write(strElement.getBytes());
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			Log.i("Util.setScreenOrientation ", e.getMessage());
		} catch (IOException e) {
			Log.i("Util.setScreenOrientation ", e.getMessage());
		}
	}


	private void createDefaultFile() {
		if (fileData.size() == EMPTY_LIST) {
			String param = ConfigEnum.ORIENTATION.getCode() + SPLIT_CHARS
					+ ScreenHorientation.SCREEN_PORTRAIT.getStrCode();
			fileData.add(param);

			param = ConfigEnum.SIZE.getCode() + SPLIT_CHARS + Constants.SCREEN_SIZE_DEFAULT;
			fileData.add(param);

			param = ConfigEnum.SERVICE.getCode() + SPLIT_CHARS + OnOffEnum.OFF.getCode();
			fileData.add(param);
			saveAll();
		}
	}


	public void resetToDefault() {
		ctx.deleteFile(Constants.FILE_NAME_CFG);
		createDefaultFile();
	}


	public void updateParamValue(ConfigEnum valueEnum, String value) {
		String valueReturn = valueEnum.getCode() + SPLIT_CHARS + value;
		for (int i = 0; i < fileData.size(); i++) {
			String listLine = (String) fileData.get(i);
			if (listLine.contains(valueEnum.getCode())) {
				fileData.remove(i);
				fileData.add(i, valueReturn);
			}
		}
	}


	public void updateParamValueAuto(ConfigEnum valueEnum, String value) {
		String valueReturn = valueEnum.getCode() + SPLIT_CHARS + value;
		for (int i = 0; i < fileData.size(); i++) {
			String listLine = (String) fileData.get(i);
			if (listLine.contains(valueEnum.getCode())) {
				fileData.remove(i);
				fileData.add(i, valueReturn);
			}
		}
		saveAll();
	}


	public String getParamValue(ConfigEnum valueEnum) {
		String valueReturn = valueEnum.getCode() + SPLIT_CHARS + valueEnum;
		for (int i = 0; i < fileData.size(); i++) {
			String listLine = (String) fileData.get(i);
			if (listLine.contains(valueEnum.getCode())) {
				String[] splitLines = listLine.split(SPLIT_CHARS);
				if (splitLines != null && splitLines.length > EMPTY_LIST) {
					valueReturn = splitLines[VALUE_LIST];
					break;
				}
			}
		}
		return valueReturn;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class<R> getDrawable(Class<R> rClass) {
		final String mainR = "au.com.main.R.drawable";
		Class<R> returnRClass = null;
		if (rClass == null) {
			return null;
		}
		Class[] subClasses = rClass.getDeclaredClasses();
		for (Class<R> subclass : subClasses) {
			if (mainR.equals(subclass.getCanonicalName())) {
				returnRClass = subclass;
				break;
			}
		}
		return returnRClass;
	}


	public List<ImageFieldText> getListReady(Class<drawable> rDrawable2) {
		final String stPicture = "st";
		List<ImageFieldText> drInfo = new ArrayList<ImageFieldText>();
		if (rDrawable2 == null) {
			return drInfo;
		}
		Field[] fields = rDrawable2.getFields();
		for (Field dr : fields) {
			if (dr.getName().contains(stPicture)) {
				ImageFieldText bean = null;
				try {
					bean = new ImageFieldText(dr.getName(), dr.getInt(null));
					// bean.setmImage(ctx.getResources().getDrawable(bean.getmNumberImg()));
				} catch (NotFoundException e) {
					Log.e(TAG, e.getMessage());
				} catch (IllegalArgumentException e) {
					Log.e(TAG, e.getMessage());
				} catch (IllegalAccessException e) {
					Log.e(TAG, e.getMessage());
				}
				drInfo.add(bean);
			}
		}
		return drInfo;
	}


	public List<Map<String, Object>> getMapReady(Class<drawable> rDrawable2) {
		final String stPicture = "st";
		if (rDrawable2 == null) {
			return null;
		}
		List<Map<String, Object>> drInfo = new ArrayList<Map<String, Object>>();
		Field[] fields = rDrawable2.getFields();
		for (Field dr : fields) {
			if (dr.getName().contains(stPicture)) {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(ImageMap.IMAGE_NUMBER.getCode(), dr.getInt(null));
					map.put(ImageMap.IMAGE_NAME.getCode(), dr.getName());
					drInfo.add(map);
				} catch (NotFoundException e) {
					Log.e(TAG, e.getMessage());
				} catch (IllegalArgumentException e) {
					Log.e(TAG, e.getMessage());
				} catch (IllegalAccessException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
		return drInfo;
	}


	public Bitmap[] copyIdToBitmap(Integer[] intIds) {
		if (intIds == null || intIds.length == 0) {
			return null;
		}
		List<Bitmap> thumbList = new ArrayList<Bitmap>();
		for (int i = 0; i < intIds.length; i++) {
			if (intIds[i] == null) {
				continue;
			}
			int[] xyValues = getScreenSize();
			Bitmap originalImage = BitmapFactory.decodeResource(ctx.getResources(), intIds[i]);
			Bitmap thumb = ThumbnailUtils.extractThumbnail(originalImage, xyValues[0], xyValues[1]);
			thumbList.add(thumb);
			originalImage = null;
			thumb = null;
		}
		return thumbList.toArray(new Bitmap[0]);
	}


	public Integer[] loadImageArray(List<Map<String, Object>> drawsList) {
		List<Integer> listToArray = new ArrayList<Integer>();
		for (Map<String, Object> drawImage : drawsList) {
			for (String key : drawImage.keySet()) {
				if (key.equals(ImageMap.IMAGE_NUMBER.getCode())) {
					listToArray.add((Integer) drawImage.get(key));
				}
			}
		}
		return listToArray.toArray(new Integer[0]);
	}
	
	
	public Integer[] loadUpImagesIds() {
		List<Map<String, Object>> mapDraws = new ArrayList<Map<String, Object>>();
		Class<drawable> rDrawable = null;
		Util u = new Util(ctx);

		if (rDrawable == null) {
			rDrawable = R.drawable.class;
		}
		if (mapDraws == null || mapDraws.size() == 0) {
			mapDraws = u.getMapReady(rDrawable);
		}
		Integer[] mThumbIds = u.loadImageArray(mapDraws);
		return mThumbIds;
	}

	public Integer[] loadBitmapList(List<Map<String, Object>> drawsList) {
		List<Integer> listToArray = new ArrayList<Integer>();
		for (Map<String, Object> drawImage : drawsList) {
			for (String key : drawImage.keySet()) {
				if (key.equals(ImageMap.IMAGE_NUMBER.getCode())) {
					listToArray.add((Integer) drawImage.get(key));
				}
			}
		}
		return listToArray.toArray(new Integer[0]);
	}

	public int[] getIntArrayFromIntegerList(List<Integer> integerList) {
		if (integerList == null || integerList.size() == 0) {
			return null;
		}
		int[] fromInteger = new int[integerList.size()];
		for (int i = 0; i < integerList.size(); i++) {
			fromInteger[i] = integerList.get(i);
		}
		return fromInteger;
	}


	public Integer[] getIntegerArrayFromIntArray(int[] intArray) {
		if (intArray == null || intArray.length == 0) {
			return null;
		}
		Integer[] fromInteger = new Integer[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			fromInteger[i] = intArray[i];
		}
		return fromInteger;
	}


	public boolean checkForServiceProject() {
		boolean existApplication = false;
		try {
			Intent intent = new Intent(Intent.ACTION_RUN);
			intent.setComponent(new ComponentName("au.com.services.main",
					"au.com.services.main.StarTrekServiceActivity"));
			final PackageManager packageManager = ctx.getPackageManager();
			List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
					PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

			if (list.size() > 0) {
				existApplication = true;
				Log.i("Log", "Service installed." + list.size());
				// ctx.startActivity(intent); // Just use this line if itent to start up the application.
			} else {
				existApplication = false;
				Log.i("Log", "Service missing.");
			}
		} catch (Exception e) {
			existApplication = false;
		}
		return existApplication;
	}


	private int[] getScreenSize() {
		final int largeScreenXAxisPortrait = 480;
		final int largeScreenXAxisLandscape = 800;
		int[] xyValueReturn = new int[2];
		Display d = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point p = getDisplaySize(d);
		int maxWidth = 0;
		if (d.getRotation() == 0) {
			// Portrait
			maxWidth = p.x;
		} else {
			// Landscape
			maxWidth =  p.x;
		}
		if ((d.getRotation() == 0 && maxWidth > largeScreenXAxisPortrait)
				|| (d.getRotation() != 0 && maxWidth > largeScreenXAxisLandscape)) {
			xyValueReturn[0] = 512;
			xyValueReturn[1] = 384;
		} else {
			xyValueReturn[0] = 96;
			xyValueReturn[1] = 96;
		}
		return xyValueReturn;
	}


	public boolean createNewFile(long interval, int device, int[] imagesIds, boolean active) {
		boolean isOk = false;
		if (imagesIds == null || imagesIds.length == 0) {
			return isOk;
		}
		File sdDir = new File(Environment.getExternalStorageDirectory().getPath());
		File testDir = new File(sdDir.getAbsolutePath() + pathForSerivces);
		if (!testDir.isDirectory()) {
			testDir.mkdir();
		}
		if (sdDir.canRead() && sdDir.canWrite()) {
			File readFile = new File(sdDir.getPath() + pathForSerivces + slashPath + serviceFileName);
			FileWriter fw = null;
			PrintWriter out = null;
			try {
				if (!readFile.exists()) {
					readFile.createNewFile();
				} else {
					readFile.delete();
					readFile.createNewFile();
				}
				fw = new FileWriter(readFile);
				out = new PrintWriter(fw);
				if (active) {
					out.print(interval + NEW_LINE);
					out.print(device + NEW_LINE);
					for (int i = 0; i < imagesIds.length; i++) {
						if (imagesIds[i] != 0) {
							out.print(imagesIds[i] + NEW_LINE);
						}
					}
				} else {
					out.print(deactivate);
				}
				isOk = true;
			} catch (IOException e) {
				Log.i("Log", "createFolder IOException.");
			} finally {
				if (out != null) {
					out.close();
				}
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						Log.i("Log", "createFolder IOException.");
					}
				}
			}
		}
		return isOk;
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public Point getDisplaySize(final Display display) {
	    final Point point = new Point();
	    try {
	        display.getSize(point);
	    } catch (java.lang.NoSuchMethodError ignore) { // Older device
	        point.x = display.getWidth();
	        point.y = display.getHeight();
	    }
	    return point;
	}
}
