package com.example.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.buyer.BuyerMainActivity;
import com.example.bookapp.seller.SellerMainActivity;

public class WelcomeScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIMER = 1000;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcomepage);
        session= new SessionManager(WelcomeScreenActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(session.isLoggedIn()){
                    if(session.getUserType().equals("Seller")) {
                        Intent intent = new Intent(WelcomeScreenActivity.this, SellerMainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(WelcomeScreenActivity.this, BuyerMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Intent i = new Intent(WelcomeScreenActivity.this, loginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIMER);
    }


}