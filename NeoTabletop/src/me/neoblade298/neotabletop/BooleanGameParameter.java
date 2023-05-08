package me.neoblade298.neotabletop;

public class BooleanGameParameter extends GameParameter {
	public BooleanGameParameter(String key, String desc, boolean defaultVal) {
		super(key, desc, defaultVal);
	}

	@Override
	public boolean set(Object obj) {
		if (obj instanceof Boolean) {
			return true;
		}
		return false;
	}
}
