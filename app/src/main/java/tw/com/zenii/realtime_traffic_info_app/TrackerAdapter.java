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
        TextView txtPlateNumb; // 車牌名稱
        TextView txtNearStop; // 最近站牌
        TextView txtBusStatus; // 客運狀態（ex: 客滿）
        TextView txtA2EventType; // 離站進站
        TextView txtSubRouteName; // 客運路線名稱

        public ViewHolder(TextView txtNearStop, TextView txtPlateNumb,
                          TextView txtBusStatus, TextView txtA2EventType, TextView txtSubRouteName){

            this.txtNearStop = txtNearStop;
            this.txtPlateNumb = txtPlateNumb;
            this.txtBusStatus = txtBusStatus;
            this.txtA2EventType = txtA2EventType;
            this.txtSubRouteName = txtSubRouteName;

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
                    (TextView) convertView.findViewById(R.id.txtPlateNumber),
                    (TextView) convertView.findViewById(R.id.txtBusStatus),
                    (TextView) convertView.findViewById(R.id.txtA2EventType),
                    (TextView) convertView.findViewById(R.id.txtSubRouteName)
            );

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tracker tracker = (Tracker) getItem(position);

        holder.txtNearStop.setText(tracker.getNearStop());
        holder.txtPlateNumb.setText(tracker.getPlateNumb());
        holder.txtBusStatus.setText(tracker.getBusStatus());
        holder.txtA2EventType.setText(tracker.getA2EventType());
        holder.txtSubRouteName.setText(tracker.getSubRouteName());

        return convertView;
    }



}
