package tw.com.zenii.realtime_traffic_info_app;

public class Tracker {
    private String estimateTime;
    private String plateNumb;
    private String stopName;

    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getPlateNumb() {
        return plateNumb;
    }

    public void setPlateNumb(String plateNumb) {
        this.plateNumb = plateNumb;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public Tracker(String estimateTime, String plateNumb, String stopName) {
        this.estimateTime = estimateTime;
        this.plateNumb = plateNumb;
        this.stopName = stopName;
    }
}
