package au.com.constants;

public enum OnOffEnum {
	ON("on"), OFF("off");
	
	private String stateOfAction;
	
	private OnOffEnum(String stateOfAction) {
		this.stateOfAction = stateOfAction;
	}
	
	public String getCode() {
		return stateOfAction;
	}
}
