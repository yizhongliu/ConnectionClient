package com.iview.android.connectionclient.view;

import android.util.Log;

import com.iview.android.connectionclient.model.upnp.IUpnpDevice;

import org.cybergarage.upnp.Device;

public class DeviceDisplay {
    private final static String TAG = "DeviceDisplay";

    public final static int DEVICE_TYPE_UNKNOWN = -1;
    public final static int DEVICE_TYPE_MEDIASERVER = 0;
    public final static int DEVICE_TYPE_RENDERER = 1;

    private final IUpnpDevice device;

    public DeviceDisplay(IUpnpDevice device) {
        this.device = device;
    }

    public IUpnpDevice getDevice()
    {
        return device;
    }

    public String getDeviceName() {
        return device.getModelName();
    }

    public boolean isEquals(DeviceDisplay dev) {
        boolean ret = false;
        Log.e(TAG, "UDN:" + dev.getUDN());
        if (device.getUDN().equals(dev.getUDN())) {
            ret = true;
        }
        return ret;
    }

    public int getDeviceType() {
        int ret = DEVICE_TYPE_UNKNOWN;
        if (device.getDeviceType().contains("MediaServer")) {
            ret = DEVICE_TYPE_MEDIASERVER;
        } else if (device.getDeviceType().contains("MediaRenderer")) {
            ret = DEVICE_TYPE_RENDERER;
        }
        return ret;
    }

    public String getUDN () {
        return device.getUDN();
    }

    @Override
    public String toString()
    {
        if (device == null)
            return "";

        String name = getDevice().getFriendlyName();

        return name;
    }
}
