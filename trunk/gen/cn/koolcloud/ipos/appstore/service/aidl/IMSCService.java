/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\ZPos\\trunk\\src\\cn\\koolcloud\\ipos\\appstore\\service\\aidl\\IMSCService.aidl
 */
package cn.koolcloud.ipos.appstore.service.aidl;
public interface IMSCService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements cn.koolcloud.ipos.appstore.service.aidl.IMSCService
{
private static final java.lang.String DESCRIPTOR = "cn.koolcloud.ipos.appstore.service.aidl.IMSCService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an cn.koolcloud.ipos.appstore.service.aidl.IMSCService interface,
 * generating a proxy if needed.
 */
public static cn.koolcloud.ipos.appstore.service.aidl.IMSCService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof cn.koolcloud.ipos.appstore.service.aidl.IMSCService))) {
return ((cn.koolcloud.ipos.appstore.service.aidl.IMSCService)iin);
}
return new cn.koolcloud.ipos.appstore.service.aidl.IMSCService.Stub.Proxy(obj);
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
case TRANSACTION_checkUpdate:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp _result = this.checkUpdate(_arg0, _arg1);
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
case TRANSACTION_openAppDetail:
{
data.enforceInterface(DESCRIPTOR);
cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp _arg0;
if ((0!=data.readInt())) {
_arg0 = cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.openAppDetail(_arg0);
reply.writeNoException();
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements cn.koolcloud.ipos.appstore.service.aidl.IMSCService
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
@Override public cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp checkUpdate(java.lang.String packageName, int versionCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
_data.writeInt(versionCode);
mRemote.transact(Stub.TRANSACTION_checkUpdate, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp.CREATOR.createFromParcel(_reply);
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
@Override public void openAppDetail(cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp app) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((app!=null)) {
_data.writeInt(1);
app.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_openAppDetail, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
app.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_checkUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_openAppDetail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp checkUpdate(java.lang.String packageName, int versionCode) throws android.os.RemoteException;
public void openAppDetail(cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp app) throws android.os.RemoteException;
}
