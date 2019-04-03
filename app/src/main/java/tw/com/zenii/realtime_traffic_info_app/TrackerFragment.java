package tw.com.zenii.realtime_traffic_info_app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TrackerFragment extends Fragment {

    private View view;
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tracker, container, false);
        list = view.findViewById(R.id.list_stopName);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

