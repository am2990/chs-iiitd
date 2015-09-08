/*
 * Copyright 2014, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iiitd.android.fab;

import com.iiitd.form.RegistrationActivity;
import com.iiitd.navigationexample.R;
import com.iiitd.networking.SensorActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * This fragment inflates a layout with two Floating Action Buttons and acts as a listener to
 * changes on them.
 */
public class FloatingActionButtonBasicFragment extends Fragment implements FloatingActionButton.OnCheckedChangeListener{

    private final static String TAG = "FloatingActionButtonBasicFragment";
	private static final String ARG_ACTIVITY_NUMBER = "activity_number";

    
    public static FloatingActionButtonBasicFragment newInstance(int activityNumber) {
    	FloatingActionButtonBasicFragment fragment = new FloatingActionButtonBasicFragment();
		Bundle args = new Bundle();
		
		args.putInt(ARG_ACTIVITY_NUMBER, activityNumber);
		fragment.setArguments(args);
		
		
		return fragment;
	}

	public FloatingActionButtonBasicFragment() {
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fab_layout, container, false);

        // Make this {@link Fragment} listen for changes in both FABs.
        FloatingActionButton fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab_1);
        fab1.setOnCheckedChangeListener(this);
        return rootView;
    }


    @Override
    public void onCheckedChanged(FloatingActionButton fabView, boolean isChecked) {
    	// When a FAB is toggled, log the action.
    	switch (fabView.getId()){
    	case R.id.fab_1:
    		Log.d(TAG, String.format("FAB 1 was %s.", isChecked ? "checked" : "unchecked"));
    		int activityNumber = getArguments().getInt(ARG_ACTIVITY_NUMBER);
    		Intent fabIntent;
    		switch(activityNumber){
    		case 0:
    			fabIntent = new Intent(fabView.getContext(), RegistrationActivity.class);
    			startActivity(fabIntent);
    			break;
    		case 1:
    			fabIntent = new Intent(fabView.getContext(), SensorActivity.class);
    			startActivity(fabIntent);
    			break;
    		}
    		//TODO once registration is successful (startRegistrationActivty for Result.. show ticked button and  animate to add new 
    		break;
    	default:
    		break;
    	}
    }
}
