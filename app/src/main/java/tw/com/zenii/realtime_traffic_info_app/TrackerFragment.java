package tw.com.zenii.realtime_traffic_info_app;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TrackerFragment extends Fragment {

    private View view;
    private ListView list;
    private TrackerDB helper;
    private SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tracker, container, false);
        list = view.findViewById(R.id.list_tracker);

        helper = new TrackerDB(getActivity(), "Tracker", null, 1);
        Cursor c = helper.getReadableDatabase().query("Tracker", null, null,
                null, null, null, null);
        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_tracker, c ,
                new String[] {"nearStop", "plateNumb", "busStatus", "a2EventType", "routeName"},
                new int[] {R.id.txtNearStop, R.id.txtPlateNumber, R.id.txtBusStatus, R.id.txtA2EventType, R.id.txtRouteName}, 0);

        list.setAdapter(simpleCursorAdapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

