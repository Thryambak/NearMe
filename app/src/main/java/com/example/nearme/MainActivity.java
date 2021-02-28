package com.example.nearme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    int i=0;

    FirebaseAuth myAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks ;
    String code= null;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    PhoneAuthCredential phoneAuthCredentials =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent gpsOptionsIntent = new Intent(
//                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(gpsOptionsIntent);



        myAuth = FirebaseAuth.getInstance();

        FirebaseUser f = myAuth.getCurrentUser();

        if(f!=null){
            Intent intent = new Intent(getApplicationContext(),HomePage.class);
            startActivity(intent);
            finish();
        }
        String s = "New To NearMe? Signup.";
        SpannableString ss = new SpannableString(s);
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
              //  Toast.makeText(MainActivity.this, "Move to signup activity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),Signup.class);
                startActivity(intent);
            }
        };
        ss.setSpan(cs,15,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = findViewById(R.id.textViewOtp);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        final EditText phone = findViewById(R.id.phoneEdit);
        Button login = findViewById(R.id.button);
        final EditText otp = findViewById(R.id.editTextNumber);
        final Button submitOtp= findViewById(R.id.subOtp);



        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                if(Signup.isValid(phone)){
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            List<Post> userList = new ArrayList<>();

                            for(DataSnapshot dataValues:snapshot.getChildren()){
                                Post myuser = dataValues.getValue(Post.class);
                                userList.add(myuser);

                                for(int j=0;j<userList.size();j++){
                                    if(userList.get(j).getPhno().equals(phone.getText().toString())){

                                        i=-1;Log.i("check if Exist in Db ",Integer.toString(i));

                                       // Toast.makeText(MainActivity.this, "HERE Boi", Toast.LENGTH_SHORT).show();
                                        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                            @Override
                                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                super.onCodeSent(s, forceResendingToken);
                                                code= s;
                                                otp.setVisibility(View.VISIBLE);
                                                submitOtp.setVisibility(View.VISIBLE);

                                            }

                                            @Override
                                            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                                                super.onCodeAutoRetrievalTimeOut(s);
                                            }

                                            @Override
                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                phoneAuthCredentials= phoneAuthCredential;
                                                otp.setVisibility(View.VISIBLE);
                                                submitOtp.setVisibility(View.VISIBLE);
                                                Toast.makeText(MainActivity.this, "Signup Successfull", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),HomePage.class);
                                                startActivity(intent);
                                                finish();

                                            }

                                            @Override
                                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                                Toast.makeText(MainActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();

                                            }
                                        };

                                        PhoneAuthOptions phoneAuthOptions =  PhoneAuthOptions
                                                .newBuilder(myAuth)
                                                .setPhoneNumber("+91"+phone.getText().toString())
                                                .setCallbacks(callbacks)
                                                .setActivity(MainActivity.this)
                                                .setTimeout(10L, TimeUnit.SECONDS)
                                                .build();
                                        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);




                                        break;

                                    }



                                }
                                try {
                                    Thread.sleep(2000);
                                    if(i==0){

                                        Log.i("checking i == -1 On ",Integer.toString(i));
                                        Toast.makeText(MainActivity.this, "You aren't registered. Please Sign up", Toast.LENGTH_SHORT).show();


                                    }
                                    i=0;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }



                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                                    Log.i("DBRead","FAiled");
                        }
                    };
                    databaseReference.addValueEventListener(valueEventListener);




                }
            }
        });

        submitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Signup.isValid(otp)){
                    if(!code.isEmpty()){

                      PhoneAuthCredential myCred = PhoneAuthProvider.getCredential(code,otp.getText().toString());
                        myAuth.signInWithCredential(myCred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),HomePage.class);
                                    startActivity(intent);
                                    finish();

                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });


    }
}