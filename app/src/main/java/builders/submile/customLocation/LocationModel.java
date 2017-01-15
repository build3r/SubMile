package builders.submile.customLocation;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Shabaz on 15-Jan-17.
 */

public class LocationModel
{
    String user_id,type,address;
    public ArrayList<LatLng> ltlng;

    public LocationModel(String user_id, String type, String address, ArrayList<LatLng> ltlng)
    {
        this.user_id = user_id;
        this.type = type;
        this.address = address;
        this.ltlng = ltlng;
    }

}
