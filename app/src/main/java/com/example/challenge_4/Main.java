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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main extends FragmentActivity implements View.OnClickListener  {

    private static final String TAG = "Main";

    // Front end components
    ImageButton bottle;
    ImageView Baby_Picture;
    RelativeLayout Baby_Profile;
    TextView Baby_Name, Baby_Age, Baby_Weight;

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

        // Front-end initializers
        Baby_Profile = findViewById(R.id.Baby_Profile);
        Baby_Name = findViewById(R.id.Baby_Name);
        Baby_Age = findViewById(R.id.Baby_Age);
        Baby_Weight = findViewById(R.id.Baby_Weight);
        Baby_Picture = findViewById(R.id.Baby_Picture);

        // Front-end click listeners
        Baby_Profile.setOnClickListener(this);

        // Call the class/method that initializes the app and asks for permissions
        checkBluetoothPermissions();

        // History Tab
        initData();
        initRecyclerView();

    }

    @Override
    public void onClick(View view) {

        // When profile area gets clicked, moved into the settings layout
        if (view.equals(Baby_Profile)){

        }


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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        String current_time = formatter.format(date);
        activityList.add(new Model(R.drawable.bottle, "bottle",current_time , "_______________________________________"));
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

