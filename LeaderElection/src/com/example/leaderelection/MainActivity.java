package com.example.leaderelection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	
	static WifiP2pManager mManager;
	static Channel mChannel;
	static BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	private static WifiP2pConfig mConfig = new WifiP2pConfig();
	
	private static ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();
	private static Button mConnectButton;
	
//    private ServerThread serverThread;
//	private ClientThread clientThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
	    mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

	    mIntentFilter = new IntentFilter();
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    
	    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
	        @Override
	        public void onSuccess() {
	        	//Log.d(TAG, "DISCOVERED PEERS BITCHES!!!!");
	        }

	        @Override
	        public void onFailure(int reasonCode) {
	        	//Log.d(TAG, "FUUUUUUCK!!!!!!!");
	        }
	    });
		
	}

	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
	    super.onResume();
	    registerReceiver(mReceiver, mIntentFilter);
	    
	}
	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
	    super.onPause();
	    unregisterReceiver(mReceiver);
	}
	
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			mConnectButton = (Button) rootView.findViewById(R.id.connection);
			
			mConnectButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//mConfig = ((WiFiDirectBroadcastReceiver) mReceiver).getWifiP2pConfig();
					devices = ((WiFiDirectBroadcastReceiver) mReceiver).getWifiDevices();
					for (WifiP2pDevice device : devices) {
						mConfig.deviceAddress = device.deviceAddress;
						Log.d(TAG, "CONNECTING TO: " + device.deviceName);
					    mManager.connect(mChannel, mConfig, new ActionListener() {
	
				            @Override
				            public void onSuccess() {
				                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
				            	Log.d("STATE", "CONNECTION SUCCESS!");
				            }
	
				            @Override
				            public void onFailure(int reason) {
				                Log.d("STATE", "CONNECTION FAILED!");
				            }
				        });
					}
				}
				
			});
			
			return rootView;
		}
	}
	
}
