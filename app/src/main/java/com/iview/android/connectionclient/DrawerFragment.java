package com.iview.android.connectionclient;

import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.iview.android.connectionclient.model.upnp.IDeviceDiscoveryListener;
import com.iview.android.connectionclient.model.upnp.IUpnpDevice;
import com.iview.android.connectionclient.view.ContentDeviceAdapter;
import com.iview.android.connectionclient.view.DeviceDisplay;
import com.iview.android.connectionclient.view.RendererAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class DrawerFragment extends Fragment implements IDeviceDiscoveryListener, Observer {

    private final static String TAG = "DrawerFragment";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private View mDrawerView;
	private View mFragmentContainerView;

	private RecyclerView mContentCyclerView;
	private List<DeviceDisplay> mContentDispalyList = new ArrayList<>();
	private ContentDeviceAdapter mContentDeviceAdapter;

	private RecyclerView mRendererCyclerView;
	private List<DeviceDisplay> mRendererList = new ArrayList<>();
	private RendererAdapter mRendererAdapter;

	private boolean mUserLearnedDrawer;

    private final static int MSG_ADD_DEVICE = 0;
    private final static int MSG_REMOVE_DEVICE = 1;

    private Handler mHandler = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_DEVICE:
                    ArrayList addData = msg.getData().getParcelableArrayList("data");
                    DeviceDisplay addDeviceDisplay = (DeviceDisplay) addData.get(0);
                    addDevice(addDeviceDisplay);
                    break;
                case MSG_REMOVE_DEVICE:
                    ArrayList removeData = msg.getData().getParcelableArrayList("data");
                    DeviceDisplay removeDeviceDisplay = (DeviceDisplay) removeData.get(0);
                    switch (removeDeviceDisplay.getDeviceType()) {
                        case DeviceDisplay.DEVICE_TYPE_MEDIASERVER:
                            removeDevice(removeDeviceDisplay);
                            break;
                    }
                    break;
            }
        }
    };

	public DrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);

		ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().addDeviceListener(this);
	}

    @Override
    public void onDestroy() {
	    super.onDestroy();
        ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().removeDeviceListener(this);
    }



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerView =  inflater.inflate(
				R.layout.device_fragment, container, false);

		return mDrawerView;
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),					/* host Activity */
				mDrawerLayout,					/* DrawerLayout object */
				R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
				R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}

				getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.addDrawerListener(mDrawerToggle);

		//Add MediaServier recycleView
		mContentCyclerView = (RecyclerView) getActivity().findViewById(R.id.contentdirectory_recyclerview);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		mContentCyclerView.setLayoutManager(layoutManager);
		mContentDeviceAdapter = new ContentDeviceAdapter(mContentDispalyList);
		mContentCyclerView.setAdapter(mContentDeviceAdapter);

		//Add MediaRenderer recycleView
		mRendererCyclerView = (RecyclerView) getActivity().findViewById(R.id.renderer_recyclerview);
		LinearLayoutManager rendererLayoutManager = new LinearLayoutManager(getActivity());
		mRendererCyclerView.setLayoutManager(rendererLayoutManager);
		mRendererAdapter = new RendererAdapter(mRendererList);
		mRendererCyclerView.setAdapter(mRendererAdapter);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private ActionBar getActionBar() {
		return ((AppCompatActivity)getActivity()).getSupportActionBar();
	}

	public void addContetnDisplayDevice(DeviceDisplay dev) {
		if (!isContain(dev, mContentDispalyList)) {
			mContentDispalyList.add(dev);
			Log.e(TAG, "addContetnDisplayDevice, size:" + mContentDispalyList.size() + " ,name:" + dev.getDeviceName());
			mContentDeviceAdapter.notifyDataSetChanged();
		}
	}

	public void removeContetnDisplayDevice(DeviceDisplay dev) {
		for (int i = 0; i < mContentDispalyList.size(); i++) {
			if (mContentDispalyList.get(i).isEquals(dev)) {
				mContentDispalyList.remove(i);
				mContentDeviceAdapter.notifyItemRemoved(i);
				mContentDeviceAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	public void addRendererDevice(DeviceDisplay dev) {
		if (!isContain(dev, mRendererList)) {
			mRendererList.add(dev);
			Log.e(TAG, "addRendererDevice, size:" + mRendererList.size() + " ,name:" + dev.getDeviceName());
			mRendererAdapter.notifyDataSetChanged();
		}
	}

	public void removeRendererDevice(DeviceDisplay dev) {
		for (int i = 0; i < mRendererList.size(); i++) {
			if (mRendererList.get(i).isEquals(dev)) {
				mRendererList.remove(i);
				mRendererAdapter.notifyItemRemoved(i);
				mRendererAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	public boolean isContain(DeviceDisplay dev, List<DeviceDisplay> list) {
		boolean ret = false;
		Log.e(TAG, "isContain size:" +  list.size());
		for (int i = 0; i < list.size(); i++) {
		    Log.e(TAG, "isContain");
			if (list.get(i).isEquals(dev)) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	@Override
	public void addedDevice(IUpnpDevice device){
        Log.d(TAG, "device add");
        DeviceDisplay deviceDisplay = new DeviceDisplay(device);
        Bundle bundle = new Bundle();
        ArrayList arr = new ArrayList();
        arr.add(deviceDisplay);
        bundle.putStringArrayList("data",arr);
        Message message = new Message();
        message.what = MSG_ADD_DEVICE;
        message.setData(bundle);
        mHandler.sendMessage(message);
	}

	@Override
	public void removedDevice(IUpnpDevice device) {
        Log.d(TAG, "device remove");
        DeviceDisplay deviceDisplay = new DeviceDisplay(device);
        Bundle bundle = new Bundle();
        ArrayList arr = new ArrayList();
        arr.add(deviceDisplay);
        bundle.putStringArrayList("data",arr);
        Message message = new Message();
        message.what = MSG_REMOVE_DEVICE;
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    public void addDevice(DeviceDisplay dev) {
        Log.e(TAG, "addDevie und" + dev.getUDN());
        switch (dev.getDeviceType()) {
            case DeviceDisplay.DEVICE_TYPE_MEDIASERVER:
                addContetnDisplayDevice(dev);
                break;
            case DeviceDisplay.DEVICE_TYPE_RENDERER:
                addRendererDevice(dev);
                break;
        }
    }

    public void removeDevice(DeviceDisplay dev) {
        Log.e(TAG, "removeDevice und" + dev.getUDN());
        switch (dev.getDeviceType()) {
            case DeviceDisplay.DEVICE_TYPE_MEDIASERVER:
                removeContetnDisplayDevice(dev);
                break;
            case DeviceDisplay.DEVICE_TYPE_RENDERER:
                removeRendererDevice(dev);
                break;
        }
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
