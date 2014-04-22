package com.example.leaderelection;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "BroadcastReceiver";
	
    private WifiP2pManager mManager;
    private Channel mChannel;
    private MainActivity mActivity;
    
    private WifiP2pConfig mConfig = new WifiP2pConfig();
	
	private ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();

    PeerListListener myPeerListListener;
    
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
            MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        
        this.myPeerListListener = new PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
            	Log.d("STATE", "P2P Peers Available");
            	for (WifiP2pDevice device : peers.getDeviceList()) {
            		Log.d("STATE", "Device " + device.deviceName + " discovered");
            		mConfig.deviceAddress = device.deviceAddress;
            	}
            }
        };
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, action);
        
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
            }
        	
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        	
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
        	Log.d(TAG, "REQUESTING LIST OF SHIT!!!!");
            if (mManager != null) {
                mManager.requestPeers(mChannel, myPeerListListener);
            }
        	
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
    
    public WifiP2pConfig getWifiP2pConfig() {
    	return mConfig;
    }
    
    public ArrayList<WifiP2pDevice> getWifiDevices() {
    	return devices;
    }
}