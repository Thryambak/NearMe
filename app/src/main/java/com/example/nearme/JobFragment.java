package com.example.nearme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import  com.example.nearme.forRecievingDb.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class JobFragment extends Fragment {


    @Nullable Location location = null ;
    final Looper looper = null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("users");
    public  Boolean isLookingForJob = false;
   public Geocoder geocoder;
   LocationListener locationListener;
   LocationManager locationManager;
Criteria criteria = new Criteria();
    public JobFragment() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length > 0) {
            if (requestCode == 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

                        //get location

                        locationManager.requestSingleUpdate(criteria, locationListener, looper);
                    }
                }

            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button findJobs = getView().findViewById(R.id.findJobs);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());



        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseUser user = myAuth.getCurrentUser();
        final String myNo = user.getPhoneNumber();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
              myRef.child(myNo).child("lat").setValue(location.getLatitude());
                myRef.child(myNo).child("longi").setValue(location.getLongitude());
                List<Address> addresses = new ArrayList<Address>();
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    myRef.child(myNo).child("city").setValue(addresses.get(0).getLocality());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("LAt","Status changed");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("LAt","provider enable");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("LAt","provider disabled");

            }
        };
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);


//        boolean gps_enabled=false;boolean network_enabled=false;
//        try {
//            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch(Exception ex) {}
//
//
//        try {
//            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}
//
//        while(!gps_enabled && !network_enabled) {
//            try {
//                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            } catch(Exception ex) {}
//
//
//            try {
//                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            } catch(Exception ex) {}
//
//            if(gps_enabled||network_enabled)
//                break;
//            try {
//                Thread.currentThread().wait(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//
//            // notify user
//            new AlertDialog.Builder(view.getContext())
//                    .setMessage("please enable GPS")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            getActivity().finish();
//                            System.exit(0);
//                        }
//                    })
//                    .show();
//        }

         myRef.child(myNo).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             //  isAvail avail  =  snapshot.child("isAvailable").getValue(isAvail.class);

                   Post post = snapshot.getValue(Post.class);

             //       Toast.makeText(getContext(), "MMMM "+post.getIsAvailable().toString(), Toast.LENGTH_LONG).show();
                    if(post.getIsAvailable().equals("true")) {
                        isLookingForJob = true;
                        findJobs.setText("Stop Searching");
                        findJobs.setBackgroundColor(Color.parseColor("#cc0000"));






                    }
                    else {
                        isLookingForJob = false;
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        findJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(!isLookingForJob) {

                    //  Toast.makeText(getContext(), "Hello there"+myNo, Toast.LENGTH_SHORT).show();
                    myRef.child(myNo).child("isAvailable").setValue("true");
                    findJobs.setText("Stop Searching");
                    findJobs.setBackgroundColor(Color.parseColor("#cc0000"));
                    if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                       locationManager.requestSingleUpdate(criteria,locationListener,looper);
                    }
                    else ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
                }
                else
                    myRef.child(myNo).child("isAvailable").setValue("false");
                findJobs.setText("Start Finding jobs");
                findJobs.setBackgroundColor(Color.parseColor("#FF151212"));

            }
        });

    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.jobs,container,false);



    }
}
