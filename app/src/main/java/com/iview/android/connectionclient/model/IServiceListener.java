package com.iview.android.connectionclient.model;

import java.util.Collection;

import android.content.ServiceConnection;

import org.cybergarage.upnp.Device;

public interface IServiceListener {

	public void deviceAdded(final Device device);

	public void deviceRemoved(final Device device);
}
