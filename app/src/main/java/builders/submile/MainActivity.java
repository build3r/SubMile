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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import builders.submile.messaging.MApp;
import builders.submile.messaging.Worker;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener
{
    final static String tag = MainActivity.class.getSimpleName();
    Boolean Is_MAP_Moveable = true; // to detect map is movable
    Button btn_draw_State,customerCall;
    FrameLayout fram_map;
    Projection projection;
    double latitude, longitude;
    ArrayList<LatLng> val;
    ArrayList<LatLng> tempval = new ArrayList<>();
    Worker w;
    int c = 0;
    PolylineOptions rectOptions;
    MarkerOptions pilotMarker;
    LatLng pilotLocation ;
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
    MarkerOptions customerMarker;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (LinearLayout) findViewById(R.id.container);
        w = Worker.getWorker(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (MApp.whoAmI() == MApp.CUSTOMER)
        {
            getLayoutInflater().inflate(R.layout.customer_layout,container);
            w.loginAsCustomer();
            customerCall = (Button) findViewById(R.id.customer_call);
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
            val = new ArrayList<>();
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
                                sendData(new customerDraw(tempval));
                                tempval.clear();
                                break;
                        }


                    }
                    return true;
                }
            });

        } else
        {
            getLayoutInflater().inflate(R.layout.driver_layout,container);
            w.loginAsPilot();
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

    public void sendData(customerDraw d)
    {
        String s = gsonUtil.tojson(d);
        w.sendMessageToPilot(s);
    }

    public void Draw_MapOnPilot(ArrayList<LatLng> tempval)
    {

        val.addAll(tempval);
        rectOptions = new PolylineOptions();
        rectOptions.addAll(val);
        rectOptions.color(Color.BLUE);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

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
                if (pilotMarker == null )
                {
                    if( ltlng!=null && mMap!=null)
                    {
                        Log.d(tag, "Creating a new PilotMarker");
                        pilotMarker = new MarkerOptions().position(ltlng).title("Gojek Ground Pilot").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
                        mMap.addMarker(pilotMarker);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(ltlng);
                        builder.include(currentLocation);
                        LatLngBounds bounds = builder.build();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,150));/*
                        mMap.moveCamera(CameraUpdateFactory.zoomBy(15));*/
                    }
                }
                else
                    pilotMarker.position(ltlng);
                Log.d(tag,"Current pilotLocation = "+ltlng.toString());
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;


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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.d(tag, "Connection to Google API suspended");
    }

    private LocationRequest createLocationRequest()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    LatLng currentLocation = new LatLng(12.9043315,77.630193);
    @Override
    public void onLocationChanged(Location location)
    {
        Log.d("Location Update", "Latitude: " + location.getLatitude() +
                " Longitude: " + location.getLongitude());
        // Add a marker in Sydney and move the camera
        if (MApp.whoAmI() == MApp.CUSTOMER)
        {
            if (customerMarker == null)
            {
                customerMarker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Mr Shabaz Ahmed").icon(BitmapDescriptorFactory.fromResource(R.mipmap.customer_marker));
                mMap.addMarker(customerMarker);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
            }
            else
                customerMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        } else
        {
            if (customerMarker == null)
            {
                if(mMap!=null)

                {
                    customerMarker = new MarkerOptions().position(new LatLng(location.getLatitude() + 0.0125215, location.getLongitude() - 0.002034)).title("Gojek Ground Pilot").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
                    mMap.addMarker(customerMarker);

                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Mr Shabaz Ahmed").icon(BitmapDescriptorFactory.fromResource(R.mipmap.customer_marker)));
                }
            }
            else
            {
                if(i==0)
                {
                    i=1;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(location.getLatitude() + 0.0125215, location.getLongitude() - 0.002034));
                    builder.include(currentLocation);
                    LatLngBounds bounds = builder.build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                }
                customerMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        }

        if (MApp.whoAmI() == MApp.DRIVER)
            sendCurrentLocation(new LatLng(location.getLatitude()+0.0125215, location.getLongitude()-0.002034));
    }
    int i=0;

    public void sendCurrentLocation(final LatLng l)
    {
        String s = gsonUtil.tojson(l);
        Log.d(tag, "Sending Locations Updates In 5 second delays");
        w.sendMessageToCustomer(s);
    }


}
