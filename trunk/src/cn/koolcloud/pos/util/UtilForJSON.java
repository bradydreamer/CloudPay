package cn.koolcloud.pos.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

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
					if (tmpSet.contains(jsonObj.optString("brhMchtId")) || TextUtils.isEmpty(jsonObj.optString("brhMchtId"))) {
						continue;
					} else {
						tmpSet.add(jsonObj.optString("brhMchtId"));
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
					acquireInstitute.setPrintType(jsonObj.optInt("printType"));
					acquireInstitute.setProductDesc(jsonObj.optString("prdtDesc"));
					acquireInstitute.setProductNo(jsonObj.optString("prdtNo"));
					acquireInstitute.setProductTitle(jsonObj.optString("prdtTitle"));
					acquireInstitute.setProductType(jsonObj.optInt("prdtType"));
					acquireInstitute.setTypeId(jsonObj.optString("typeId"));
					acquireInstitute.setTypeName(jsonObj.optString("typeName"));
					
					acquireList.add(acquireInstitute);
				}
			}
		} catch (Exception e) {
			Logger.e(e.getMessage());
			e.printStackTrace();
		}
		return acquireList;
	}
	
}
