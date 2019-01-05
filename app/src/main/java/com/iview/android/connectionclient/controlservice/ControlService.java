package com.iview.android.connectionclient.controlservice;

import android.app.Service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.iview.android.connectionclient.control.RemoteControl;
import com.iview.android.connectionclient.model.IServiceListener;
import com.iview.android.connectionclient.model.upnp.CDevice;
import com.iview.android.connectionclient.model.upnp.IDeviceDiscoveryListener;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;

import java.util.ArrayList;
import java.util.List;

import static org.cybergarage.upnp.device.ST.ALL_DEVICE;


public class ControlService extends Service implements IServiceListener{

    private final static String TAG = "ControlService";

    private ControlBinder mBinder = new ControlBinder();

    private RemoteControl mControlPoint;

    private List<IDeviceDiscoveryListener> mDeviceListerner = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
        uninit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e(TAG, "onBind");
        return mBinder;
    }

    public class ControlBinder extends Binder {
        public ControlService getService() {
            return ControlService.this;
        }
    }

    private void init() {
        mControlPoint = new RemoteControl();
        mControlPoint.regisiterControlListener(this);
        mControlPoint.search();
    }

    private void uninit() {
        mControlPoint.unregisiterControlListener();
        mControlPoint = null;
    }

    public void searchDevice() {
        mControlPoint.search(ALL_DEVICE);
    }

    public void addDeviceListerner(IDeviceDiscoveryListener deviceListerner) {
        mDeviceListerner.add(deviceListerner);
    }

    public void removeDeviceListerner(IDeviceDiscoveryListener deviceListener) {
        if (mDeviceListerner.contains(deviceListener)) {
            mDeviceListerner.remove(deviceListener);
        }
    }
    @Override
    public void deviceAdded(final Device device) {
        if (!mDeviceListerner.isEmpty()) {
            for (int i = 0; i < mDeviceListerner.size(); i++) {
                mDeviceListerner.get(i).addedDevice(new CDevice(device));
            }
        }
    }

    @Override
    public void deviceRemoved(final Device device) {
        if (!mDeviceListerner.isEmpty()) {
            for (int i = 0; i < mDeviceListerner.size(); i++) {
                mDeviceListerner.get(i).removedDevice(new CDevice(device));
            }
        }
    }

    public DeviceList getDeviceList() {
        DeviceList list = mControlPoint.getDeviceList();
        return list;
    }

    public RemoteControl getControlPoint() {
        return mControlPoint;
    }
}