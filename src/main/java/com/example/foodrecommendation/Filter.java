package com.example.foodrecommendation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Filter extends AppCompatActivity {
    private RadioButton f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12;
    private double latitude, longitude;
    private static double proximityRadius =200;
    private GoogleMap mMap;
    private ArrayList<String> list = new ArrayList<>();
    private List<HashMap<String, String>> placeListF = null; //filter array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();

            if(extras !=null){
                latitude=extras.getDouble("latitude");
                longitude=extras.getDouble("longitude");
                Log.d("LatLng", latitude + String.valueOf(longitude));
            }
        }

        addListenerOnButton();

    }


    private void addListenerOnButton(){
        final RadioGroup rg = findViewById(R.id.radG);

        /** Checkbox listener*/
        f1 = findViewById(R.id.radB);
        f2 = findViewById(R.id.radB2);
        f3 = findViewById(R.id.radB3);
        f4 = findViewById(R.id.radB4);
        f5 = findViewById(R.id.radB5);
        f6 = findViewById(R.id.radB6);
        f7 = findViewById(R.id.radB7);
        f8 = findViewById(R.id.radB8);
        f9 = findViewById(R.id.radB9);
        f10 = findViewById(R.id.radB10);
        f11= findViewById(R.id.radB11);
        f12= findViewById(R.id.radB12);

        /**sad */
        f10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("rice");
                    Log.d("f9", "checked " + list.toString());
                    rg.clearCheck();
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f11.setChecked(false);
                }
                else {
                    list.remove("rice");
                    Log.d("f9", "unchecked " + list.toString());

                }
            }
        });

        f2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("tea");
                    Log.d("f2", "checked " + list.toString());
                    rg.clearCheck();
                    f10.setChecked(false);
                    f3.setChecked(false);
                    f11.setChecked(false);

                }
                else {
                    list.remove("tea");
                    Log.d("f2", "unchecked " + list.toString());
                }
            }
        });

        /**angry*/
        f3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("salad");
                    Log.d("f3", "checked " + list.toString());
                    rg.clearCheck();
                    f2.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else {
                    list.remove("salad");
                    Log.d("f3", "unchecked " + list.toString());
                }
            }
        });

        f11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("iceCream");
                    Log.d("f3", "checked " + list.toString());
                    rg.clearCheck();
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                }
                else {
                    list.remove("iceCream");
                    Log.d("f3", "unchecked " + list.toString());
                }
            }
        });

        /**other types*/
        f4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("chinese");
                    Log.d("f3", "checked " + list.toString());
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else {
                    list.remove("chinese");
                    Log.d("f3", "unchecked " + list.toString());
                }
            }
        });

        f5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("malay");
                    Log.d("f3", "checked " + list.toString());
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else {
                    list.remove("malay");
                    Log.d("f3", "unchecked " + list.toString());
                }
            }
        });

        f6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("indian");
                    Log.d("f3", "checked " + list.toString());
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else {
                    list.remove("indian");
                    Log.d("f3", "unchecked " + list.toString());
                }
            }
        });

        f7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("western");
                    Log.d("f3", "checked " + list.toString());
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else {
                    list.remove("western");
                    Log.d("f3", "unchecked " + list.toString());
                }
            }
        });

        f8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("thai");
                    Log.d("f3", "checked " + list.toString());
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else {
                    list.remove("thai");
                    Log.d("f3", "unchecked " + list.toString());
                }
            }
        });

        f9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    list.add("fastFood");
                    Log.d("f9", "checked " + list.toString());
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else {
                    list.remove("fastFood");
                    Log.d("f9", "unchecked " + list.toString());
                }
            }
        });

        f1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.isChecked()){
                    list.add("bbq");
                    Log.d("f1", "checked " + list.toString() );
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else{
                    list.remove("bbq");
                    Log.d("f1", "unchecked " + list.toString());
                }
            }
        });

        f12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.isChecked()){
                    list.add("steamboat");
                    Log.d("f1", "checked " + list.toString() );
                    f2.setChecked(false);
                    f3.setChecked(false);
                    f10.setChecked(false);
                    f11.setChecked(false);
                }
                else{
                    list.remove("steamboat");
                    Log.d("f1", "unchecked " + list.toString());
                }
            }
        });

    }

    /** Info message on list*/
    public void onClickB(View view){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        switch(view.getId()){
            case R.id.sad1:
                ab.setMessage("Tea, especially green tea is high in amino acid L-theanine, " +
                        "which helps to calm and relax your mind.");
                ab.create();
                ab.show();
                break;
            case R.id.sad2:
                ab.setMessage("Rice is rich in carbohydrate which is needed to increase the serotonin " +
                        "level in your body. Serotonin helps to cheer your mood up!");
                ab.create().show();
                break;
            case R.id.ang1:
                ab.setMessage("Angry increases your blood pressure, low sodium foods like salad comes " +
                        "in handy to regulate blood pressure.");
                ab.create().show();
                break;
            case R.id.ang2:
                ab.setMessage("Low in blood sugar may be causing all the anger. Have some ice cream to " +
                        "cool down yourself and regulate your blood sugar level.");
                ab.create().show();
                break;
        }
    }

    private String stringUrl(){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< list.size(); i++){
            sb.append(list.get(i));
            sb.append("+");
        }
        return sb.toString();
    }

    private String getUrl(double latitude, double longitude, String query){
        String type="restaurant+cafe";
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        googleURL.append("&query=" + query);
        googleURL.append("&type=" + type);
        googleURL.append("&location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + proximityRadius);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyAiw5F8PrLlLOTReVvyQoSBxeUFhMTH_5M");

        Log.d("GoogleMapActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }


    public void searchF(View view){
        Object transferData[] = new Object[2];
        GetNearbyPlacesF getNearbyPlacesF = new GetNearbyPlacesF();
        String url = getUrl(latitude, longitude, stringUrl());

        transferData[0] = mMap;
        transferData[1] = url;

        getNearbyPlacesF.execute(transferData);

        Toast.makeText(this, "Searching for filtered restaurants...", Toast.LENGTH_SHORT).show();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 1 seconds
                intentF();
            }
        }, 2000);


    }

    public void intentF(){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        intent.putExtra("filtered", (Serializable) placeListF);
        setResult(Filter.RESULT_OK, intent);
        finish();
    }


    /**Inner class Filtered Places*/

    public class GetNearbyPlacesF extends AsyncTask<Object, String, String> {
        private String googlePlaceData, url;
        //private GoogleMap mMap;

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

            DataParserF dataParser = new DataParserF();
            placeListF = dataParser.parse(s);

            displayNearbyPlaceF(placeListF);
        }

        private void displayNearbyPlaceF(List<HashMap<String, String>> nearbyPlacesList){
            for(int i=0; i<nearbyPlacesList.size(); i++){
                HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
                if(googleNearbyPlace != null)
                    Log.d("hash_MapF", googleNearbyPlace.toString());
            }
        }

    }
}
