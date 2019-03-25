package tw.com.zenii.realtime_traffic_info_app;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MongoRunnable mongoRunnable = new MongoRunnable();
    private Thread mongoThread = new Thread(mongoRunnable);
    List<LatLng> stopPositions;
    List<LatLng> busPositions;
    int DEFAULT_ZOOM = 12;
    Polyline line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mongoThread.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

        stopPositions = mongoRunnable.getStopPosition();
        busPositions = mongoRunnable.getBusPosition();
        for(int i = 0; i < stopPositions.size(); i++){

            //String url = getUrl()
            // 劃線的地方
            mMap.addPolyline(new PolylineOptions()
                    .addAll(stopPositions)
                    .color(Color.BLACK)
                    .width(7));

            // 鏡頭初始位置
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(stopPositions.get(0).latitude,
                    stopPositions.get(0).longitude), DEFAULT_ZOOM));
        }

        for(int i = 0; i < busPositions.size(); i++){

            // 測試
            Log.d("Lat" , "" + busPositions.get(i).latitude);
            Log.d("Lng" , "" + busPositions.get(i).longitude);

            // Marker
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(busPositions.get(i).latitude,
                            busPositions.get(i).longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));

        }

    }

    public class MongoRunnable implements Runnable{

        Handler handler;
        private double lat;
        private double lng;
        private String subRouteId = "155102";

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // 將 Looper 與 worker thread 連結在一起
            Looper.prepare();
            // 設定 Handler，讓 producer 可以插入訊息

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // 更新UI
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);

                    mapFragment.getMapAsync(MapsActivity.this);

                }
            });

            // blocking 呼叫，讓 message queue 可發送訊息給 consumer thread
            Looper.loop();
        }

        public List<LatLng> getStopPosition(){
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

        public List<LatLng> getBusPosition(){
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

    }

    public void closeActivity(View view) {
        finish();
    }
}