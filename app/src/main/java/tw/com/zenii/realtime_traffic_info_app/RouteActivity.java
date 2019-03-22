package tw.com.zenii.realtime_traffic_info_app;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteActivity extends AppCompatActivity {

    private MongoRunnable mongoRunnable = new MongoRunnable();
    private Thread mongoThread = new Thread(mongoRunnable);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Bundle bundle = this.getIntent().getExtras();
        String route = "";
        if(bundle != null) {
            route = bundle.getString("route");
        }

        mongoThread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mongoRunnable.handler != null && !route.equals("")) {
            Message msg = new Message();
            msg.obj = route;
            mongoRunnable.handler.sendMessage(msg);
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
            handler = new Handler() {
                // 當訊息被送到 MongoRunnable 時的 callback
                public void handleMessage(Message msgIn) {
                    final String subRouteId = msgIn.obj.toString();
                    final List<String> estimateTimes = InterCityBus.extractEstimateTime(subRouteId);
                    final List<String> stopNames = InterCityBus.extractStopNames(subRouteId);
                    /*final List<String> plateNumbs = InterCityBus.extractPlateNumbs(subRouteId);*/

                    Log.d("estimateTimes.size()", String.valueOf(estimateTimes.size()));
                    Log.d("stopNames.size()", String.valueOf(stopNames.size()));
                    /*Log.d("stopNames.size()", String.valueOf(stopNames.size()));*/

                    final List<HashMap<String, String>> list = hashList();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView listView = findViewById(R.id.listStops);
                            ListViewAdapter adapter = new ListViewAdapter(RouteActivity.this, list);
                            listView.setAdapter(adapter);

                            TextView lblRoute = findViewById(R.id.lblRoute);
                            lblRoute.setText(subRouteId);
                        }
                    });
                }
            };

            // blocking 呼叫，讓 message queue 可發送訊息給 consumer thread
            Looper.loop();
        }
    }

    private List<HashMap<String, String>> hashList() {
        final String FIRST_COLUMN = "First";
        final String SECOND_COLUMN = "Second";
        final String THIRD_COLUMN = "Third";
        List<HashMap<String, String>> list = new ArrayList<>();

        HashMap<String, String> hashmap = new HashMap<>();
        hashmap.put(FIRST_COLUMN, "Allo messaging");
        hashmap.put(SECOND_COLUMN, "google");
        hashmap.put(THIRD_COLUMN, "Free");
        list.add(hashmap);
        return list;
    }
}
