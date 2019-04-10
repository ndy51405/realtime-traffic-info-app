package tw.com.zenii.realtime_traffic_info_app;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class TrackerDB extends SQLiteOpenHelper {

    public TrackerDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TrackerDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE  TABLE  tracker" +
                "(_id INTEGER PRIMARY KEY NOT NULL , " +
                " nearStop VARCHAR , " +
                " plateNumb VARCHAR , " +
                " busStatus VARCHAR , " +
                " a2EventType VARCHAR , " +
                " routeName VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
