package com.example.httpurlconnectionexample;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SendNReceive {
    private final String _url;
    private StringBuilder myURL = null;
    private HttpURLConnection urlConn = null;
    private URL url;
    private ContentValues contentValues;

    public SendNReceive(String _url, ContentValues contentValues) {
        this._url = "http://210.181.138.119:7010/kidslock.php?";
        this.contentValues = contentValues;
    }

    public void sendData() {
        myURL = new StringBuilder(_url);
        setURLType("set");

        addParam();

        try{
            Log.d("여기는 어디?", "try");
            url = new URL(myURL.toString());
            urlConn = (HttpURLConnection) url.openConnection();
            urlConnSetting();

            urlConn.getInputStream();
        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
    }

    public String receiveData() {
        myURL = new StringBuilder(_url);
        setURLType("get");

        addParam();

        try{
            url = new URL(myURL.toString());
            urlConn = (HttpURLConnection) url.openConnection();

            urlConnSetting();

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            String line;
            String page = "";

            while ((line = reader.readLine()) != null){
                page += line;
            }

            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }

    private void addParam() {
        for(Object o : contentValues.valueSet().toArray())
            myURL.append("&").append(o);
    }

    private void setURLType(String type) {
        myURL.append("type=").append(type);
    }

    private void urlConnSetting() throws ProtocolException {
        urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
        urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
        urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");
    }
}
