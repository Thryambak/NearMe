package com.example.nearme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity {
//    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    FirebaseAuth myAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String tempOTP=null;
    PhoneAuthCredential globalCredential;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        myAuth = FirebaseAuth.getInstance();
        final EditText name = findViewById(R.id.name);

        final EditText phoneNo = findViewById(R.id.regPhone);
        final EditText occupation = findViewById(R.id.regOccupation);

        Button Register = findViewById(R.id.submit);
        final  EditText otpForVerify = findViewById(R.id.otp);
        final Button verifyOtp = findViewById(R.id.submitOtp);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid(name)&&isValid(phoneNo)&&isValid(occupation)){



                   callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            Toast.makeText(Signup.this, "Signed up", Toast.LENGTH_SHORT).show();
//                            root.child("phno").setValue(phoneNo.getText().toString());
//                            root.child("name").setValue(name.getText().toString());
//                            root.child("occupation").setValue(occupation.getText().toString());
                            globalCredential = phoneAuthCredential;
                            myAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Signup.this, "Logged In successfully", Toast.LENGTH_SHORT).show();
                          //Toast.makeText(Signup.this, "HEre", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = myAuth.getCurrentUser();
                                        Log.i("Phno",user.getPhoneNumber());
                                        DatabaseReference newUser = root.child(user.getPhoneNumber());
                                        newUser.child("phno").setValue(phoneNo.getText().toString());
                                        newUser.child("name").setValue(name.getText().toString());
                                        newUser.child("occupation").setValue(occupation.getText().toString());
                                        globalCredential = null;
                                        FirebaseUser currentUser = myAuth.getCurrentUser();
                                        Log.i("check new here",currentUser.toString());
                                    }
                                    else {
                                        Toast.makeText(Signup.this, "Failed ", Toast.LENGTH_SHORT).show();
                                        Log.i("FireBaseLogin Error",task.getException().toString());


                                    }
                                }
                            });
                            globalCredential=null;
                            FirebaseUser currentUser = myAuth.getCurrentUser();
                            Log.i("check new here",currentUser.toString());
                        }

                       @Override
                       public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                           super.onCodeSent(s, forceResendingToken);
                           tempOTP=s;
                           otpForVerify.setVisibility(View.VISIBLE);
                           verifyOtp.setVisibility(View.VISIBLE);

                       }

                       @Override
                       public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                           super.onCodeAutoRetrievalTimeOut(s);
                       }

                       @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            {
                                Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    };










                    PhoneAuthOptions phoneAuthOptions =
                            PhoneAuthOptions.newBuilder(myAuth)
                            .setPhoneNumber("+91"+phoneNo.getText().toString())
                            .setTimeout(6L, TimeUnit.SECONDS)
                            .setActivity(Signup.this)
                            .setCallbacks(callbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

                }
            }
        });



    verifyOtp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!tempOTP.isEmpty()){
                if(isValid(otpForVerify)){
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(tempOTP,otpForVerify.getText().toString());
                    globalCredential = credential;

                    myAuth.signInWithCredential(credential).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Signup.this, "Logged In successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = myAuth.getCurrentUser();
                                Log.i("Phno",user.getPhoneNumber());
                                DatabaseReference newUser = root.child(user.getPhoneNumber());
                                newUser.child("phno").setValue(phoneNo.getText().toString());
                                newUser.child("name").setValue(name.getText().toString());
                                newUser.child("occupation").setValue(occupation.getText().toString());
                                globalCredential = null;


                            }
                            else {
                                Toast.makeText(Signup.this, "Failed ", Toast.LENGTH_SHORT).show();
                                Log.i("FireBaseLogin Error",task.getException().toString());


                            }
                        }
                    });




                }


            }
        }
    });





    }



    public static boolean isValid(EditText editText){
        if(editText.getText().toString().isEmpty()) {
            editText.setError("This field cant be empty");
            return false;
        }
        return true;
    }

//    public static boolean performLogin(final Activity context, PhoneAuthCredential myCred, final FirebaseAuth mAuth){
//        final boolean[] isOK = new boolean[1];
//        final CountDownLatch done = new CountDownLatch(1);
//
//        mAuth.signInWithCredential(myCred).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(context, "Logged In successfully", Toast.LENGTH_SHORT).show();
//
//                    isOK[0] = true;
//                    Log.i("BOOLEAN here 1",Boolean.toString(isOK[0]));
//                    done.countDown();
//                }
//                else {
//                    Toast.makeText(context, "Failed ", Toast.LENGTH_SHORT).show();
//                    Log.i("FireBaseLogin Error",task.getException().toString());
//                    isOK[0] = false;
//
//                }
//            }
//        });
//
//        try {
//            done.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } Log.i("BOOLEAN here",Boolean.toString(isOK[0]));
//            return isOK[0];
//    }


}