//package com.polytechnic.astra.ac.id.smartglowapp.Adapter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.CompoundButton;
//import android.widget.ImageView;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.List;
//
//import com.polytechnic.astra.ac.id.smartglowapp.R;
//import com.polytechnic.astra.ac.id.smartglowapp.Model.*;
//
//public class LampuAdapter extends ArrayAdapter<Lampu> {
//    private Activity context;
//    private List<Lampu> RuanganList;
//    private DatabaseReference databaseReference;
//
//    public LampuAdapter(Context context, List<Lampu> houses) {
//        super(context, R.layout.layout_lampu_list, houses);
//        this.context = (Activity) context;
//        this.RuanganList = houses;
//        databaseReference = FirebaseDatabase.getInstance().getReference("smart_home/lampu");
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_lampu_list, parent, false);
//        }
//
//        ImageView imgDevice = convertView.findViewById(R.id.imgDevice1);
//        TextView txtData = convertView.findViewById(R.id.txtData1);
//        TextView txtDataDetail = convertView.findViewById(R.id.txtDataDetail1);
//        Switch btnDevice = convertView.findViewById(R.id.switchbutton);
//
//        Lampu oi = RuanganList.get(position);
//        if (oi != null) {
//            txtData.setText(oi.getNama());
//            txtDataDetail.setText(oi.getStatus());
//            //imgDevice.setImageResource(R.drawable.shofabaru);
//
//            btnDevice.setChecked(oi.getStatus().equals("on")); // Set switch status based on current data
//        }
//
//        btnDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                String status = isChecked ? "on" : "off";
//                oi.setStatus(status); // Update local status
//
//                // Update status in Firebase
//                databaseReference.child(oi.getLampuId()).child("status").setValue(status);
//            }
//        });
//
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //showHouseDetail(oi);
//            }
//        });
//
//        return convertView;
//    }
//
//    private void showHouseDetail(Lampu house) {
//        Intent intent = new Intent(getContext(), DetailActivity.class);
//        intent.putExtra("house_id", house.getRuanganId());
//        intent.putExtra("house_name", house.getNama());
//        intent.putExtra("house_address", house.getStatus());
//        getContext().startActivity(intent);
//    }
//}
