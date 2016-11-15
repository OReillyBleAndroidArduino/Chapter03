package tonyg.example.com.exampleblescan;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import tonyg.example.com.exampleblescan.ble.BleCommManager;


/**
 * Scan for bluetooth low energy devices
 */
public class MainActivity extends AppCompatActivity {
    /** Constants **/
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    /** Bluetooth Stuff **/
    private BleCommManager mBleCommManager;

    /** UI Stuff **/
    private TextView mBluetoothStatusTV;

    /**
     * Load Activity for the first time
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadUI();

    }

    private void loadUI() {
        mBluetoothStatusTV = (TextView) findViewById(R.id.bluetooth_status);
    }

    /**
     * Turn on Bluetooth Radio notifications when App resumes
     */
    @Override
    public void onResume() {
        super.onResume();

        initializeBluetooth();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mBluetoothBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "receiver not registered");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    /**
     * Bluetooth Radio has been turned on.  Update UI
     */
    private void onBluetoothActive() {
        mBluetoothStatusTV.setText(R.string.bluetooth_active);
    }


    /**
     * Initialize the Bluetooth Radio
     */
    public void initializeBluetooth() {

        // notify when bluetooth is turned on or off
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothBroadcastReceiver, filter);

        try {
            mBleCommManager = new BleCommManager(this);
        } catch (Exception e) {
            Toast.makeText(this, "Could not initialize bluetooth", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
            finish();
        }

        // should prompt user to open settings if Bluetooth is not enabled.
        if (mBleCommManager.getBluetoothAdapter().isEnabled()) {
            onBluetoothActive();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }




    /**
     * When the Bluetooth radio turns on, initialize the Bluetooth connection
     */
    private final BroadcastReceiver mBluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        initializeBluetooth();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBluetoothActive();
                            }
                        });

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };



}
