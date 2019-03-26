package tw.com.zenii.realtime_traffic_info_app.bak;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tw.com.zenii.realtime_traffic_info_app.R;

import static com.mongodb.client.model.Filters.eq;

public class MainActivity extends AppCompatActivity {

    private DataHandler dataHandler = new DataHandler();
    private MongoRunnable mongoRunnable = new MongoRunnable();
    private Thread mongoThread = new Thread(mongoRunnable);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mongoThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mongoThread.interrupt();
        mongoThread = null;
    }

    public void btnSearch_click(View view){
        TextView lblResult = findViewById(R.id.lblResult);
        lblResult.setText("");
        EditText txtSearch = findViewById(R.id.txtSearch);
        String route = txtSearch.getText().toString();

        if (mongoRunnable.handler != null && !route.equals("")) {
//            Message msg = mongoRunnable.handler.obtainMessage(0, route);
            Message msg = new Message();
            msg.obj = route;
            mongoRunnable.handler.sendMessage(msg);
        }
    }

    // UI thread
    public class DataHandler extends Handler{

        @Override
        public void handleMessage(Message msgIn){
            Iterator<Document> iterator = (Iterator<Document>)msgIn.obj;
            TextView lblResult = findViewById(R.id.lblResult);

            while(iterator.hasNext()){
                JSONObject jsonObject;
                String plateNumb;
                double positionLat;
                double positionLon;
                String busData = "";
                try {
                    jsonObject = new JSONObject(iterator.next().toJson());
                    plateNumb = jsonObject.getString("PlateNumb");
                    JSONObject busPosition = jsonObject.getJSONObject("BusPosition");
                    positionLat = busPosition.getDouble("PositionLat");
                    positionLon = busPosition.getDouble("PositionLon");
                    busData = "車牌： " + plateNumb + "\n經度： " + positionLat + "\n緯度： " + positionLon + "\n\n";
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                busData = lblResult.getText() + busData;
                lblResult.setText(busData);
            }

            if(lblResult.getText().equals("")){
                lblResult.setText(getString(R.string.dnf));
            }
        }
    }

    // worker thread
    public class MongoRunnable implements Runnable{

        Handler handler;

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // 將 Looper 與 worker thread 連結在一起
            Looper.prepare();
            // 設定 Handler，讓 producer 可以插入訊息
            handler = new Handler() {
                // 當訊息被送到 MongoRunnable 時的 callback
                public void handleMessage(Message msgIn) {
                    MongoCollection mongoCollection = getCollection("icb_stopOfRoute");
                    String subRouteId = msgIn.obj.toString();
                    List<Document> results = new ArrayList<>();
                    // query condition
                    mongoCollection.find(eq("SubRouteID", subRouteId)).into(results);
                    Iterator<Document> iterator = results.iterator();

                    Message msgOut = new Message();
                    msgOut.obj = iterator;
                    dataHandler.sendMessage(msgOut);
                }
            };

            // blocking 呼叫，讓 message queue 可發送訊息給 consumer thread
            Looper.loop();
        }

        private MongoCollection getCollection(String collection){
            MongoClient mongoClient = MongoClients.create(getString(R.string.mongo_host));
            MongoDatabase mdb = mongoClient.getDatabase(getString(R.string.db));
            return mdb.getCollection(collection);
        }
    }
}
