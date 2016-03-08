/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\poli\\workspace\\virtualradio\\src\\com\\giantrabbit\\nagare\\INagareService.aidl
 */
package com.giantrabbit.nagare;
public interface INagareService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.giantrabbit.nagare.INagareService
{
private static final java.lang.String DESCRIPTOR = "com.giantrabbit.nagare.INagareService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.giantrabbit.nagare.INagareService interface,
 * generating a proxy if needed.
 */
public static com.giantrabbit.nagare.INagareService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.giantrabbit.nagare.INagareService))) {
return ((com.giantrabbit.nagare.INagareService)iin);
}
return new com.giantrabbit.nagare.INagareService.Stub.Proxy(obj);
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
case TRANSACTION_download:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.download(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_errors:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.errors();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_file_name:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.file_name();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_position:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.position();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_positionT:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.positionT();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_duration:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.duration();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_seek:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.seek(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_pause:
{
data.enforceInterface(DESCRIPTOR);
this.pause();
reply.writeNoException();
return true;
}
case TRANSACTION_resume:
{
data.enforceInterface(DESCRIPTOR);
this.resume();
reply.writeNoException();
return true;
}
case TRANSACTION_play:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.play(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_state:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.state();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.giantrabbit.nagare.INagareService
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
@Override public void download(java.lang.String url) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(url);
mRemote.transact(Stub.TRANSACTION_download, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String errors() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_errors, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String file_name() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_file_name, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long position() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_position, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int positionT() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_positionT, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int duration() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_duration, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void seek(int i) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(i);
mRemote.transact(Stub.TRANSACTION_seek, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void pause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void resume() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_resume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void play(java.lang.String uri) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(uri);
mRemote.transact(Stub.TRANSACTION_play, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int state() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_state, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_download = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_errors = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_file_name = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_position = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_positionT = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_duration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_seek = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_pause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_resume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_play = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_state = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
}
public void download(java.lang.String url) throws android.os.RemoteException;
public java.lang.String errors() throws android.os.RemoteException;
public java.lang.String file_name() throws android.os.RemoteException;
public long position() throws android.os.RemoteException;
public int positionT() throws android.os.RemoteException;
public int duration() throws android.os.RemoteException;
public void stop() throws android.os.RemoteException;
public void seek(int i) throws android.os.RemoteException;
public void pause() throws android.os.RemoteException;
public void resume() throws android.os.RemoteException;
public void play(java.lang.String uri) throws android.os.RemoteException;
public int state() throws android.os.RemoteException;
}
