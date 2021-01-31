package com.example.nearme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth myAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks ;
    String code= null;
    PhoneAuthCredential phoneAuthCredentials =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();
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
                 //  Toast.makeText(MainActivity.this, "HERE Boi", Toast.LENGTH_SHORT).show();
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

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(MainActivity.this, "Signup Successfull", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });


    }
}