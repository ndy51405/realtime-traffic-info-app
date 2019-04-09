package tw.com.zenii.realtime_traffic_info_app;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TrackerFragment extends Fragment {

    private View view;
    private ListView list;
    private List<Tracker> trackers_list = new ArrayList<>();
    private TrackerAdapter adapter;
    private ArrayList<String> plateNumbList = new ArrayList<>();
    private String plateNumb;
    private TrackerDB helper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tracker, container, false);
        list = view.findViewById(R.id.list_tracker);

        helper = new TrackerDB(getActivity(), "Tracker", null, 1);
        Cursor c = helper.getReadableDatabase().query("Tracker", null, null, null, null, null, null);
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_tracker, c ,
                new String[] {"nearStop", "plateNumb", "busStatus", "a2EventType", "subRouteName"},
                new int[] {R.id.txtNearStop, R.id.txtPlateNumber, R.id.txtBusStatus, R.id.txtA2EventType, R.id.txtSubRouteName}, 0);

        trackers_list.add(new Tracker("捷運大橋頭站", "KKA-707", "客滿", "離站", "9001"));

        adapter = new TrackerAdapter(getActivity(), trackers_list);
        list.setAdapter(simpleCursorAdapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

