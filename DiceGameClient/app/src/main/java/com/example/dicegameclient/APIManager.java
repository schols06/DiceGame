package com.example.dicegameclient;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Luc on 18-12-2015.
 */
public class APIManager extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
        String urlString=params[0];
        String resultToDisplay;
        scoreResult result = null;
        InputStream in = null;

        // HTTP Get
        try {
            URL url = new URL(urlString);
            System.out.println("URL: " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e ) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        // Parse XML
        XmlPullParserFactory pullParserFactory;

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            result = parseXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Simple logic to determine if the email is dangerous, invalid, or valid
        if (result != null ) {
            resultToDisplay = result.toString();
            System.out.println("Result APIManager: " + resultToDisplay);

        }
        else {
            resultToDisplay = "Exception Occured";
        }

        return resultToDisplay;
    }

    protected void onPostExecute(String result) {

        SessionManager.getInstance().result = result;
        System.out.println("Result: " + result);
    }

    private scoreResult parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {

        int eventType = parser.getEventType();
        scoreResult result = new scoreResult();

        while( eventType!= XmlPullParser.END_DOCUMENT) {
            String name = null;

            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    name = parser.getName();

                    if( name.equals("Error")) {
                        System.out.println("Web API Error!");
                    }
                    else if ( name.equals("scoreId")) {
                        result.scoreId = parser.nextText();
                    }
                    else if ( name.equals("androidId")) {
                        result.androidId = parser.nextText();
                    }
                    else if ( name.equals("location")) {
                        result.location = parser.nextText();
                    }
                    else if ( name.equals("value")) {
                        result.value = parser.nextText();
                    }
                    else if ( name.equals("timestamp")) {
                        result.timestamp = parser.nextText();
                    }

                    break;

                case XmlPullParser.END_TAG:
                    break;
            } // end switch

            eventType = parser.next();
        } // end while

        return result;
    }

} // end CallAPI