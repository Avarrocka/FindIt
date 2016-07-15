package caiji.alexli.findit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LostIt2 extends Activity {

    private EditText mItemName, mDescription, mReward;
    private String itemName, description, reward, address;
    private double latitude, longitude;

    private String getPhoneNumber() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_it2);

        mItemName = (EditText) findViewById(R.id.editText);
        mDescription = (EditText) findViewById(R.id.editText2);
        mReward = (EditText) findViewById(R.id.editText3);

        address = getIntent().getStringExtra("address");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);


        Button submit = (Button)findViewById(R.id.button);

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                itemName = mItemName.getText().toString();
                description = mDescription.getText().toString();
                reward = mReward.getText().toString();

                // return to main activity if name and description provided (reward not necessary)
                if (!(itemName.isEmpty() || description.isEmpty())) {
                    // send itemName, description, address, latitude, longitude, reward to database
                    Log.i("send stuff to database", itemName + " " + description + " " + address + " " + latitude + " " + longitude + " " + reward);

                    String url = "http://alexli.ca/iji/filelost.php?" +
                          "name=" + URLEncoder.encode(itemName) + "&" +
                          "desc=" + URLEncoder.encode(description) + "&" +
                          "address=" + URLEncoder.encode(address) + "&" +
                          "lat=" + URLEncoder.encode("" + latitude) + "&" +
                          "lng=" + URLEncoder.encode("" + longitude) + "&" +
                          "reward=" + URLEncoder.encode(reward) + "&" +
                          "phone=" + URLEncoder.encode(getPhoneNumber()) + "&" +
                          "date=" + URLEncoder.encode((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));

                    Log.i("downloaded", download(url));

                    // then return to main activity
                    finish();
                }
            }
        });

    }

    public static String download(String url)
    {
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("Praeda",response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result= convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
            }


        } catch (Exception e) {}

        return result;
    }

    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
