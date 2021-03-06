

package com.example.sendimagebluetoothsample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sendimagebluetoothsample.bluetooth.BluetoothConstants;
import com.example.sendimagebluetoothsample.bluetooth.BluetoothService;
import com.example.sendimagebluetoothsample.bluetooth.DeviceListActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int SIZE = 213994;
    private byte[] tempImageStorage = new byte[SIZE];
    private int indexPointer = 0;


    private static final int WRITE_STATE = 21;
    private static final int WRITE_FINISH_STATE = 22;
    private static final int RECEIVE_STATE = 23;
    private static final int RECEIVE_FINISH_STATE = 24;
    private static final int WAIT_STATE = 25;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_PERMISSION_LOCATION = 4;

    private TextView tvConnectState;
    private Button btnSendData;
    private Button btnScanAccess;
    private Button btnScanDevice;
    private Button btnDisconnectDevice;
    private ImageView ivReceive;

    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService mChatService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
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
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mChatService != null) {
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                mChatService.start();
            }
        }
    }


    private void initView() {
        tvConnectState = findViewById(R.id.tvConnectState);
        ivReceive = findViewById(R.id.ivReceive);
        btnSendData = findViewById(R.id.btnSendData);
        btnScanAccess = findViewById(R.id.btnScanAccess);
        btnScanDevice = findViewById(R.id.btnScanDevice);
        btnDisconnectDevice = findViewById(R.id.btnDisconnectDevice);
    }

    private void initEvent() {
        btnScanAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanAccess();
            }
        });

        btnScanDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanDevice();
            }
        });

        btnDisconnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectDevice();
            }
        });

        btnSendData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                arr = bitmapConvertToByteArray();
                temp = new byte[25];

                handler.obtainMessage(WRITE_STATE).sendToTarget();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < SIZE; i++) {
                            byteList.add(arr[indexPointer++]);
                            if(byteList.size() % 25 == 0) {
                                tempByte = Arrays.copyOf(byteList.toArray(), byteList.size(), Byte[].class);
                                for (int j = 0; j < tempByte.length; j++) {
                                    temp[j] = tempByte[j].byteValue();
                                }
                                sendMessage(temp);
                                byteList.clear();
                            }
                        }
                        if (!byteList.isEmpty()) {
                            tempByte = Arrays.copyOf(byteList.toArray(), byteList.size(), Byte[].class);
                            for (int j = 0; j < tempByte.length; j++) {
                                temp[j] = tempByte[j].byteValue();
                            }
                            sendMessage(temp);
                            byteList.clear();
                        }
                        indexPointer = 0;
                        handler.obtainMessage(WRITE_FINISH_STATE).sendToTarget();
                    }
                }).start();
            }
        });
    }
    ArrayList<Byte> byteList = new ArrayList<>();
    byte[] arr;
    byte[] temp;
    Byte[] tempByte;

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        mChatService = new BluetoothService(getApplicationContext(), mHandler);
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private void sendMessage(byte[] data) {
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (data.length > 0) {
            mChatService.write(data);
        }
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothConstants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            tvConnectState.setText("연결된 기기 : " + mConnectedDeviceName);
//                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            tvConnectState.setText("연결중..");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            tvConnectState.setText("대기중");
                            break;
                    }
                    break;
                case BluetoothConstants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    String writeMessage = new String(writeBuf);
//                    writeMessage = byteArrayToHex(writeBuf, writeBuf.length);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case BluetoothConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
//                    String readMessage = byteArrayToHex(readBuf, msg.arg1);
                    dataSave(readBuf, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case BluetoothConstants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
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
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
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

    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device, secure);
    }

    private void scanAccess() {
        ensureDiscoverable();
    }

    private void scanDevice() {
        Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    }

    private void disconnectDevice() {
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED)
            Toast.makeText(MainActivity.this, "already not connected", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(MainActivity.this, "Disconnect Device", Toast.LENGTH_SHORT).show();
            mChatService.stop();
        }
    }

    private String byteArrayToHex(byte[] a, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(String.format("%02x", a[i]&0xff));
        return sb.toString();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private byte[] bitmapConvertToByteArray() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.galaxy_gray);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


        Log.d(TAG, "parsing...");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        Log.d(TAG, "complete!");
        return imageBytes;
    }

    private Bitmap byteArrayToBitmap(byte[] bytes) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        bytes = null;
        return bitmap;
    }

    private synchronized void dataSave(byte[] buffer, int length) {
        handler.obtainMessage(RECEIVE_STATE).sendToTarget();
        try {
            for (int i = 0; i < length; i++)
                tempImageStorage[indexPointer + i] = buffer[i];
            indexPointer += length;
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            indexPointer = SIZE;
        }

        if (indexPointer == SIZE) {
            Log.d("herekm", "진입");
            handler.obtainMessage(RECEIVE_FINISH_STATE).sendToTarget();
            indexPointer = 0;
            Bitmap bmp = byteArrayToBitmap(tempImageStorage);
            try {
                Toast.makeText(this, "count : " + bmp.getByteCount(), Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            }
            ivReceive.setImageBitmap(bmp);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WRITE_STATE:
                    ((TextView)findViewById(R.id.tvSendState)).setText("전송중...");
                    break;
                case WRITE_FINISH_STATE:
                    ((TextView)findViewById(R.id.tvSendState)).setText("전송완료!!");
                    break;
                case RECEIVE_STATE:
                    ((TextView)findViewById(R.id.tvSendState)).setText("수신중...");
                    break;
                case RECEIVE_FINISH_STATE:
                    ((TextView)findViewById(R.id.tvSendState)).setText("수신완료!!");
                    break;
                case WAIT_STATE:
                    ((TextView)findViewById(R.id.tvSendState)).setText("수신대기중");
                    break;

            }
        }
    };
}
