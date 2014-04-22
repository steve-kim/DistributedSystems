package com.example.leaderelection;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";
	
	static WifiP2pManager mManager;
	static Channel mChannel;
	static BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	private static WifiP2pConfig mConfig = null;
	private static ConnectionActionListener connectionListener;
	
	private ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();
	private static Button mConnectButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		Log.d(TAG,"CREATING THE SHIT WE NEED!!");
	    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
	    mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
	    connectionListener = new ConnectionActionListener();
	    
	    Log.d(TAG,"INTENTS BITCHES!!!!!");
	    mIntentFilter = new IntentFilter();
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    Log.d(TAG,"DONE WITH THIS!!!!");
	    
	    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
	        @Override
	        public void onSuccess() {
	        	Log.d(TAG, "DISCOVERED PEERS BITCHES!!!!");
	        }

	        @Override
	        public void onFailure(int reasonCode) {
	        	Log.d(TAG, "FUUUUUUCK!!!!!!!");
	        }
	    });
		
	}

	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
	    super.onResume();
	    Log.d(TAG, "REGISTERING THE FUCKING RECEIVER!!!");
	    registerReceiver(mReceiver, mIntentFilter);
	    
	}
	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
	    super.onPause();
	    unregisterReceiver(mReceiver);
	}
	
	class ConnectionActionListener implements ActionListener {

		@Override
		public void onSuccess() {
			Log.d(TAG, "Connection Successful!");
		}

		@Override
		public void onFailure(int reason) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Connection Failed!");
		}

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
					mConfig = ((WiFiDirectBroadcastReceiver) mReceiver).getWifiP2pConfig();
				    
				    mManager.connect(mChannel, mConfig, connectionListener);
				}
				
			});
			
			return rootView;
		}
	}
	
}
