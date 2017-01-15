package builders.submile.customLocation;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import builders.submile.MainActivity;
import builders.submile.gsonUtil;

/**
 * Created by Shabaz on 15-Jan-17.
 */

public class CustomLocationService
{
    MainActivity activity;

    public void sendLocation(String jsonString)
    {

    }
    public void getLocation(MainActivity activity, String userId)
    {
        this.activity = activity;
        (new GetData()).execute(userId);

    }

    class GetData extends AsyncTask<String,Void,LocationModel>
    {
        @Override
        protected void onPostExecute(LocationModel locationModel)
        {
            super.onPostExecute(locationModel);
            activity.Draw_MapOnPilot(locationModel.ltlng);
        }

        @Override
        protected LocationModel doInBackground(String... strings)
        {
            URL _url;
            HttpURLConnection urlConnection;
            BufferedInputStream is;
            String output;
            LocationModel m = null;
            try {

                _url = new URL("http://172.16.10.144:8080/users/"+strings[0]);
                urlConnection = (HttpURLConnection) _url.openConnection();
            }
            catch (MalformedURLException e) {
                Log.e("JSON Parser", "Error due to a malformed URL " + e.toString());
                return null;
            }
            catch (IOException e) {
                Log.e("JSON Parser", "IO error " + e.toString());
                return null;
            }

            try {
                is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder(is.available());
                String line;
                while ((line = reader.readLine()) != null) {
                    total.append(line).append('\n');
                }
                output = total.toString();
            }
            catch (IOException e) {
                Log.e("JSON Parser", "IO error " + e.toString());
                return null;
            }
            finally{
                urlConnection.disconnect();
            }

            try {

                 m = (LocationModel) gsonUtil.tojava(output,LocationModel.class);
            }
            catch (Exception e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            return m;
        }
    }
}
