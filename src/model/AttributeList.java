package model;

import java.util.HashMap;

public class AttributeList {

//---  Constants   ----------------------------------------------------------------------------
	
	//false means you only need one, true means you need all (AON = "all or nothing")
	public final static String ATTRIBUTE_INITIAL = "Initial";
	public final static boolean ATTRIBUTE_AON_INITIAL = false;
	public final static String ATTRIBUTE_MARKED = "Marked";
	public final static boolean ATTRIBUTE_AON_MARKED = true;
	public final static String ATTRIBUTE_PRIVATE = "Private";
	public final static boolean ATTRIBUTE_AON_PRIVATE = true;
	public final static String ATTRIBUTE_OBSERVABLE = "Observable";
	public final static boolean ATTRIBUTE_AON_OBSERVABLE = false;
	public final static String ATTRIBUTE_CONTROLLABLE = "Controllable";
	public final static boolean ATTRIBUTE_AON_CONTROLLABLE = false;
	
	private static HashMap<String, Boolean> map;
	
	public static boolean getAON(String ref) {
		if(map == null) {
			setupMap();
		}
		return map.get(ref);
	}
	
	private static void setupMap() {
		map = new HashMap<String, Boolean>();
		map.put(ATTRIBUTE_INITIAL, ATTRIBUTE_AON_INITIAL);
		map.put(ATTRIBUTE_MARKED, ATTRIBUTE_AON_MARKED);
		map.put(ATTRIBUTE_PRIVATE, ATTRIBUTE_AON_PRIVATE);
		map.put(ATTRIBUTE_OBSERVABLE, ATTRIBUTE_AON_OBSERVABLE);
		map.put(ATTRIBUTE_CONTROLLABLE, ATTRIBUTE_AON_CONTROLLABLE);
	}
	
}