package tw.com.zenii.realtime_traffic_info_app;

/*public class ScheduledExecutorTest {

    public static void main(String [] args) {

        MapsActivity ma = new MapsActivity();

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        service.scheduleAtFixedRate(ma.new MongoRunnable(), 0, 100, TimeUnit.MILLISECONDS);

    }


}*/

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorTest {
    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("run "+ System.currentTimeMillis());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }
}
