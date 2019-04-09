package tw.com.zenii.realtime_traffic_info_app;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

class InterCityBusHandler {

    static JsonArray getRouteSearchResult(String key) {
        JsonArray jaToReturn = new JsonArray();
        List<String> subRouteNamesGotten = new ArrayList<>();

        String result = Mongo.call("getRouteSearchResult", key);
        if(result == null) { return jaToReturn; }
        Log.d("result", result);
        JsonObject resObj = new JsonParser().parse(result).getAsJsonObject();

        // build jaToReturn here
        JsonArray subRoutes = resObj.get("SubRoutes").getAsJsonArray();
        for(JsonElement subRoute : subRoutes) {
            JsonObject joToAdd = new JsonObject();
            JsonObject subRouteObj = subRoute.getAsJsonObject();
            String subRouteName = subRouteObj.get("SubRouteName").getAsJsonObject().get("Zh_tw").getAsString();
            if(/*subRouteName.length() == 5 && */subRouteName.substring(4,5).equals("0")) {
                subRouteName = subRouteName.substring(0,4);
            }
            String headsign = subRouteObj.get("Headsign").getAsString();
            if(!subRouteNamesGotten.contains(subRouteName)) {
                subRouteNamesGotten.add(subRouteName);
                joToAdd.addProperty("SubRouteID", subRouteName);
                joToAdd.addProperty("Headsign", headsign);
                jaToReturn.add(joToAdd);
            }
        }

        return jaToReturn;
    }

    // 預計到站時間
    public static List<String> getEstimateTime(String subRouteId) {
        List<String> estimateTimes = new ArrayList<>();

        String results = Mongo.call("getEstimated", subRouteId);
        if(results == null) { return estimateTimes; }
        JsonArray ja = new JsonParser().parse(results).getAsJsonArray();

        for(JsonElement je : ja){
            JsonObject jsonObject = je.getAsJsonObject();
            String estimateTime = "未發車";
            if(jsonObject.has("EstimateTime")) {
                estimateTime = jsonObject.get("EstimateTime").getAsInt()/60 + " min";
            }
            estimateTimes.add(estimateTime);
        }

        return estimateTimes;
    }

    // 預計到站之站牌名稱
    static List<String> getStopNames(String subRouteId) {
        List<String> stopNames = new ArrayList<>();

        String results = Mongo.call("getEstimated", subRouteId);
        if(results == null) { return stopNames; }
        JsonArray ja = new JsonParser().parse(results).getAsJsonArray();

        for(JsonElement je : ja){
            JsonObject res = je.getAsJsonObject();
            JsonObject stop =  res.get("StopName").getAsJsonObject();
            String tw = stop.getAsJsonObject()
                    .get("Zh_tw").getAsString();
            stopNames.add(tw);
        }
        return stopNames;
    }

    // 取得站牌位置
    public static List<LatLng> getStopPosition(String subRouteId){
        double lat;
        double lng;
        List<LatLng> stopPositions = new ArrayList<>();

        String results = Mongo.call("getStopOfRoute", subRouteId);
        if(results == null) { return stopPositions; }
        JsonArray ja = new JsonParser().parse(results).getAsJsonArray();

        for(JsonElement je : ja){
            JsonObject res = je.getAsJsonObject();
            JsonArray stops = res.get("Stops").getAsJsonArray();
            for(JsonElement stop : stops) {
                lat = stop.getAsJsonObject()
                        .get("StopPosition").getAsJsonObject()
                        .get("PositionLat").getAsDouble();
                lng = stop.getAsJsonObject()
                        .get("StopPosition").getAsJsonObject()
                        .get("PositionLon").getAsDouble();
                stopPositions.add(new LatLng(lat, lng));
            }
        }
        return stopPositions;
    }

    // 取得站牌名稱（搭配經緯度）
    public static Map<LatLng, String> getStopName(String subRouteId){
        double lat;
        double lng;
        Map<LatLng, String> stopName = new HashMap<>();
        String name;

        String results = Mongo.call("getStopOfRoute", subRouteId);
        if(results == null) { return stopName; }
        JsonArray ja = new JsonParser().parse(results).getAsJsonArray();

        for(JsonElement je : ja){
            JsonObject res = je.getAsJsonObject();
            JsonArray stops = res.get("Stops").getAsJsonArray();
            for(JsonElement stop : stops) {
                lat = stop.getAsJsonObject()
                        .get("StopPosition").getAsJsonObject()
                        .get("PositionLat").getAsDouble();
                lng = stop.getAsJsonObject()
                        .get("StopPosition").getAsJsonObject()
                        .get("PositionLon").getAsDouble();
                name = stop.getAsJsonObject()
                        .get("StopName").getAsJsonObject()
                        .get("Zh_tw").getAsString();
                stopName.put(new LatLng(lat, lng), name);
            }
        }
        return stopName;
    }

    // 取得公車位置
    public static List<LatLng> getBusPosition(String subRouteId){
        double lat;
        double lng;
        List<LatLng> busPositions = new ArrayList<>();

        String results = Mongo.call("getFrequency", subRouteId);
        if(results == null) { return busPositions; }
        JsonArray ja = new JsonParser().parse(results).getAsJsonArray();

        for(JsonElement je : ja){
            JsonObject res = je.getAsJsonObject();
            JsonObject stops = res.get("BusPosition").getAsJsonObject();
            lat = stops.getAsJsonObject()
                    .get("PositionLat").getAsDouble();
            lng = stops.getAsJsonObject()
                    .get("PositionLon").getAsDouble();
            busPositions.add(new LatLng(lat, lng));
        }
        return busPositions;
    }

    // 取得車牌號碼（搭配經緯度）
    public static Map<LatLng, String> getPlateNumb(String subRouteId){
        double lat;
        double lng;
        Map<LatLng, String> plateNumb = new HashMap<>();
        String numb;

        String results = Mongo.call("getFrequency", subRouteId);
        if(results == null) { return plateNumb; }
        JsonArray ja = new JsonParser().parse(results).getAsJsonArray();

        for(JsonElement je : ja){
            JsonObject res = je.getAsJsonObject();
            JsonObject stops = res.get("BusPosition").getAsJsonObject();
            numb = res.get("PlateNumb").getAsString();
            lat = stops.getAsJsonObject()
                    .get("PositionLat").getAsDouble();
            lng = stops.getAsJsonObject()
                    .get("PositionLon").getAsDouble();
            plateNumb.put(new LatLng(lat, lng), numb);
        }
        return plateNumb;
    }

    // 取得客運之方位角 Azimuth 暫時沒有用，看之後要不要新增功能
    public static Map<LatLng, Integer> getAzimuth(String subRouteId){
        double lat;
        double lng;
        Map<LatLng, Integer> azimuth = new HashMap<>();
        int numb;

        String results = Mongo.call("getFrequency", subRouteId);
        if(results == null) { return azimuth; }
        JsonArray ja = new JsonParser().parse(results).getAsJsonArray();

        for(JsonElement je : ja){
            JsonObject res = je.getAsJsonObject();
            JsonObject stops = res.get("BusPosition").getAsJsonObject();
            numb = res.get("Azimuth").getAsInt();
            lat = stops.getAsJsonObject()
                    .get("PositionLat").getAsDouble();
            lng = stops.getAsJsonObject()
                    .get("PositionLon").getAsDouble();
            azimuth.put(new LatLng(lat, lng), numb);
        }
        return azimuth;
    }

    // 取得最近站牌
    public static Map<String, String> getNearStop(String subRouteId){
        Map<String, String> nearStop = new HashMap<>();
        String name;
        String numb;

        String results = Mongo.call("getNearStop", subRouteId);
        if(results == null) { return nearStop; }
        JsonArray ja = new JsonParser().parse(results).getAsJsonArray();

        for(JsonElement je : ja){
            JsonObject res = je.getAsJsonObject();
            JsonObject stops = res.get("StopName").getAsJsonObject();
            numb = res.get("PlateNumb").getAsString();
            name = stops.getAsJsonObject()
                    .get("Zh_tw").getAsString();
            nearStop.put(numb, name);
        }
        return nearStop;
    }
}

