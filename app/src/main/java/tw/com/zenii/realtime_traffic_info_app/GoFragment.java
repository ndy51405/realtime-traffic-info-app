package tw.com.zenii.realtime_traffic_info_app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GoFragment extends Fragment {

    private View view;
    private ListView list;
    private static List<Stop> stopList;
    private static List<String> stopName;
    private static List<String> estimateTime;
    private MyAdapter adapter;
    private static String route = MapsActivity.subRouteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_item, container, false);
        new Thread(new GoRunnable()).start();
        return view;
    }

    public class GoRunnable implements Runnable {
        @Override
        public void run() {

            route = MapsActivity.subRouteId + "01";

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    list = view.findViewById(R.id.list_stopName);
                    if (route != null) {
                        stopName = InterCityBusHandler.getStopNames(route);
                        estimateTime = InterCityBusHandler.getEstimateTime(route);
                        Log.d("goRoute", route);

                        stopList = new ArrayList();

                        for (int i = 0; i < estimateTime.size(); i++) {
                            Log.d("EstimateTime", estimateTime.get(i) + "\t" + stopName.get(i));
                            stopList.add(new Stop(estimateTime.get(i), stopName.get(i)));
                        }

                    }

                    adapter = new MyAdapter(getActivity(), stopList);
                    list.setAdapter(adapter);
                }
            });

        }
    }

}