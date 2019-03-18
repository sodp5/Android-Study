package com.example.autocall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CallActivity extends AppCompatActivity {

    int pressCount;
    String tel;
    Button btnCall;
    int permissionCheck;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                System.out.println(pressCount);

                if (pressCount == 1) {
                        pressCount++;
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            pressCount = 1;
                        }
                    }.start();
                }
                else if (pressCount < 3) pressCount++;
                else if (pressCount == 3) {
                    pressCount = 0;
                    autoCall();
                }
                return true;
        }

        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        pressCount = 1;
        tel = "tel:01024789885";

        btnCall = findViewById(R.id.btnCall);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCall();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionCheck = ContextCompat.checkSelfPermission(CallActivity.this, Manifest.permission.CALL_PHONE);
    }

    private void autoCall(){
        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CallActivity.this,new String[]{Manifest.permission.CALL_PHONE},0);
        }
        else {
            startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
        }
    }
}
