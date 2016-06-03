package com.example.humanalone.diploma;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "Debug";
    //private Button button;
    private LocationManager locationManager;
    private String provider;
    private Button btn;
    private MobileServiceClient mClient;
    private Data data;
    private String deviceId;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting LocationManager object
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);


        try {
            mClient = new MobileServiceClient("https://diplomamsu.azurewebsites.net",this);

//            mClient.getTable(Data.class).insert(data, new TableOperationCallback<Data>() {
//                public void onCompleted(Data entity, Exception exception, ServiceFilterResponse response) {
//                    if (exception == null) {
//                        // Insert succeeded
//                    } else {
//                        // Insert failed
//                    }
//                }
//            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        tm  = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm.getDeviceId();



        // Проверка на включение провайдера
        final TextView tvStat = (TextView) findViewById(R.id.textView8);
        tvStat.setText("" + locationManager.isProviderEnabled(provider));

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.exit(0);
            }
        });

//        if (provider != null && !provider.equals("")) {
//
//            // Get the location from the given provider
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
//                    (this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//
//            // Последние известные координаты, которые частенько равны null
//            Location location = locationManager.getLastKnownLocation(provider);
//
//            // Типа узнать об обновлении
//            locationManager.requestLocationUpdates(provider, 2000, 1, this);
//
//            if (location != null)
//                onLocationChanged(location);
//            else
//                Toast.makeText(getBaseContext(), "Данные не могут быть извлечены, включите GPS", Toast.LENGTH_SHORT).show();
//
//        } else {
//            Toast.makeText(getBaseContext(), "Провайдер не найден", Toast.LENGTH_SHORT).show();
//        }


    }

    @Override
    protected void onResume() {
        // включаем отслеживание
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, this);

        super.onResume();
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG,"LONGITUDE = " +  location.getLongitude() + " LATITUDE " + location.getLatitude()
                + " SPEED " + location.getSpeed() * 3.6 );
        // Getting reference to TextView tv_longitude
        TextView tvLongitude = (TextView)findViewById(R.id.textView6);

        // Getting reference to TextView tv_latitude
        TextView tvLatitude = (TextView)findViewById(R.id.textView7);

        // Getting reference to TextView tv_altitude
        TextView tvAltitude = (TextView)findViewById(R.id.textView8);

        // Getting reference to TextView tv_speed
        TextView tvSpeed = (TextView)findViewById(R.id.textView9);

        //TextView tvTime = (TextView)findViewById(R.id.textView10);

        // Setting Current Longitude
        tvLongitude.setText("" + location.getLongitude());

        // Setting Current Latitude
        tvLatitude.setText("" + location.getLatitude() );

        // Setting Current Altitude
        tvAltitude.setText("" + location.getAccuracy());

        // Setting Current Speed
        tvSpeed.setText("" + location.getSpeed() * 3.6);

        //tvTime.setText("" + location.getTime());

        long date = System.currentTimeMillis();
        TextView tvData = (TextView) findViewById(R.id.textView10);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateString = sdf.format(date);
        tvData.setText(dateString);

        Data data = new Data();
        data.Latitude = location.getLatitude();
        data.Longitude = location.getLongitude();
        data.Speed = location.getSpeed();
        data.deviceId = deviceId;
        data.Timestamp = dateString;
        mClient.getTable(Data.class).insert(data, new TableOperationCallback<Data>() {
            public void onCompleted(Data entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    // Insert succeeded
                } else {
                    // Insert failed
                }
            }
        });
        // Setting Current Time

    }

    // Интерфейсные методы
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}




