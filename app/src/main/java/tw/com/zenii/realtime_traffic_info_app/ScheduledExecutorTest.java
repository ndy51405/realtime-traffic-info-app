package tw.com.zenii.realtime_traffic_info_app;

import android.util.Log;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorTest {
    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                /*MapsActivity.stopPositions = InterCityBusHandler.getStopPosition(MapsActivity.subRouteId);
                MapsActivity.stopName = InterCityBusHandler.getStopName(MapsActivity.subRouteId);
                MapsActivity.busPositions = InterCityBusHandler.getBusPosition(MapsActivity.subRouteId);
                MapsActivity.plateNumb = InterCityBusHandler.getPlateNumb(MapsActivity.subRouteId);
                Log.d("latitude", MapsActivity.busPositions.get(0).latitude+"");*/
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}
