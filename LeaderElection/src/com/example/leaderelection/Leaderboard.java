package com.example.leaderelection;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class Leaderboard extends Activity {

	private static BroadcastReceiver mReceiver;
	private LocationListener locationListener;
	
	private static double lat;
	private static double lng;
	private static double distanceToFinish;
	
	private static ArrayList<String> networkAddresses = null;
	
	private static final double latFinish = 30.2747;
	private static final double lngFinish = 97.7406;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		Intent intent = getIntent();
		networkAddresses = intent.getStringArrayListExtra("networkAddresses");
		if (networkAddresses != null) {
			for (String s : networkAddresses)
				Log.d("ARRAYLIST", s);
		}
		
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
   	 
	   	Criteria criteria = new Criteria();
	   	 
	   	String provider = locationManager.getBestProvider(criteria, true);
	   	 
	   	Location location = locationManager.getLastKnownLocation(provider);
	   	 
	   	if (location == null) {
		   	locationListener = new LocationListener() {
		   		public void onLocationChanged(Location location) {
		   			lat = location.getLatitude();
				   	lng = location.getLongitude();
			   	}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
				}
	
		   	};
	   		 
	   		 locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);
	   	 }
	   	else {
	   		lat = location.getLatitude();
	   		lng = location.getLongitude();
	   	}
	   	
	   	//Creating timer which executes once after 30 seconds
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new distFrom(), 0, 30000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leaderboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			View rootView = inflater.inflate(R.layout.fragment_leaderboard,
					container, false);
			return rootView;
		}
	}
	
	private static class distFrom extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			double earthRadius = 3958.75;
			double dLat = Math.toRadians(latFinish - lat);
			double dLng = Math.toRadians(lngFinish - lng);
			
			double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		               Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(lat)) *
		               Math.sin(dLng/2) * Math.sin(dLng/2);
			
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    double dist = earthRadius * c;
		    
		    int meterConversion = 1609;
		    
		    distanceToFinish = (dist * meterConversion);
		}

	}

}
