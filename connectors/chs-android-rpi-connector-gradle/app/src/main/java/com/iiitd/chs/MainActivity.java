package com.iiitd.chs;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.iiitd.amqp.AMQPService;
import com.iiitd.android.fab.FloatingActionButtonBasicFragment;
import com.iiitd.navigationexample.NavigationDrawerFragment;
import com.iiitd.navigationexample.R;
import com.iiitd.networking.Sensor;
import com.iiitd.sqlite.helper.DatabaseHelper;
import com.iiitd.sqlite.model.Notification;
import com.iiitd.sqlite.model.Patient;
import com.iiitd.sqlite.model.PatientObservation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	FloatingActionButtonBasicFragment fabFragment;
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private static Context mContext;
	
	public static Intent amqpIntent;
	
	private ListView mDrawerListView;

	public static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		mContext = this;
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
			
		
		if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fabFragment = FloatingActionButtonBasicFragment.newInstance(0);
            transaction.replace(R.id.sample_content_fragment, fabFragment);
            transaction.commit();
        }
		
		amqpIntent = new Intent(this, AMQPService.class);
		startService(amqpIntent);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(onNotification,
				new IntentFilter(Constants.BROADCAST_ACTION));

	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		
		Log.d("MAct", "Drawer list--" + position);
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = PlaceholderFragment.newInstance(position + 1);
		
		//tranasction for FAB frament
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); 
		
		switch(position){

		case 0:
		
            fabFragment = FloatingActionButtonBasicFragment.newInstance(0);
            transaction.replace(R.id.sample_content_fragment, fabFragment);
            transaction.commit();
			fragment = PlaceholderFragment.newInstance(position + 1);
			break;
		case 1:
			fragment = NotificationFragment.newInstance(position + 1);
			break;
		case 2:
			//replace fab fragment to take to the scan activity
			
            fabFragment = FloatingActionButtonBasicFragment.newInstance(1);
            transaction.replace(R.id.sample_content_fragment, fabFragment);
            transaction.commit();
			fragment = SensorFragment.newInstance(position + 1, mContext);
		}

		fragmentManager
		.beginTransaction()
		.replace(R.id.container,
				fragment).commit();

		
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		// TODO : Implement Search View
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		}
		
		if (id == R.id.action_sync) {
			//TODO only sync patients which need to be synced
//			AMQPImpl syncer = new AMQPImpl();
			String msg = "UUID: e4a94948-3e77-4279-8b05-f898a2db94d9:test patient:08/25/15:Male:57:list of allergies:11:11";
			
			DatabaseHelper db = new DatabaseHelper(mContext);
			List<Patient> p_list = db.getAllPatientsObjects();
			for(Patient p: p_list){
				List<PatientObservation> obs_list = db.getObsById(p.getId());
				for(PatientObservation obs: obs_list){
					Sensor s = db.getSensorByObsId(obs.getId());

					JSONObject obj = new JSONObject();
					try {
						obj.put("uuid", p.getUUID());
						obj.put("name", p.getName());
						obj.put("dob", p.getDob());
						obj.put("gender", p.getGender());

						JSONObject obs_j = new JSONObject();

						obs_j.put("temperature", Double.parseDouble(obs.getTemperature()));
						obs_j.put("allergies", obs.getAllergies());
						obs_j.put("sensorname", s.getSensorName());
						JSONArray sensor_readings = new JSONArray();

						List<String> sensor_arr = s.getReadings();
						sensor_readings.put(sensor_arr.get(0));
						sensor_readings.put(sensor_arr.get(1));

						if(s.getSensorName().equalsIgnoreCase("bp"))
							sensor_readings.put(sensor_arr.get(3));
						obs_j.put("sensor_readings", sensor_readings);

						obj.put("obs", obs);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

//					Log.d(Tag,"Publishing Json" + obj.toString());

					msg = p.getUUID() +":"+p.getName()+":"+p.getDob()+":"+p.getGender()+":"+obs.getTemperature()+":"+obs.getAllergies()+":84:78";
					msg = obj.toString();

				}
				amqpIntent.putExtra(Constants.AMQP_PUBLISH_MESSAGE, msg);
				startService(MainActivity.amqpIntent);
			}
			Toast.makeText(this, "Started Sync", Toast.LENGTH_LONG).show();;
			
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	private BroadcastReceiver onNotification= new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        // intent can contain anydata
	        Log.d("MainActivity","onReceive called");
	        //tv.setText("Broadcast received !");
	        String msg = intent.getStringExtra(Constants.AMQP_SUBSCRIBED_MESSAGE);

			String[] vals = msg.split(":");

	        DatabaseHelper db = new DatabaseHelper(mContext);

			db.addNotification(new Notification(vals[0], vals[1]));
	        
	        //TODO push notification to separate function

			//save notification in table
	        
	        NotificationCompat.Builder mBuilder =
	                new NotificationCompat.Builder(mContext)
	                .setSmallIcon(R.drawable.collections_cloud)
	                .setContentTitle("IIITD Chs")
	                .setContentText(msg);
	        // Creates an explicit intent for an Activity in your app
	        Intent resultIntent = new Intent(mContext, MainActivity.class);

	        // The stack builder object will contain an artificial back stack for the
	        // started Activity.
	        // This ensures that navigating backward from the Activity leads out of
	        // your application to the Home screen.
	        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
	        // Adds the back stack for the Intent (but not the Intent itself)
	        stackBuilder.addParentStack(MainActivity.class);
	        // Adds the Intent that starts the Activity to the top of the stack
	        stackBuilder.addNextIntent(resultIntent);
	        PendingIntent resultPendingIntent =
	                stackBuilder.getPendingIntent(
	                    0,
	                    PendingIntent.FLAG_UPDATE_CURRENT
	                );
	        mBuilder.setContentIntent(resultPendingIntent);
	        NotificationManager mNotificationManager =
	            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	        // mId allows you to update the notification later on.
	        int mId = 0;
	        mNotificationManager.notify(mId, mBuilder.build());

	    }
	};

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private ArrayAdapter<String> adapter;
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
					
			DatabaseHelper db = new DatabaseHelper(mContext);
			
			//populated the list with fake data
			List<String> data = new ArrayList<String>();
			data = db.getAllPatients();
			db.closeDb();

			//ArrayAdapter takes data from a source (fake data) and uses it to populate listView attached to it
			adapter = new ArrayAdapter<String>(
			getActivity(),  //fragment's parent activity (context)
			R.layout.list_item_desc,    //ID of list item layout
			R.id.list_item_desc_textview,   //ID of text view to populate
			data    //dummy forecast data
			);
			
			//get reference to list view and attach adapter to it
			//adapter supplies list item layouts to the list view based on dummy forecast data
			ListView listView = (ListView) rootView.findViewById(
			R.id.listview_desc
			);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
			      int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				String[] pat_id = (item.split("\\."));
				System.out.println("Item Selected from List-" + item + "(and has patient id" + pat_id);
				Integer patient_id = Integer.parseInt(pat_id[0]);
				
				Intent intent = new Intent(mContext, PatientActivity.class);
				intent.putExtra(Patient.PATIENT_ID, patient_id);
				startActivity(intent);
//			view.animate().setDuration(2000).alpha(0)
//			      .withEndAction(new Runnable() {
//			       @Override
//			       public void run() {
//			             data.remove(item);
//			             adapter.notifyDataSetChanged();
//			             view.setAlpha(1);
//			           }
//			        });
			      }

			});
			

			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class NotificationFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private ArrayAdapter<Notification> adapter;
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static NotificationFragment newInstance(int sectionNumber) {
			NotificationFragment fragment = new NotificationFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public NotificationFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
					
			DatabaseHelper db = new DatabaseHelper(mContext);
			
			//populated the list with Notifications
			List<Notification> data = new ArrayList<>();
			data = db.getAllNotifications();
			db.closeDb();

			//ArrayAdapter takes data from a source (fake data) and uses it to populate listView attached to it
			adapter = new ArrayAdapter<Notification>(
			getActivity(),  //fragment's parent activity (context)
			R.layout.list_item_desc,    //ID of list item layout
			R.id.list_item_desc_textview,   //ID of text view to populate
			data    //dummy forecast data
			);
			
			//get reference to list view and attach adapter to it
			//adapter supplies list item layouts to the list view based on dummy forecast data
			ListView listView = (ListView) rootView.findViewById(
			R.id.listview_desc
			);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					final Notification item = (Notification) parent.getItemAtPosition(position);
					Log.d(TAG, item.getNotification());
					// show notification in detail
				}

			});
			

			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

}
