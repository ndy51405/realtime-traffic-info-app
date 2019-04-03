package tw.com.zenii.realtime_traffic_info_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MapTest extends AppCompatActivity {

    /*private MongoRunnable mongoRunnable = new MongoRunnable();
    private Thread mongoThread = new Thread(mongoRunnable);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);



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

    public class MongoRunnable implements Runnable{

        Handler handler;

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // 將 Looper 與 worker thread 連結在一起
            Looper.prepare();
            // 設定 Handler，讓 producer 可以插入訊息

            List<Integer> stopPositions = new ArrayList<>();
            MongoCollection mongoCollection = Mongo.getCollection("icb_stopOfRoute");
            MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", "079601"))
                    .sort(Sorts.ascending("StopSequence"))
                    .iterator();
            while(cursor.hasNext()){
                // parse response json here
                int stopPostition = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject()
                        .get("EstimateTime").getAsInt();
                stopPositions.add(stopPostition);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 更新UI
                }
            });


            // blocking 呼叫，讓 message queue 可發送訊息給 consumer thread
            Looper.loop();
        }*/
    //}

    public void closeActivity(View view) {
        finish();
    }
}
