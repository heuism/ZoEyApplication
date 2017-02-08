package curve.zoey.hientran.metawearguide;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
//import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.os.Environment;

//for Module as Sensors
import com.mbientlab.metawear.module.Bmi160Accelerometer;
import com.mbientlab.metawear.module.Bmi160Gyro.*;
import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.module.Bmi160Gyro;
import com.mbientlab.metawear.module.Logging;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
// Modules classes
import com.mbientlab.metawear.data.CartesianShort;
import com.mbientlab.metawear.data.CartesianFloat;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Gyro;
import com.mbientlab.metawear.module.Led;

import static com.mbientlab.metawear.MetaWearBoard.ConnectionStateHandler;
import static com.mbientlab.metawear.AsyncOperation.CompletionHandler;

public class MyActivity extends AppCompatActivity implements ServiceConnection, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MetaWear";

    private Switch connectSwitch;
    private Switch ledSwitch;
    private Switch accSwitch;
    private TextView accDataView;
    private Switch gyroSwitch;
    private TextView gyroDataView;
    private Switch allSwitch;

    private MetaWearBleService.LocalBinder serviceBinder;
//    private final String MW_MAC_ADDRESS= "D5:9C:DC:37:BA:AE"; //update with your board's MAC address
    private final String MW_MAC_ADDRESS = "E2:44:76:3A:C1:78";

    //...other variables
    private MetaWearBoard mwBoard;
    private Led ledModule;
    private Bmi160Accelerometer accModule;
    private Bmi160Gyro gyroModule;
    private Logging loggingModule;
    private static final float ACC_RANGE = 8.f, ACC_FREQ = 50.f;
    private static final String STREAM_KEY = "accel_stream";
    private static final String GYRO_STREAM_KEY = "gyro_stream";
    private static final String LOG_KEY = "accel_log";

    private File path;

    private final ConnectionStateHandler stateHandler= new ConnectionStateHandler() {
        @Override
        public void connected() {
            Log.i(TAG, "Connected");
            try {
                ledModule = mwBoard.getModule(Led.class);
                accModule = mwBoard.getModule(Bmi160Accelerometer.class);
                gyroModule = mwBoard.getModule(Bmi160Gyro.class);
                loggingModule = mwBoard.getModule(Logging.class);
            } catch (UnsupportedModuleException e) {
                e.printStackTrace();
            }
//            if(mwBoard.isConnected()){
            switchesEnable(true);
//            }
//            else{
//            }
        }

        @Override
        public void disconnected() {
            Log.i(TAG, "Connected Lost");
            switchesEnable(false);
            switchesDisable(false);
        }

        @Override
        public void failure(int status, Throwable error) {
            Log.e(TAG, "Error connecting", error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        /********************************************************************
        // new codes add in for the Metawear testing
         ********************************************************************/

        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class), this, BIND_AUTO_CREATE);

        Log.i(TAG, "log test"); //ADD THIS

        connectSwitch = (Switch) findViewById(R.id.connect_switch);
        connectSwitch.setOnCheckedChangeListener(this);

        ledSwitch = (Switch) findViewById(R.id.led_switch);
        ledSwitch.setOnCheckedChangeListener(this);

        accSwitch = (Switch) findViewById(R.id.accel_switch);
        accSwitch.setOnCheckedChangeListener(this);

        accDataView = (TextView) findViewById(R.id.accel_text);

        gyroSwitch = (Switch) findViewById(R.id.gyro_switch);
        gyroSwitch.setOnCheckedChangeListener(this);

        gyroDataView = (TextView) findViewById(R.id.gyro_text);

        allSwitch = (Switch) findViewById(R.id.all_switch);
        allSwitch.setOnCheckedChangeListener(this);

        switchesEnable(false);
        switchesDisable(false);
//        connectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "Connect Clicked!");
//                mwBoard.connect();//.connect() and .disconnect() are how we control connection state
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ///< Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_my, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ///< Typecast the binder to the service's LocalBinder class
        serviceBinder = (MetaWearBleService.LocalBinder) service;
        Log.i(TAG, "is here?");
        retrieveBoard();
        Log.i(TAG, "Get the board successfully");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {}


    public void retrieveBoard() {
        // get the Bluetooth Service
        final BluetoothManager btManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        // Get the device with the MAC ADDRESS put above
        final BluetoothDevice remoteDevice=
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        mwBoard= serviceBinder.getMetaWearBoard(remoteDevice);
        mwBoard.setConnectionStateHandler(stateHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            default:
                break;
        }
    }

    private void ledConfig(Led.ColorChannel color, int timeMs, int count, int intensity) {
        ledModule.configureColorChannel(color)
                .setRiseTime((short) 0).setPulseDuration((short) timeMs)
                .setRepeatCount((byte) count).setHighTime((short) (timeMs/2))
                .setHighIntensity((byte) intensity).setLowIntensity((byte) intensity)
                .commit();
    }

    private void switchesEnable(boolean enabled){
        switchEnable(ledSwitch, enabled);
        switchEnable(accSwitch, enabled);
        switchEnable(gyroSwitch, enabled);
        switchEnable(allSwitch, enabled);
    }

    private void switchesDisable(boolean enabled){
        switchDisable(ledSwitch, enabled);
        switchDisable(accSwitch, enabled);
        switchDisable(gyroSwitch, enabled);
        switchDisable(allSwitch, enabled);
    }

    private void switchEnable(CompoundButton sw, boolean enabled){
        sw.setClickable(enabled);
        if(enabled){
            sw.setAlpha(64);
        }
        else{
            sw.setAlpha(0);
        }
    }

    private void switchDisable(CompoundButton sw, boolean enabled){
        sw.setChecked(enabled);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.connect_switch:
                if (isChecked) {
                    if(!mwBoard.isConnected()){
                        mwBoard.connect();//.connect() and .disconnect() are how we control connection state
//                        final String CSV_HEADER = String.format("sensor,x_axis,y_axis,z_axis"); //add the header
//                        // Name for the csv writing
//                        final String filename = "METAWEAR.csv";
//                        path = new File(Environment.getExternalStoragePublicDirectory(
//                                Environment.DIRECTORY_DOWNLOADS), filename);

//                        Log.v(TAG, path);
                    }
                    else {
                        mwBoard.disconnect();
                        mwBoard.connect();
//                        final String filename = "METAWEAR.csv";
//                        path = new File(Environment.getExternalStoragePublicDirectory(
//                                Environment.DIRECTORY_DOWNLOADS), filename);
                    }
//                    Log.v(TAG, String.valueOf(path));
                } else {
                    switchesDisable(false);
                    mwBoard.disconnect();//.connect() and .disconnect() are how we control connection state
//                    final String filename = "METAWEAR.csv";
//                    final File path = new File(Environment.getExternalStoragePublicDirectory(
//                            Environment.DIRECTORY_DOWNLOADS), filename);
//
//                    boolean is_deleted = path.delete();
//                    Log.i(TAG, "deleted previous csv: " + is_deleted);
                }

                break;
            case R.id.led_switch:
                if (isChecked) {
                    ledConfig(Led.ColorChannel.BLUE,1000,-1,16);
                    ledModule.play(true);
                } else {
                    ledModule.stop(true);
                }
                break;
            case R.id.accel_switch:
                Log.i("Accel Switch State=", "" + isChecked);
                if (isChecked) {
                    accModule.setOutputDataRate(ACC_FREQ);
                    accModule.setAxisSamplingRange(ACC_RANGE);

                    AsyncOperation<RouteManager> routeManagerResultAccel = accModule.routeData().fromAxes().stream(STREAM_KEY).commit();
                    routeManagerResultAccel.onComplete(new CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe(STREAM_KEY, new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    final CartesianFloat axes = msg.getData(CartesianFloat.class);
                                    Log.i(TAG, String.format("Accelerometer: %s", axes.toString()));
                                    sensorMsg(axes.toString(), "accel");
                                }
                            });

                        }

                        @Override
                        public void failure(Throwable error) {
                            super.failure(error);
                        }
                    });

                    // setting up the route -> use stream as method (or logging)
                    /*accModule.routeData()
                            .fromAxes().stream(STREAM_KEY)
                            // completion manager to handle thing when the route is completely set up
                            .commit().onComplete(new CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
//                            super.success(result);
                            result.subscribe(STREAM_KEY, new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    CartesianFloat axes = msg.getData(CartesianFloat.class);
                                    Log.i(TAG, axes.toString());
//                                    dataView.setText("Accel Data: " + axes.toString());
                                    sensorMsg(axes.toString(), "accel");
                                }
                            });

*//*                            result.setLogMessageHandler(LOG_KEY, new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    final CartesianShort axisData = msg.getData(CartesianShort.class);
                                    Log.i(TAG, String.format("Log: %s", axisData.toString()));
                                }
                            });*//*
                        }

                        @Override
                        public void failure(Throwable error) {
//                            super.failure(error);
                            Log.e(TAG, "Error committing route", error);
                        }
                    });
//                    loggingModule.startLogging();*/
                    accModule.enableAxisSampling(); // must enable this before starting
                    accModule.start();
                } else {
//                    loggingModule.stopLogging();
                    accModule.disableAxisSampling();
                    accModule.stop();
/*                    loggingModule.downloadLog(0.05f, new Logging.DownloadHandler() {
                        @Override
                        public void onProgressUpdate(int nEntriesLeft, int totalEntries) {
                            Log.i(TAG, String.format("Progress = %d / %d", nEntriesLeft, totalEntries));
                        }
                    });
                    Log.i(TAG, "Log size: " + loggingModule.getLogCapacity());*/
                }
                break;
            case R.id.gyro_switch:
                if (isChecked) {
                    Log.i(TAG, "gyro is check");
                    gyroModule.configure()
                            .setOutputDataRate(OutputDataRate.ODR_50_HZ)
                            .setFullScaleRange(FullScaleRange.FSR_500)
                            .commit();

                    AsyncOperation<RouteManager> routeManagerResultGyro = gyroModule.routeData().fromAxes().stream(GYRO_STREAM_KEY).commit();
                    routeManagerResultGyro.onComplete(new CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe(GYRO_STREAM_KEY, new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    final CartesianFloat spinData = msg.getData(CartesianFloat.class);
                                    Log.i(TAG, String.format("Gyroscope: %s", spinData.toString()));
                                    sensorMsg(spinData.toString(), "gyro");
                                }
                            });
                        }

                        @Override
                        public void failure(Throwable error) {
                            super.failure(error);
                        }
                    });
                    gyroModule.start();

                } else {
                    Log.i(TAG, "gyro is not check");
                    gyroModule.stop();
                }
                break;
            case R.id.all_switch:
                Log.i("Switch State=", "" + isChecked);
                final String CSV_HEADER = String.format("sensor,x_axis,y_axis,z_axis");
                final String filename = "metawear.csv";
                File sdCard = Environment.getExternalStorageDirectory();
                File writeDir = new File (sdCard + "/Zoey");
                writeDir.mkdirs();
                final File path = new File(writeDir, filename);
                if (isChecked) {
                    //delete the csv file if it already exists (will be from older recordings)
                    boolean is_deleted = path.delete();
                    Log.i(TAG, "deleted: " + is_deleted);

                    OutputStream out;
                    try {
                        out = new BufferedOutputStream(new FileOutputStream(path, true));
                        out.write(CSV_HEADER.getBytes());
                        out.write("\n".getBytes());
                        out.close();
                    } catch (Exception e) {
                        Log.e(TAG, "CSV creation error", e);
                    }
                    accModule.setOutputDataRate(ACC_FREQ);
                    accModule.setAxisSamplingRange(ACC_RANGE);
                    gyroModule.configure()
                            .setOutputDataRate(OutputDataRate.ODR_50_HZ)
                            .setFullScaleRange(FullScaleRange.FSR_500)
                            .commit();

                    AsyncOperation<RouteManager> routeManagerResultAccel = accModule.routeData().fromAxes().stream(STREAM_KEY).commit();
                    AsyncOperation<RouteManager> routeManagerResultGyro = gyroModule.routeData().fromAxes().stream(GYRO_STREAM_KEY).commit();

                    routeManagerResultAccel.onComplete(new CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe(STREAM_KEY, new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    final CartesianFloat axes = msg.getData(CartesianFloat.class);
                                    sensorMsg(axes.toString(), "accel");
                                    String temp_axes = axes.toString();
                                    String final_val = temp_axes.substring(1, temp_axes.length()-1);
                                    Log.i(TAG, String.format("Accelerometer: %s", final_val));
                                    // CSV CODE
                                    String accel_entry = String.format("Accel, %s", final_val);
                                    String csv_accel_entry = accel_entry + ",";
                                    OutputStream out;
                                    try {
                                        out = new BufferedOutputStream(new FileOutputStream(path, true));
                                        out.write(csv_accel_entry.getBytes());
                                        out.write("\n".getBytes());
                                        out.close();
                                    } catch (Exception e) {
                                        Log.e(TAG, "CSV creation error", e);
                                    }
                                }
                            });
                        }
                    });

                    routeManagerResultGyro.onComplete(new CompletionHandler<RouteManager>() {
                        @Override
                        public void success(RouteManager result) {
                            result.subscribe(GYRO_STREAM_KEY, new RouteManager.MessageHandler() {
                                @Override
                                public void process(Message msg) {
                                    final CartesianFloat spinData = msg.getData(CartesianFloat.class);
                                    sensorMsg(spinData.toString(), "gyro");

                                    String temp_spin = spinData.toString();
                                    String final_spinData = temp_spin.substring(1, temp_spin.length()-1);
                                    Log.i(TAG, String.format("Gyroscope: %s", final_spinData));
                                    String gyro_entry = String.format("Gyro, %s", final_spinData);
                                    String csv_gyro_entry = gyro_entry + ",";
                                    OutputStream out;
                                    try {
                                        out = new BufferedOutputStream(new FileOutputStream(path, true));
                                        out.write(csv_gyro_entry.getBytes());
                                        out.write("\n".getBytes());
                                        out.close();
                                    } catch (Exception e) {
                                        Log.e(TAG, "CSV creation error", e);
                                    }
                                }
                            });
                        }
                    });
                    accModule.enableAxisSampling();
                    accModule.start();
                    gyroModule.start();
                } else

                {
                    gyroModule.stop();
                    accModule.disableAxisSampling();
                    accModule.stop();
                }
                break;
            default:
                break;
        }
    }

    public void sensorMsg(String msg, final String sensor) {
        final String reading = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sensor == "accel") {
                    accDataView.setText("Accelerometer: " + reading);
                } else {
                    gyroDataView.setText("Gyroscope: " + reading);
                }
            }
        });
    }
}
