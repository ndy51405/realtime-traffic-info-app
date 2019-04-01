package tw.com.zenii.realtime_traffic_info_app.bak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import tw.com.zenii.realtime_traffic_info_app.R;

public class IcbActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icb);
    }

    public void btnSearch_click(View view) {
        System.out.println("button clicked");
        TextView lblResult = findViewById(R.id.lblResult);
        lblResult.setText("");
        EditText txtSearch = findViewById(R.id.txtRoute);
        String route = txtSearch.getText().toString();

        /*Intent intent = new Intent(this, RouteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("route", route);
        intent.putExtras(bundle);
        startActivity(intent);*/

    }
}
