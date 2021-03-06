package edu.iut.m414.distimate.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CountryList {
    //-------------------------------------------------------------------
    // Pattern Singleton : Instance unique
    // Toutes les méthodes manipulant l'instance unique sont statiques
    //-------------------------------------------------------------------
    private static CountryList instance;
    private final List<Country> countries;

    private CountryList() {
        countries = new ArrayList<>();
    }

    public static Country get(int position) {
        createInstance();
        return instance.countries.get(position);
    }

    public static int size() {
        createInstance();
        return instance.countries.size();
    }

    /**
     * -----------------------------------------------------------------
     * Singleton : création de l'instance unique
     * -----------------------------------------------------------------
     */
    private static void createInstance() {
        if (instance == null)
            instance = new CountryList();
    }

    /**
     * -----------------------------------------------------------------
     * Chargement d'un fichier JSON pour remplir l'instance CountryList
     * -----------------------------------------------------------------
     * @param context - contexte
     */
    public static void constructCountries(Context context) {
        createInstance();
        if (size() == 0) {
            try {
                CountryList currentInstance = instance;
                JSONArray countryArray = new JSONArray(getJSONFromAssets(context));
                for (int i = 0; i < countryArray.length(); i++) {
                    currentInstance.countries.add(getCountryFromJSONObject(countryArray.getJSONObject(i), context));
                }
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static String getJSONFromAssets(Context context) {
        String jsonContent;
        try {
            InputStream inputStream = context.getAssets().open("countries.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonContent = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        return jsonContent;
    }

    private static Country getCountryFromJSONObject(JSONObject jsonObject, Context context) throws JSONException {
        String id = jsonObject.getString("id");
        int nameId = context.getResources().getIdentifier(jsonObject.getString("name"), "string", context.getPackageName());
        int citiesCount = jsonObject.getInt("citiesCount");
        int area = jsonObject.getInt("area");
        String flagName = "flag_" + id.toLowerCase();
        int flagFile = context.getResources().getIdentifier(flagName, "drawable", context.getPackageName());
        String imageName = "image_" + id.toLowerCase();
        int imageFile = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        int baseDistance = jsonObject.getInt("baseDistance");
        return new Country(id, nameId, citiesCount, area, flagFile, imageFile, baseDistance);
    }
}
