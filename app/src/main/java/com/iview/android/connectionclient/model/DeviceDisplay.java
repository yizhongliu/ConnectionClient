package com.iview.android.connectionclient.model;

import android.util.Log;

import org.cybergarage.upnp.Device;

public class DeviceDisplay {
    private final static String TAG = "DeviceDisplay";

    public final static int DEVICE_TYPE_UNKNOWN = -1;
    public final static int DEVICE_TYPE_MEDIASERVER = 0;
    public final static int DEVICE_TYPE_RENDERER = 1;

    private Device device;

    public DeviceDisplay(Device device) {
        this.device = device;
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
}
