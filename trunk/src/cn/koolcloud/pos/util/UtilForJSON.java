package cn.koolcloud.pos.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class UtilForJSON {
	
	public static List<String> JSONArrayOfStrings2ListOfStrings(JSONArray array) {
		if (null == array) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.optString(i));
		}
		return list;
	}
	
	public static List<JSONObject> JSONArrayOfJSONObjects2ListOfJSONObjects(JSONArray array) {
		if (null == array) {
			return null;
		}
		List<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.optJSONObject(i));
		}
		return list;
	}
	
	public static List<Object> JSONArrayOfObjects2ListOfObjects(JSONArray array) {
		if (null == array) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.opt(i));
		}
		return list;
	}
	
}
