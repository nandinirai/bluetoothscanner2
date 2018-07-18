package com.example.nandini.bluetoothscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button scan,paired;
    BluetoothAdapter mBluetoothAdapter = null;
    private int REQUEST_ENABLE_BT = 99;
    ListView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan = (Button) findViewById(R.id.button1);
        paired = (Button) findViewById(R.id.button2);
        list = findViewById(R.id.list_item);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        if( !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Toast.makeText(getApplicationContext(),"Not Enabled...Reuesting",Toast.LENGTH_LONG).show();
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        paired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> pairedDevice = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1);
                Set<BluetoothDevice> deviceSet = mBluetoothAdapter.getBondedDevices();
                if(deviceSet.size() > 0)
                {
                    for (BluetoothDevice device: deviceSet)
                    {
                        pairedDevice.add(device.getName().toString()+"\n"+device.getAddress().toString());
                        Log.d("Paired",device.getName().toString());
                    }
                }
                list.setAdapter(pairedDevice);
            }
        });
    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),"Broadcast Receiving",Toast.LENGTH_LONG).show();
            ArrayAdapter<String> btAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the bluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btAdapter.add(device.getName().toString());
                list.setAdapter(btAdapter);
                Toast.makeText(getApplicationContext(),btAdapter.getCount(),Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onPause()
    {
        super.onPause();

        mBluetoothAdapter.cancelDiscovery();
    }
}

