package builders.submile;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Shabaz on 01/14/2017
 */
public class MessageData
{

    public ArrayList<LatLng> ltlng;
    public String TYPE;

    public MessageData(String TYPE, ArrayList<LatLng> ltlng)
    {
        this.TYPE = TYPE;
        this.ltlng = ltlng;
    }
}
