//package com.polytechnic.astra.ac.id.smartglowapp.Adapter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.fragment.app.FragmentActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.polytechnic.astra.ac.id.smartglowapp.Fragment.HomeFragment;
//import com.polytechnic.astra.ac.id.smartglowapp.Fragment.RoomFragment;
//import com.polytechnic.astra.ac.id.smartglowapp.Model.*;
//import com.polytechnic.astra.ac.id.smartglowapp.R;
//
//import java.util.List;
//
//public class HouseAdapter extends ArrayAdapter<Rumah> {
//
//    private Activity context;
//    private List<Rumah> userList;
//    private String creadby;
//    private String owner;
//    public HouseAdapter(Context context, List<Rumah> houses,String creadby, String owner) {
//        super(context, R.layout.layout_home_list, houses);
//        this.context = (Activity) context;
//        this.userList = houses;
//        this.creadby = creadby;
//        this.owner = owner;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get the data item for this position
//        //Rumah house = getItem(position);
//
//        // Check if an existing view is being reused, otherwise inflate the view
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_home_list, parent, false);
//        }
//
//        // Lookup view for data population
//        ImageView imgDevice = convertView.findViewById(R.id.imgDevice1);
//        TextView txtData = convertView.findViewById(R.id.txtData1);
//
//
//        TextView txtDataDetail = convertView.findViewById(R.id.txtDataDetail1);
//        //Switch btnDevice = convertView.findViewById(R.id.btnDevice1);
//
//        Rumah oi = userList.get(position);
//        // Populate the data into the template view using the data object
//        if (oi != null) {
//            // Here you can set the values based on the house object
//            txtData.setText(oi.getNama());
//            txtDataDetail.setText(oi.getAlamat_rumah());
//            // Set image resource if you have any
//            //imgDevice.setImageResource(R.drawable.shofabaru);
//        }
//
//        // Set onClickListener for the convertView (item in the list)
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Call method to show house detail
//                showHouseDetail(oi,creadby);
//            }
//        });
//
//        // Return the completed view to render on screen
//        return convertView;
//    }
//
//    private void showHouseDetail(Rumah house, String creadby) {
//        // Create a new instance of RoomFragment
//        RoomFragment roomFragment = new RoomFragment();
//
//        // Prepare arguments to pass to the fragment
//        Bundle args = new Bundle();
//        args.putString("house_id", house.getRumahId());
//        args.putString("creadby", creadby);
//        args.putString("house_name", house.getNama());
//        args.putString("house_address", house.getAlamat_rumah());
//        args.putString("owner", owner);
//        // Add other necessary data to the bundle
//
//        // Set arguments to the fragment
//        roomFragment.setArguments(args);
//
//        // Get the fragment manager from the hosting activity
//        FragmentActivity activity = (FragmentActivity) context; // context should be set properly in your adapter
//        FragmentManager fragmentManager = activity.getSupportFragmentManager();
//
//        // Begin a fragment transaction
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        // Replace the fragment_container with roomFragment
//        fragmentTransaction.replace(R.id.fragment_listRoom, roomFragment);
//
//        // Optionally add to back stack
//        fragmentTransaction.addToBackStack(null);
//
//        // Commit the transaction
//        fragmentTransaction.commit();
//    }
//
//
//}
