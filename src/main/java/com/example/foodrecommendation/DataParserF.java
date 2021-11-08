package com.example.foodrecommendation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParserF {
    private HashMap<String, String> getPlace(JSONObject googlePlaceJSON){
        HashMap<String, String> googlePlaceMap = new HashMap();

        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        //fetch data
        try {
            if(!googlePlaceJSON.isNull("name")){
                placeName = googlePlaceJSON.getString("name");
            }

            if(!googlePlaceJSON.isNull("formatted_address")){
                vicinity= googlePlaceJSON.getString("formatted_address");
            }

            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");

            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("latitude", latitude);
            googlePlaceMap.put("longitude", longitude);
            googlePlaceMap.put("reference", reference);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }


    //get single hash map data from getPlace method
    //then store them into nearbyPlaceList
    private List<HashMap<String, String>> getAllPlaces(JSONArray jsonArray){
        int counter = jsonArray.length();

        List<HashMap<String, String>> nearbyPlaceList = new ArrayList<>();

        HashMap<String, String> nearbyPlaceMap = null;

        for( int i=0; i<counter; i++){
            try {
                nearbyPlaceMap = getPlace( (JSONObject) jsonArray.get(i) );
                nearbyPlaceList.add(nearbyPlaceMap);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearbyPlaceList;

    }

    //get all data into getAllPlaces method in list
    public List<HashMap<String, String>> parse (String jSONData){
        JSONArray jsonArray = null;


        try {
            JSONObject jsonObject = new JSONObject(jSONData);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllPlaces(jsonArray);
    }
}
