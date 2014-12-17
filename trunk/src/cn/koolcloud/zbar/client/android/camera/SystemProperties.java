package cn.koolcloud.zbar.client.android.camera;

import java.lang.reflect.Method;

public class SystemProperties {

	
	public static int getInt(String key,int defaultvalue)
	{
		String flag = get(key, Integer.toString(defaultvalue));
		return Integer.parseInt(flag);
	}
	
	public static boolean getBoolean(String key,boolean defaultvalue)
	{
		String flag = get(key,Boolean.toString(defaultvalue));
		return Boolean.parseBoolean(flag);
	}
	
	public static long getLong(String key,long defaultvalue)
	{
		String flag = get(key,Long.toString(defaultvalue));
		return Long.parseLong(flag);
	}
	
	public static String get(String key,String defaultvalue){

        String ret= defaultvalue;

		try{
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			@SuppressWarnings("rawtypes")
			Class[] paramTypes= new Class[1];
			paramTypes[0]= String.class;
			Method get = SystemProperties.getMethod("get", paramTypes);
			//Parameters
			Object[] params= new Object[1];
			params[0]= new String(key);
			ret= (String) get.invoke(SystemProperties, params);
		}
		catch( Exception e )
		{
			ret= defaultvalue;
		}

        return ret;
    }
	
	public static void set(String key,String value){

		try{
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			@SuppressWarnings("rawtypes")
			Class[] paramTypes= new Class[2];
			paramTypes[0]= String.class;
			paramTypes[1]= String.class;
			
			Method set = SystemProperties.getMethod("set", paramTypes);
			//Parameters
			Object[] params= new Object[2];
			params[0]= new String(key);
			params[1]= new String(value);
			set.invoke(SystemProperties, params);
		}catch( Exception e ){
		
		}
    }
}
