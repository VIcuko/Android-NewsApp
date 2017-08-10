package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.MainActivity.LOG_TAG;

/**
 * Created by Vicuko on 10/8/17.
 */

public class QueryUtils {

    //An empty private constructor makes sure that the class is not going to be initialised.
    private QueryUtils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        final int READ_TIMEOUT = 10000;
        final int CONNECT_TIMEOUT = 15000;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<News> extractNews(String newsJSON) {

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        String webTitle;
        String sectionName;
        String webPublicationDate;
        ArrayList<String> authors = new ArrayList<String>();
        String webUrl;

        ArrayList<News> news = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(newsJSON);

            JSONObject response = jsonObj.getJSONObject("response");

            JSONArray results = response.has("results") ? response.getJSONArray("results") : null;

            if (results != null) {
                for (int i = 0; i < results.length(); i++) {

                    JSONObject article = results.getJSONObject(i);
                    webTitle = article.has("webTitle") ? article.getString("webTitle") : "";
                    sectionName = article.has("sectionName") ? article.getString("sectionName") : "";
                    webPublicationDate = article.has("webPublicationDate") ? article.getString("webPublicationDate").substring(0, 10) : "";
                    webUrl = article.has("webUrl") ? article.getString("webUrl") : "";

                    JSONArray tags = article.has("tags") ? article.getJSONArray("tags") : null;

                    if (tags != null) {
                        for (int j = 0; j < tags.length(); j++) {
                            JSONObject tag = tags.getJSONObject(j);
                            String author = tag.has("webTitle") ? tag.getString("webTitle") : null;
                            if (author != null) {
                                authors.add(author);
                            }
                        }
                    }

                    News oneNews = new News(webTitle, authors, sectionName, webPublicationDate, webUrl);
                    news.add(oneNews);
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        return news;
    }

    public static List<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractNews(jsonResponse);
    }

}
