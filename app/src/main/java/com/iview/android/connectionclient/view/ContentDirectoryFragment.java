package com.iview.android.connectionclient.view;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iview.android.connectionclient.ControlPointActivity;
import com.iview.android.connectionclient.R;
import com.iview.android.connectionclient.control.ContentDirectroyControl;
import com.iview.android.connectionclient.control.RemoteControl;
import com.iview.android.connectionclient.control.RendererControl;
import com.iview.android.connectionclient.model.upnp.CDevice;
import com.iview.android.connectionclient.model.upnp.IDeviceDiscoveryListener;
import com.iview.android.connectionclient.model.upnp.IUpnpDevice;
import com.iview.android.connectionclient.model.upnp.didl.DIDLContainer;
import com.iview.android.connectionclient.model.upnp.didl.DIDLDevice;
import com.iview.android.connectionclient.model.upnp.didl.DIDLItem;
import com.iview.android.connectionclient.model.upnp.didl.DIDLObject;
import com.iview.android.connectionclient.model.upnp.didl.DIDLObjectDisplay;
import com.iview.android.connectionclient.model.upnp.didl.IDIDLContainer;
import com.iview.android.connectionclient.model.upnp.didl.IDIDLItem;
import com.iview.android.connectionclient.model.upnp.didl.IDIDLObject;

import org.cybergarage.upnp.std.av.server.ContentDirectory;
import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ContentDirectoryFragment extends Fragment implements IDeviceDiscoveryListener, Observer {

    private final static String TAG = "ContentDirectoryFrag";

    private final static int MSG_UPDATE = 0;
    private final static int MSG_REFRESH = 1;
    private final static int MSG_PLAY = 2;

    private Activity mActivity;

    Handler mWorkHandler;
    HandlerThread mHandlerThread;

    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mContentReCyclerView;
    private TextView mTextView;
    private ContentDirectoryAdapter mContentDirectoryAdapter;

    private List<DIDLObjectDisplay> mContentList;
    private LinkedList<String> tree = null;
    private String currentID = null;
    private IUpnpDevice device = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        ControlPointActivity.setContentDirectoryFragment(this);
        super.onCreate(savedInstanceState);
        initWorkThread();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.browsing_list_fragment, container, false);

        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mContentList = new ArrayList<>();
        mContentReCyclerView = (RecyclerView) view.findViewById(R.id.contentlist_recyclerview);
        RecyclerViewLinearLayout layoutManager = new RecyclerViewLinearLayout(getActivity());
        mContentReCyclerView.setLayoutManager(layoutManager);
        mContentDirectoryAdapter = new ContentDirectoryAdapter(mContentList);
        mContentDirectoryAdapter.setOnItemClickListener(new ContentDirectoryAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, DIDLObjectDisplay data) {
                handleClickEvent(view, data);
            }
        });

        mContentReCyclerView.setAdapter(mContentDirectoryAdapter);

        mTextView = (TextView) view.findViewById(R.id.emptyContent);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (ControlPointActivity.mUpnpServiceController != null) {
            ControlPointActivity.mUpnpServiceController.addSelectedContentDirectoryObserver(this);
            ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().addDeviceListener(this);
        } else {
            Log.w(TAG, "upnpServiceController was not ready !!!");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mContentList.clear();
    //    refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ControlPointActivity.mUpnpServiceController.delSelectedContentDirectoryObserver(this);
        ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().removeDeviceListener(this);
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ControlPointActivity) context;//保存Context引用
    }

    @Override
    public void update(Observable observable, Object data)
    {
        Log.i(TAG, "ContentDirectory have changed");
        update();
    }

    public void update()
    {
        Message msg = Message.obtain();
        msg.what = MSG_UPDATE;
        mWorkHandler.sendMessage(msg);
    }

    public synchronized void refresh() {
        Log.e(TAG, "send refresh");
        Message msg = Message.obtain();
        msg.what = MSG_REFRESH;
        mWorkHandler.sendMessage(msg);
    }

    public void initWorkThread() {
        mHandlerThread = new HandlerThread("handlerThread");
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE:
                        /*
                        CDevice device = (CDevice) ControlPointActivity.mUpnpServiceController.getSelectedContentDirectory();
                        RemoteControl controlPoint = ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().getControlPoint();
                        ContentDirectroyControl contentDirectroyControl = new ContentDirectroyControl(controlPoint);
                        contentDirectroyControl.printContentDirectory(device.getDevice());*/
                        handleMsgRefresh();
                        break;
                    case MSG_REFRESH:
                        handleMsgRefresh();
                        break;
                    case MSG_PLAY:
                        ArrayList addData = msg.getData().getParcelableArrayList("data");
                        IUpnpDevice device = (IUpnpDevice) addData.get(0);
                        IDIDLItem item = (IDIDLItem) addData.get(1);
                        handleMsgPlay(device, item);
                        break;
                }
            }
        };
    }

    private void handleMsgPlay(IUpnpDevice device, IDIDLItem item) {
        RendererControl rendererControl = new RendererControl(ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().getControlPoint());
        rendererControl.play(((CDevice)device).getDevice(), item.getItem());
    }

    private void handleMsgRefresh() {
        Log.d(TAG, "refresh");

        setEmptyText(getString(R.string.loading));

        if (getActivity() != null) {// Visible
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Uncheck device
                    mSwipeContainer.setRefreshing(true);
                }
            });
        }

        IUpnpDevice contentDirectoryDevice = ControlPointActivity.mUpnpServiceController.getSelectedContentDirectory();

        if (contentDirectoryDevice == null) {
            Log.d(TAG, "contentDirectoryDevice == null");
            setEmptyText(getString(R.string.device_list_empty));

            if (device != null) {
                device = null;
                tree = null;
            }

            final Collection<IUpnpDevice> upnpDevices = ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().getMediaServerDeviceList();
            ArrayList<DIDLObjectDisplay> list = new ArrayList<DIDLObjectDisplay>();
            for (IUpnpDevice upnpDevice : upnpDevices) {
                list.add(new DIDLObjectDisplay(new DIDLDevice(upnpDevice)));
            }

            mContentList.clear();
            mContentList.addAll(list);

            if (getActivity() != null) {// Visible
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Uncheck device
                        mContentDirectoryAdapter.notifyDataSetChanged();
                    }
                });
            }
        } else {

            ContentDirectroyControl contentDirectroyControl = new ContentDirectroyControl(ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().getControlPoint());
            if (contentDirectroyControl == null) {
                Log.e(TAG, " contentDirectroyControl is null");
                return;
            }

            ContentNode contentNode;
            if (device == null || !device.equals(contentDirectoryDevice)) {

                device = ControlPointActivity.mUpnpServiceController.getSelectedContentDirectory();
                Log.i(TAG, " Content diretory change:" + device.getFriendlyName());
                tree = new LinkedList<String>();

                Log.i(TAG, "Browse root of a new device");
                contentNode = contentDirectroyControl.getContentDirectory(((CDevice)device).getDevice(),"0");
                currentID = "0";
            } else {
                if (tree != null && tree.size() > 0)
                {
                    String parentID = (tree.size() > 0) ? tree.getLast() : null;
                    Log.i(TAG, "Browse, currentID : " + currentID + ", parentID : " + parentID + ", device:" + device.getFriendlyName());
                    contentNode = contentDirectroyControl.getContentDirectory(((CDevice)device).getDevice(),currentID);
                    contentDirectroyControl.printContentNode(contentNode, 1);

                }
                else
                {
                    Log.i(TAG, "Browse, root"); contentNode = contentDirectroyControl.getContentDirectory(((CDevice)device).getDevice(), "0");
                }
            }

            mContentList.clear();
            mContentList.addAll(constructDIDLObject(contentNode));
            if (getActivity() != null) {// Visible
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Uncheck device
                        mContentDirectoryAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        if (getActivity() != null) {// Visible
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Uncheck device
                    mSwipeContainer.setRefreshing(false);
                }
            });
        }
    }

    public void setEmptyText(CharSequence text) {
        mTextView.setText(text);
    }

    public ArrayList<DIDLObjectDisplay> constructDIDLObject(ContentNode node) {
        ArrayList<DIDLObjectDisplay> list = new ArrayList<DIDLObjectDisplay>();

        Log.e(TAG, "node.name :" + node.getTitle());
        if (node.isContainerNode()) {
            Log.e(TAG, "node.getID() :" + node.getID() + ", parentID: " + node.getParentID());
            if (node.getTitle().equals("Root")) {
                ContainerNode containerNode = (ContainerNode)node;
                int nContentNodes = containerNode.getNContentNodes();
                Log.e("TAG","node :" + node.getTitle()+ " is Container. NContentNodes num is:" + nContentNodes + ", stringid:" + node.getID());
                for (int n=0; n<nContentNodes; n++) {
                    ContentNode cnode = containerNode.getContentNode(n);
                    Log.e("TAG","print Content Node :" + cnode.getTitle() + ", isContainer:" +  cnode.isContainerNode() + " ,isItem:" + cnode.isItemNode());
                    if (cnode.isContainerNode()) {
                        list.add(new DIDLObjectDisplay(new DIDLContainer((ContainerNode) cnode)));
                    } else if (cnode.isItemNode()) {
                        list.add(new DIDLObjectDisplay(new DIDLItem((ItemNode) cnode)));
                    }
                }
            }
        }

        return list;
    }

    private void handleClickEvent(View v, DIDLObjectDisplay didlObjectDisplay) {
        Log.e(TAG, "handleClickEvent");
        IDIDLObject didl = didlObjectDisplay.getDIDLObject();
        if (didl instanceof DIDLDevice) {
            ControlPointActivity.mUpnpServiceController.setSelectedContentDirectory(((DIDLDevice)didl).getDevice());
            refresh();
        } else if (didl instanceof IDIDLContainer) {
            currentID = didl.getId();
            String parentID = didl.getParentID();
            tree.push(parentID);

            refresh();
        } else if (didl instanceof IDIDLItem) {
            launchURI((IDIDLItem) didl);
        }
    }

    @Override
    public void addedDevice(IUpnpDevice device){
        Log.e(TAG, "device add location:" + device.getLocation());
        refresh();
    }

    @Override
    public void removedDevice(IUpnpDevice device) {
        Log.d(TAG, "device remove");
        refresh();
    }

    public void launchURI(final IDIDLItem uri) {
        Log.e(TAG, " get item uri" + uri.getURI());
        IUpnpDevice device = ControlPointActivity.mUpnpServiceController.getSelectedRenderer();
        int deviceNum = ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().getMediaServerDeviceList().size();

        if ((device == null) || (deviceNum > 1)) {
            showMultiChoiceDialog(uri);
        } else {
            launchRendererUri(device, uri);
        }
    }

    public void launchRendererUri(final IUpnpDevice device, final IDIDLItem uri) {
        Bundle bundle = new Bundle();
        ArrayList arr = new ArrayList();
        arr.add(device);
        arr.add(uri);
        bundle.putStringArrayList("data",arr);
        Message message = new Message();
        message.what = MSG_PLAY;
        message.setData(bundle);
        mWorkHandler.sendMessage(message);
    }

    public Boolean goBack()
    {
        if(tree == null || tree.isEmpty())
        {
            if(ControlPointActivity.mUpnpServiceController.getSelectedContentDirectory() != null)
            {
                // Back on device root, unselect device
                ControlPointActivity.mUpnpServiceController.setSelectedContentDirectory(null);
                return false;
            }
            else
            {
                // Already at the upper level
                return true;
            }
        }
        else
        {
            Log.d(TAG, "Go back in browsing");
            currentID = tree.pop();
            refresh();
            return false;
        }
    }

    ArrayList<IUpnpDevice> mediaRendererList = new ArrayList<>();
    private void showMultiChoiceDialog(final IDIDLItem uri) {

        mediaRendererList.clear();
        final Collection<IUpnpDevice> upnpDevices = ControlPointActivity.mUpnpServiceController.getUpnpServiceListener().getMediaRendererDeviceList();
        Log.e(TAG, "device size: " + upnpDevices.size());

        final String[] items = new String[upnpDevices.size()];
        final boolean checkItem[] = new boolean[upnpDevices.size()];
        int i = 0;
        for (IUpnpDevice upnpDevice : upnpDevices) {
            mediaRendererList.add(upnpDevice);
            items[i] = upnpDevice.getFriendlyName();
            checkItem[i] = false;
            i++;
        }
        if (getActivity() == null) {
            Log.e(TAG, "getActivity is null");
        }
        AlertDialog.Builder multiChoiceDialog =
                new AlertDialog.Builder(getActivity());
        multiChoiceDialog.setTitle("MediaRenderer");
        multiChoiceDialog.setMultiChoiceItems(items, checkItem,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                    }
                });
        multiChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkItem.length; i++) {
                            if(checkItem[i] == true) {
                                Log.e(TAG, "device :" + mediaRendererList.get(i).getFriendlyName());
                                ControlPointActivity.mUpnpServiceController.setSelectedRenderer(mediaRendererList.get(i));
                                launchRendererUri(mediaRendererList.get(i), uri);
                            }
                        }
                    }
                });
        multiChoiceDialog.show();
    }
}
