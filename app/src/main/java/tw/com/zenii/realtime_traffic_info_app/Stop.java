package tw.com.zenii.realtime_traffic_info_app;

public class Stop {
    private String estimateTime;
    private String stopName;

    public Stop(String estimateTime, String stopName) {
        this.estimateTime = estimateTime; // 預計到站時間
        this.stopName = stopName; // 站牌名稱
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
