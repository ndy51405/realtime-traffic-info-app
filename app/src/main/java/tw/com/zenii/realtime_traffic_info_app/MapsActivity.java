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
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;

import org.bson.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MongoRunnable mongoRunnable = new MongoRunnable();
    private Thread mongoThread = new Thread(mongoRunnable);
    List<LatLng> stopPositions;
    List<LatLng> busPositions;
    HashMap<LatLng, String> plateNumb;
    HashMap<LatLng, String> stopName;
    String subRouteId = "181801";
    int DEFAULT_ZOOM = 10;
    boolean once = true; // 只執行一次
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new Thread(mongoRunnable).start();
                Log.d("Time", LocalDateTime.now() +" ");
            }
        }, 0, 20, TimeUnit.SECONDS);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GoFragment(), "去程");
        adapter.addFragment(new BackFragment(), "回程");

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mongoThread.interrupt();
        mongoThread = null;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.clear();

        stopPositions = mongoRunnable.getStopPosition(subRouteId);
        stopName = mongoRunnable.getStopName(subRouteId);
        busPositions = mongoRunnable.getBusPosition(subRouteId);
        plateNumb = mongoRunnable.getPlateNumb(subRouteId);

        for(int i = 0; i < stopPositions.size() - 1; i++){

            // 劃線的地方
            mMap.addPolyline(new PolylineOptions()
                    .addAll(stopPositions)
                    .color(Color.BLACK)
                    .width(7));

            // Marker
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(stopPositions.get(i).latitude,
                                stopPositions.get(i).longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop))
                    .title(stopName.get(stopPositions.get(i))));

            if (once) {
                // 鏡頭初始位置
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(stopPositions.get(0).latitude,
                        stopPositions.get(0).longitude), DEFAULT_ZOOM));
                once = false;
            }
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


    public class MongoRunnable implements Runnable {

        Handler handler;
        private double lat;
        private double lng;
        private String numb;
        private String name;
        private ArrayList bundleSubRouteId = new ArrayList();
        private boolean first = true;
        SupportMapFragment mapFragment;

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // 將 Looper 與 worker thread 連結在一起
            Looper.prepare();
            // 設定 Handler，讓 producer 可以插入訊息

            //Bundle 傳過來應只剩 181801 or 181802
            /*bundleSubRouteId = getIntent().getExtras().getStringArrayList("bndSubRouteId");
            Log.d("bndSubRouteId", bundleSubRouteId + "");
            subRouteId = bundleSubRouteId.get(0) + "01";*/

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
                                    subRouteId = "181801";
                                    Log.d("onPageSelected", subRouteId);
                                    break;
                                case 1:
                                    subRouteId = "181802";
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

        public List<LatLng> getStopPosition(String subRouteId){
            MongoCollection mongoCollection = Mongo.getCollection("icb_stopOfRoute");
            MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                    .sort(Sorts.ascending("StopSequence"))
                    .iterator();
            stopPositions = new ArrayList<>();
            while(cursor.hasNext()){
                // parse response json here
                JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
                JsonArray stops = res.get("Stops").getAsJsonArray();
                for(JsonElement stop : stops) {
                    lat = stop.getAsJsonObject()
                            .get("StopPosition").getAsJsonObject()
                            .get("PositionLat").getAsDouble();
                    lng = stop.getAsJsonObject()
                            .get("StopPosition").getAsJsonObject()
                            .get("PositionLon").getAsDouble();
                    stopPositions.add(new LatLng(lat, lng));
                }
            }
            return stopPositions;
        }

        public HashMap<LatLng, String> getStopName(String subRouteId){
            MongoCollection mongoCollection = Mongo.getCollection("icb_stopOfRoute");
            MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                    .iterator();
            stopPositions = new ArrayList<>();
            stopName = new HashMap<>();
            while(cursor.hasNext()){
                // parse response json here
                JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
                JsonArray stops = res.get("Stops").getAsJsonArray();
                for(JsonElement stop : stops) {
                    lat = stop.getAsJsonObject()
                            .get("StopPosition").getAsJsonObject()
                            .get("PositionLat").getAsDouble();
                    lng = stop.getAsJsonObject()
                            .get("StopPosition").getAsJsonObject()
                            .get("PositionLon").getAsDouble();
                    name = stop.getAsJsonObject()
                            .get("StopName").getAsJsonObject()
                            .get("Zh_tw").getAsString();
                    stopPositions.add(new LatLng(lat, lng));
                    stopName.put(new LatLng(lat, lng), name);
                }
            }
            return stopName;
        }

        public List<LatLng> getBusPosition(String subRouteId){
            MongoCollection mongoCollection = Mongo.getCollection("icb_rtFrequency");
            MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                    .iterator();
            busPositions = new ArrayList<>();
            while(cursor.hasNext()){
                // parse response json here
                JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
                JsonObject stops = res.get("BusPosition").getAsJsonObject();
                lat = stops.getAsJsonObject()
                        .get("PositionLat").getAsDouble();
                lng = stops.getAsJsonObject()
                        .get("PositionLon").getAsDouble();
                busPositions.add(new LatLng(lat, lng));
            }
            return busPositions;
        }

        public HashMap<LatLng, String> getPlateNumb(String subRouteId){
            MongoCollection mongoCollection = Mongo.getCollection("icb_rtFrequency");
            MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                    .iterator();
            busPositions = new ArrayList<>();
            plateNumb = new HashMap<>();
            while(cursor.hasNext()){
                // parse response json here
                JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
                JsonObject stops = res.get("BusPosition").getAsJsonObject();
                numb = res.get("PlateNumb").getAsString();
                lat = stops.getAsJsonObject()
                        .get("PositionLat").getAsDouble();
                lng = stops.getAsJsonObject()
                        .get("PositionLon").getAsDouble();
                busPositions.add(new LatLng(lat, lng));
                plateNumb.put(new LatLng(lat, lng), numb);
            }
            return plateNumb;
        }

    }

    public void closeActivity(View view) {
        finish();
    }
}