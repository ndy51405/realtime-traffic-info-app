package tw.com.zenii.realtime_traffic_info_app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    static List<LatLng> stopPositions;
    static List<LatLng> busPositions;
    static Map<LatLng, String> plateNumb;
    static Map<LatLng, String> stopName;
    static Map<LatLng, Integer> azimuth;
    static Map<String, String> nearStop;
    static Map<String, String> RouteName;
    public static String subRouteId;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private String bundleSubRouteId;
    private boolean first = true;
    ScheduledExecutorService executorService;
    boolean once = true; // 只執行一次
    int DEFAULT_ZOOM = 10;
    private String trackNearStop;
    private String trackPlateNumb;
    private String trackBusStatus = "客滿";
    private String trackA2EventType = "離站";
    private String trackRouteName = "9001";
    private TrackerDB helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // 處理 Thread
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Bundle
        bundleSubRouteId = getIntent().getExtras().getString("bndSubRouteId");
        Log.d("bndSubRouteId", bundleSubRouteId + "");
        subRouteId = bundleSubRouteId;

        // 定時抓資料
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new MapsAsync().execute();
                new TabsAsync().execute();
                Log.d("Time", new Date().toString());
            }
        }, 0, 10, TimeUnit.SECONDS);

    }

    class MapsAsync extends AsyncTask<Void, Void, Void> implements OnMapReadyCallback {

        String mapSubRouteId;

        @Override
        protected Void doInBackground(Void... voids) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (subRouteId.length() != 6) {
                        mapSubRouteId = bundleSubRouteId + "01";
                    } else {
                        mapSubRouteId = subRouteId;
                    }

                    Log.d("mapSubRouteId", mapSubRouteId);
                    // 設定 Map
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);

                    // 測試地圖
                    mapFragment.getMapAsync(MapsAsync.this);
                }
            });

            return null;
        }

        // 設定地圖
        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    stopPositions = InterCityBusHandler.getStopPosition(mapSubRouteId);
                    stopName = InterCityBusHandler.getStopName(mapSubRouteId);
                    busPositions = InterCityBusHandler.getBusPosition(mapSubRouteId);
                    plateNumb = InterCityBusHandler.getPlateNumb(mapSubRouteId);
                    azimuth = InterCityBusHandler.getAzimuth(mapSubRouteId);

                    runOnUiThread(new Runnable() {

                        Marker[] busMarkerList = new Marker[busPositions.size()];

                        @Override
                        public void run() {
                            mMap.clear();

                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.style_json
                            ));

                            for (int i = 0; i < stopPositions.size() - 1; i++) {

                                // 劃線的地方
                                mMap.addPolyline(new PolylineOptions()
                                        .addAll(stopPositions)
                                        .color(Color.rgb(91, 142, 125))
                                        .width(7))
                                ;

                                if (once) {
                                    // 鏡頭初始位置
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(stopPositions.get(0).latitude,
                                            stopPositions.get(0).longitude), DEFAULT_ZOOM));
                                    once = false;
                                }
                            }

                            for (int i = 0; i < stopPositions.size(); i++) {

                                // Marker
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(stopPositions.get(i).latitude,
                                                stopPositions.get(i).longitude))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop))
                                        .title(stopName.get(stopPositions.get(i))));

                            }

                            for (int i = 0; i < busPositions.size(); i++) {

                                // 測試
                                Log.d("Lat", "" + busPositions.get(i).latitude);
                                Log.d("Lng", "" + busPositions.get(i).longitude);
                                Log.d("PlateNumb", "" + plateNumb.get(busPositions.get(i)));

                                // Marker
                                busMarkerList[i] = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(busPositions.get(i).latitude,
                                                busPositions.get(i).longitude))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                                        .title(plateNumb.get(busPositions.get(i))));
                                        //.rotation(azimuth.get(busPositions.get(i))));

                            }

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    for (int i = 0; i < busPositions.size(); i++) {
                                        if (marker.equals(busMarkerList[i])) {
                                            trackPlateNumb = plateNumb.get(busPositions.get(i));

                                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                            builder.setTitle("是否追蹤此車？")
                                                    .setMessage("這台車牌是：" + trackPlateNumb)
                                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Log.d("trackPlateNumb", trackPlateNumb+"null");
                                                            nearStop = InterCityBusHandler.getNearStop(trackPlateNumb);
                                                            RouteName = InterCityBusHandler.getSubRouteName(trackPlateNumb);

                                                            trackNearStop = nearStop.get(trackPlateNumb);
                                                            trackRouteName = RouteName.get(trackPlateNumb);

                                                            Log.d("trackNearStop", trackNearStop+"");
                                                            Log.d("trackSubRouteName", trackRouteName+"");

                                                            helper = new TrackerDB(MapsActivity.this, "Tracker", null, 1);
                                                            ContentValues values = new ContentValues();
                                                            values.put("nearStop", trackNearStop);
                                                            values.put("plateNumb", trackPlateNumb);
                                                            values.put("busStatus", trackBusStatus);
                                                            values.put("a2EventType", trackA2EventType);
                                                            values.put("routeName", trackRouteName);
                                                            long id = helper.getWritableDatabase().insert("tracker", null, values);
                                                            Log.d("ADD", id+"");
                                                            Intent intent = new Intent(MapsActivity.this, NavInterCityBus.class);
                                                            intent.putExtra("id",1);
                                                            startActivity(intent);

                                                        }
                                                    })
                                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            isCancelled();
                                                        }
                                                    });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    }

                                    return false;
                                }
                            });

                        }
                    });
                }
            };
            new Thread(runnable).start();
        }
    }

    class TabsAsync extends AsyncTask<Void, Void, Void> implements OnMapReadyCallback {

        @Override
        protected Void doInBackground(Void... voids) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    subRouteId = bundleSubRouteId;
                    // 設定 ViewPager
                    mViewPager = findViewById(R.id.container);
                    mViewPager.addOnPageChangeListener(OnPageChangeListener);

                    setupViewPager(mViewPager);
                    // 設定 Tabs
                    mTabLayout = findViewById(R.id.tabs);
                    mTabLayout.setupWithViewPager(mViewPager);
                }
            });
            return null;
        }

        // 設定 ViewPager
        private void setupViewPager(ViewPager viewPager) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new GoFragment(), getString(R.string.go));
            adapter.addFragment(new BackFragment(), getString(R.string.back));

            //mViewPager.setCurrentItem(0);
            viewPager.setAdapter(adapter);
        }

        private OnPageChangeListener OnPageChangeListener = new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (first && positionOffset == 0 && positionOffsetPixels == 0) {
                    onPageSelected(0);
                    first = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        subRouteId = bundleSubRouteId + "01";
                        Log.d("onPageSelected", subRouteId);
                        break;
                    case 1:
                        subRouteId = bundleSubRouteId + "02";
                        Log.d("onPageSelected", subRouteId);
                        break;
                }
                // 更新 Map
                mapFragment.getMapAsync(TabsAsync.this);
                once = true;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        // 設定地圖
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    stopPositions = InterCityBusHandler.getStopPosition(subRouteId);
                    stopName = InterCityBusHandler.getStopName(subRouteId);
                    busPositions = InterCityBusHandler.getBusPosition(subRouteId);
                    plateNumb = InterCityBusHandler.getPlateNumb(subRouteId);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.clear();

                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.style_json
                            ));

                            for (int i = 0; i < stopPositions.size() - 1; i++) {

                                // 劃線的地方
                                mMap.addPolyline(new PolylineOptions()
                                        .addAll(stopPositions)
                                        .color(Color.rgb(91, 142, 125))
                                        .width(7));

                                if (once) {
                                    // 鏡頭初始位置
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(stopPositions.get(0).latitude,
                                            stopPositions.get(0).longitude), DEFAULT_ZOOM));
                                    once = false;
                                }
                            }

                            for (int i = 0; i < stopPositions.size(); i++) {

                                // Marker
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(stopPositions.get(i).latitude,
                                                stopPositions.get(i).longitude))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop))
                                        .title(stopName.get(stopPositions.get(i))));

                            }

                            for (int i = 0; i < busPositions.size(); i++) {

                                // 測試
                                Log.d("Lat", "" + busPositions.get(i).latitude);
                                Log.d("Lng", "" + busPositions.get(i).longitude);
                                Log.d("PlateNumb", "" + plateNumb.get(busPositions.get(i)));

                                // Marker
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(busPositions.get(i).latitude,
                                                busPositions.get(i).longitude))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                                        .title(plateNumb.get(busPositions.get(i))));

                            }
                        }
                    });
                }
            };
            new Thread(runnable).start();
        }

    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
