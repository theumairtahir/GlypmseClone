package com.example.glypmse_clone.MapsWork;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Route {
    private List<LatLng> lstDirections;
    private String startAddress, endAddress, timeRequired, distanceRequired, summary;
    private int timeRequiredValue, distanceRequiredValue;

    public Route(LatLng startPosition, LatLng endPosition, String key, Activity callingActivity) {
        lstDirections=new ArrayList<>();
        //code to get the directions by requesting to the API by JSON
        final String startPointLat, startPointLng, endPointLat, endPointLng;
        startPointLat=startPosition.latitude+"";
        startPointLng=startPosition.longitude+"";
        endPointLat=endPosition.latitude+"";
        endPointLng=endPosition.longitude+"";
        String requestUrl="https://maps.googleapis.com/maps/api/directions/json?" +
                "origin="+startPointLat+","+startPointLng +
                "&destination="+endPointLat+","+endPointLng +
                "&key="+key;
        RequestQueue requestQueue= Volley.newRequestQueue(callingActivity);
        final JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, requestUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray routes=response.getJSONArray("routes");
                    JSONObject route=routes.getJSONObject(0);
                    String encodedPolyline=route.getJSONObject("overview_polyline").getString("points");
                    lstDirections=decodePoly(encodedPolyline);
                    JSONObject leg=response.getJSONArray("legs").getJSONObject(0);
                    distanceRequired=leg.getJSONObject("distance").getString("text");
                    distanceRequiredValue=leg.getJSONObject("distance").getInt("value");
                    timeRequired=leg.getJSONObject("duration").getString("text");
                    timeRequiredValue=leg.getJSONObject("duration").getInt("value");
                    startAddress=leg.getString("start_address");
                    endAddress=leg.getString("end_address");
                    summary=route.getString("summary");

                } catch (JSONException e) {
                    Log.e("Error in getting Routes",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error in getting Routes",error.getMessage());
            }
        });
        requestQueue.add(request);
        //end of JSON code
    }

    public Route(LatLng startPosition, LatLng endPosition, String mode, String key, Activity callingActivity) {
        lstDirections=new ArrayList<>();
        //code to get the directions by requesting to the API by JSON
        final String startPointLat, startPointLng, endPointLat, endPointLng;
        startPointLat=startPosition.latitude+"";
        startPointLng=startPosition.longitude+"";
        endPointLat=endPosition.latitude+"";
        endPointLng=endPosition.longitude+"";
        String requestUrl="https://maps.googleapis.com/maps/api/directions/json?" +
                "mode="+ mode +
                "&origin="+startPointLat+","+startPointLng +
                "&destination="+endPointLat+","+endPointLng +
                "&key="+key;
        RequestQueue requestQueue= Volley.newRequestQueue(callingActivity);
        final JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET, requestUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray routes=response.getJSONArray("routes");
                    JSONObject route=routes.getJSONObject(0);
                    String encodedPolyline=route.getJSONObject("overview_polyline").getString("points");
                    lstDirections=decodePoly(encodedPolyline);
                    JSONObject leg=response.getJSONArray("legs").getJSONObject(0);
                    distanceRequired=leg.getJSONObject("distance").getString("text");
                    distanceRequiredValue=leg.getJSONObject("distance").getInt("value");
                    timeRequired=leg.getJSONObject("duration").getString("text");
                    timeRequiredValue=leg.getJSONObject("duration").getInt("value");
                    startAddress=leg.getString("start_address");
                    endAddress=leg.getString("end_address");
                    summary=route.getString("summary");
                } catch (JSONException e) {
                    Log.e("Error in getting Routes",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error in getting Routes",error.getMessage());
            }
        });
        requestQueue.add(request);
        //end of JSON code
    }

    public List<LatLng> getDirections() {

        return lstDirections;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public String getTimeRequired() {
        return timeRequired;
    }

    public String getDistanceRequired() {
        return distanceRequired;
    }

    public String getSummary() {
        return summary;
    }

    public int getTimeRequiredValue() {
        return timeRequiredValue;
    }

    public int getDistanceRequiredValue() {
        return distanceRequiredValue;
    }

    private List<LatLng> decodePoly(@org.jetbrains.annotations.NotNull String encoded) {
        /*
        Method to decode polyline from JSON Object downloaded from gitHub
        https://github.com/gripsack/android/blob/master/app/src/main/java/com/github/gripsack/android/data/model/DirectionsJSONParser.java
         */
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
