package io.hpp.whoismyrepresentative;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class for handling the http request and printing out the data
 *  associated with the whoismyrepresentative.com api
 */
public class GetMyRep {
    final String TAG = "io.hpp.GetMyRep"; //error TAG for GetMyRep Class

    WhoIsMyRepActivity mainActivity; //reference to mainActivity
    String dialogTitle; //the title for the response dialog box.



    /**
     * Constructor for GetMyRep class
     * @param main a reference of the main WhoIsMyRepActivity
     */
    public GetMyRep(WhoIsMyRepActivity main){
        mainActivity = main;

    }

/*=============================/
        Public Methods
/=============================*/

    /**
     * Search for Representatives and Senators by ZIP code
     * @param zip 5 digit number corresponding to the users postal ZIP code.
     */
    public void byZipCode(String zip){
        // construct the appropriate Title for dialog box
        dialogTitle = mainActivity.getString(R.string.reps4zip) + " " + zip;

        // send request with correct url
        new SendRequestTask().execute("http://whoismyrepresentative.com/getall_mems.php?zip=" + zip);
    }

    /**
     * Search for Representatives by state abbreviation
     * @param state two letter abbreviation
     */
    public void repsByState(String state){
        // construct the appropriate Title for dialog box
        dialogTitle = mainActivity.getString(R.string.reps4state) + " " + state;

        // send request with correct url
        new SendRequestTask().execute("http://whoismyrepresentative.com/getall_reps_bystate.php?state=" + state);
    }

    /**
     * Search for Representatives by last name
     * @param name last name of representative
     */
    public void repsByName(String name){
        // construct the appropriate Title for dialog box
        dialogTitle = mainActivity.getString(R.string.reps4name) + " " + name;

        // send request with correct url
        new SendRequestTask().execute("http://whoismyrepresentative.com/getall_reps_byname.php?name=" + name);
    }

    /**
     * Search for Senators by state abbreviation
     * @param state two letter abbreviation
     */
    public void senatorsByState(String state){
        // construct the appropriate Title for dialog box
        dialogTitle = mainActivity.getString(R.string.sentors4state) + " " + state;

        // send request with correct url
        new SendRequestTask().execute("http://whoismyrepresentative.com/getall_sens_bystate.php?state=" + state);
    }

    /**
     * Search for Senators by last name
     * @param name last name of Senator
     */
    public void senatorsByName(String name){
        // construct the appropriate Title for dialog box
        dialogTitle = mainActivity.getString(R.string.sentors4name) + " " + name;

        // send request with correct url
        new SendRequestTask().execute("http://whoismyrepresentative.com/getall_sens_byname.php?name=" + name);
    }

/*=============================/
        Private Methods
/=============================*/

    /**
     * Asyncronous task for sending request in backgroud
     */
    private class SendRequestTask extends AsyncTask<String, String, String>{

        /**
         * the background task launches send request with url
         * @param string the url in string form
         * @return the parsed response from server for dialog
         */
        protected String doInBackground(String... string){
            String urlString = string[0];
            return sendRequest(urlString);
        }

        /**
         * After background task completes posts message to dialog on main thread
         * @param message the message to post to dialog
         */
        protected void onPostExecute(String message){
            // Send the accumulated message from the xml parser to user via dialog box.
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle(dialogTitle)
                    .setMessage(message)
                            // only one button that cancels dialog
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * Handles sending request to given url and parsing results.
     * @param urlString the url to query
     * @return the parsed response from server
     */
    private String sendRequest(String urlString){
        String response = "";
        URL url;
        HttpURLConnection connection = null;

        // open connection and parse response.
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = parseXML(in);

            // handle exceptions
        } catch (MalformedURLException e) {
            response = reportError(e.toString() + e.getStackTrace());

        } catch (IOException e) {
            response = reportError(e.toString() + e.getStackTrace());

        } finally {
            if (connection!=null)
                connection.disconnect();
        }
        return response;
    }


    /**
     * Parses xml response
     * @param input the response from WhoIsMyRepresentative.com
     * @throws IOException
     * @return the parsed response from server
     */
    private String parseXML(InputStream input) throws IOException{
        String response = "";
        try {
            // create a new xmlParser and read the input feed
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(input, null);
            parser.nextTag();
            response = readFeed(parser);
        } catch (XmlPullParserException e) {
            response = reportError(e.toString() + e.getStackTrace());
        } finally {
            input.close();
        }
        return response;
    }

    /**
     * Read the xml feed from WhoIsMyRepresentative.com
     * @param parser the xmlPullParser loaded with the input xml feed response from server.
     * @throws XmlPullParserException
     * @throws IOException
     * @return the parsed response from server
     */
    private String readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        // message for dialog box
        String message = "";

        // parse xml one field at a time.
        parser.require(XmlPullParser.START_TAG, null, null);
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            // ignore start tags
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // ignore result tags
            if (!name.equals("result")){

                // add each attribute name and value pair to message.
                int count = parser.getAttributeCount();
                for (int i = 0; i < count; i++){
                    if (parser.getAttributeName(i).equals("name")) message += "\n";
                    message += parser.getAttributeName(i) + "=" +
                            parser.getAttributeValue(i) + "\n";
                }


            }
        }

        // Check for no reponse and let user know no data was found
        if (message.equals("")){
            message = mainActivity.getString(R.string.no_reponse);
        }

        return message;

    }

    /**
     * Send an error message and stack trace to logcat.
     * @param errorNtrace error message and stack trace from error
     * @return the message to put in error dialog
     */
    private String reportError(String errorNtrace){
        // send error and stack trace to logCat
        Log.e(TAG, errorNtrace);

        // change dialog title to error
        dialogTitle = mainActivity.getString(R.string.error_title);
        return mainActivity.getString(R.string.error_message);
    }
}




