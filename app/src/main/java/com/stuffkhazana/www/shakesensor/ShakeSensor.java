package com.stuffkhazana.www.shakesensor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShakeSensor extends AppCompatActivity implements SensorEventListener, LocationListener {


    @InjectView(R.id.textViewData)
    TextView txtData;

    @InjectView(R.id.buttonActivate)
    Button btnActivate;

    SensorManager sensorManager;
    Sensor sensor;
    LocationManager locationManager;

    double latitude, longitude;
    // String ADDRESS;
    String msg ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_sensor);

        ButterKnife.inject(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);



        btnActivate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sensorManager.registerListener(ShakeSensor.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);


            }

        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] values = event.values;
        float proximity = values[0];

        /*if (proximity == 0) {
            String phone = "+91 9569661447";
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please Grant the Permissions in Settings", Toast.LENGTH_LONG).show();
            } else {
                startActivity(intent);
                txtData.setText("Call has been placed");
            }



        /*float x=values[0];
        float y=values[1];
        float z=values[2];
        txtData.setText("x = "+x+" y = "+y+" z = "+z);


        } else {
            txtData.setText("Proximity: " + proximity);
            Toast.makeText(this,"Bring the hand near the phn sensor",Toast.LENGTH_LONG).show();
            //txtData.setText("Bring the handset near the your ears to place the call");
        } */

        float x = values[0];
        float y = values[1];
        float z = values[2];
        float cal = ((x * x) + (y * y) + (z * z)) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        if (cal > 5) {
            txtData.setText("Device shaken");
            sensorManager.unregisterListener(this);



            //String msg = "Device has been shaken and location fetched";

            //  SmsManager smsManager = SmsManager.getDefault();
            //smsManager.sendTextMessage(phn,null,msg,null,null);

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please Grant the Permissions in Settings", Toast.LENGTH_LONG).show();
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 5, ShakeSensor.this);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                latitude = location.getLatitude();
        //        longitude = location.getLongitude();
                try {
                    Geocoder geocoder = new Geocoder(this);
                    List<Address> adrsList = geocoder.getFromLocation(latitude, longitude, 5);

                    if (adrsList != null && adrsList.size() > 0) {
                        Address address = adrsList.get(0);
                        StringBuffer buffer = new StringBuffer();

                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            buffer.append(address.getAddressLine(i) + "\n");
                        }

                        //ADDRESS
                        txtData.setText("Address: " + buffer.toString());
                        String phn = "+91 80542 47703";
                        msg = buffer.toString();

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phn,null,msg,null,null);
                        Toast.makeText(this, "Your Current Location", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            txtData.setText("Shake The Device");
            // Intent intent = new Intent(MySensors.this,MyLocaion.class);
            // startActivity(intent);





        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();






        locationManager.removeUpdates(this);

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
}
