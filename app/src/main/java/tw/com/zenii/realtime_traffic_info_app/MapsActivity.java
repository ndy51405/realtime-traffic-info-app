package tw.com.zenii.realtime_traffic_info_app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tw.com.zenii.realtime_traffic_info_app.tabview.BackFragment;
import tw.com.zenii.realtime_traffic_info_app.tabview.GoFragment;
import tw.com.zenii.realtime_traffic_info_app.tabview.ViewPagerAdapter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MongoRunnable mongoRunnable = new MongoRunnable();
    private Thread mongoThread = new Thread(mongoRunnable);
    List<LatLng> stopPositions;
    List<LatLng> busPositions;
    HashMap<LatLng, String> plateNumb;
    HashMap<LatLng, String> stopName;
    public static String subRouteId;
    int DEFAULT_ZOOM = 10;
    boolean once = true; // 只執行一次
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    ScheduledExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // 處理 Thread
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // 定時抓資料
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new Thread(mongoRunnable).start();
                Log.d("Time", new Date().toString());
            }
        }, 0, 20, TimeUnit.SECONDS);
    }

    // 設定 ViewPager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GoFragment(), getString(R.string.go));
        adapter.addFragment(new BackFragment(), getString(R.string.back));

        viewPager.setAdapter(adapter);
    }

    // 設定地圖
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.clear();

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json
        ));

        stopPositions = InterCityBus.getStopPosition(subRouteId);
        stopName = InterCityBus.getStopName(subRouteId);
        busPositions = InterCityBus.getBusPosition(subRouteId);
        plateNumb = InterCityBus.getPlateNumb(subRouteId);

        for (int i = 0; i < stopPositions.size() - 1; i++){

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

    // Thread
    public class MongoRunnable implements Runnable {

        Handler handler;
        private String bundleSubRouteId;
        private boolean first = true;
        SupportMapFragment mapFragment;

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // 將 Looper 與 worker thread 連結在一起
            Looper.prepare();
            // 設定 Handler，讓 producer 可以插入訊息

            // Bundle
            bundleSubRouteId = getIntent().getExtras().getString("bndSubRouteId");
            Log.d("bndSubRouteId", bundleSubRouteId + "");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // 設定 ViewPager
                    mViewPager = findViewById(R.id.container);
                    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            if (first && positionOffset == 0 && positionOffsetPixels == 0){
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

                            // 更新UI
                            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(MapsActivity.this);
                            once = true;

                        }
                        @Override
                        public void onPageScrollStateChanged(int state) {}
                    });

                    setupViewPager(mViewPager);

                    // 設定 Tabs
                    mTabLayout = findViewById(R.id.tabs);
                    mTabLayout.setupWithViewPager(mViewPager);

                }
            });

            // blocking 呼叫，讓 message queue 可發送訊息給 consumer thread
            Looper.loop();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mongoThread.interrupt();
        mongoThread = null;
    }
}