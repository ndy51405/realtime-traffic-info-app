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
        TextView txtEstimateTime;
        TextView txtStopName;
        public ViewHolder(TextView txtEstimateTime, TextView txtPlateNumb, TextView txtStopName){
            this.txtEstimateTime = txtEstimateTime;
            this.txtPlateNumb = txtPlateNumb;
            this.txtStopName = txtStopName;
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
                    (TextView) convertView.findViewById(R.id.txtEstimateTime),
                    (TextView) convertView.findViewById(R.id.txtPlateNumber),
                    (TextView) convertView.findViewById(R.id.txtStopName)
            );

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tracker tracker = (Tracker) getItem(position);

        holder.txtEstimateTime.setText(tracker.getEstimateTime());
        holder.txtPlateNumb.setText(tracker.getPlateNumb());
        holder.txtStopName.setText(tracker.getStopName());

        return convertView;
    }
}