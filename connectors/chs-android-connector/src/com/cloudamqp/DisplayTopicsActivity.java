package com.cloudamqp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayTopicsActivity extends Activity {

	final TextView mTextView = (TextView) findViewById(R.id.topicsView);

	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_display_topics);

	        super.onCreate(savedInstanceState);

	        // Get the message from the intent
	        Intent intent = getIntent();
	        String username = intent.getStringExtra(StartActivity.USERNAME);
	        String password = intent.getStringExtra(StartActivity.PASSWORD);

	        // Create the text view
	        TextView textView = new TextView(this);
	        textView.setTextSize(40);
	        textView.setText(username);

	        // Set the text view as the activity layout
	        setContentView(textView);
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

	   
}
