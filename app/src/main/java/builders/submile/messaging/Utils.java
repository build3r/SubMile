package builders.submile.messaging;

import android.app.Activity;

import com.quickblox.core.QBSettings;

/**
 * Created by dpallagolla on 5/14/2016.
 */
public class Utils {

    public void initCredentials(Activity obj, String APP_ID, String AUTH_KEY, String AUTH_SECRET, String ACCOUNT_KEY) {
        QBSettings.getInstance().init(obj.getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }

}
