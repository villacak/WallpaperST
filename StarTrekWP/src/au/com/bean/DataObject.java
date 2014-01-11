package au.com.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

public class DataObject {
	private boolean[] thumbnailsselection;
	private Bitmap[] thumbnails;
	private Integer[] mThumbIds;
	private List<Map<String, Object>> mapDraws = new ArrayList<Map<String, Object>>();


	public boolean[] getThumbnailsselection() {
		return thumbnailsselection;
	}


	public void setThumbnailsselection(boolean[] thumbnailsselection) {
		this.thumbnailsselection = thumbnailsselection;
	}


	public Bitmap[] getThumbnails() {
		return thumbnails;
	}


	public void setThumbnails(Bitmap[] thumbnails) {
		this.thumbnails = thumbnails;
	}


	public Integer[] getmThumbIds() {
		return mThumbIds;
	}


	public void setmThumbIds(Integer[] mThumbIds) {
		this.mThumbIds = mThumbIds;
	}


	public List<Map<String, Object>> getMapDraws() {
		return mapDraws;
	}


	public void setMapDraws(List<Map<String, Object>> mapDraws) {
		this.mapDraws = mapDraws;
	}

}
