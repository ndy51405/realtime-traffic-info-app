package tw.com.zenii.realtime_traffic_info_app;

import android.os.AsyncTask;
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

public class BackFragment extends Fragment {

    private View view;
    private ListView list;
    private static List<Stop> backStopList;
    private static List<String> backStopName;
    private static List<String> backEstimateTime;
    private MyAdapter adapter;
    private static String backRoute = MapsActivity.subRouteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_item_back, container, false);

        new Thread(new BackRunnable()).start();

        return view;
    }

    public class BackRunnable implements Runnable {

        @Override
        public void run() {
            backRoute = MapsActivity.subRouteId + "02";

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list = view.findViewById(R.id.list_stopName_back);
                    if (backRoute != null) {
                        backStopName = InterCityBusHandler.getStopNames(backRoute);
                        backEstimateTime = InterCityBusHandler.getEstimateTime(backRoute);
                        Log.d("backRoute", backRoute);

                        backStopList = new ArrayList();

                        for (int i = 0; i < backEstimateTime.size(); i++) {
                            Log.d("EstimateTime", backEstimateTime.get(i) + "\t" + backStopName.get(i));
                            backStopList.add(new Stop(backEstimateTime.get(i), backStopName.get(i)));
                        }
                    }
                    adapter = new MyAdapter(getActivity(), backStopList);

                    list.setAdapter(adapter);
                }
            });

        }
    }
}
