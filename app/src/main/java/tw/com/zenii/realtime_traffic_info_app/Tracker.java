package tw.com.zenii.realtime_traffic_info_app;

public class Tracker {
    private String nearStop; // 最近站牌
    private String plateNumb; // 車牌號碼
    private String busStatus; // 客運狀態（ex:客滿）
    private String a2EventType; // 進站離站
    private String subRouteName; // 路線名稱


    public Tracker(String nearStop, String plateNumb, String busStatus, String a2EventType, String subRouteName) {
        this.nearStop = nearStop;
        this.plateNumb = plateNumb;
        this.busStatus = busStatus;
        this.a2EventType = a2EventType;
        this.subRouteName = subRouteName;
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

    public String getBusStatus() {
        return busStatus;
    }

    public void setBusStatus(String busStatus) {
        this.busStatus = busStatus;
    }

    public String getA2EventType() {
        return a2EventType;
    }

    public void setA2EventType(String a2EventType) {
        this.a2EventType = a2EventType;
    }

    public String getSubRouteName() {
        return subRouteName;
    }

    public void setSubRouteName(String subRouteName) {
        this.subRouteName = subRouteName;
    }
}
