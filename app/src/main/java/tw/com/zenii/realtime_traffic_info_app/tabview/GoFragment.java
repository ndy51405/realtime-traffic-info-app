package tw.com.zenii.realtime_traffic_info_app.tabview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.com.zenii.realtime_traffic_info_app.InterCityBus;
import tw.com.zenii.realtime_traffic_info_app.MapsActivity;
import tw.com.zenii.realtime_traffic_info_app.MyAdapter;
import tw.com.zenii.realtime_traffic_info_app.R;
import tw.com.zenii.realtime_traffic_info_app.Stop;

public class GoFragment extends Fragment {

    private View view;
    ListView list;
    List<Stop> stopList;
    List<String> stopName;
    List<String> estimateTime;
    private MyAdapter adapter;
    private String route = MapsActivity.subRouteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_item, container, false);
        list = view.findViewById(R.id.list_stopName);

        route = MapsActivity.subRouteId + "01";

        if (route != null) {
            Log.d("MapsActivity.subRouteId", route);
            stopName = InterCityBus.extractStopNames(route);
            estimateTime = InterCityBus.extractEstimateTime(route);
            stopList = new ArrayList();

            for (int i = 0; i < estimateTime.size() ; i++) {
                Log.d("EstimateTime", estimateTime.get(i) + "\t" + stopName.get(i));
                stopList.add(new Stop(estimateTime.get(i), stopName.get(i)));
            }
            adapter = new MyAdapter(getActivity(), stopList);
        }

        list.setAdapter(adapter);
        return view;
    }
}
