package com.iiitd.chs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.iiitd.navigationexample.R;
import com.iiitd.networking.NetworkDevice;
import com.iiitd.sqlite.helper.DatabaseHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class SensorFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private ArrayAdapter<NetworkDevice> adapter;
	private static Context mContext;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SensorFragment newInstance(int sectionNumber, Context context) {
		SensorFragment fragment = new SensorFragment();
		Bundle args = new Bundle();
		
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		
		mContext = context; 
		
		return fragment;
	}

	public SensorFragment() {
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
		
		
		DatabaseHelper db = new DatabaseHelper(mContext);
		
		//populated the list with Notifications
		List<NetworkDevice> data = new ArrayList<NetworkDevice>();
		
		data = db.getAllConnectedDevices();
		
		//		TODO : Fetch Sensor List from the database
//		data = db.getAllNotifications();
//		db.closeDb();

		//ArrayAdapter takes data from a source (fake data) and uses it to populate listView attached to it
		adapter = new ArrayAdapter<NetworkDevice>(
		getActivity(),  //fragment's parent activity (context)
		R.layout.list_item_desc,    //ID of list item layout
		R.id.list_item_desc_textview,   //ID of text view to populate
		data  
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
				final NetworkDevice item = (NetworkDevice) parent.getItemAtPosition(position);
				
				// show go to page with delete option 
			}

		});
		
//		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
//		    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
//		    	final NetworkDevice item = (NetworkDevice) arg0.getItemAtPosition(arg2);
//		        // adapter.notifyDataSetChanged();
//		        //adapter.notifyDataSetInvalidated();
//		        return true;
//		    }
//		});

		registerForContextMenu(listView);
		
		return rootView;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    //TODO Use Menu Inflater
	    menu.setHeaderTitle("Select The Action");    
        menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title   
        menu.add(0, v.getId(), 0, "Delete");  
	}
	
	@Override    
	public boolean onContextItemSelected(MenuItem item){
		//TODO implement Edit a Network Device
		if(item.getTitle()=="Edit"){  
			Toast.makeText(mContext,"Edit",Toast.LENGTH_LONG).show();  
		}    
		else if(item.getTitle()=="Delete"){			
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		    int index = info.position;
			
			
			DatabaseHelper db = new DatabaseHelper(mContext);
			db.removeConnectedDevice(adapter.getItem(index));
			adapter.remove(adapter.getItem(index));
			Toast.makeText(mContext,"Deleted",Toast.LENGTH_LONG).show();
			
		}else{  
			return false;  
		}    
		return true;    
	} 

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}
