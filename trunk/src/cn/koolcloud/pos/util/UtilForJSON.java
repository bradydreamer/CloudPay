package cn.koolcloud.pos.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.entity.AcquireInstituteBean;

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

	public static List<AcquireInstituteBean> parseJsonArray2AcquireInstitute(JSONArray jsonArray) {
		List<AcquireInstituteBean> acquireList = null;
		HashSet<String> tmpSet = null;
		try {
			if (null != jsonArray && jsonArray.length() > 0) {
				acquireList = new ArrayList<AcquireInstituteBean>();
				tmpSet = new HashSet<String>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					if (tmpSet.contains(jsonObj.optString("openBrh") + jsonObj.optString("brhMchtId") + jsonObj.optString("brhTermId")) ||
							TextUtils.isEmpty(jsonObj.optString("brhMchtId"))) {
						continue;
					} else {
						tmpSet.add(jsonObj.optString("openBrh") + jsonObj.optString("brhMchtId") + jsonObj.optString("brhTermId"));
					}
					AcquireInstituteBean acquireInstitute = new AcquireInstituteBean();
					acquireInstitute.setBrhMchtId(jsonObj.optString("brhMchtId"));
					acquireInstitute.setBrhTermId(jsonObj.optString("brhTermId"));
					acquireInstitute.setDeviceNumOfMerch(jsonObj.optString("openBrh"));
					acquireInstitute.setImgName(jsonObj.optString("imgName"));
					acquireInstitute.setInstituteName(jsonObj.optString("openBrhName"));
					acquireInstitute.setMerchNumOfMerch(jsonObj.optString("mch_no"));
					acquireInstitute.setPaymentId(jsonObj.optString("paymentId"));
					acquireInstitute.setPaymentName(jsonObj.optString("paymentName"));
					acquireInstitute.setPrintType(jsonObj.optString("printType"));
					acquireInstitute.setProductDesc(jsonObj.optString("prdtDesc"));
					acquireInstitute.setProductNo(jsonObj.optString("prdtNo"));
					acquireInstitute.setProductTitle(jsonObj.optString("prdtTitle"));
					acquireInstitute.setProductType(jsonObj.optString("prdtType"));
					acquireInstitute.setTypeId(jsonObj.optString("typeId"));
					acquireInstitute.setTypeName(jsonObj.optString("typeName"));
					acquireInstitute.setBrhKeyIndex(jsonObj.optString("brhKeyIndex"));
					acquireInstitute.setBrhMsgType(jsonObj.optString("brhMsgType"));
					acquireInstitute.setBrhMchtMcc(jsonObj.optString("brhMchtMcc"));
					
					acquireList.add(acquireInstitute);
				}
			}
		} catch (Exception e) {
			Logger.e(e.getMessage());
			e.printStackTrace();
		}
		return acquireList;
	}
	
	public static List<AcquireInstituteBean> parseJsonArray2AcquireInstituteWithJson(JSONArray jsonArray) {
		List<AcquireInstituteBean> acquireList = null;
		HashSet<String> tmpSet = null;
		try {
			if (null != jsonArray && jsonArray.length() > 0) {
				acquireList = new ArrayList<AcquireInstituteBean>();
				tmpSet = new HashSet<String>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					if (tmpSet.contains(jsonObj.optString("paymentId"))) {
						continue;
					} else {
						tmpSet.add(jsonObj.optString("paymentId"));
					}
					AcquireInstituteBean acquireInstitute = new AcquireInstituteBean();
					acquireInstitute.setBrhMchtId(jsonObj.optString("brhMchtId"));
					acquireInstitute.setBrhTermId(jsonObj.optString("brhTermId"));
					acquireInstitute.setDeviceNumOfMerch(jsonObj.optString("openBrh"));
					acquireInstitute.setImgName(jsonObj.optString("imgName"));
					acquireInstitute.setInstituteName(jsonObj.optString("openBrhName"));
					acquireInstitute.setMerchNumOfMerch(jsonObj.optString("mch_no"));
					acquireInstitute.setPaymentId(jsonObj.optString("paymentId"));
					acquireInstitute.setPaymentName(jsonObj.optString("paymentName"));
					acquireInstitute.setPrintType(jsonObj.optString("printType"));
					acquireInstitute.setProductDesc(jsonObj.optString("prdtDesc"));
					acquireInstitute.setProductNo(jsonObj.optString("prdtNo"));
					acquireInstitute.setProductTitle(jsonObj.optString("prdtTitle"));
					acquireInstitute.setProductType(jsonObj.optString("prdtType"));
					acquireInstitute.setTypeId(jsonObj.optString("typeId"));
					acquireInstitute.setTypeName(jsonObj.optString("typeName"));
					acquireInstitute.setBrhKeyIndex(jsonObj.optString("brhKeyIndex"));
					acquireInstitute.setBrhMsgType(jsonObj.optString("brhMsgType"));
					acquireInstitute.setBrhMchtMcc(jsonObj.optString("brhMchtMcc"));
					acquireInstitute.setJsonItem(jsonObj.toString());
					acquireList.add(acquireInstitute);
				}
			}
		} catch (Exception e) {
			Logger.e(e.getMessage());
			e.printStackTrace();
		}
		return acquireList;
	}
	
	public static JSONArray parseCardNumberByPackageName(String packageName, JSONArray jsonArray, Context context) {
		JSONArray resultArray = null;
		
		try {
			if (null != jsonArray && jsonArray.length() > 0) {
				String[] packageNameArray = context.getResources().getStringArray(R.array.package_name_item);
				List<String> list = Arrays.asList(packageNameArray);
				Set<String> packageSet = new HashSet<String>(list);
				
				resultArray = new JSONArray();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsObj = jsonArray.getJSONObject(i);
					
					if (!packageSet.contains(packageName)) {
						String cardNum = jsObj.getString("accountNo");
						if (!TextUtils.isEmpty(cardNum)) {
							
							String tempPan = cardNum.substring(0, 6) + "******"
									+ cardNum.substring(cardNum.length() - 4, cardNum.length());
							jsObj.put("accountNo", tempPan);
						}
					}
					
					resultArray.put(jsObj);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultArray;
	}
	
}
