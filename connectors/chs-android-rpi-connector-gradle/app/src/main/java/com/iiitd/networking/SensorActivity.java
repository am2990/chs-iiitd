package com.iiitd.networking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.iiitd.navigationexample.AddDeviceActivity;
import com.iiitd.navigationexample.R;

public class SensorActivity extends ListActivity{

	public static final String TAG = "SensorActivity";
	private Context mContext;
	private List<NetworkDevice> deviceList;
    ListView listView ;
    SimpleCursorAdapter mAdapter;
    ArrayAdapter<NetworkDevice> adapter;
    BlockingQueue<String> ipList;
    AsyncTask UdpReceiver = null;
	private static boolean receiverRunning = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		mContext = this;
		ipList = new LinkedBlockingQueue<String>();
		// Create a progress bar to display while the list loads
		//TODO Push progress bar to the centre
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);


        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        deviceList = new ArrayList<NetworkDevice>();

        adapter = new ArrayAdapter<NetworkDevice>(this,
        		android.R.layout.simple_list_item_1, deviceList);
        setListAdapter(adapter);
		UdpReceiver = new UDPReceiveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		getClientList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
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


	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {

		final NetworkDevice item = (NetworkDevice) l.getItemAtPosition(position);
		//Why has the item been removed ?

		deviceList.remove(item);
		Log.v(TAG, "Network Device" + item + " has MAC "+item.getMacAddress());

		Intent i = new Intent(this, AddDeviceActivity.class);
		i.putExtra("device", item);
		startActivity(i);

    }


	public void startDiscovery(View v){

        if(!receiverRunning)
            UdpReceiver = new UDPReceiveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		//TODO if hotspot is enabled then find clients from hotspot else use Multicast discovery over Network
		getClientList();
	}

	public void getClientList() {
	    int macCount = 0;
	    BufferedReader br = null;
	    try {
	        br = new BufferedReader(new FileReader("/proc/net/arp"));
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] splitted = line.split(" +");
	            if (splitted != null ) {
	                // Basic sanity check
	                String mac = splitted[3];
	                System.out.println("Mac : Outside If "+ mac );
	                if (mac.matches("..:..:..:..:..:..")) {
	                	macCount++;
	                	/* ClientList.add("Client(" + macCount + ")");
	                    IpAddr.add(splitted[0]);
	                    HWAddr.add(splitted[3]);
	                    Device.add(splitted[5]);*/
	                	System.out.println("Mac : "+ mac + " IP Address : "+splitted[0] );
	                	System.out.println("Mac_Count  " + macCount + " MAC_ADDRESS  "+ mac);
	                	Toast.makeText(
	                			mContext,
	                			"Mac_Count  " + macCount + "   MAC_ADDRESS  "
	                					+ mac, Toast.LENGTH_SHORT).show();
	                	//TODO Send a request and get back info
	                	ipList.add(splitted[0]);
	                	new UDPSendTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;

	                }
	               /* for (int i = 0; i < splitted.length; i++)
	                    System.out.println("Addressssssss     "+ splitted[i]);*/

	            }
	        }
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	}

	private class UDPSendTask extends AsyncTask<String, Integer, Long> {
	     protected Long doInBackground(String... ips) {
//	    	 UDPMulticast udp = new UDPMulticast(mContext, "224.3.29.71" , 10000);
	    	 try{
	    		 String messageStr="discover:10001";
	    		 int server_port = 10000;
	    		 DatagramSocket send = new DatagramSocket();
	    		 String ip = ipList.poll();
	    		 InetAddress local = InetAddress.getByName(ip);
	    		 int msg_length=messageStr.length();
	    		 byte[] message = messageStr.getBytes();
	    		 DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
	    		 for(int i =0;i<10;i++){
	    			 send.send(p);
	    		 }
//	    		 Toast.makeText(
//             			mContext,
//             			"Scanning", Toast.LENGTH_SHORT).show();
	    		 send.close();
	    	 }catch(Exception e){
	    		 e.printStackTrace();
	    	 }

//	    	 udp.sendMessage("discover");
	    	 return 0L;
	     }

	     protected void onProgressUpdate(Integer... progress) {
//	         setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(Long result) {
//	         showDialog("Downloaded " + result + " bytes");
	     }
	 }

	private String parseMessage(String msg){

		try{


			JSONObject obj = new JSONObject(msg);
			String packet_type = obj.getString("type");

			if(packet_type.equalsIgnoreCase("discover_reply")){
				String name = obj.getString("name");
				String ip = obj.getString("sdr");
				String mac = obj.getString("mac");
				JSONArray arr = obj.getJSONArray("argv");

				List<Sensor> sensorList = new ArrayList<Sensor>();
				for (int i = 0; i < arr.length(); i++){
					Object c = arr.get(i);
					JSONObject sens = new JSONObject(c.toString());
					String sensorname = (String) sens.get("sensorname");
					int sensorid = (int) sens.get("sensorid");

					Sensor s = new Sensor();
					s.setId(sensorid);
					s.setSensorName(sensorname);
					s.setSensorType(SensorType.MEDICAL);
					sensorList.add(s);

				}

				NetworkDevice n = new NetworkDevice();
		    	n.setDeviceName(name);
		    	n.setIpAddress(ip);
		    	n.setMacAddress(mac);
		    	n.setSensorList(sensorList);

				if(!deviceList.contains(n)){
		    		deviceList.add(n);
		    	}
			}



		}catch(Exception e){
			e.printStackTrace();
		}

		return msg;

	}

	private class UDPReceiveTask extends AsyncTask<URL, Integer, Long> {
	     protected Long doInBackground(URL... urls) {
//	    	 UDPMulticast udp = new UDPMulticast(mContext, "224.3.29.71" , 10000);
	    	 DatagramSocket receive = null;
	    	 try{
	    		 while(true){
		    		 String text;
		    		 int server_port = 10001;
		    		 byte[] message = new byte[1500];
		    		 DatagramPacket p = new DatagramPacket(message, message.length);
		    		 receive = new DatagramSocket(server_port);
		    		 receive.setSoTimeout(5000);
					 receiverRunning = true;
					 Log.d("Udp Receive","Receiver Waiting to Discovery Response...");
		    		 receive.receive(p);
		    		 text = new String(message, 0, p.getLength());
		    		 Log.d("Udp Receive","message:" + text);
	                 receive.close();
	                 parseMessage(text);
					 publishProgress(0);
	    		 }
	    	 }catch(InterruptedIOException e){
	    		 receive.close();
	    	 }catch(Exception e){
	    		 e.printStackTrace();
	    	 }

//	    	 udp.sendMessage("discover");
	    	 return 0L;
	     }

	     protected void onProgressUpdate(Integer... progress) {
//	         setProgressPercent(progress[0]);
			 adapter.notifyDataSetChanged();
	     }

	     protected void onPostExecute(Long result) {
//	         showDialog("Downloaded " + result + " bytes");
			 Toast.makeText(mContext,"Discovery Complete...", Toast.LENGTH_LONG).show();
			 receiverRunning = false;

	     }
	 }


}
