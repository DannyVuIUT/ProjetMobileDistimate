package edu.iut.m414.distimate.request;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.stream.Collectors;

public final class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();

    private HttpHandler() {
        super();
    }

    public static String executeRequest(String requestURL) {
        String result = "";

        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            result = convertInputStreamToString(conn.getInputStream());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream is) {
        String toReturn = "";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
            toReturn = in.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        return toReturn;
    }
}
