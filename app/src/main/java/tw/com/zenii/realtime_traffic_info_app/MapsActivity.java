package tw.com.zenii.realtime_traffic_info_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/*import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;*/
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import org.bson.Document;


import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MapsActivity extends FragmentActivity {

    /*private GoogleMap mMap;
//    private MongoRunnable mongoRunnable = new MongoRunnable();
//    private Thread mongoThread = new Thread(mongoRunnable);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/


//        mongoThread.start();
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
   /* }*/

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mongoThread.interrupt();
//        mongoThread = null;
//    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/

    /*public class MongoRunnable implements Runnable{

        Handler handler;

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // 將 Looper 與 worker thread 連結在一起
            Looper.prepare();
            // 設定 Handler，讓 producer 可以插入訊息

            List<Integer> stopPositions = new ArrayList<>();
            MongoCollection mongoCollection = Mongo.getCollection("icb_stopOfRoute");
            // 明天問取資料的結構
            MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", "079601"))
                    .sort(Sorts.ascending("StopSequence"))
                    .iterator();
            while(cursor.hasNext()){
                // parse response json here
                int stopPosition = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject()
                        .get("EstimateTime").getAsInt();
                stopPositions.add(stopPosition);
                Log.d("stopPosition", stopPositions+ "");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 更新UI
                }
            });


            // blocking 呼叫，讓 message queue 可發送訊息給 consumer thread
            Looper.loop();
        }
    }

    public void closeActivity(View view) {
        finish();
    }*/
}
