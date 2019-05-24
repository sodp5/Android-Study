package com.example.bluetoothexample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int ACTION_REQUEST_DISCOVERY_CODE = 1;
    private static final int AGREE_TIME = 120;
    private static final String DEFAULT_STATE = "대기중";

    private static final UUID MY_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private int agreeTime;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    private TextView tvBluetoothState;
    private TextView tvAgreeTime;
    private TextView tvSendData;
    private TextView tvReceiveData;
    private Button btnDiscoveryDevice;
    private Button btnCancelDiscoveryDevice;
    private Button btnSearchDevice;
    private Button btnCancelConnect;
    private Button btnSendData;
    private Button btnReceiveData;

    private ArrayAdapter<String> pairedDeviceInfoAdapter;
    private ArrayAdapter<String> searchedDeviceInfoAdapter;
    private ListView lvPairedDevice;
    private ListView lvSearchedDevice;

    private Set<BluetoothDevice> pairedDevices;
    private Set<BluetoothDevice> searchedDevices;

    private OutputStream btOutputStream;
    private InputStream btInputStream;

    private boolean isConnect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initInstance();
        initClickEvent();

        getBlueToothAdapter();
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 위치확인권한 ( 없으면 검색이 안됨 )
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }

        if (!bluetoothAdapter.isEnabled()) {
            requestEnableBluetooth();
        }
        else {
            // 페어링된 기기 검색
            findPairedDevices();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothAdapter != null) {
            cancelDiscovery(true);
        }
        unregisterReceiver(btReceiver);
    }

    private void initView() {
        lvPairedDevice = findViewById(R.id.lvPairedDevice);
        lvSearchedDevice = findViewById(R.id.lvSearchedDevice);
        btnDiscoveryDevice = findViewById(R.id.btnDiscoveryDevice);
        btnCancelDiscoveryDevice = findViewById(R.id.btnCancelDiscoveryDevice);
        btnSearchDevice = findViewById(R.id.btnSearchDevice);
        btnCancelConnect = findViewById(R.id.btnCancelConnect);
        btnSendData = findViewById(R.id.btnSendData);
        btnReceiveData = findViewById(R.id.btnReceiveData);
        tvBluetoothState = findViewById(R.id.tvBluetoothState);
        tvAgreeTime = findViewById(R.id.tvAgreeTime);
        tvSendData = findViewById(R.id.tvSendData);
        tvReceiveData = findViewById(R.id.tvReceiveData);
    }

    private void initInstance() {
        pairedDeviceInfoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        searchedDeviceInfoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        searchedDevices = new HashSet<>();
    }

    private void initClickEvent() {
        btnDiscoveryDevice.setOnClickListener(btnDiscoveryDeviceClickListener);
        btnCancelDiscoveryDevice.setOnClickListener(btnCancelDiscoveryDeviceClickListener);
        btnSearchDevice.setOnClickListener(btnSearchDeviceClickListener);
        btnCancelConnect.setOnClickListener(btnCancelConnectClickListener);

        lvPairedDevice.setOnItemClickListener(lvPairedDeviceClickListener);
        lvSearchedDevice.setOnItemClickListener(lvSearchedDeviceClickListener);
    }

    private void getBlueToothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void requestEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void findPairedDevices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        pairedDeviceInfoAdapter.clear();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceInfoAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            lvPairedDevice.setAdapter(pairedDeviceInfoAdapter);
        }
    }

    private void cancelDiscovery(boolean isDestroy) {
        if (bluetoothAdapter.isDiscovering()) {
            Toast.makeText(MainActivity.this, "탐색을 종료합니다.", Toast.LENGTH_SHORT).show();
            bluetoothAdapter.cancelDiscovery();
        }
        else {
            if (!isDestroy)
                Toast.makeText(MainActivity.this, "탐색 중이 아닙니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void agreeTimeCounter() {
        agreeTime = AGREE_TIME;
        new CountDownTimer(AGREE_TIME * 1000 - 1000, 1000) {
            @Override
            public void onTick(long l) {
                tvAgreeTime.setText(String.valueOf(agreeTime));
                agreeTime--;
            }

            @Override
            public void onFinish() {
                tvAgreeTime.setText("0");
            }
        }.start();
    }

    private View.OnClickListener btnDiscoveryDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!bluetoothAdapter.isDiscovering()) {
                boolean result = bluetoothAdapter.startDiscovery();
                if (result) {
                    searchedDeviceInfoAdapter.clear();
                    searchedDevices.clear();
                    tvBluetoothState.setText("탐색중");
                    Toast.makeText(MainActivity.this, "탐색 시작", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(MainActivity.this, "탐색 중 입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener btnCancelDiscoveryDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cancelDiscovery(false);
        }
    };

    private View.OnClickListener btnSearchDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (tvAgreeTime.getText().equals("0")) {
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, AGREE_TIME); // 120초간 검색허용
                startActivityForResult(discoverableIntent, ACTION_REQUEST_DISCOVERY_CODE);
            }
            else {
                Toast.makeText(MainActivity.this, "허용중입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener btnCancelConnectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConnect) {
                isConnect = false;
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "연결이 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                tvBluetoothState.setText(String.valueOf(DEFAULT_STATE));
            }
            else
                Toast.makeText(MainActivity.this, "연결중이 아닙니다.", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener btnSendDataClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener btnReceiveDataClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private ListView.OnItemClickListener lvPairedDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            connectDevice(pairedDevices, getBluetoothAddress((String)adapterView.getItemAtPosition(i)));
        }
    };

    private ListView.OnItemClickListener lvSearchedDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            connectDevice(searchedDevices, getBluetoothAddress((String)adapterView.getItemAtPosition(i)));
        }
    };

    private String getBluetoothAddress(String btInfo) {
        int index = btInfo.indexOf('\n');

        return btInfo.substring(0, index);
    }

    private void connectDevice(Set<BluetoothDevice> deviceSet, String deviceAddress) {
        if (isConnect) {
            Toast.makeText(this, "이미 연결중인 디바이스가 존재합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        BluetoothDevice btDevice = null;

        for(BluetoothDevice device : deviceSet) {
            if (device.getAddress().equals(deviceAddress)) {
                btDevice = device;
                break;
            }
        }

        try {
            bluetoothSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            isConnect = true;
            tvBluetoothState.setText(String.valueOf(btDevice.getName() + "과 연결중"));

            btOutputStream = bluetoothSocket.getOutputStream();
            btInputStream = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                searchedDevices.add(device);
                searchedDeviceInfoAdapter.add(device.getName() + "\n" + device.getAddress());

                lvSearchedDevice.setAdapter(searchedDeviceInfoAdapter);
            }

            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                tvBluetoothState.setText(DEFAULT_STATE);
                Toast.makeText(context, "탐색 완료", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(btReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_REQUEST_DISCOVERY_CODE) {
            if (resultCode == AGREE_TIME) {
                Toast.makeText(this, "검색을 허용합니다.", Toast.LENGTH_SHORT).show();
                agreeTimeCounter();
            }
            else {
                Toast.makeText(this, "검색을 허용하지않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
