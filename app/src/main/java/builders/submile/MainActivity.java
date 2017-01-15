package builders.submile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import builders.submile.customLocation.CustomLocationService;
import builders.submile.customLocation.LocationModel;
import builders.submile.messaging.MApp;
import builders.submile.messaging.MessageData;
import builders.submile.messaging.Worker;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener
{
    final static String tag = MainActivity.class.getSimpleName();
    Boolean Is_MAP_Moveable = true; // to detect map is movable
    Button btn_draw_State, customerCall;
    ImageButton reset;
    FrameLayout fram_map;
    Projection projection;
    double latitude, longitude;
    ArrayList<LatLng> val;
    ArrayList<LatLng> tempval = new ArrayList<>();
    Worker w;
    int c = 0;
    PolylineOptions rectOptions;
    MarkerOptions pilotMarkerOptions;
    LatLng pilotLocation;
    ScheduledExecutorService worker = Executors.newScheduledThreadPool(1);
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    MarkerOptions customerMarkerOptions, driverMarkerOptions;
    Marker customerMarker, driverMarker,pilotMarker;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    protected ArrayList<Geofence> mGeofenceList;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (LinearLayout) findViewById(R.id.container);
        w = Worker.getWorker(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val = new ArrayList<>();
        if (MApp.whoAmI() == MApp.CUSTOMER)
        {
            getLayoutInflater().inflate(R.layout.customer_layout, container);
            w.loginAsCustomer();
            customerCall = (Button) findViewById(R.id.customer_call);
            reset = (ImageButton) findViewById(R.id.reset_id);
            reset.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    sendData(new MessageData("RESET", null));
                }
            });
            fram_map = (FrameLayout) findViewById(R.id.fram_map);
            btn_draw_State = (Button) findViewById(R.id.btn_draw_State);
            ((View) btn_draw_State.getParent()).setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    Log.d("Frame", "Touched");
                    return false;
                }
            });
            btn_draw_State.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    if (Is_MAP_Moveable != true)
                    {
                        Is_MAP_Moveable = true;
                    } else
                    {
                        Is_MAP_Moveable = false;
                    }
                }
            });
            fram_map.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (Is_MAP_Moveable == true)
                    {
                        return false;

                    } else
                    {

                        float x = event.getX();
                        float y = event.getY();

                        int x_co = Math.round(x);
                        int y_co = Math.round(y);

                        projection = mMap.getProjection();
                        Point x_y_points = new Point(x_co, y_co);

                        LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
                        latitude = latLng.latitude;

                        longitude = latLng.longitude;

                        int eventaction = event.getAction();
                        c++;
                        switch (eventaction)
                        {
                            case MotionEvent.ACTION_DOWN:
                                // finger touches the screen
                                tempval.add(new LatLng(latitude, longitude));

                            case MotionEvent.ACTION_MOVE:
                                // finger moves on the screen
                                tempval.add(new LatLng(latitude, longitude));

                            case MotionEvent.ACTION_UP:
                                // finger leaves the screen
                                Log.d(tag, "Count = " + c);
                                Draw_Map(tempval);
                                Log.d("Sending",tempval.toString());
                                sendData(new MessageData("DRAW", tempval));
                                tempval.clear();
                                break;
                        }


                    }
                    return true;
                }
            });

        } else
        {
            getLayoutInflater().inflate(R.layout.driver_layout, container);
            w.loginAsPilot();
            ((Button)findViewById(R.id.delivered_btn_id)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String jsonString = gsonUtil.tojson(new LocationModel("9972115447","SAVED","Gojek,4th, floor, diamond district, Bangalore - 560018",val));
                    Log.d("JSON String ",jsonString);

                }
            });
            ((Button)findViewById(R.id.call_btn)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    (new CustomLocationService()).getLocation(MainActivity.this,"9972115447");

                }
            });
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        this.buildGoogleApiClient();
        mGoogleApiClient.connect();
        mapFragment.getMapAsync(this);
    }

    private synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this).addApi(LocationServices.API)
                .build();
    }

    public void Draw_Map(ArrayList<LatLng> tempval)
    {
        val.addAll(tempval);
        rectOptions = new PolylineOptions();
        rectOptions.addAll(val);
        rectOptions.color(Color.BLUE);
        mMap.addPolyline(rectOptions);

        //Log.d("Draw Map",polygon.toString());


    }

    public void sendData(MessageData d)
    {
        String s = gsonUtil.tojson(d);
        w.sendMessageToPilot(s);
    }

    public void Draw_MapOnPilot(final ArrayList<LatLng> tempval)
    {


        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                val.addAll(tempval);
                rectOptions = new PolylineOptions();
                rectOptions.addAll(val);
                rectOptions.color(Color.BLUE);
                mMap.addPolyline(rectOptions);
            }
        });

        //Log.d("Draw Map",polygon.toString());


    }

    public void updatePilotLocation(final LatLng ltlng)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (pilotMarkerOptions == null)
                {
                    if (ltlng != null && mMap != null)
                    {
                        Log.d(tag, "Creating a new PilotMarker");
                        pilotMarkerOptions = new MarkerOptions().position(ltlng).title("Gojek Ground Pilot").icon(BitmapDescriptorFactory.fromResource(R.mipmap.driver_marker));
                        pilotMarker = mMap.addMarker(pilotMarkerOptions);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(ltlng);
                        builder.include(currentLocation);
                        LatLngBounds bounds = builder.build();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));/*
                        mMap.moveCamera(CameraUpdateFactory.zoomBy(15));*/
                    }
                } else
                {
                    pilotMarker.setPosition(ltlng);

                }
                Log.d(tag, "Current pilotLocation = " + ltlng.toString());
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng t;
        if(location == null)
        {

                t = currentLocation;
        }
        else
           t = new LatLng(location.getLatitude(), location.getLongitude());
        if(MApp.CUSTOMER == MApp.whoAmI())
        {
            customerMarkerOptions = new MarkerOptions().position(t).title("Mr Shabaz Ahmed").icon(BitmapDescriptorFactory.fromResource(R.mipmap.customer_marker));
            customerMarker = mMap.addMarker(customerMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t, 17f));
        }
        else
        {
            double lat = 12.959399;
            double lng = 77.643374;
            customerMarkerOptions = new MarkerOptions().position(new LatLng(lat, lng)).title("Mr Shabaz Ahmed").icon(BitmapDescriptorFactory.fromResource(R.mipmap.customer_marker));
            customerMarker =  mMap.addMarker(customerMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16f));
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId("Diamond District")

                    .setCircularRegion(
                            lat,
                            lng,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        LocationRequest mLocationRequest = createLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.d(tag, "Connection to Google API suspended");
    }

    private LocationRequest createLocationRequest()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    LatLng currentLocation = new LatLng(12.959977, 77.644028);

    @Override
    protected void onStop()
    {
        super.onStop();

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d("Location Update", "Latitude: " + location.getLatitude() +
                " Longitude: " + location.getLongitude());
        // Add a marker in Sydney and move the camera
        if (MApp.whoAmI() == MApp.CUSTOMER)
        {
         /*   if (customerMarkerOptions == null)
            {
                customerMarkerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Mr Shabaz Ahmed").icon(BitmapDescriptorFactory.fromResource(R.mipmap.customer_marker));
                mMap.addMarker(customerMarkerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
            }
            else
                customerMarkerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));*/
        } else
        {

            sendCurrentLocation(new LatLng(location.getLatitude() , location.getLongitude()));
            if (driverMarkerOptions == null)
            {
                if(mMap!=null)

                {
                    driverMarkerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.driver_marker));
                    driverMarker = mMap.addMarker(driverMarkerOptions);

                }
            }
            else
            {
                driverMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
/*                if(i==0)
                {
                    i=1;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                    builder.include(currentLocation);
                    LatLngBounds bounds = builder.build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                }
                customerMarkerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));*/
            }
        }

    }
    int i=0;

    public void sendCurrentLocation(final LatLng l)
    {
        ArrayList<LatLng> t = new ArrayList<LatLng>();
        t.add(l);
        MessageData d = new MessageData("CURRENT_LOCATION",t);
        String s = gsonUtil.tojson(d);
        Log.d(tag, "Sending Locations Updates In 5 second delays");
        w.sendMessageToCustomer(s);
    }


    public void resetMAP()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                mMap.addPolyline(new PolylineOptions());
                val.clear();
            }
        });

    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }
}
