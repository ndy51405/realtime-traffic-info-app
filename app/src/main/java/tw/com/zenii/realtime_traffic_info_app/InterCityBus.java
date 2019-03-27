package tw.com.zenii.realtime_traffic_info_app;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

public class InterCityBus {

    // 把 get 系列移至此
    private final static int ELAPSE_ALLOW = -2;

    private static double lat;
    private static double lng;
    private static String numb;
    private static String name;
    private static ArrayList<LatLng> stopPositions;
    private static List<LatLng> busPositions;
    private static HashMap<LatLng, String> plateNumb;
    private static HashMap<LatLng, String> stopName;
    private static ArrayList<String> stopNameOnly;

    static JsonArray getRouteSearchResult(String key) {
        JsonArray jaToReturn = new JsonArray();
        List<String> subRouteNamesGotten = new ArrayList<>();

        MongoCollection mongoCollection = Mongo.getCollection("icb_route");
        Document document = (Document)mongoCollection.find(eq("RouteID", key)).first();
        if(document == null) {
            return jaToReturn;
        }
        String result = document.toJson();
        Log.d("result", result);
        JsonObject resObj = new JsonParser().parse(result).getAsJsonObject();

        // build jaToReturn here
        JsonArray subRoutes = resObj.get("SubRoutes").getAsJsonArray();
        for(JsonElement subRoute : subRoutes) {
            JsonObject joToAdd = new JsonObject();
            JsonObject subRouteObj = subRoute.getAsJsonObject();
            String subRouteName = subRouteObj.get("SubRouteName").getAsJsonObject().get("Zh_tw").getAsString();
            Log.d(">>> ", subRouteName.substring(4,5));
            if(/*subRouteName.length() == 5 && */subRouteName.substring(4,5).equals("0")) {
                subRouteName = subRouteName.substring(0,4);
            }
            String headsign = subRouteObj.get("Headsign").getAsString();
            if(!subRouteNamesGotten.contains(subRouteName)) {
                subRouteNamesGotten.add(subRouteName);
                joToAdd.addProperty("SubRouteID", subRouteName);
                joToAdd.addProperty("Headsign", headsign);
                jaToReturn.add(joToAdd);
                Log.d("joToAdd", joToAdd.toString());
                Log.d("jaToReturn", jaToReturn.toString());
            }
        }

        return jaToReturn;
    }

    public static ArrayList<String> extractEstimateTime(String subRouteId) {
        ArrayList<String> estimateTimes = new ArrayList<>();
        MongoCollection mongoCollection = Mongo.getCollection("icb_rtEstimated");
        Date beforeTime = getBeforeMinute();
        // noinspection unchecked
        MongoCursor<Document> cursor = mongoCollection
                .find(and(eq("SubRouteID", subRouteId), gte("UpdateTime", beforeTime)))
                .sort(Sorts.ascending("StopSequence"))
                .iterator();
        while(cursor.hasNext()){
            JsonObject jsonObject = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
            String estimateTime = "未發車";
            if(jsonObject.has("EstimateTime")) {
                estimateTime = jsonObject.get("EstimateTime").getAsInt()/60 + " min";
            }
            estimateTimes.add(estimateTime);
        }
        return estimateTimes;
    }

    public static List<String> extractStopNames(String subRouteId) {
        final List<String> stopNames = new ArrayList<>();
        MongoCollection mongoCollection = Mongo.getCollection("icb_rtEstimated");
        Date beforeTime = getBeforeMinute();
        // noinspection unchecked
        MongoCursor<Document> cursor = mongoCollection
                .find(and(eq("SubRouteID", subRouteId), gte("UpdateTime", beforeTime)))
                .sort(Sorts.ascending("StopSequence"))
                .iterator();
        while(cursor.hasNext()){
            JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
            JsonObject stop =  res.get("StopName").getAsJsonObject();
            String tw = stop.getAsJsonObject()
                    .get("Zh_tw").getAsString();
            stopNames.add(tw);
        }
        return stopNames;
    }

    private static Date getBeforeMinute() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, ELAPSE_ALLOW);
        return calendar.getTime();
    }

    public static List<LatLng> getStopPosition(String subRouteId){
        MongoCollection mongoCollection = Mongo.getCollection("icb_stopOfRoute");
        MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                .sort(Sorts.ascending("StopSequence"))
                .iterator();
        stopPositions = new ArrayList<>();
        while(cursor.hasNext()){
            // parse response json here
            JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
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

    public static HashMap<LatLng, String> getStopName(String subRouteId){
        MongoCollection mongoCollection = Mongo.getCollection("icb_stopOfRoute");
        MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                .iterator();
        stopPositions = new ArrayList<>();
        stopName = new HashMap<>();
        while(cursor.hasNext()){
            // parse response json here
            JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
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
                stopPositions.add(new LatLng(lat, lng));
                stopName.put(new LatLng(lat, lng), name);
            }
        }
        return stopName;
    }

    public static List<LatLng> getBusPosition(String subRouteId){
        MongoCollection mongoCollection = Mongo.getCollection("icb_rtFrequency");
        MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                .iterator();
        busPositions = new ArrayList<>();
        while(cursor.hasNext()){
            // parse response json here
            JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
            JsonObject stops = res.get("BusPosition").getAsJsonObject();
            lat = stops.getAsJsonObject()
                    .get("PositionLat").getAsDouble();
            lng = stops.getAsJsonObject()
                    .get("PositionLon").getAsDouble();
            busPositions.add(new LatLng(lat, lng));
        }
        return busPositions;
    }

    public static HashMap<LatLng, String> getPlateNumb(String subRouteId){
        MongoCollection mongoCollection = Mongo.getCollection("icb_rtFrequency");
        MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                .iterator();
        busPositions = new ArrayList<>();
        plateNumb = new HashMap<>();
        while(cursor.hasNext()){
            // parse response json here
            JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
            JsonObject stops = res.get("BusPosition").getAsJsonObject();
            numb = res.get("PlateNumb").getAsString();
            lat = stops.getAsJsonObject()
                    .get("PositionLat").getAsDouble();
            lng = stops.getAsJsonObject()
                    .get("PositionLon").getAsDouble();
            busPositions.add(new LatLng(lat, lng));
            plateNumb.put(new LatLng(lat, lng), numb);
        }
        return plateNumb;
    }

    public static ArrayList<String> getStopNameOnly(String subRouteId){
        MongoCollection mongoCollection = Mongo.getCollection("icb_stopOfRoute");
        MongoCursor<Document> cursor =  mongoCollection.find(eq("SubRouteID", subRouteId))
                .iterator();
        stopNameOnly = new ArrayList<>();
        while(cursor.hasNext()){
            // parse response json here
            JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
            JsonArray stops = res.get("Stops").getAsJsonArray();
            for(JsonElement stop : stops) {
                name = stop.getAsJsonObject()
                        .get("StopName").getAsJsonObject()
                        .get("Zh_tw").getAsString();
                stopNameOnly.add(name);
            }
        }
        return stopNameOnly;
    }
}
