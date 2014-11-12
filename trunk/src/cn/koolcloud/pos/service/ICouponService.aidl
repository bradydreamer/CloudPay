package cn.koolcloud.pos.service;

interface ICouponService {
   	boolean cancelCoupon(String pkgName, String txnID);
}