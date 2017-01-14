package builders.submile.messaging;

import android.app.Application;
import android.content.Context;

import com.quickblox.core.QBSettings;

/**
 * Created by Shabaz on 01/14/2017
 */
public class MApp extends Application
{

    public static final boolean CUSTOMER =true;
    public static final boolean DRIVER =false;
    public static Context ctx;
    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        QBSettings.getInstance().init(this, Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Constants.ACCOUNT_KEY);
        /*//who = CUSTOMER;
        who = DRIVER; //Means Driver*/
    }
    public static boolean whoAmI()
    {
        return DRIVER;
    }
}
