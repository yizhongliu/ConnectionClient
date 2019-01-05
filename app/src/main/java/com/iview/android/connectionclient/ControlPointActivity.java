package com.iview.android.connectionclient;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.iview.android.connectionclient.control.UpnpServiceController;
import com.iview.android.connectionclient.controlservice.ControlService;
import com.iview.android.connectionclient.view.ContentDirectoryFragment;
import com.iview.android.connectionclient.view.DeviceDisplay;
import com.iview.android.connectionclient.model.IServiceListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

public class ControlPointActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "ControlPointActivity";

    public static UpnpServiceController mUpnpServiceController = null;

    private DrawerFragment mDrawerFragment;

    private static ContentDirectoryFragment mContentDirectoryFragment;

    public static void setContentDirectoryFragment(ContentDirectoryFragment f) {
        mContentDirectoryFragment = f;
    }

    public static ContentDirectoryFragment getContentDirectoryFragment() {
        return mContentDirectoryFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_point);
        Log.e(TAG, "onCreate");
        if (Build.VERSION.SDK_INT > 23) {
            checkPermission();
        }

        if (mUpnpServiceController == null) {
            mUpnpServiceController = new UpnpServiceController(this);
        }

        initView();
    }

    @Override
    public void onResume()
    {
        Log.v(TAG, "Resume activity");
        mUpnpServiceController.resume(this);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.v(TAG, "Pause activity");
        mUpnpServiceController.pause();
        super.onPause();
    }


    private void initView() {
        if(getFragmentManager().findFragmentById(R.id.navigation_drawer) instanceof DrawerFragment)
        {
            mDrawerFragment = (DrawerFragment)
                    getFragmentManager().findFragmentById(R.id.navigation_drawer);

            // Set up the drawer.
            mDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void checkPermission() {
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(
                        Manifest.permission.INTERNET)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Log.d(TAG, "Internet permission accept");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d(TAG, permission.name + " is denied. More info should be provided.");
                            finish();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d(TAG, permission.name + " is denied.");
                            finish();
                        }
                    }
                });
    }
}
