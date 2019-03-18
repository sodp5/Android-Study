package com.example.httpurlconnectionexample;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static TextView tvResult;
    private EditText edtKey, edtValue;
    private Button btnSendData, btnReceiveData;
    private ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        contentValues = new ContentValues();

        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putParam("key", edtKey.getText().toString());
                putParam("value", edtValue.getText().toString());
                new NetworkTask(NetworkTask.SEND_DATA, contentValues).execute();
            }
        });

        btnReceiveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putParam("key", edtKey.getText().toString());
                new NetworkTask(NetworkTask.RECEIVE_DATA, contentValues).execute();
            }
        });
    }

    private void putParam(String key, String value) {
        contentValues.put(key, value);
    }

    private void initView() {
        tvResult = findViewById(R.id.tvResult);
        edtKey = findViewById(R.id.edtKey);
        edtValue = findViewById(R.id.edtValue);
        btnSendData = findViewById(R.id.btnSendData);
        btnReceiveData = findViewById(R.id.btnReceiveData);
    }

//    public class NetworkTask extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            String result; // 요청 결과를 저장할 변수.
////            Send s = new Send();
////            s.request();
////            Receive r = new Receive();
////            result = r.request();
//
//            SendNReceive snr = new SendNReceive();
//            snr.sendData();
//            result = snr.receiveData();
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            tv_outPut.setText(s);
//        }
//    }
}