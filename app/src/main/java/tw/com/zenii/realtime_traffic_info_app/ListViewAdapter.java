package tw.com.zenii.realtime_traffic_info_app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    private Activity activity;
    private List<HashMap<String, String>> list;

    ListViewAdapter(Activity activity, List<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return convertView;
    }
}
