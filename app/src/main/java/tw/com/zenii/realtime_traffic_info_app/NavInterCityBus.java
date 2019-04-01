package tw.com.zenii.realtime_traffic_info_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import tw.com.zenii.realtime_traffic_info_app.tabview.GoFragment;

public class NavInterCityBus extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavInterCityBus.MongoRunnable mongoRunnable = new NavInterCityBus.MongoRunnable();
    private SearchView searchView;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_inter_city_bus);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 在 searchView 內取值
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("search",query);
                new Thread(mongoRunnable).start();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mongoRunnable.handler != null && !query.equals("")) {
                    Message msg = new Message();
                    msg.obj = query;
                    mongoRunnable.handler.sendMessage(msg);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_inter_city_bus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class MongoRunnable implements Runnable{

        Handler handler;

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // 將 Looper 與 worker thread 連結在一起
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            // 設定 Handler，讓 producer 可以插入訊息
            handler = new Handler() {
                // 當訊息被送到 MongoRunnable 時的 callback
                public void handleMessage(Message msgIn) {
                    final String subRouteId = msgIn.obj.toString();
                    final ArrayList<String> routeNameResults = new ArrayList<>();
                    final ArrayList<String> routeIdResults = new ArrayList<>();
                    String result;
                    String resultId;

                    JsonArray resJa = InterCityBus.getRouteSearchResult(subRouteId);
                    for(JsonElement je : resJa) {
                        JsonObject jo = je.getAsJsonObject();
                        result = jo.get("SubRouteID").getAsString() + "\n" + jo.get("Headsign").getAsString();
                        resultId = jo.get("SubRouteID").getAsString();
                        Log.d("result", result);
                        routeNameResults.add(result);
                        routeIdResults.add(resultId);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            list = findViewById(R.id.listView);
                            final ArrayAdapter adapter = new ArrayAdapter(NavInterCityBus.this, android.R.layout.simple_list_item_1, routeNameResults);
                            list.setAdapter(adapter);

                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent();
                                    intent.setClass(NavInterCityBus.this, MapsActivity.class);

                                    String bundleResult = routeIdResults.get(position);
                                    Log.d("onItemClick", routeIdResults.get(position)+"");

                                    Bundle bundle = new Bundle();
                                    bundle.putString("bndSubRouteId", bundleResult);
                                    intent.putExtras(bundle);

                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            };

            // blocking 呼叫，讓 message queue 可發送訊息給 consumer thread
            Looper.loop();
        }
    }
}
