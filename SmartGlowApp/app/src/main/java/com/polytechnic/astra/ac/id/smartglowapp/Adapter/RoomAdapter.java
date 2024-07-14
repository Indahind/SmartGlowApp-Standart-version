package com.polytechnic.astra.ac.id.smartglowapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.Model.*;

public class RoomAdapter extends ArrayAdapter<Ruangan> {
    private Activity context;
    private List<Ruangan> RuanganList;
    private String creadby;

    public RoomAdapter(Context context, List<Ruangan> houses, String creadby) {
        super(context, R.layout.layout_room_list, houses);
        this.context = (Activity) context;
        this.RuanganList = houses;
        this.creadby = creadby;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
//        Ruangan house = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_room_list, parent, false);
        }

        // Lookup view for data population
        ImageView imgDevice = convertView.findViewById(R.id.imgDevice1);
        TextView txtData = convertView.findViewById(R.id.txtData1);
        TextView txtDataDetail = convertView.findViewById(R.id.txtDataDetail1);
        //Switch btnDevice = convertView.findViewById(R.id.btnDevice1);

        Ruangan oi = RuanganList.get(position);
        // Populate the data into the template view using the data object
        if (oi != null) {
            // Here you can set the values based on the house object
            txtData.setText(oi.getNama());
            txtDataDetail.setText(oi.getRumahId());
            // Set image resource if you have any
            //imgDevice.setImageResource(R.drawable.shofabaru);
        }

        // Set onClickListener for the convertView (item in the list)
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to show house detail
                showHouseDetail(oi);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private void showHouseDetail(Ruangan house) {
        // Open detail activity or show detail dialog
//        Intent intent = new Intent(getContext(), MainActivity_Lampu.class);
//        intent.putExtra("house_id", house.getRuanganId());
//        intent.putExtra("creadby", creadby);
//        intent.putExtra("house_name", house.getNama());
//        intent.putExtra("house_address", house.getRumahId());
//        // Add other necessary data
//        getContext().startActivity(intent);
    }
}
