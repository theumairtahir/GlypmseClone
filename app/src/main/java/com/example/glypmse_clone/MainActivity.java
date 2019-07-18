package com.example.glypmse_clone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.glypmse_clone.Models.Destination;
import com.example.glypmse_clone.Models.Glympse;
import com.example.glypmse_clone.Models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.tasks.Tasks.await;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap map;
    ImageButton btnChangeMapType;
    ImageButton btnActiveGlympse;
    NavigationView navigationView;
    Marker destinationMarker, startMarker;
    //Business components
    User activeUser;
    ArrayList<Glympse> lstActiveGlympses;

    FusedLocationProviderClient fusedLocationProviderClient; //gets device last known location
    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE=7000;
    private static final int PLAY_SERVICE_RES_REQUEST=7001;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            //initializing model from db
            initializeModel();
            //binding the view components
            btnActiveGlympse = findViewById(R.id.btnActiveGlympse);
            btnActiveGlympse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.AlertDialog.Builder dialogBuider = new android.app.AlertDialog.Builder(MainActivity.this);
                    dialogBuider.setTitle("Active Glympses");
                    String[] activeGlympsesNames = new String[lstActiveGlympses.size()];
                    for (int i = 0; i < activeGlympsesNames.length; i++) {
                        activeGlympsesNames[i] = "Sent by: " + lstActiveGlympses.get(i).getSender().getName();
                    }
                    dialogBuider.setItems(activeGlympsesNames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateGlympse(lstActiveGlympses.get(i));
                        }
                    });
                    dialogBuider.show();
                }
            });
            btnChangeMapType = findViewById(R.id.btnChangeMapType);
            btnChangeMapType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //new pop-up for multiple options
                    String[] mapTypes = {"Street", "Satellite", "Terrain", "Traffic"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("MAP TYPE");
                    builder.setItems(mapTypes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on mapTypes[which]
                            if (which == 0)
                                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            else if (which == 1)
                                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            else if (which == 2)
                                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            else if (which == 3) {
                                if (map.isTrafficEnabled())
                                    map.setTrafficEnabled(false);
                                else
                                    map.setTrafficEnabled(true);
                            }

                        }
                    });
                    builder.show();
                }
            });
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.setDrawerTitle(DrawerLayout.TEXT_ALIGNMENT_GRAVITY, "Glympse");
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            //Initializing Map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            //set the alignment of the zoom buttons of the google map
            @SuppressLint("ResourceType") View zoomControls = mapFragment.getView().findViewById(0x1);

            if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                // ZoomControl is inside of RelativeLayout
                RelativeLayout.LayoutParams params_zoom = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

                // Align it to - parent top|left
                params_zoom.addRule(RelativeLayout.ALIGN_PARENT_START);

                // Update margins, set to 10dp
                final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                        getResources().getDisplayMetrics());
                params_zoom.setMargins(margin, margin, margin, margin);

            }
            mapFragment.getMapAsync(this);
            //starting service to update location of the device every 10 sec
            startService(new Intent(MainActivity.this, LocationUpdateService.class));
        }
        catch (Exception e){
            Log.e("Error in OnCreate:",e.getMessage());
        }
    }

    private void updateGlympse(Glympse glympse) {
        try {
            final Glympse g = glympse;
            final String[] startAddress = new String[1];
            final String[] endAddress = new String[1];
            final String[] timeRequired = new String[1];
            final String[] distanceRequired = new String[1];
            final String[] summary = new String[1];
            final int[] timeRequiredValue = new int[1];
            final int[] distanceRequiredValue = new int[1];
            final String[] encodedPolyline = new String[1];
            //code to get the directions by requesting to the API by JSON
            final String startPointLat, startPointLng, endPointLat, endPointLng;
            startPointLat = activeUser.lastPosition.latitude + "";
            startPointLng = activeUser.lastPosition.longitude + "";
            endPointLat = g.getDestination().getDestination().latitude + "";
            endPointLng = g.getDestination().getDestination().longitude + "";
            String requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + startPointLat + "," + startPointLng +
                    "&destination=" + endPointLat + "," + endPointLng +
                    "&key=" + getResources().getString(R.string.google_maps_key);
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        JSONObject route = routes.getJSONObject(0);
                        encodedPolyline[0] = route.getJSONObject("overview_polyline").getString("points");
                        JSONArray legs = route.getJSONArray("legs");
                        JSONObject leg = legs.getJSONObject(0);
                        distanceRequired[0] = leg.getJSONObject("distance").getString("text");
                        distanceRequiredValue[0] = leg.getJSONObject("distance").getInt("value");
                        timeRequired[0] = leg.getJSONObject("duration").getString("text");
                        timeRequiredValue[0] = leg.getJSONObject("duration").getInt("value");
                        startAddress[0] = leg.getString("start_address");
                        endAddress[0] = leg.getString("end_address");
                        summary[0] = route.getString("summary");
                        List<LatLng> lstDirections = decodePoly(encodedPolyline[0]);
                        //adjusting bounds
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        for (LatLng latlng :
                                lstDirections) {
                            boundsBuilder.include(latlng);
                        }
                        LatLngBounds bounds = boundsBuilder.build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                        map.animateCamera(cameraUpdate);

                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.clickable(true).width(5).color(Color.BLACK);
                        polylineOptions.addAll(lstDirections);


                        map.addPolyline(polylineOptions);

                        String description = "Distance: " + distanceRequired[0] + " Time: " + timeRequired[0];
                        if (destinationMarker != null) {
                            destinationMarker.remove();
                        }

                        destinationMarker = map.addMarker(new MarkerOptions()
                                .position(new LatLng(g.getDestination().getDestination().latitude, g.getDestination().getDestination().longitude))
                                .title(g.getDestination().getName())
                                .snippet(description));


                    } catch (JSONException e) {
                        Log.e("Error in getting Routes", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error in getting Routes", error.getMessage());
                }
            });
            requestQueue.add(request);
            //end of JSON code
        }
        catch (Exception e){
            Log.e("Error in UpdateGlympse",e.getMessage());
        }
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

    private void initializeModel() {
        try {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference users = db.getReference("users");
            final SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.glympse_shared_preferences), MODE_PRIVATE);
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        activeUser = dataSnapshot.child(preferences.getString("phoneNumber", "")).getValue(User.class);
                        if (activeUser.lastPosition != null) {
                            if (startMarker != null) {
                                startMarker.remove();
                            }
                            LatLng currentPosition = new LatLng(activeUser.lastPosition.getLatitude(), activeUser.lastPosition.getLongitude());
                            startMarker = map.addMarker(new MarkerOptions().position(currentPosition).title(activeUser.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));

                        } else {
                            LatLng deviceLocation = getDeviceLatestLocation();
                            activeUser.setLastPosition(new com.example.glypmse_clone.Models.LatLng(deviceLocation.latitude, deviceLocation.longitude));
                            updateActiveUserLocation(new LatLng(activeUser.lastPosition.latitude, activeUser.lastPosition.longitude));
                        }
                        //setting user name on the drawer text
                        View headerView = navigationView.getHeaderView(0);
                        TextView txtUserName = headerView.findViewById(R.id.txtDrawerUserName);
                        txtUserName.setText(activeUser.getName());
                        lstActiveGlympses.add(new Glympse(activeUser, new User("Sarim", "03031234567", "Pakistan", new com.example.glypmse_clone.Models.LatLng(31.389280, 74.240503)), 60, true, "7/15/2019 06:52:00 PM", new Destination(new com.example.glypmse_clone.Models.LatLng(31.461814, 74.321742), "Model Town", 60)));
                    }
                    catch (Exception e){
                        Log.e("Error:",e.getMessage());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getBaseContext(), "Something went wrong! Please check your Internet connection.", Toast.LENGTH_LONG).show();
                }
            });
            //will be replaced by DB values
            lstActiveGlympses = new ArrayList<>();
            //lstActiveGlympses.add(new Glympse(activeUser,new User("Sarim","03031234567","Pakistan",new LatLng(31.389280,74.240503)),60,true,"7/15/2019 06:52:00 PM",new Destination(new LatLng(31.461814,74.321742),"Model Town",60)));
        }
        catch (Exception e){
            Log.e("Error in InitializeModel",e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.setPadding(10, 0, 0, 10);
        //getting device location
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Request runtime permission
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_CODE);
            }
            else{
                //if request granted
                //updateActiveUserLocation();
            }
        }
    }

    private LatLng getDeviceLatestLocation(){
        final LatLng[] latestLocation = {null};
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    latestLocation[0] =new LatLng(location.getLatitude(),location.getLongitude());
                    updateActiveUserLocation(latestLocation[0]);
                }
            }
        });
        return latestLocation[0];
    }

    private void updateActiveUserLocation(LatLng latestLocation) {
        activeUser.setLastPosition(new com.example.glypmse_clone.Models.LatLng(latestLocation.latitude,latestLocation.longitude));
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    LatLng currentPosition=new LatLng(location.getLatitude(),location.getLongitude());
                    if(startMarker!=null)
                        startMarker.remove();
                    startMarker=map.addMarker(new MarkerOptions().position(currentPosition).title(activeUser.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getBaseContext(),LocationUpdateService.class));
        super.onDestroy();
    }
}
