package com.example.httpurlconnectionexample;

import android.content.ContentValues;
import android.os.AsyncTask;

public class NetworkTask extends AsyncTask<Void, Void, String> {
    final static int SEND_DATA = 0, RECEIVE_DATA = 1;
    private ContentValues contentValues;
    private int choice;

    public NetworkTask (int choice, ContentValues contentValues) {
        this.choice = choice;
        this.contentValues = contentValues;
    }

    @Override
    protected String doInBackground(Void... params) {

        SendNReceive snr = new SendNReceive("url", contentValues);
        if (choice == SEND_DATA) {
            snr.sendData();
            return "Send Success";
        }
        else if (choice == RECEIVE_DATA)
            return snr.receiveData();

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s != null)
            MainActivity.tvResult.setText(s);
        else
            MainActivity.tvResult.setText("Not Found");
    }
}