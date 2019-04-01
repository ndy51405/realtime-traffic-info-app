package tw.com.zenii.realtime_traffic_info_app;

public class Stop {
    private String estimateTime;
    private String stopName;

    public Stop(String estimateTime, String stopName) {
        this.estimateTime = estimateTime;
        this.stopName = stopName;
    }

    public String getEstimateTime() {

        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
