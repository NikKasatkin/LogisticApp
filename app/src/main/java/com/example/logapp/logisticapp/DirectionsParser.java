package com.example.logapp.logisticapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


public class DirectionsParser {



    public DirectionsParser() {
    }

    public List<List<HashMap<String, String>>> parse(JSONObject jsonObject){
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
        
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        String distansAll = "";
        List<List<HashMap<String, String>>> dist = new ArrayList<List<HashMap<String,String>>>();

        try {

            jRoutes = jsonObject.getJSONArray("routes");

            for (int i = 0; i< jRoutes.length(); i++){
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                List path = new ArrayList<HashMap<String, String>>();

                for (int j = 0; j<jLegs.length(); j++){
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        for (int k = 0; k<jSteps.length(); k++){
                        String polyline = "";
                        String distance = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyline(polyline);
                        distance = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("distance")).get("text");//.get("value");
                            //distansAll = distansAll + distance + "|";

                            for (int l = 0; l< list.size(); l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lon", Double.toString(((LatLng) list.get(l)).longitude));
                            hm.put("dist", distance);
                            path.add(hm);

                                //Log.i(TAG, distance);
                        }
                    }
                    routes.add(path);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        //Log.i(TAG, distansAll);
        return routes;
    }


    private List decodePolyline(String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng =0;

        while (index < len){
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1): (result >> 1));
            lat += dlat;

            shift = 0;
            result  = 0;
            do {
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while (b >= 0x20);
            int dlng = ((result & 1) !=0 ? ~(result >>1):(result >>1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double)lng / 1E5)));
            poly.add(p);
            }
            return poly;
    }

}


