package com.iiitd.chs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iiitd.navigationexample.R;
import com.iiitd.sqlite.model.Patient;

import java.io.ByteArrayOutputStream;

/**
 * Created by apurv on 9/6/2015.
 */
public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {
    private Patient[] itemsData;

    public PatientAdapter(Patient[] itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PatientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create new views
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_item_layout, null);


        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(v, itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.txtViewTitle.setText(itemsData[position].getName());
//        TODO Add Image to patient
//        viewHolder.imgViewIcon.setImageResource(itemsData[position].getImageUrl());
        viewHolder.txtViewActivity.setText(itemsData[position].getGender());


    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.length;
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context context;
        public TextView txtViewTitle;
        public ImageView imgViewIcon;
        public TextView txtViewActivity;

        public ViewHolder(View v, View itemLayoutView) {
            super(itemLayoutView);
            context = itemLayoutView.getContext();
            itemLayoutView.setOnClickListener(this);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            txtViewActivity = (TextView) itemLayoutView.findViewById(R.id.item_activity);
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(v.getContext(), "Set up your profile!", Toast.LENGTH_SHORT).show();

            String value = txtViewTitle.getText().toString();

            imgViewIcon.buildDrawingCache();
            Bitmap b = imgViewIcon.getDrawingCache();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 50, bs);

            String activity_name = txtViewActivity.getText().toString();

            Intent intent = new Intent(context, PatientActivity.class);
            intent.putExtra(Patient.PATIENT_ID, 1);
            context.startActivity(intent);
        }

    }

}