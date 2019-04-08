package tw.com.zenii.realtime_traffic_info_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class MyAdapter extends BaseAdapter{
    private LayoutInflater myInflater;
    private List<Stop> stops;

    public MyAdapter(Context context, List<Stop> stops){
        myInflater = LayoutInflater.from(context);
        this.stops = stops;
    }

    private class ViewHolder {
        TextView txtEstimateTime;
        TextView txtStopName;
        public ViewHolder(TextView txtEstimateTime, TextView txtStopName){
            this.txtEstimateTime = txtEstimateTime;
            this.txtStopName = txtStopName;
        }
    }

    @Override
    public int getCount() {
        return stops.size();
    }

    @Override
    public Object getItem(int arg0) {
        return stops.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return stops.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = myInflater.inflate(R.layout.listview_tabs, null);

            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.txtNearStop),
                    (TextView) convertView.findViewById(R.id.txtStopName)
            );

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Stop stop = (Stop)getItem(position);

        holder.txtEstimateTime.setText(stop.getEstimateTime());
        holder.txtStopName.setText(stop.getStopName());

        return convertView;
    }
}