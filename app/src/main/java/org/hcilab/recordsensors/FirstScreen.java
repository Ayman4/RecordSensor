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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
     int AverageRate=5;
    SensorManager sm;
    //Sensor s;
    //Sensor Gyro,LinearAcc,MyGyro;
    Sensor Accelerometer;
    LocationManager lm;
    String timeStamp;
    double CurrecntLocationLong=0;
    double CurrecntLocationAlt=0;
    private FirebaseDatabase database;
    DatabaseReference nodeReference ;
    static public Integer count;
    Integer countFinished;
    float x;
    float y;
    float z;
    float AverageACC[]=new float[5];
    int windowAverage=0;
    TextView txtviewAcc;
    ArrayList<PointACC> AllPoints=new ArrayList<PointACC>(10);

    TextView txtviewloc;
    TextView txtviewTotalcount;
    TextView txtviewProgress;
    EditText txtGestureName;

    TextView  textViewAngleY;
    TextView textViewAngleX;


    public String GetSelectedGestureName()
    {
        if (!txtGestureName.getText().equals(""))
        {
            return txtGestureName.getText().toString();
        }
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




        txtviewAcc= (TextView) findViewById(R.id.textViewAcc);

        txtviewloc = (TextView) findViewById(R.id.textViewLocation);
        txtviewTotalcount=(TextView) findViewById(R.id.textViewTotalCounts);
        txtviewProgress=(TextView) findViewById(R.id.textViewProgress);
        txtGestureName=(EditText) findViewById(R.id.TxtGestureName);

        textViewAngleY=(TextView)findViewById((R.id.textViewAngleY));
        textViewAngleX=(TextView)findViewById((R.id.textViewAngleX));


        SeekBar s = (SeekBar) findViewById(R.id.seekBarAveraging);
        s.setProgress(5);
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
          //      Toast.makeText(getBaseContext(), "discrete = " + String.valueOf(discrete), Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                // To convert it as discrete value
                AverageRate= progress;

            }
        });


        StartRecording();
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
        count=1;
        countFinished=1;
        windowAverage=0;
        AverageACC=new float[5];
        txtviewProgress.setText(""+0);
        AllPoints.clear();

    }

    public void initListeners(){
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

       // sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),SensorManager.SENSOR_DELAY_FASTEST);

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

        double x2, y2, z2; //24 bit

        // Work out the squares
        x2 = x*x;
        y2 = y*y;
        z2 = z*z;

        //X Axis
        double result=Math.sqrt(y2+z2);
        result=x/result;
        double accel_angle_x = Math.atan(result);

        //Y Axis
        result=Math.sqrt(x2+z2);
        result=y/result;
        double  accel_angle_y = Math.atan(result);

        textViewAngleX.setText(Double.valueOf(accel_angle_x).toString());
        textViewAngleY.setText(Double.valueOf(accel_angle_y).toString());


        String Dataa="";
        String DataG="";

        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // copy new accelerometer data into accel array
                // then calculate new orientation

         /*
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
*/
                Dataa+="Acc," +x +","+y+","+z+",";
                break;


          /* case Sensor.TYPE_ROTATION_VECTOR:
                // copy new magnetometer data into magnet array
                //System.arraycopy(event.values, 0, magnet, 0, 3);
                DataG+="Magnetic," +x +","+y+","+z+",";
                break;
                */
        }



        txtviewAcc.setText("Dataa= "+ Dataa);
        //txtviewloc.setText("Magnetic : "+ DataG);

        ToggleButton tb=(ToggleButton) findViewById(R.id.TBStartRecording);
        if (tb.isChecked()) {

            AverageACC[0]+=x;
            AverageACC[1]+=y;
            AverageACC[2]+=z;
            AverageACC[3]+=accel_angle_x;
            AverageACC[4]+=accel_angle_y;
            windowAverage++;

            if (windowAverage%AverageRate==0) {

                windowAverage=0;
                x=AverageACC[0]/AverageRate;
                y=AverageACC[1]/AverageRate;
                z=AverageACC[2]/AverageRate;
                accel_angle_x=AverageACC[3]/AverageRate;
                accel_angle_y=AverageACC[4]/AverageRate;


                PointACC temp=new PointACC();
                temp.GestureName=GetSelectedGestureName();
                temp.timeStamp=timeStamp.toString();
                temp.CurrecntLocationAlt=String.valueOf(CurrecntLocationAlt);
                temp.CurrecntLocationLong=String.valueOf(CurrecntLocationLong);
                temp.x=x;
                temp.y=y;
                temp.z=z;
                temp.AngleX=accel_angle_x;
                temp.AngleY=accel_angle_y;
                AllPoints.add(temp);








                if (AllPoints.size()==200/AverageRate)
                {
                    //   windowAverage=0;
                    //   x=AverageACC[0]/AverageRate;
                    //   y=AverageACC[1]/AverageRate;
                    //   z=AverageACC[2]/AverageRate;
                    // Write a message to the database
                    final String finalDataa = Dataa;
                    //int MyProgres=((Integer.parseInt((String) txtviewProgress.getText()))/count)*100;
                    //progressBar.setProgress(MyProgres);


                    BackGroundWorker bg = new BackGroundWorker(this);
                    // String GestureName = GetSelectedGestureName();
                    bg.setCountFinished(txtviewProgress);
                    //bg.execute(timeStamp.toString(), String.valueOf(CurrecntLocationLong), String.valueOf(CurrecntLocationAlt), GestureName, String.valueOf(x), String.valueOf(y), String.valueOf(z));

                    bg.execute((ArrayList<PointACC>) AllPoints.clone());
                    AllPoints.clear();

                }
                AverageACC=new float[5];
                count++;
                txtviewTotalcount.setText("" + count);
            }



            //txtviewProgress.setText(count);

            /*Thread t = new Thread(new Runnable() {
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
                */



        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        txtviewloc.setText("Long : "+location.getLongitude()+"Altitude: "+location.getLatitude());
        CurrecntLocationAlt=location.getLatitude();
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
           // sm.registerListener(this, MyGyro, SensorManager.);
//            sm.registerListener(this, LinearAcc, SensorManager.SENSOR_DELAY_NORMAL);
            sm.registerListener(this, Accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        catch (Exception e)
        {

        }

    }
}
