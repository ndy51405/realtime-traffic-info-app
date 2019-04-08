package tw.com.zenii.realtime_traffic_info_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TrackerAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private List<Tracker> trackers;

    public TrackerAdapter(Context context, List<Tracker> trackers){
        myInflater = LayoutInflater.from(context);
        this.trackers = trackers;
    }

    private class ViewHolder {
        TextView txtPlateNumb;
        TextView txtNearStop;

        public ViewHolder(TextView txtNearStop, TextView txtPlateNumb){
            this.txtNearStop = txtNearStop;
            this.txtPlateNumb = txtPlateNumb;
        }
    }

    @Override
    public int getCount() {
        return trackers.size();
    }

    @Override
    public Object getItem(int arg0) {
        return trackers.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return trackers.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.listview_tracker, null);

            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.txtNearStop),
                    (TextView) convertView.findViewById(R.id.txtPlateNumber)
            );

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tracker tracker = (Tracker) getItem(position);

        holder.txtNearStop.setText(tracker.getNearStop());
        holder.txtPlateNumb.setText(tracker.getPlateNumb());

        return convertView;
    }
}
