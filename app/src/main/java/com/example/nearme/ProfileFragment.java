package com.example.nearme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    FirebaseAuth myAuth = FirebaseAuth.getInstance();

    public ProfileFragment(){

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] myS = {"Log out","View My Profile"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.settings_row,myS);
        ListView listView = view.findViewById(R.id.listSettings);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 ) {
                    myAuth.signOut();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    //  Log.i("is done?","YES");
                    getActivity().finish();
                }
            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.profie,container,false);
        return myView;
    }
}
