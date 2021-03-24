package edu.iut.m414.distimate.request;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import edu.iut.m414.distimate.data.City;

/**
 * ########################################################################
 * Classe permettant d'accéder au service web GeoDB.
 * Le service renvoie des données en JSON.
 * ------------------------------------------------------------------------
 * Le service est utilisé pour récupérer des villes du pays de la partie.
 * Les noms de villes sont récupérés dans la langue désirée.
 *
 * Le service est aussi utilisé pour calculer les distances pour les
 * questions du jeu.
 * ########################################################################
 */
public final class GeoDB {
    private static final String TAG = GeoDB.class.getSimpleName();
    private static final String MAIN_LINK = "http://geodb-free-service.wirefreethought.com/v1/geo/";

    private GeoDB() {
        super();
    }

    public static City requestCity(String countryId, int cityNumber, String requestLanguage) {
        City requestedCity = null;

        try {
            String request = String.format(
                    MAIN_LINK + "cities?countryIds=%s&languageCode=%s&limit=1&offset=%s",
                    countryId,
                    requestLanguage,
                    cityNumber);

            JSONObject cityRequestJSON = new JSONObject(HttpHandler.executeRequest(request));
            JSONObject cityData = cityRequestJSON.getJSONArray("data").getJSONObject(0);
            requestedCity = new City(cityData.getInt("id"), cityData.getString("name"));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

        return requestedCity;
    }

    public static int requestDistance(int firstCityId, int secondCityId) {
        int distance = 0;

        try {
            String request = String.format(
                    MAIN_LINK + "cities/%s/distance?fromCityId=%s&distanceUnit=KM",
                    firstCityId,
                    secondCityId);

            JSONObject distanceJSON = new JSONObject(HttpHandler.executeRequest(request));
            distance = (int) Math.round(distanceJSON.getDouble("data"));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

        return distance;
    }
}
