package me.neoblade298.neotabletop;

public abstract class GameParameter {
	private String key, desc;
	private Object defaultVal;
	private Object val;
	
	public GameParameter(String key, String desc, Object defaultVal) {
		this.key = key;
		this.desc = desc;
		this.defaultVal = defaultVal;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public Object get() {
		return val == null ? defaultVal : val;
	}
	
	public abstract boolean set(Object obj);
}
