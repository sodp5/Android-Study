

package com.example.webbluetoothsample;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webbluetoothsample.bluetooth.BluetoothChatService;
import com.example.webbluetoothsample.bluetooth.BluetoothConstants;
import com.example.webbluetoothsample.bluetooth.DeviceListActivity;
import com.example.webbluetoothsample.db.DBController;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private static final String STATE_DEFAULT = "대기중입니다.";
    private static final String STATE_CONNECTED = "연결되었습니다.";
    private static final String STATE_CONNECTING = "연결중입니다.";
    private static final String STATE_DISCOVERY = "탐색중입니다.";


    private String mConnectedDeviceName = null;
    private String status = STATE_DEFAULT;

    private WebView wvWebView;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;

    private DBController dbController;

    private static final String SEARCH = "search";
    private int searchIdx = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        dbController = DBController.getInstance();
        dbController.initController(this);

        setupSetting();
        setWebView();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    private void setupSetting() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice d : pairedDevices) {
            addDeviceListDB(d.getName(), d.getAddress());
        }
    }

    private void setWebView() {
        wvWebView = findViewById(R.id.wvWebView);

        wvWebView.setWebChromeClient(new WebChromeClient());
        wvWebView.setWebViewClient(new MyWebViewClient());

        wvWebView.getSettings().setJavaScriptEnabled(true);

        wvWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void put(String key, String data) {
                dbController.put(key, data);
                Toast.makeText(MainActivity.this, "put", Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public void del(String key) {
                dbController.del(key);
                Toast.makeText(MainActivity.this, "del : " + key, Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public String get(String key) {
                String s = dbController.get(key);
                Toast.makeText(MainActivity.this, "get : " + s, Toast.LENGTH_SHORT).show();
                return dbController.get(key);
            }

            @JavascriptInterface
            public void scanAccess() {
                MainActivity.this.scanAccess();
                Toast.makeText(MainActivity.this, "scanAccess", Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public void scanDevice() {
                MainActivity.this.scanDevice();
                Toast.makeText(MainActivity.this, "scanDevice", Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public void disconnectDevice() {
                MainActivity.this.disconnectDevice();
            }

            @JavascriptInterface
            public void connectDevice(String address) {
                MainActivity.this.connectDevice(address);
            }

            @JavascriptInterface
            public String getStatus() {
                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                return status;
            }
        }, "communicate");

        wvWebView.loadUrl("file:///android_asset/www/index.html");
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        mChatService = new BluetoothChatService(getApplicationContext(), mHandler);
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
//                            tvConnectState.setText(getString(R.string.title_connected_to, mConnectedDeviceName));
                            status = STATE_CONNECTED;
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                            tvConnectState.setText(R.string.title_connecting);
                            status = STATE_CONNECTING;
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//                            tvConnectState.setText(R.string.title_not_connected);
                            status = STATE_DEFAULT;
                            break;
                    }
                    break;
                case BluetoothConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case BluetoothConstants.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(BluetoothConstants.DEVICE_NAME);
                        Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothConstants.MESSAGE_TOAST:
                        Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothConstants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getApplicationContext(), "exit: not enable bluetooth",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//        mChatService.connect(device, secure);
    }

    private void scanAccess() {
        ensureDiscoverable();
    }

    private void scanDevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        status = STATE_DISCOVERY;
        mBluetoothAdapter.startDiscovery();
    }

    private void disconnectDevice() {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED)
            Toast.makeText(MainActivity.this, "already not connected", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(MainActivity.this, "Disconnect Device", Toast.LENGTH_SHORT).show();
            mChatService.stop();
        }
    }

    private void addDeviceListDB(String name, String address) {
        String info = name + "|" + address;
        String temp;
        if (searchIdx < 10) {
            temp = SEARCH + "00";
        }
        else if (searchIdx < 100)
            temp = SEARCH + "0";
        else
            temp = SEARCH;
        temp += searchIdx;

        dbController.put(temp, info);
    }

    private class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    // 여기부터 DeviceList

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    addDeviceListDB(device.getName(), device.getAddress());
                    //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                status = STATE_DEFAULT;
                /*if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }*/
            }
        }
    };

}