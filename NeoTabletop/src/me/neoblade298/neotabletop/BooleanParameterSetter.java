package me.neoblade298.neotabletop;

public class BooleanParameterSetter implements GameParameterSetter {

	// Returns the object to set, or null if the validation fails
	@Override
	public Object set(String str) {
		if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
			return null;
		}
		return Boolean.parseBoolean(str);
	}
	
}
