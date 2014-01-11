package au.com.constants;

public enum ImageMap {
	IMAGE_NUMBER("drimg"), IMAGE_NAME("drname");

	private String imageMap;


	private ImageMap(String imageMap) {
		this.imageMap = imageMap;
	}


	public String getCode() {
		return imageMap;
	}
}
