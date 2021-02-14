package com.example.nearme;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class JobFragment extends Fragment {


    Location location ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("users");
    public  Boolean isLookingForJob;

    public JobFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button findJobs = getView().findViewById(R.id.findJobs);

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseUser user = myAuth.getCurrentUser();
        final String myNo = user.getPhoneNumber();



         myRef.child(myNo).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             //  isAvail avail  =  snapshot.child("isAvailable").getValue(isAvail.class);

               Post post = snapshot.getValue(Post.class);

                    Toast.makeText(getContext(), "MMMM "+post.getIsAvailable().toString(), Toast.LENGTH_LONG).show();
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
                }
                else
                    myRef.child(myNo).child("isAvailable").setValue("false");
                findJobs.setText("Start Finding jobs");
                findJobs.setBackgroundColor(Color.parseColor("#FF151212"));

            }
        });

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Toast.makeText(getContext(), "Hello there", Toast.LENGTH_SHORT).show();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.jobs,container,false);



    }
}
