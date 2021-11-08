package com.example.foodrecommendation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Randomizer extends AppCompatActivity{
    private ListView lv;
    private static Random rand = new Random();
    private ArrayList<HashMap<String, String>> placeList = null; //intent array
    private ArrayList<HashMap<String, String>> adaptList = new ArrayList<>();//list array
    private static String rPlace;
    private static double lat, lng;

    /**Menu selection*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.ran_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.ran_choose:
                if(placeList != null){
                    pickAlert();
                    return true;
                }
                else{
                    nullAlert();
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomizer);

        lv = findViewById(R.id.listView);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            placeList = (ArrayList<HashMap<String, String>>) bundle.getSerializable("places");
        }

        /** if local arrayList is not empty, then print list in view*/
        if(placeList != null){
            printList(placeList);
            if(adaptList != null) {
                /**save array list into file*/
                saveList(adaptList);
            }
        }
        else{
            /**when local list is empty, read from file*/
            readList();
            printAdaptList(adaptList);
        }

    }

    /**get place list from bundle and display in list view*/
    public void printList(ArrayList<HashMap<String, String>> placeList){
        for (int i = 0; i < placeList.size(); i++) {

            HashMap<String, String> pl = placeList.get(i);
            Log.d("JSON place", pl.toString());

            pl.put("reference", pl.get("reference"));

            pl.put("name", pl.get("place_name"));

            Log.d("pl_log", pl.get("place_name"));

            adaptList.add(pl);

        }

        ListAdapter adapter = new SimpleAdapter(Randomizer.this, placeList,
                R.layout.list_item,
                new String[] {"name"}, new int[] {R.id.name });

        // Add a header to the ListView
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listview_header, lv,false);
        lv.addHeaderView(header);

        lv.setAdapter(adapter);
    }

    /**if adapter array is invoked before place list, this will display old results*/
    public void printAdaptList(ArrayList<HashMap<String, String>> adaptList){
        ListAdapter adapter = new SimpleAdapter(Randomizer.this, adaptList,
                R.layout.list_item,
                new String[] {"name"}, new int[] {R.id.name });

        // Add a header to the ListView
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listview_header,lv,false);
        lv.addHeaderView(header);

        lv.setAdapter(adapter);
    }

    public static HashMap pickOne(ArrayList<HashMap<String, String>> adaptList){
        HashMap one = adaptList.get(rand.nextInt(adaptList.size()));
        Log.d("pick one", String.valueOf(one));
        return one;
    }

    public void nullAlert(){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("No restaurant searched yet, search by pressing search button at map");
        ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        ab.create();
        ab.show();
    }

    public void pickAlert(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.r_alert);

        Button dialogClose = dialog.findViewById(R.id.alertX);
        // close the dialog
        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //details of random place
        HashMap rd = pickOne(adaptList);
        rPlace= rd.get("place_name").toString();
        Log.d("pick one", rPlace);

        lat= Double.parseDouble(rd.get("latitude").toString());
        Log.d("pick one", String.valueOf(lat));

        lng= Double.parseDouble(rd.get("longitude").toString());
        Log.d("pick one", String.valueOf(lng));

        // set the dialog text
        TextView text = dialog.findViewById(R.id.alertText);
        text.setText("Restaurant chosen is " + rPlace);

        Button dialogButton = dialog.findViewById(R.id.alertChoose);
        // Pick restaurant again
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickAlert();
            }
        });

        Button dialogButton2 = dialog.findViewById(R.id.alertMap);
        /** Show chosen one on map*/
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intentR();
            }
        });

        dialog.show();
    }

    public void intentR(){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("place", rPlace);
        setResult(Filter.RESULT_OK, intent);
        finish();
    }

    /**serialize adapt array and store in txt file*/
    public void saveList(ArrayList<HashMap<String, String>> placelist){
        String ser = SerializeObject.objectToString(placelist);
        if (ser != null && !ser.equalsIgnoreCase("")) {
            SerializeObject.WriteSettings(this, ser, "Saved.txt");
            Log.d("saveList", ser);
        } else {
            SerializeObject.WriteSettings(this, "", "Saved.txt");
        }
    }

    /**deserialize object and get data*/
    public void readList(){
        String ser = SerializeObject.ReadSettings(this, "Saved.txt");
        if (ser != null && !ser.equalsIgnoreCase("")) {
            Object obj = SerializeObject.stringToObject(ser);
            // Then cast it to your object and
            if (obj instanceof ArrayList) {
                // Do something
                adaptList = (ArrayList<HashMap<String, String>>)obj;
            }
        }
        else{
            printNull();
        }
    }

    public void printNull(){
        HashMap<String, String> nul = new HashMap<>();
        nul.put("Message", "Nothing to show, press the search button in map");

        adaptList.add(nul);
    }

}


