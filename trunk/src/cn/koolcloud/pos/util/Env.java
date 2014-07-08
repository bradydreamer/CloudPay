package cn.koolcloud.pos.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * <p>Title: Env.java </p>
 * <p>Description: class for application</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2014-3-28
 * @version 	
 */
public class Env {
	
	/**
	* @Title: uninstallApp
	* @Description: TODO uninstsall APP
	* @param @param context
	* @param @param packageName
	* @return void 
	* @throws
	*/
	public static void uninstallApp(Context context, String packageName) {
	    Uri packageURI = Uri.parse("package:" + packageName);   
	    Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);   
	    uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    context.startActivity(uninstallIntent);
	}
	
	/**
	 * install app
	 */
	public static void openAPK(File f, Context context) {
	    context.startActivity(getInstallApp(f, context));
	}

	private static Intent getInstallApp(File f, Context context) {
	    Intent intent = new Intent();
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    //set the place where the file come from
	    intent.putExtra("android.intent.extra.INSTALLER_PACKAGE_NAME", context.getPackageName());
	    intent.setAction(android.content.Intent.ACTION_VIEW);

	    /* set file for intent */
	    intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
	    return intent;
	}
	
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	public static String getPackageName(Context context) {
		String packageName = "";
		try {
			packageName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packageName;
	}
	
	/**
	* @Title: getAPKPackageName
	* @Description: get apk file package name
	* @param @param context
	* @param @param file
	* @param @return
	* @return String 
	* @throws
	*/
	public static String getAPKPackageName(Context context, File file) {
		String packageName = null;
		if (null != file && file.isFile() && file.exists()) {
			String fileName = file.getName();
			String apk_path = null;
			if (fileName.toLowerCase().endsWith(".apk")) {
				// apk absolute path
				apk_path = file.getAbsolutePath();
				PackageManager pm = context.getPackageManager();
				PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
				ApplicationInfo appInfo = packageInfo.applicationInfo;
				/* apk icon */
				appInfo.sourceDir = apk_path;
				appInfo.publicSourceDir = apk_path;
				Drawable apk_icon = appInfo.loadIcon(pm);
				/* apk package name*/
				packageName = packageInfo.packageName;
				/* apk version String */
				String versionName = packageInfo.versionName;
				/* apk version code int */
				int versionCode = packageInfo.versionCode;
			}
		}
		return packageName;
	}
	
	/**
	* @Title: getAPKPackageInfo
	* @Description: get apk file package information
	* @param @param context
	* @param @param file
	* @param @return
	* @return PackageInfo 
	* @throws
	*/
	public static PackageInfo getAPKPackageInfo(Context context, File file) {
		PackageInfo packageInfo = null;
		String packageName = null;
		if (null != file && file.isFile() && file.exists()) {
			String fileName = file.getName();
			String apk_path = null;
			if (fileName.toLowerCase().endsWith(".apk")) {
				// apk absolute path
				apk_path = file.getAbsolutePath();
				PackageManager pm = context.getPackageManager();
				packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
				if (packageInfo != null) {
					
					ApplicationInfo appInfo = packageInfo.applicationInfo;
					/* apk icon */
					appInfo.sourceDir = apk_path;
					appInfo.publicSourceDir = apk_path;
					Drawable apk_icon = appInfo.loadIcon(pm);
					/* apk package name*/
					packageName = packageInfo.packageName;
					/* apk version String */
					String versionName = packageInfo.versionName;
					/* apk version code int */
					int versionCode = packageInfo.versionCode;
				}
			}
		}
		return packageInfo;
	}

	public static void install(Activity ctx, File file, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		try {
			Runtime.getRuntime().exec("chmod 644 " + file.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		ctx.startActivityForResult(intent, requestCode);
	}
	
	public static Intent getLaunchIntent(Context ctx, String packageName, Map<String, PackageInfo> installedPackage) {
		return ctx.getPackageManager().getLaunchIntentForPackage(
				installedPackage.get(packageName).packageName);
	}
	
	public static String getResourceString(Context context, int strId) {
		return context.getResources().getString(strId);
	}
	
	/**
	 * @Title: isAppRunning
	 * @Description: TODO Checks if the application is being sent in the background (i.e behind another application's Activity)
	 * @param context
	 * @return true if another application will be above this one.
	 * @return: boolean
	 */
	public static boolean isAppRunning(Context context) {
	    // check with the first task(task in the foreground)
	    // in the returned list of tasks
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
	    if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString())) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * @Title: checkApkExist
	 * @Description: TODO check if the app is installed.
	 * @param context
	 * @param packageName
	 * @return
	 * @return: boolean
	 */
	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
//			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	public static String getDeviceInfo(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuilder sb = new StringBuilder();
		sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
		sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
		sb.append("\nLine1Number = " + tm.getLine1Number());
		sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		sb.append("\nNetworkType = " + tm.getNetworkType());
		sb.append("\nPhoneType = " + tm.getPhoneType());
		sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		sb.append("\nSimOperator = " + tm.getSimOperator());
		sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
		sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
		sb.append("\nSimState = " + tm.getSimState());
		sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
		sb.append("\nandroid.os.Build.BOARD = " + android.os.Build.BOARD);
		sb.append("\nandroid.os.Build.BOOTLOADER = "
				+ android.os.Build.BOOTLOADER);
		sb.append("\nandroid.os.Build.BRAND = " + android.os.Build.BRAND);
		sb.append("\nandroid.os.Build.CPU_ABI = " + android.os.Build.CPU_ABI);
		sb.append("\nandroid.os.Build.CPU_ABI2 = " + android.os.Build.CPU_ABI2);
		sb.append("\nandroid.os.Build.DEVICE = " + android.os.Build.DEVICE);
		sb.append("\nandroid.os.Build.DISPLAY = " + android.os.Build.DISPLAY);
		sb.append("\nandroid.os.Build.FINGERPRINT = "
				+ android.os.Build.FINGERPRINT);
		sb.append("\nandroid.os.Build.HARDWARE = " + android.os.Build.HARDWARE);
		sb.append("\nandroid.os.Build.HOST = " + android.os.Build.HOST);
		sb.append("\nandroid.os.Build.ID = " + android.os.Build.ID);
		sb.append("\nandroid.os.Build.MANUFACTURER = "
				+ android.os.Build.MANUFACTURER);
		sb.append("\nandroid.os.Build.MODEL = " + android.os.Build.MODEL);
		sb.append("\nandroid.os.Build.PRODUCT = " + android.os.Build.PRODUCT);
		// sb.append("\nandroid.os.Build.RADIO = " + android.os.Build.RADIO);
		sb.append("\nandroid.os.Build.SERIAL = " + android.os.Build.SERIAL);
		sb.append("\nandroid.os.Build.TAGS = " + android.os.Build.TAGS);
		sb.append("\nandroid.os.Build.TIME = " + android.os.Build.TIME);
		sb.append("\nandroid.os.Build.TYPE = " + android.os.Build.TYPE);
		sb.append("\nandroid.os.Build.UNKNOWN = " + android.os.Build.UNKNOWN);
		sb.append("\nandroid.os.Build.USER = " + android.os.Build.USER);
		sb.append("\nandroid.os.Build.VERSION.RELEASE = " + android.os.Build.VERSION.RELEASE);
		
		return sb.toString();
	}
}