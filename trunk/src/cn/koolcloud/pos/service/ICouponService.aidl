package cn.koolcloud.pos.service;

import cn.koolcloud.pos.service.CouponInfo;

interface ICouponService {
   	CouponInfo startCoupon(String transAmount, String actionType, String packageName, String orderNo, String orderDesc);
}