/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\ZPos\\trunk\\src\\cn\\koolcloud\\pos\\service\\ISecureService.aidl
 */
package cn.koolcloud.pos.service;
public interface ISecureService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements cn.koolcloud.pos.service.ISecureService
{
private static final java.lang.String DESCRIPTOR = "cn.koolcloud.pos.service.ISecureService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an cn.koolcloud.pos.service.ISecureService interface,
 * generating a proxy if needed.
 */
public static cn.koolcloud.pos.service.ISecureService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof cn.koolcloud.pos.service.ISecureService))) {
return ((cn.koolcloud.pos.service.ISecureService)iin);
}
return new cn.koolcloud.pos.service.ISecureService.Stub.Proxy(obj);
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
case TRANSACTION_getSecureInfo:
{
data.enforceInterface(DESCRIPTOR);
cn.koolcloud.pos.service.SecureInfo _result = this.getSecureInfo();
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
case TRANSACTION_setSecureInfo:
{
data.enforceInterface(DESCRIPTOR);
cn.koolcloud.pos.service.SecureInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = cn.koolcloud.pos.service.SecureInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.setSecureInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getUserInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getUserInfo();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setUserInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setUserInfo(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements cn.koolcloud.pos.service.ISecureService
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
@Override public cn.koolcloud.pos.service.SecureInfo getSecureInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
cn.koolcloud.pos.service.SecureInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSecureInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = cn.koolcloud.pos.service.SecureInfo.CREATOR.createFromParcel(_reply);
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
@Override public void setSecureInfo(cn.koolcloud.pos.service.SecureInfo si) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((si!=null)) {
_data.writeInt(1);
si.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setSecureInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getUserInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUserInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setUserInfo(java.lang.String ui) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(ui);
mRemote.transact(Stub.TRANSACTION_setUserInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getSecureInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setSecureInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getUserInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setUserInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public cn.koolcloud.pos.service.SecureInfo getSecureInfo() throws android.os.RemoteException;
public void setSecureInfo(cn.koolcloud.pos.service.SecureInfo si) throws android.os.RemoteException;
public java.lang.String getUserInfo() throws android.os.RemoteException;
public void setUserInfo(java.lang.String ui) throws android.os.RemoteException;
}
