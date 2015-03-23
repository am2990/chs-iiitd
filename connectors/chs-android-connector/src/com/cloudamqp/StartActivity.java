package com.cloudamqp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cloudamqp.androidexample.ActivityHome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class StartActivity extends Activity {
	
    public final static String USERNAME = "com.mycompany.myfirstapp.USERNAME";
    public final static String PASSWORD = "com.mycompany.myfirstapp.PASSWORD";
    public final static String PTOPICS = "com.mycompany.myfirstapp.PUBLISHTOPICS";
    public final static String STOPICS = "com.mycompany.myfirstapp.SUBSCRIBETOPICS";
    private TextView textView;
    private String ptopics ="";
    private String stopics ="";
    private String username = "";
    private String password = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
        textView = (TextView) findViewById(R.id.textStatus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
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
	
	public void sendMessage(View view) {
		  EditText Username = (EditText) findViewById(R.id.editTextUsername);
		  EditText Password = (EditText) findViewById(R.id.editTextPassword);
		  username = Username.getText().toString();
		  password = Password.getText().toString();
		  
		  //TODO get topics from get request
		  String[] stringUrl = {"http://192.168.48.21:8080/chs/api/topics/publisher?username="+username+"&pass="+password,
				  				"http://192.168.48.21:8080/chs/api/topics/subscriber?username="+username+"&pass="+password,
		  					   }; 
		  
		  ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		  if (networkInfo != null && networkInfo.isConnected()) {
	            new DownloadWebpageTask().execute(stringUrl);
		  } else {
	            textView.setText("No network connection available.");
		  }
      	  //TODO If result is no topics or authentication failure then no intent goes through
//		  intent.putExtra(USERNAME, username);
		  
	}
	
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
        	Log.d("StartActivity",result.trim());
        	
        	String[] temp = result.split(":"); 
        	ptopics = temp[0]; stopics = temp[1];
            textView.setText(result);
            Intent intent = new Intent(getApplicationContext(), ActivityHome.class);
            intent.putExtra(USERNAME, username);
            intent.putExtra(PASSWORD, password);
            intent.putExtra(PTOPICS, ptopics);
            intent.putExtra(STOPICS, stopics);
            startActivity(intent);
        }
	}
	
	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private String downloadUrl(String... urls) throws IOException {
	    InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	    int len = 500;
	    
	    try {
	    	String responseString = "";
	    	for(int i =0 ; i< urls.length; i++){    
	        URL url = new URL(urls[i]);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.d("DEBUG", "The response is: " + response);
	        is = conn.getInputStream();
	        
	        // Convert the InputStream into a string
	        String contentAsString = readIt(is, len);
	        responseString += contentAsString+":";
	    	}
	        return responseString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	// Reads an InputStream and converts it to a String.
	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[len];
	    reader.read(buffer);
	    return new String(buffer);
	}
	
}
