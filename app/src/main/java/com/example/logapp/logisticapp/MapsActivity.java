package com.example.logapp.logisticapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    DB db;

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;
    Button distanse;

    String longit, latit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toast.makeText(getApplicationContext(), "опеределите Точку на КАРТЕ", Toast.LENGTH_LONG).show();

        distanse = (Button) findViewById(R.id.button2);
        distanse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mMap.clear();
                Intent intent = new Intent(getApplicationContext(), Add_Information.class);
                startActivity(intent);

                //Log.i(TAG, distansAll);

            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listPoints = new ArrayList<LatLng>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        List<Marker> originMarkers = new ArrayList<>();

        mMap = googleMap;
        LatLng dhl = new LatLng(53.210463, 50.189022);
        listPoints.add(new LatLng(53.210463, 50.189022));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhl, 13));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("DHL")
                .position(dhl)));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            // иcправить на установку только 1 маркера
            public void onMapLongClick(LatLng latLng) {
                List<Marker> originMarkers = new ArrayList<>();
                //ставим только 2 маркера
                /*if (listPoints.size() == 2){
                    listPoints.clear();
                    //mMap.clear();
                }*/
                //сохраняем первое положение
                listPoints.add(latLng);


                //cоздаем маркер
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listPoints.size() == 1) {
                    //создаем первый маркер
                    //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                } else {
                    //создаем второй маркер
                    //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    //Toast.makeText(getApplicationContext(),  "координаты доставки"+listPoints.get(1)+"", Toast.LENGTH_LONG).show();
                }
                mMap.addMarker(markerOptions);
                //добавим направление
                if (listPoints.size() == 2) {
                    //создаем URL запрос по расссоянию
                    String url = getRequestedUrl(listPoints.get(0), listPoints.get(1));
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);

                } else if (listPoints.size() > 2) {
                    listPoints.clear();
                    mMap.clear();
                    LatLng dhl = new LatLng(53.210463, 50.189022);
                    listPoints.add(new LatLng(53.210463, 50.189022));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhl, 13));
                    originMarkers.add(mMap.addMarker(new MarkerOptions()
                            .title("DHL")
                            .position(dhl)));

                }
            }
        });

    }


    private String getRequestedUrl(LatLng origin, LatLng dest) {

        String str_org = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        longit = String.valueOf(dest.longitude);
        latit = String.valueOf(dest.latitude);
        //Toast.makeText(getApplicationContext(), "test " +longit +latit, Toast.LENGTH_SHORT).show();
        String sensor = "sensor=false";

        String mode = "mode=driving";

        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpsURLConnection httpsURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.connect();

            //получаем результат
            inputStream = httpsURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpsURLConnection.disconnect();
        }
        return responseString;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();

            }
            return responseString;
        }


        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // вставляем json

            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        public void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //все на карту

            ArrayList points = null;
            ArrayList distance = null;
            List<String> al = new ArrayList<>();
            List<String> alres = new ArrayList<>();
            Set<String> hs = new HashSet<>();
            PolylineOptions polylineOptions = null;
            //String a = "";
            String[] array = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                distance = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    String dist = point.get("dist");
                    distance.add(dist);
                    points.add(new LatLng(lat, lon));
                    hs.addAll(distance);
                    al.clear();
                    al.addAll(hs);

                }


                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Ошибка в расчете, попробуйте снова", Toast.LENGTH_SHORT).show();
            }

            String a = "";
            for (int i = 0; i < al.size(); i++) {
                if (al.get(i).endsWith(" m")) {
                    //Log.i(TAG, al.get(i) +"metr");
                    Double k = Double.parseDouble(al.get(i).substring(0, al.get(i).length() - 2));
                    k = k * 0.001;
                    String f = k.toString();
                    alres.add(f);

                    //Log.i(TAG, al.get(i));

                } else {//Log.i(TAG, al.get(i)+ "km");
                    alres.add(al.get(i).substring(0, al.get(i).length() - 3));
                    //Log.i(TAG, al.get(i));
                }


            }
            Double result = 0.0;
            for (int i = 0; i < alres.size(); i++) {
                result = result + Double.parseDouble(alres.get(i));
            }
            String distansOnStartToEnd = result.toString();
            Log.i(TAG, distansOnStartToEnd);
            distance(distansOnStartToEnd);

            //Toast.makeText(getApplicationContext(), "расстояние" + distansOnStartToEnd, Toast.LENGTH_LONG).show();


        }


    }

    public void distance(String distansOnStartToEnd) {
        //goto AddActivity
        //Double dest = Double.parseDouble(distansOnStartToEnd);
                Intent modify_intent = new Intent(getApplicationContext(), Add_Information.class);
        Toast.makeText(getApplicationContext(), "test " +longit +latit, Toast.LENGTH_SHORT).show();
        modify_intent.putExtra("dest", distansOnStartToEnd);
        modify_intent.putExtra("longit", longit);
        modify_intent.putExtra("latit", latit);
        startActivity(modify_intent);
    }
}
