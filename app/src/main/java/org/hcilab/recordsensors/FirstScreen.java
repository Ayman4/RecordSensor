package org.hcilab.recordsensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.gravity;
import static android.R.attr.logo;

public class FirstScreen extends AppCompatActivity implements SensorEventListener, LocationListener {


    private static final int TAG_CODE_PERMISSION_LOCATION = 1;

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
    }
    SensorManager sm;
    //Sensor s;
    Sensor Gyro,LinearAcc,MyGyro;
    LocationManager lm;
    String timeStamp;
    double CurrecntLocationLong=0;
    double CurrecntLocationAlt=0;
    private FirebaseDatabase database;
    DatabaseReference nodeReference ;
    int count;
    float x;
    float y;
    float z;

    TextView txtviewAcc;

    TextView txtviewloc;


    public String GetSelectedGestureName()
    {
        ToggleButton Tb = (ToggleButton) findViewById(R.id.TBTurnLeft);
        if (Tb.isChecked())
        return "Turn Left";
        Tb = (ToggleButton) findViewById(R.id.TBTurnRightAcute);
        if (Tb.isChecked())
            return "Turn Right";
        Tb = (ToggleButton) findViewById(R.id.TBAccelerate);
        if (Tb.isChecked())
            return "Accelerate";
        Tb = (ToggleButton) findViewById(R.id.TBBreak);
        if (Tb.isChecked())
            return "Break";
        Tb = (ToggleButton) findViewById(R.id.TBZigZagL);
        if (Tb.isChecked())
            return "ZigZag Left";
        Tb = (ToggleButton) findViewById(R.id.TBZigZagR);
        if (Tb.isChecked())
            return "ZigZag Right";
        return "NA";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        sm =(SensorManager) this.getSystemService(SENSOR_SERVICE);
        initListeners();
        //sm= (SensorManager)getSystemService(SENSOR_SERVICE);
        //      mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //    mGyroscope = mSensorManager.getDefaultSensor(TYPE_GYROSCOPE);

//        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //  LinearAcc = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //MyGyro=sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //Gyro = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

           // return false;
        }



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(lm.GPS_PROVIDER, 500, 1, this);

        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }



        StartRecording();
        txtviewAcc= (TextView) findViewById(R.id.textViewAcc);

        txtviewloc = (TextView) findViewById(R.id.textViewLocation);


       /* List<Sensor> listsensor =  sm.getSensorList(Sensor.TYPE_ALL);


        for(int i = 0; i<listsensor.size(); i++){

            txtviewloc.setText(listsensor.get(i).toString());
            Log.i("Sensor name",listsensor.get(i).toString());
        }
        */



    }
    private void StartRecording() {

       // ToggleButton tb=(ToggleButton) findViewById(R.id.toggleButton);
        timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        nodeReference = database.getReference().child(timeStamp);
        count=0;


    }

    public void initListeners(){
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),SensorManager.SENSOR_DELAY_FASTEST);

        //sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_screen, menu);



        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void StartMe(View view)
    {
        StartRecording();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        x=event.values[0];
        y=event.values[1];
        z=event.values[2];



        String Dataa="";
        String DataG="";

        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // copy new accelerometer data into accel array
                // then calculate new orientation
                final float alpha = (float) 0.8;

                float[] gravity=new float[3];
                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                // Remove the gravity contribution with the high-pass filter.
                x = event.values[0] - gravity[0];
                y = event.values[1] - gravity[1];
                z = event.values[2] - gravity[2];

                Dataa+="Acc," +x +","+y+","+z+",";
                break;


            case Sensor.TYPE_ROTATION_VECTOR:
                // copy new magnetometer data into magnet array
                //System.arraycopy(event.values, 0, magnet, 0, 3);
                DataG+="Magnetic," +x +","+y+","+z+",";
                break;
        }



        txtviewAcc.setText("Dataa= "+ Dataa);
        //txtviewloc.setText("Magnetic : "+ DataG);

        ToggleButton tb=(ToggleButton) findViewById(R.id.TBStartRecording);
        if (tb.isChecked()) {


                // Write a message to the database
            final String finalDataa = Dataa;
            Thread t = new Thread(new Runnable() {
                    public void run() {
                        String timeStamp1 = new SimpleDateFormat("HH-mm-ss-SSS").format(new Date());

                        nodeReference.child(timeStamp).child(String.valueOf(count));

                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("TimeStamp").setValue(timeStamp1);
                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("Alt").setValue(CurrecntLocationAlt);
                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("Long").setValue(CurrecntLocationLong);

                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("Data").setValue(finalDataa);
                       // nodeReference.child(timeStamp).child(String.valueOf(count)).child("AccY").setValue(y);
                        //nodeReference.child(timeStamp).child(String.valueOf(count)).child("AccX").setValue(x);
                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("GestureName").setValue(GetSelectedGestureName());

                        count++;
                    }
                });

                t.start();



        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        txtviewloc.setText("Long : "+location.getLongitude()+"Altitude: "+location.getAltitude());
        CurrecntLocationAlt=location.getAltitude();
        CurrecntLocationLong=location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }
    protected  void onResume()
    {
        super.onResume();
        try {
            sm.registerListener(this, MyGyro, SensorManager.SENSOR_DELAY_NORMAL);
//            sm.registerListener(this, LinearAcc, SensorManager.SENSOR_DELAY_NORMAL);
            sm.registerListener(this, Gyro, SensorManager.SENSOR_DELAY_NORMAL);
        }
        catch (Exception e)
        {

        }

    }
}
