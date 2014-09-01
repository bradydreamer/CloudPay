/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\Projects\\ZPos\\trunk\\src\\cn\\koolcloud\\pos\\service\\IMerchService.aidl
 */
package cn.koolcloud.pos.service;
public interface IMerchService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements cn.koolcloud.pos.service.IMerchService
{
private static final java.lang.String DESCRIPTOR = "cn.koolcloud.pos.service.IMerchService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an cn.koolcloud.pos.service.IMerchService interface,
 * generating a proxy if needed.
 */
public static cn.koolcloud.pos.service.IMerchService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof cn.koolcloud.pos.service.IMerchService))) {
return ((cn.koolcloud.pos.service.IMerchService)iin);
}
return new cn.koolcloud.pos.service.IMerchService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getMerchInfo:
{
data.enforceInterface(DESCRIPTOR);
cn.koolcloud.pos.service.MerchInfo _result = this.getMerchInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setMerchInfo:
{
data.enforceInterface(DESCRIPTOR);
cn.koolcloud.pos.service.MerchInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = cn.koolcloud.pos.service.MerchInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.setMerchInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setLoginStatus:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setLoginStatus(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_endCallPayEx:
{
data.enforceInterface(DESCRIPTOR);
this.endCallPayEx();
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
cn.koolcloud.pos.service.IMerchCallBack _arg0;
_arg0 = cn.koolcloud.pos.service.IMerchCallBack.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
cn.koolcloud.pos.service.IMerchCallBack _arg0;
_arg0 = cn.koolcloud.pos.service.IMerchCallBack.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPaymentInfos:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<cn.koolcloud.pos.service.PaymentInfo> _result = this.getPaymentInfos();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements cn.koolcloud.pos.service.IMerchService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public cn.koolcloud.pos.service.MerchInfo getMerchInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
cn.koolcloud.pos.service.MerchInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMerchInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = cn.koolcloud.pos.service.MerchInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setMerchInfo(cn.koolcloud.pos.service.MerchInfo mi) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((mi!=null)) {
_data.writeInt(1);
mi.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setMerchInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setLoginStatus(java.lang.String ls) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(ls);
mRemote.transact(Stub.TRANSACTION_setLoginStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void endCallPayEx() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_endCallPayEx, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallback(cn.koolcloud.pos.service.IMerchCallBack cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(cn.koolcloud.pos.service.IMerchCallBack cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.List<cn.koolcloud.pos.service.PaymentInfo> getPaymentInfos() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<cn.koolcloud.pos.service.PaymentInfo> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPaymentInfos, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(cn.koolcloud.pos.service.PaymentInfo.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getMerchInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setMerchInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setLoginStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_endCallPayEx = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getPaymentInfos = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
public cn.koolcloud.pos.service.MerchInfo getMerchInfo() throws android.os.RemoteException;
public void setMerchInfo(cn.koolcloud.pos.service.MerchInfo mi) throws android.os.RemoteException;
public void setLoginStatus(java.lang.String ls) throws android.os.RemoteException;
public void endCallPayEx() throws android.os.RemoteException;
public void registerCallback(cn.koolcloud.pos.service.IMerchCallBack cb) throws android.os.RemoteException;
public void unregisterCallback(cn.koolcloud.pos.service.IMerchCallBack cb) throws android.os.RemoteException;
public java.util.List<cn.koolcloud.pos.service.PaymentInfo> getPaymentInfos() throws android.os.RemoteException;
}
