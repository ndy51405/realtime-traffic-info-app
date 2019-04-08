package tw.com.zenii.realtime_traffic_info_app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TrackerFragment extends Fragment {

    private View view;
    private ListView list;
    private List<Tracker> trackers_list = new ArrayList<>();
    private TrackerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tracker, container, false);
        list = view.findViewById(R.id.list_tracker);

        String platNumb = getArguments().getString("trackPlateNumb");

        trackers_list.add(new Tracker("捷運大橋頭站", platNumb));

        adapter = new TrackerAdapter(getActivity(), trackers_list);
        list.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

