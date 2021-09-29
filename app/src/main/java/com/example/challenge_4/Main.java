package com.example.challenge_4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Main extends FragmentActivity  {

    private static final String TAG = "Main";

    // Front end components
    ImageButton bottle;

    // Recycler View Initialisation
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<Model> activityList;
    Adapter adapter;

    // For bluetooth devices recognition
    private BroadcastReceiver broadcastReceiver;
    private BluetoothAdapter bluetoothAdapter;

    BluetoothSocket socket;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Call the class/method that initializes the app and asks for permissions
        checkBluetoothPermissions();

        // History Tab
        initData();
        initRecyclerView();

    }

    private void checkBluetoothPermissions() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, 10);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled()){
            ConnectToArduino();
        } else {
            // Ask the user to enable bluetooth
            Intent ask_bluetooth_enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ask_bluetooth_enable, 1);
            checkBluetoothPermissions();
        }
    }

    // Right now it is displaying all bluetooth connections
    private void ConnectToArduino() {

        bluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();

        bluetoothAdapter.startDiscovery();

        ArrayList<String> arrayList = new ArrayList<>();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    arrayList.add(bluetoothDevice.getName());
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);
    }



    // Needed for the history Tab
    private void initData() {
        Log.d(TAG,"initData");
        activityList = new ArrayList<>();
    }

    // Needed for the history Tab
    private void initRecyclerView() {
        Log.d(TAG,"initRecyclerView");
        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Adapter(activityList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}

