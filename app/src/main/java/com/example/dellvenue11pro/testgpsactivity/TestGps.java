package com.example.dellvenue11pro.testgpsactivity;

import android.app.Activity;
import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TestGps extends Activity {

    private TextView lat;
    private TextView lng;
    private TextView alt;

    double latitude;
    double longtitude;
    double altitude;

    private LocationManager locationManager;
    private String locationProvider;
    private LocationListener mLocationListener;

    // Criteria 클래스로 최선의 Provider를 선택하도록 조건 설정
    public static Criteria getCriteria(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gps);

        lat = (TextView)findViewById(R.id.textLat);
        lng = (TextView)findViewById(R.id.textLng);
        alt = (TextView)findViewById(R.id.textAlt);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Toast.makeText(TestGps.this, "GPS is OFF, Please GPS ON", Toast.LENGTH_SHORT).show();

        locationProvider = locationManager.getBestProvider(getCriteria(), true);

        // LocationListener 등록
        mLocationListener = new myLocationListener();

        // 마지막 위치 가져와서 출력
        getLastLocation();

        try {
            locationManager.requestLocationUpdates(locationProvider, 5000, 0, mLocationListener);
            Log.e("requestLocationUpdate Try", "Permission Allowed");
        }
        catch (SecurityException e){
            Log.e("requestLocationUpdate Catch", "Permission Denied");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            locationManager.removeUpdates(mLocationListener);
            Log.e("onDestroy", "");
        }
        catch(SecurityException e){

        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        try {
            locationManager.removeUpdates(mLocationListener);
            Log.e("onPause", "");
        }
        catch(SecurityException e){

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            locationManager.requestLocationUpdates(locationProvider, 5000, 0, mLocationListener);
            Log.e("onResume", "");
        }
        catch(SecurityException e){

        }
    }

    // 마지막 위치를 받아와 출력하는 메소드
    public void getLastLocation(){
        Location myLocation = getLastKnownLocation();

        latitude = myLocation.getLatitude();
        longtitude = myLocation.getLongitude();
        altitude = myLocation.getAltitude();

        lat.setText("Lat : " + String.valueOf(latitude));
        lng.setText("Lng : " + String.valueOf(longtitude));
        alt.setText("Alt : " + String.valueOf(altitude));

        Log.e("getData", "lat = " + latitude + " lng = " + longtitude + " alt = " + altitude);

    }

    // 마지막 위치를 확인하여 그 위치를 반환하는 메소드, 최초실행시 locationManager.getLastKnownLocation(provider) 사용하면 null 반환으로 인한 회피법
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        Location l = null;
        for (String provider : providers) {
            try {
                l = locationManager.getLastKnownLocation(provider);
                Log.e("getLastKnownLocation", "bestProvider : " + locationProvider);
            }
            catch(SecurityException e){
                Log.e("getLastKnownLocation Catch", "Permission Denied");
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    // LocationListener 외부 클래스로 작성
    public class myLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(TestGps.this, "Location Changed", Toast.LENGTH_SHORT).show();

            latitude = location.getLatitude();
            longtitude = location.getLongitude();
            altitude = location.getAltitude();
            lat.setText("Lat : " + String.valueOf(latitude));
            lng.setText("Lng : " + String.valueOf(longtitude));
            alt.setText("Alt : " + String.valueOf(altitude));

            Log.e("onLocationChanged", "bestProvider : " + locationProvider);
            Log.e("getData", "lat = " + latitude + " lng = " + longtitude + " alt = " + altitude);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Toast.makeText(TestGps.this, provider +" Available", Toast.LENGTH_SHORT).show();
                    Log.e("LocationProvider : ", provider + " Available");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(TestGps.this, provider +" Out of Service", Toast.LENGTH_SHORT).show();
                    Log.e("LocationProvider : ", provider + " Out of Service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(TestGps.this, provider +" Service Stop", Toast.LENGTH_SHORT).show();
                    Log.e("LocationProvider : ", provider + " Service Stop");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(TestGps.this, provider +" Provider Enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(TestGps.this, provider +" Provider Disenabled", Toast.LENGTH_SHORT).show();
        }
    }
}