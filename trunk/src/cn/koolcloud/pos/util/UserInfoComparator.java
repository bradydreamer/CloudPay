package cn.koolcloud.pos.util;

import java.util.Comparator;

import org.json.JSONObject;

public class UserInfoComparator implements Comparator {

	public static final int COMP_USER_NAME = 1;
	public static final int COMP_USER_GRADEID = 2;
	public static final int COMP_USER_ALIASNAME = 3;
	public static final int COMP_USER_LASTLOGINTIME = 4;
	public static final int COMP_USER_LOGINCOUNT = 5;
	private Boolean compareType = false;
	private int compareContent = 0;

	@Override
	public int compare(Object lhs, Object rhs) {
		int tag = -1;
		JSONObject userlhs = (JSONObject) lhs;
		JSONObject userrhs = (JSONObject) rhs;

		switch (compareContent) {
		case 1:
			tag = userlhs.optString("operator").compareTo(
					userrhs.optString("operator"));
			break;
		case 2:
			tag = userlhs.optString("gradeId").compareTo(
					userrhs.optString("gradeId"));
			break;
		case 3:
			tag = userlhs.optString("aliasName").compareTo(
					userrhs.optString("aliasName"));
			break;
		case 4:
			tag = userlhs.optString("lastLoginTime").compareTo(
					userrhs.optString("lastLoginTime"));
			break;
		case 5:
			if (Integer.parseInt(userlhs.optString("loginCount")) == Integer
					.parseInt(userrhs.optString("loginCount"))) {
				tag = 0;
			} else if (Integer.parseInt(userlhs.optString("loginCount")) < Integer
					.parseInt(userrhs.optString("loginCount"))) {
				tag = -1;
			} else {
				tag = 1;
			}
			break;
		default:
			break;
		}
		if (compareType) {
			tag = tag;
		} else {
			tag = ~tag;
		}
		return tag;
	}

	public Boolean getCompareType() {
		return compareType;
	}

	public void setCompareType(Boolean compareType) {
		this.compareType = compareType;
	}

	public int getCompareContent() {
		return compareContent;
	}

	public void setCompareContent(int compareContent) {
		this.compareContent = compareContent;
	}

}
