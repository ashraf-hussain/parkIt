package com.project.parkit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.parkit.common.SetupRetrofit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends FragmentActivity implements
        OnMapReadyCallback, LocationListener {

    private static final int REQUEST_LOCATION = 101;
    LocationManager locationManager;
    LatLng userLocation;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_max_time)
    TextView tvTime;
    @BindView(R.id.tv_reserve_time)
    TextView tvReserveTime;
    @BindView(R.id.tv_reserve_remaining)
    TextView tvReserveRemaining;
    @BindView(R.id.sb_time)
    SeekBar sbTime;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.btn_search_down)
    ImageButton btnSearchDown;
    @BindView(R.id.cv_search)
    CardView cvSearch;
    @BindView(R.id.ll_location)
    LinearLayout llLocation;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_date)
    LinearLayout llDate;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    List<ParkingModel> parkingModelList;


    ParkingModel parkingModel;
    private GoogleMap mMap;
    private String lat, lng;
    private Calendar calendar;
    private Location location;
    private int mHour, mMinute;
    private String reserveTime, reserveDate;
    private DatePickerDialog.OnDateSetListener date;
    private static final String TAG = MainActivity.class.getName();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_activity);
        ButterKnife.bind(this);
        calendar = Calendar.getInstance();
        notificationBarSetup();
        datePickerAction();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Check Permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            Toast.makeText(this, "Not Granted", Toast.LENGTH_SHORT).show();


        } else {
            // permission has been granted, continue as usual
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
        }


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        getLastLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setMinZoomPreference(5);

    }

    protected Marker createMarker(double latitude,
                                  double longitude,
                                  String title,
                                  int minTime,
                                  int maxTime,
                                  String costPerMin,
                                  int iconResID) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(bitmapDescriptorFromVector(this,
                        iconResID)));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }

    //Checks GPS status
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //This is were magic happens!
    @Override
    public void onLocationChanged(Location location) {
        //remove location callback:
        locationManager.removeUpdates(this);

        //open the map:
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:"
                    + longitude, LENGTH_SHORT).show();
            lat = String.valueOf(latitude);
            lng = String.valueOf(longitude);
            Log.d(TAG, "latitude:" + latitude + " longitude:" + longitude);


        } else {
            Toast.makeText(MainActivity.this, "Null", LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    //NotificationBar setup
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void notificationBarSetup() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    //TimePicker
    private void timePickerAction() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        tvTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    //DatePicker
    private void datePickerAction() {
        //DatePicker
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

            private void updateDate() {
                String dateFormat = "MM-dd-yy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                tvDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
    }

    //Processing vector
    private BitmapDescriptor
    bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context,
                vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(),
                background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat
                .getDrawable(this, vectorDrawableResourceId);
        vectorDrawable.setBounds(80, 30,
                vectorDrawable.getIntrinsicWidth() + 40,
                vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(),
                background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    //Click events
    @OnClick({R.id.ll_location, R.id.ll_date, R.id.ll_time, R.id.btn_search, R.id.btn_search_down})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_location:
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else {
                    onLocationChanged(location);
                    tvLocation.setText("Lat: " + location.getLatitude() + "\n" + "Lng: " + location.getLongitude());
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 18);

                    mMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .title("Current Position")
                            .icon(bitmapDescriptorFromVector(this, R.drawable.ic_person_black_24dp))
                    );
                    mMap.moveCamera(cameraUpdate);
                }
                break;
            case R.id.ll_date:
                new DatePickerDialog(MainActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.ll_time:
                timePickerAction();
                break;
            case R.id.btn_search:
                getSearchData();
                cvSearch.setVisibility(View.GONE);
                break;
            case R.id.btn_search_down:
                if (cvSearch.getVisibility() == View.VISIBLE) {
                    // Its visible
                    cvSearch.setVisibility(View.GONE);
                } else {
                    // Either gone or invisible
                    cvSearch.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    private void getSearchData() {
        SetupRetrofit setupRetrofit = new SetupRetrofit();
        Retrofit retrofit = setupRetrofit.getRetrofit();

        ParkingApiInterface musicApiInterface = retrofit.create(ParkingApiInterface.class);
        musicApiInterface.getSearchData(Double.toString(location.getLatitude()),
                Double.toString(location.getLongitude())).enqueue(new Callback<List<ParkingModel>>() {

            @Override
            public void onResponse(Call<List<ParkingModel>> call, Response<List<ParkingModel>> response) {
                Log.d(TAG, String.valueOf(response.code()));
                Log.d(TAG, String.valueOf(response.raw().request().url()));
                Log.d(TAG, Double.toString(location.getLatitude()) + "check " + Double.toString(location.getLongitude()));

                parkingModelList = response.body();

                Log.d(TAG, String.valueOf(parkingModelList.size()));

                for (int i = 0; i < parkingModelList.size(); i++) {
                    parkingModel = new ParkingModel();

                    createMarker(Double.parseDouble(parkingModelList.get(i).getLat()),
                            Double.parseDouble(parkingModelList.get(i).getLng()),
                            parkingModelList.get(i).getName(),
                            parkingModelList.get(i).getMinReserveTimeMins(),
                            parkingModelList.get(i).getMaxReserveTimeMins(),
                            parkingModelList.get(i).getCostPerMinute(),
                            R.drawable.ic_radio_button_checked_black_24dp);

                    parkingModel.setName("sadas");


                    Log.d(TAG, parkingModelList.get(i).getCostPerMinute() + "GOT" +
                            parkingModelList.get(i).getName());
                    MyInfoWindowAdapter customInfoWindow = new
                            MyInfoWindowAdapter(MainActivity.this, parkingModelList);
                    mMap.setInfoWindowAdapter(customInfoWindow);

                }



            }

            @Override
            public void onFailure(Call<List<ParkingModel>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Sth wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
