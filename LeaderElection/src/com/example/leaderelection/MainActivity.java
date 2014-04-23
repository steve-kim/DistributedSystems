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
	private final String TAG = "MainActivity";
	
	static WifiP2pManager mManager;
	static Channel mChannel;
	static BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	private static WifiP2pConfig mConfig = null;
	private static ConnectionActionListener connectionListener;
	
	private ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();
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
				
			});
			
			return rootView;
		}
	}
	
/*	class ClientThread extends Thread{
		Socket socket;
		InputStream inputStream;
		OutputStream outputStream;
		PrintWriter outToServer;
		ObjectOutputStream objOutputStream;
		String string = "mensagem de comunicacao";
		private int port;
		private String host;
		private ObjectInputStream objInputStream;

		public ClientThread(String host, int port) {
			// TODO Auto-generated constructor stub
			this.host = host;
			this.port = port;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			socket = new Socket();
			try {
				socket.bind(null);
				socket.connect(new InetSocketAddress(host, port), 500);

				outputStream = socket.getOutputStream();
				outToServer = new PrintWriter(outputStream);
				String input = "HELLO!!!";
				Log.d("STATE", "client sends: " + string);
				
				//send to server
				outToServer.flush();
				outToServer.println(input);

				inputStream = socket.getInputStream();
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(inputStream));
				String response = inFromServer.readLine();
				Log.d(TAG, response);

			} catch (IOException e) {
				e.printStackTrace();
			} 
			finally {
				if ( socket != null ) {
					if ( socket.isConnected()) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}

	}

	class ServerThread extends Thread{
		ServerSocket serverSocket;
		Socket client;
		InputStream inputStream;
		ObjectInputStream objInputStream ;
		OutputStream outputStream;
		ObjectOutputStream objOutputStream;
		String string = "mensagem de comunicacao";

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				serverSocket = new ServerSocket(8888);
				client = serverSocket.accept();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter outToClient = new PrintWriter(client.getOutputStream(), true);
				
				while(!inFromClient.ready()) {}
				String fromClient = inFromClient.readLine();
				
				//Send the results back to client
	        	outToClient.flush();
	        	outToClient.println("World!!!!");


				serverSocket.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		// TODO Auto-generated method stub
		Log.d("STATE", "ConnectionInfo " + info.describeContents());
		if (info.isGroupOwner) {
			Log.d("STATE", "I am the MASTER! muahuaha");
			createServerSocket();
		} else {
			createClientSocket(info);
			Log.d("STATE", "Slave :~");
		}
	}
	
	private void createClientSocket(WifiP2pInfo info) {
		clientThread = new ClientThread(info.groupOwnerAddress.getHostAddress(), 8888);
		clientThread.start();
	}

	private void createServerSocket() {
		serverThread = new ServerThread();
		serverThread.start();
	}*/

}
