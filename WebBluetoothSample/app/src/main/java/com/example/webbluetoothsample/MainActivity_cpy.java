

package com.example.webbluetoothsample;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webbluetoothsample.bluetooth.BluetoothChatService;
import com.example.webbluetoothsample.bluetooth.BluetoothConstants;
import com.example.webbluetoothsample.bluetooth.DeviceListActivity;

public class MainActivity_cpy extends AppCompatActivity {
//    private static final String TAG = "MainActivity";
//
//    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
//    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
//    private static final int REQUEST_ENABLE_BT = 3;
//    private static final int REQUEST_PERMISSION_LOCATION = 4;
//
//    private TextView tvConnectState;
//    private ListView lvDataIncome;
//    private EditText edtSendData;
//    private Button btnSendData;
//    private Button btnScanAccess;
//    private Button btnScanDevice;
//    private Button btnDisconnectDevice;
//
//    private String mConnectedDeviceName = null;
//
//    private ArrayAdapter<String> mConversationArrayAdapter;
//
//    private StringBuffer mOutStringBuffer;
//
//    private BluetoothAdapter mBluetoothAdapter = null;
//
//    private BluetoothChatService mChatService = null;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initView();
//
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//        } else if (mChatService == null) {
//            setupChat();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mChatService != null) {
//            mChatService.stop();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (mChatService != null) {
//            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
//                mChatService.start();
//            }
//        }
//    }
//
//
//    private void initView() {
//        tvConnectState = findViewById(R.id.tvConnectState);
//        lvDataIncome = findViewById(R.id.tvLastData);
//        edtSendData = findViewById(R.id.edtSendData);
//        btnSendData = findViewById(R.id.btnSendData);
//        btnScanAccess = findViewById(R.id.btnScanAccess);
//        btnScanDevice = findViewById(R.id.btnScanDevice);
//        btnDisconnectDevice = findViewById(R.id.btnDisconnectDevice);
//    }
//
//    private void setupChat() {
//        Log.d(TAG, "setupChat()");
//
//        mConversationArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_data_list);
//        lvDataIncome.setAdapter(mConversationArrayAdapter);
//
//        // Initialize the compose field with a listener for the return key
//        edtSendData.setOnEditorActionListener(mWriteListener);
//
//        // Initialize the send button with a listener that for click events
//        btnSendData.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Send a message using content of the edit text widget
//                    String message = edtSendData.getText().toString();
//                    sendMessage(message);
//            }
//        });
//
//        // Initialize the BluetoothChatService to perform bluetooth connections
//        mChatService = new BluetoothChatService(getApplicationContext(), mHandler);
//
//        // Initialize the buffer for outgoing messages
//        mOutStringBuffer = new StringBuffer("");
//    }
//
//    /**
//     * Makes this device discoverable for 300 seconds (5 minutes).
//     */
//    private void ensureDiscoverable() {
//        if (mBluetoothAdapter.getScanMode() !=
//                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(discoverableIntent);
//        }
//    }
//
//    private void sendMessage(String message) {
//        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//            Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (message.length() > 0) {
//            byte[] send = message.getBytes();
//            mChatService.write(send);
//
//            mOutStringBuffer.setLength(0);
//            edtSendData.setText(mOutStringBuffer);
//        }
//    }
//
//    private TextView.OnEditorActionListener mWriteListener
//            = new TextView.OnEditorActionListener() {
//        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//            // If the action is a key-up event on the return key, send the message
//            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
//                String message = view.getText().toString();
//                sendMessage(message);
//            }
//            return true;
//        }
//    };
//
//
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BluetoothConstants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothChatService.STATE_CONNECTED:
//                            tvConnectState.setText(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
//                            break;
//                        case BluetoothChatService.STATE_CONNECTING:
//                            tvConnectState.setText(R.string.title_connecting);
//                            break;
//                        case BluetoothChatService.STATE_LISTEN:
//                        case BluetoothChatService.STATE_NONE:
//                            tvConnectState.setText(R.string.title_not_connected);
//                            break;
//                    }
//                    break;
//                case BluetoothConstants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
//                    break;
//                case BluetoothConstants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
//                    break;
//                case BluetoothConstants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(BluetoothConstants.DEVICE_NAME);
//                        Toast.makeText(getApplicationContext(), "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    break;
//                case BluetoothConstants.MESSAGE_TOAST:
//                        Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothConstants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CONNECT_DEVICE_SECURE:
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, true);
//                }
//                break;
//            case REQUEST_CONNECT_DEVICE_INSECURE:
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, false);
//                }
//                break;
//            case REQUEST_ENABLE_BT:
//                if (resultCode == Activity.RESULT_OK) {
//                    setupChat();
//                } else {
//                    Log.d(TAG, "BT not enabled");
//                    Toast.makeText(getApplicationContext(), "exit: not enable bluetooth",
//                            Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//        }
//    }
//
//    private void connectDevice(Intent data, boolean secure) {
//        String address = data.getExtras()
//                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//        mChatService.connect(device, secure);
//    }
//
//    private void scanAccess() {
//        ensureDiscoverable();
//    }
//
//    private void scanDevice() {
//        Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
//        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
//    }
//
//    private void disconnectDevice() {
//        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED)
//            Toast.makeText(MainActivity_cpy.this, "already not connected", Toast.LENGTH_SHORT).show();
//        else {
//            Toast.makeText(MainActivity_cpy.this, "Disconnect Device", Toast.LENGTH_SHORT).show();
//            mChatService.stop();
//        }
//    }
}
