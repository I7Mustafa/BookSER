package com.norina.agenda;

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

public class Utils
{
    private static String title , author , publisher , date, description , thumbnail;
    private static final String LOG_TAG = Utils.class.getSimpleName();

    public static ArrayList<Data> fetchBooksData(String requestUrl)
    {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        try
        {
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Error closing input stream ",e);
        }

        ArrayList<Data> book = extractFeatureFromJson(jsonResponse);

        return book;
    }


    private static URL createUrl(String stringUrl)
    {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest (URL url) throws IOException
    {
        String jsonRequst = "";

        if (url == null)
            return jsonRequst;

        HttpURLConnection urlConnection = null;

        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200)
            {
                inputStream = urlConnection.getInputStream();

                jsonRequst = readFromStream(inputStream);
            }
            else
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON result ." +e);
        }

        finally
        {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (inputStream != null)
                inputStream.close();
        }

        return jsonRequst;
    }

    private static String readFromStream (InputStream inputStream) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();

        if (inputStream != null)
        {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();

            while (line != null)
            {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    private static ArrayList<Data> extractFeatureFromJson (String booksJSON)
    {
        if (TextUtils.isEmpty(booksJSON))
            return null;


        ArrayList<Data> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0 ; i <itemsArray.length(); i++)
            {
                JSONObject firstItem = itemsArray.getJSONObject(i);

                JSONObject info = firstItem.getJSONObject("volumeInfo");

                if (info.has("title"))
                    title = info.getString("title");
                else
                    title = " Title not found";

                if (info.has("authors"))
                    author = info.getJSONArray("authors").getString(0);
                else
                    author = "Author Not Found";

                if (info.has("publisher"))
                    publisher = info.getString("publisher");
                else
                    publisher = "publisher not found";

                if (info.has("publisherDate"))
                    date = info.getString("publisherDate");
                else
                    date = "Publisher Date not found";

                if (info.has("description"))
                    description = info.getString("description");
                else
                    description = "Description Not Found";

                if (info.has("imageLinks"))
                {
                    JSONObject imageurl = info.getJSONObject("imageLinks");
                    thumbnail = imageurl.getString("thumbnail");
                }
                else
                {
                    thumbnail = "";
                }
                books.add(new Data(title,author,publisher,date,description,thumbnail));
            }
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON result ",e);
        }
        return books;
    }
}
