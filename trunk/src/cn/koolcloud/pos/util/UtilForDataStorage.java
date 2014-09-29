package cn.koolcloud.pos.util;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;

public class UtilForDataStorage {
	
	public static final String PREFS_NAME 	= "date_preference_file";
	public static final String DATE_KEY 	= "date_key";

//	/**
//	 * 
//	 * @param context
//	 * @param preferencesName Desired preferences file. If a preferences file by this name does not exist, it will be created.
//	 * @param key the key
//	 * @return the value of the mapping with the specified key in Desired preferences file, or null if no mapping for the specified key is found.
//	 */
//	public static Object readPropertyBySharedPreferences(Context context, String preferencesName, String key) {
//		SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
//		return preferences.getAll().get(key);
//	}
	
	/**
	 * 
	 * @param context
	 * @param preferencesName Desired preferences file. If a preferences file by this name does not exist, it will be created.
	 * @return Returns a map containing a list of pairs key/value representing the preferences.
	 */
	public static Map<String, ?> readPropertyBySharedPreferences(Context context, String preferencesName) {
		SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
		if (null == preferences) {
			return null;
		}
		return preferences.getAll();
	}
	
	/**
	 * save a <key,value> map in a preferences file,whose name is preferencesName.
	 * the value can only be instanceof Float,Boolean,Integer,String,Long,or Double.
	 * If Double,the value will be converted to Float.
	 * @param context
	 * @param preferencesName Desired preferences file. If a preferences file by this name does not exist, it will be created.
	 * @param map Map<String, Object>
	 */
	public static void savePropertyBySharedPreferences(Context context, String preferencesName, Map<String, Object> map) {
		SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry<String, Object> entry = (Map.Entry<String, Object>)iter.next(); 
		    String key = entry.getKey(); 
		    Object value = entry.getValue();
		    setObject(editor, key, value);
		}
		editor.commit();
	}
	
	public static void clearPropertyBySharedPreferences(Context context, String preferencesName) {
		SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	
	public static void removePropertyBySharedPreferences(Context context, String preferencesName, JSONArray array) {
		SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		for (int i = 0; i < array.length(); i++) {
			editor.remove(array.optString(i));
		}
		editor.commit();
	}
	
	private static void setObject(SharedPreferences.Editor editor, String key, Object value) {
		if (value instanceof Double) {
			value = ((Double)value).floatValue();
		}
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		}
	}
	
	public static String getSavedDate(Context context) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefer.getString(DATE_KEY, "");
	}

	public static void saveDate(Context context, String date) {
		SharedPreferences prefer = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefer.edit().putString(DATE_KEY, date).commit();
	}
	
}
