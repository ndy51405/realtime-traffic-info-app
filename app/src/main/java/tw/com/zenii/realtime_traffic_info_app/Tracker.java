package tw.com.zenii.realtime_traffic_info_app;

public class Tracker {
    private String nearStop;
    private String plateNumb;

    public Tracker(String nearStop, String plateNumb) {
        this.nearStop = nearStop;
        this.plateNumb = plateNumb;
    }

    public String getNearStop() {
        return nearStop;
    }

    public void setNearStop(String nearStop) {
        this.nearStop = nearStop;
    }

    public String getPlateNumb() {
        return plateNumb;
    }

    public void setPlateNumb(String plateNumb) {
        this.plateNumb = plateNumb;
    }
}
