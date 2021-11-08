package com.example.foodrecommendation;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        TaskLoadedCallback {

    private static final String LOG_TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitude, longitude;
    private int proximityRadius = 500;
    private AutoCompleteTextView addressField;
    //Field for map button.
    private ImageView locationButton;
    //List to contain address
    static List<Address> addressList = null;
    static List<HashMap<String, String>> nearbyPlacesList = null;
    static ArrayList<HashMap<String, String>> nearbyPlacesListF = null;
    MarkerOptions nearbyPlaceMarker = new MarkerOptions();

    private Polyline currentPolyLine;

    ArrayList<String> placeD = new ArrayList<>();

    /** Retrieve database */
    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1){
            checkUserPermission();
        }

        /** Obtain the SupportMapFragment and get notified when the map is ready to be used.*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /**search bar listener, runs function when pressing enter*/
        addressField = findViewById(R.id.location_search);
        /**Set arrayList to arrayAdapter of autocomplete list*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, placeD);
        addressField.setThreshold(2);
        addressField.setAdapter(adapter);
        addressField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchBar();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });


        /**get reference to my location icon*/
        locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2")); //id = 2 is location icon
        /**change icon of my location button*/
        locationButton.setImageResource(R.drawable.locon);
        // change placement of icon,
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

    }

    @Override
    protected void onStart() {
        super.onStart();

        dRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /** Store data from database in hash map*/
                HashMap<String, Object> dl= (HashMap<String, Object>) dataSnapshot.getValue();

                /** Loop through hashmap and get object to store in another hash map*/
                for(String key: dl.keySet()){
                    Object data = dl.get(key);
                    HashMap<String, Object> place = (HashMap<String, Object>)data;
                    Log.d("data", place.toString());

                    placeD.add(place.get("name").toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onClick(View v){

        String restaurant= "restaurant";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();

        switch(v.getId()){
            case R.id.search_button:
                searchBar();
                //addressField.onEditorAction(EditorInfo.IME_ACTION_DONE);
                break;

            case R.id.searchRestaurant:
                mMap.clear();
                String url = getUrl(latitude, longitude, restaurant); //lat lng string
                //String url = getUrl1(latitude, longitude);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "Searching for nearby restaurants...", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public void hideKeyboard(){
        View view = getCurrentFocus();
        if(view != null){
            InputMethodManager imm  = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /** nearby place url download*/
    private String getUrl(double latitude, double longitude, String nearbyPlace){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + proximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyAiw5F8PrLlLOTReVvyQoSBxeUFhMTH_5M");

        Log.d("GoogleMapActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }

    /** Direction url download*/
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" +
                parameters + "&key="+ getString(R.string.google_maps_key);
        return url;
    }

    /** Get polyline point parser*/
    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyLine!=null){
            currentPolyLine.remove();
        }
        currentPolyLine = mMap.addPolyline((PolylineOptions)values[0]);
    }

    public void directionAlert(final LatLng latLng){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.d_alert);

        /** Set dialog text*/
        TextView text = dialog.findViewById(R.id.alertText);
        text.setText("Do you want to get direction to there?" );

        /** Yes button*/
        Button dialogButton = dialog.findViewById(R.id.alertYes);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getDirection(latLng);
            }
        });

        /** NO button*/
        Button dialogButton2 = dialog.findViewById(R.id.alertNo);
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void getDirection(LatLng lat){
        mMap.clear();

        mMap.setInfoWindowAdapter(null);

        MarkerOptions ori = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Current Location");
        MarkerOptions dest = new MarkerOptions().position(lat).title("Destination");

        String url = getUrl(ori.getPosition(), dest.getPosition(), "driving");
        new FetchUrl(MapsActivity.this).execute(url, "driving");

        ori.icon(BitmapDescriptorFactory.fromResource(R.drawable.markeru));
        mMap.addMarker(ori);
        mMap.addMarker(dest);
    }


	@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //if statement that checks permissions in manifest file for location
        //return true is permission granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED){
            //track client method
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String m = marker.getTitle();
                if(!m.equals("Your Location") & !m.equals("Current Location")) {
                    LatLng dll = marker.getPosition();
                    Log.d("Destination marker LatLng", dll.toString());

                    directionAlert(dll);
                }
            }
        });

        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setPadding(0,300,0,300);
    }

    /** search bar method */
    public void searchBar(){
        /**get text from edit text*/
        String address = addressField.getText().toString();

        MarkerOptions userMarker = new MarkerOptions();

        /**find location, returns the location of the input place */
        if (!TextUtils.isEmpty(address)){
            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(address, 6);

                if (addressList != null){
                    for(int i=0; i<addressList.size(); i++){
                        Address userAddress = addressList.get(i);
                        Log.d("searchbar", userAddress.toString());
                        LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                        //make new marker
                        userMarker.position(latLng);
                        userMarker.title(address);
                        userMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                        mMap.setInfoWindowAdapter(null);
                        mMap.addMarker(userMarker);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    }
                }
                else{
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        else{
            Toast.makeText(this, "Please enter location name", Toast.LENGTH_SHORT).show();
        }
    }


    //indent for randomizer button
    public void random(View view) {
        Log.d(LOG_TAG, "Ran btn clicked");

        Intent intent = new Intent(getApplicationContext(), Randomizer.class);

        intent.putExtra("places", (Serializable) nearbyPlacesList);
        startActivityForResult(intent, 1);
    }

    //for filter button
    public void filter(View view){
        Log.d(LOG_TAG, "Filter btn clicked");

        Intent intent = new Intent(getApplicationContext(), Filter.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==2){
            if(resultCode==Filter.RESULT_OK){
                nearbyPlacesListF = (ArrayList<HashMap<String, String>>)data.getSerializableExtra("filtered");
                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
                if(nearbyPlacesListF != null){
                    mMap.clear();
                    for(int i=0; i<nearbyPlacesListF.size(); i++){
                        HashMap<String, String> pl = nearbyPlacesListF.get(i);
                        if(pl != null)
                            Log.d("hash_MapM", pl.toString());

                        String nameofPlace = pl.get("place_name");

                        String snippet = pl.get("vicinity");
                        double lat = Double.parseDouble(pl.get("latitude"));
                        double lng = Double.parseDouble(pl.get("longitude"));

                        if(pl != null)
                            Log.d("hash_Map", pl.toString());

                        //make new marker
                        LatLng latLng = new LatLng(lat, lng);
                        MarkerOptions nearbyPlaceM = new MarkerOptions();

                        nearbyPlaceM.position(latLng);
                        nearbyPlaceM.title(nameofPlace);
                        nearbyPlaceM.snippet(snippet).visible(true);


                        nearbyPlaceM.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                        mMap.addMarker(nearbyPlaceM);
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    }
                }
            }
            else{

            }
        }
        else if(requestCode==1){
            if(resultCode==Randomizer.RESULT_OK){
                mMap.setInfoWindowAdapter(null);
                double rLat = data.getDoubleExtra("lat", 0);
                double rLng = data.getDoubleExtra("lng", 0);
                String rPlace = data.getStringExtra("place");

                LatLng latlng = new LatLng(rLat, rLng);

                MarkerOptions rmo = new MarkerOptions();
                rmo.position(latlng);
                rmo.title(rPlace);
                rmo.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                Toast.makeText(this, "Showing place on map...", Toast.LENGTH_SHORT).show();
                mMap.clear();
                mMap.addMarker(rmo);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            }
        }
    }

    //check user permission granted or not
    public boolean checkUserPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        }
        else{
            return true;
        }
    }

    //handle permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case Request_User_Location_Code:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        //create new client if there is none
                        if(googleApiClient == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    //This method tracks location of client
    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //connect to google map Api client
        googleApiClient.connect();
    }

    //Location Listener class methods
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastLocation = location;

        //remove current marker when user moves to another place
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //set new marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your Location");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.markeru));
        //add marker
        currentUserLocationMarker = mMap.addMarker(markerOptions);

        /** moves camera to user location*/
        CameraPosition camPos = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .bearing(location.getBearing())
                .build();
        CameraUpdate camUp = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUp);

        if (googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    //Update client location constantly when it is moving
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100); //set intervals to 1100 milliseconds
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //check if permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //Google Api Client Connection Failed methods
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**Inner class GetNearbyPlace*/

    public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
        private String googlePlaceData, url;
        private GoogleMap mMap;

        @Override
        protected String doInBackground(Object... objects) {
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];

            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                googlePlaceData = downloadUrl.readUrl(url);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return googlePlaceData;
        }

        @Override
        protected void onPostExecute(String s) {

            DataParser dataParser = new DataParser();
            nearbyPlacesList = dataParser.parse(s);

            displayNearbyPlace(nearbyPlacesList);
        }

        private void displayNearbyPlace(List<HashMap<String, String>> nearbyPlacesList){
            for(int i=0; i<nearbyPlacesList.size(); i++){

                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

                HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
                String nameofPlace = googleNearbyPlace.get("place_name");

                String snippet = googleNearbyPlace.get("vicinity");
                double lat = Double.parseDouble(googleNearbyPlace.get("latitude"));
                double lng = Double.parseDouble(googleNearbyPlace.get("longitude"));

                if(googleNearbyPlace != null)
                    Log.d("hash_Map", googleNearbyPlace.toString());

                //make new marker
                LatLng latLng = new LatLng(lat, lng);
                nearbyPlaceMarker.position(latLng);
                nearbyPlaceMarker.title(nameofPlace);
                nearbyPlaceMarker.snippet(snippet).visible(true);

                nearbyPlaceMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                mMap.addMarker(nearbyPlaceMarker);
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            }
        }

    }

}
