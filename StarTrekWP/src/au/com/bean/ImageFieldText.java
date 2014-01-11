package au.com.bean;

import android.graphics.drawable.Drawable;

public class ImageFieldText implements Comparable<ImageFieldText>{

	private String mText = "";
	private int mNumberImg;
	private Drawable mImage;
	private boolean mSelectable = true;
	private boolean selected;
	

	public ImageFieldText(String mText, int mNumberImg, Drawable mImage) {
		super();
		this.mText = mText;
		this.mNumberImg = mNumberImg;
		this.mImage = mImage;
	}
	
	
	public ImageFieldText(String mText, int mNumberImg) {
		super();
		this.mText = mText;
		this.mNumberImg = mNumberImg;
	}

	@Override
	public int compareTo(ImageFieldText another) {
		if(this.mText != null)
			return this.mText.compareTo(another.getmText()); 
		else 
			throw new IllegalArgumentException();
	}



	/**
	 * @return the mText
	 */
	public String getmText() {
		return mText;
	}



	/**
	 * @param mText the mText to set
	 */
	public void setmText(String mText) {
		this.mText = mText;
	}



	/**
	 * @return the mNumberImg
	 */
	public int getmNumberImg() {
		return mNumberImg;
	}


	/**
	 * @param mNumberImg the mNumberImg to set
	 */
	public void setmNumberImg(int mNumberImg) {
		this.mNumberImg = mNumberImg;
	}


	/**
	 * @return the mImage
	 */
	public Drawable getmImage() {
		return mImage;
	}





	/**
	 * @param mImage the mImage to set
	 */
	public void setmImage(Drawable mImage) {
		this.mImage = mImage;
	}



	/**
	 * @return the mSelectable
	 */
	public boolean ismSelectable() {
		return mSelectable;
	}



	/**
	 * @param mSelectable the mSelectable to set
	 */
	public void setmSelectable(boolean mSelectable) {
		this.mSelectable = mSelectable;
	}


	public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
