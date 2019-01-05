package com.iview.android.connectionclient.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iview.android.connectionclient.model.CObservable;
import com.iview.android.connectionclient.model.upnp.IUpnpDevice;

import java.util.Observer;

public class UpnpServiceController {
    public final static String TAG = "UpnpServiceController";

    private Activity mActivity = null;
    private Context mContext = null;

    protected IUpnpDevice renderer;
    protected IUpnpDevice contentDirectory;

    protected CObservable rendererObservable;
    protected CObservable contentDirectoryObservable;

    private final UpnpServiceListener mUpnpServiceListener;

    public UpnpServiceController(Context context) {
        mContext = context;
        mUpnpServiceListener = new UpnpServiceListener(context);

        rendererObservable = new CObservable();
        contentDirectoryObservable = new CObservable();
    }

    public void pause() {
        mActivity.unbindService(mUpnpServiceListener.getControlServiceConnection());
        mActivity = null;
    }

    public void resume(Activity activity) {
        mActivity = activity;

        Intent intent = new Intent();
        intent.setAction("android.intent.action.ControlService");
        intent.setPackage("com.iview.android.connectionclient");
        mActivity.bindService(intent, mUpnpServiceListener.getControlServiceConnection(), mContext.BIND_AUTO_CREATE);
    }

    public UpnpServiceListener getUpnpServiceListener() {
        return mUpnpServiceListener;
    }

    public void setSelectedRenderer(IUpnpDevice renderer)
    {
        setSelectedRenderer(renderer, false);
    }

    public void setSelectedRenderer(IUpnpDevice renderer, boolean force)
    {
        // Skip if no change and no force
        if (!force && renderer != null && this.renderer != null && this.renderer.equals(renderer))
            return;

        this.renderer = renderer;
        rendererObservable.notifyAllObservers();
    }

    public void setSelectedContentDirectory(IUpnpDevice contentDirectory)
    {
        setSelectedContentDirectory(contentDirectory, false);
    }

    public void setSelectedContentDirectory(IUpnpDevice contentDirectory, boolean force)
    {
        // Skip if no change and no force
        if (!force && contentDirectory != null && this.contentDirectory != null
                && this.contentDirectory.equals(contentDirectory))
            return;

        this.contentDirectory = contentDirectory;
        contentDirectoryObservable.notifyAllObservers();
    }

    public IUpnpDevice getSelectedRenderer()
    {
        return renderer;
    }

    public IUpnpDevice getSelectedContentDirectory()
    {
        return contentDirectory;
    }

    public void addSelectedRendererObserver(Observer o)
    {
        Log.i(TAG, "New SelectedRendererObserver");
        rendererObservable.addObserver(o);
    }

    public void delSelectedRendererObserver(Observer o)
    {
        rendererObservable.deleteObserver(o);
    }

    public void addSelectedContentDirectoryObserver(Observer o)
    {
        contentDirectoryObservable.addObserver(o);
    }

    public void delSelectedContentDirectoryObserver(Observer o)
    {
        contentDirectoryObservable.deleteObserver(o);
    }
}
