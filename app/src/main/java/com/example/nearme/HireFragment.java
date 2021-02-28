package com.example.nearme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class HireFragment extends Fragment {
    String myLocation;
    LocationManager locationManager ;
    LocationListener locationListener;
    Geocoder geocoder;
    Looper looper = null;
    boolean gps_enabled = false;
     final ArrayList<Post> list = new ArrayList<>();
    boolean network_enabled = false;
    FirebaseDatabase myDb = FirebaseDatabase.getInstance();
    DatabaseReference myRef = myDb.getReference().child("users");
    Criteria criteria = new Criteria();
    public HireFragment(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestSingleUpdate(criteria,locationListener,looper);
            }
        }

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

//        try {
//            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch(Exception ex) {}
//
//        try {
//            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}
//
//        if(!gps_enabled && !network_enabled) {
//            try {
//                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            } catch(Exception ex) {}
//
//            try {
//                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            } catch(Exception ex) {}
//
//
//            try {
//
//                Thread.currentThread().wait(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            // notify user
//            new AlertDialog.Builder(view.getContext())
//                    .setMessage("please enable GPS")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            getActivity().finish();
//                            System.exit(0);
//                        }
//                    })
//                            .show();
//        }


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                List<Address> addressList = new ArrayList<>();
                try {
                    addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    myLocation = addressList.get(0).getLocality();
                    Log.i("loc","My Loc is "+myLocation);
                  //  Toast.makeText(getActivity(), myLocation, Toast.LENGTH_SHORT).show();





                } catch (IOException e) {
                    e.printStackTrace();
                }
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            Post myPost = dataSnapshot.getValue(Post.class);

                            if(myLocation.equalsIgnoreCase(myPost.getCity())){
                                list.add(myPost);
                            }
                        }
                        Log.i("Retreaved array",  Integer.toString(list.size()));

                        ArrayList<String> arrayList = new ArrayList<>();
                        for(Post post : list){
                            arrayList.add(post.getOccupation());

                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arrayList);
                        ListView listView = view.findViewById(R.id.listHire);
                        listView.setAdapter(arrayAdapter);
                        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.occupationSuggest);
                        autoCompleteTextView.setAdapter(arrayAdapter);
                        autoCompleteTextView.setDropDownHeight(0);
                        autoCompleteTextView.setThreshold(1);


                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });


//                Toast.makeText(getActivity(),arrayList.get(0), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            locationManager.requestSingleUpdate(criteria,locationListener,null);

        }
        else
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);




    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hire,container,false);
    }
}
