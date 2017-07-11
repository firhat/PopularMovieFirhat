package com.firhat.popularmoviefirhat.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    final static String API_KEY = "YOUR API KEY HERE";
    final static String BASE_URL ="https://api.themoviedb.org/3/movie/";
    /**
     * Sample request
     * https://api.themoviedb.org/3/movie/550?api_key=e076af695021370624df08e938299210
     * */

    public static URL buildUrl(String sortBy) {
        Uri builtUri = Uri.parse(BASE_URL+sortBy+"?api_key="+API_KEY+"&language=en-US&page=1")
                .buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
