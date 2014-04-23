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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "BroadcastReceiver";
	
    private WifiP2pManager mManager;
    private Channel mChannel;
    private MainActivity mActivity;
    
    private ServerThread serverThread;
	private ClientThread clientThread;

    
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
        	if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
					
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
				});
            }
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
    
	class ClientThread extends Thread{
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
				Log.d("STATE", "client sends: " + input);
				
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
				serverSocket = new ServerSocket(35000);
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


	
	private void createClientSocket(WifiP2pInfo info) {
		clientThread = new ClientThread(info.groupOwnerAddress.getHostAddress(), 35000);
		clientThread.start();
	}

	private void createServerSocket() {
		serverThread = new ServerThread();
		serverThread.start();
	}
    

}