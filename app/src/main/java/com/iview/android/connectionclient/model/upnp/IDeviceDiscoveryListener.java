package com.iview.android.connectionclient.model.upnp;

public interface IDeviceDiscoveryListener {

	public void addedDevice(IUpnpDevice device);

	public void removedDevice(IUpnpDevice device);
}
