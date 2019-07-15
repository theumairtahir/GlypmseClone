package com.example.glypmse_clone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.example.glypmse_clone.Models.Destination;
import com.example.glypmse_clone.Models.Glympse;
import com.example.glypmse_clone.Models.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap map;
    ImageButton btnChangeMapType;
    ImageButton btnActiveGlympse;
    TextView txtUserName;

    //Business components
    User activeUser;
    ArrayList<Glympse> lstActiveGlympses;

    FusedLocationProviderClient fusedLocationProviderClient; //gets device last known location
    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE=7000;
    private static final int PLAY_SERVICE_RES_REQUEST=7001;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient= new FusedLocationProviderClient(this);
        //initializing model from db
        initializeModel();
        //binding the view components
        btnActiveGlympse=findViewById(R.id.btnActiveGlympse);
        btnActiveGlympse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder dialogBuider= new android.app.AlertDialog.Builder(MainActivity.this);
                dialogBuider.setTitle("Active Glympses");
                String[] activeGlympsesNames=new String[lstActiveGlympses.size()];
                for (int i=0; i<activeGlympsesNames.length; i++){
                    activeGlympsesNames[i]="Sent by: "+lstActiveGlympses.get(i).getSender().getName();
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
        drawer.setDrawerTitle(DrawerLayout.TEXT_ALIGNMENT_GRAVITY,activeUser.getName());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //setting user name on the drawer text
        View headerView=navigationView.getHeaderView(0);
        TextView txtUserName=headerView.findViewById(R.id.txtDrawerUserName);
        txtUserName.setText(activeUser.getName());

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

    }

    private void updateGlympse(Glympse glympse) {
        map.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(5)
                .color(Color.CYAN)
                .add(activeUser.getLastPosition(),glympse.getDestination().getDestination()));
        map.addMarker(new MarkerOptions()
                .position(glympse.getDestination().getDestination())
                .title(glympse.getDestination().getName()));
        map.animateCamera(CameraUpdateFactory.zoomBy(10));
    }

    private void initializeModel() {
        //will be replaced by DB values
        lstActiveGlympses=new ArrayList<>();
        activeUser=new User("Umair Tahir","03074172329","ut786","12345",getDeviceLatestLocation());
        lstActiveGlympses.add(new Glympse(activeUser,new User("Sarim","03031234567","sarim123","12345",new LatLng(31.389280,74.240503)),60,true,"7/15/2019 06:52:00 PM",new Destination(new LatLng(31.461814,74.321742),"Model Town",60)));
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
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_CODE);
                getDeviceCurrentLocation();
            }
            else{
                //if request granted
                getDeviceCurrentLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    LatLng currentPosition=new LatLng(location.getLatitude(),location.getLongitude());
                    map.addMarker(new MarkerOptions().position(currentPosition).title(activeUser.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                }
            }
        });
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
        activeUser.setLastPosition(latestLocation);
    }
}
