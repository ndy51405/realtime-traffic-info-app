package tw.com.zenii.realtime_traffic_info_app;

import android.util.Log;
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
import java.util.List;

import tw.com.zenii.realtime_traffic_info_app.Mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

class InterCityBus {

    private final static int ELAPSE_ALLOW = -2;

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

    static List<String> extractEstimateTime(String subRouteId) {
        List<String> estimateTimes = new ArrayList<>();
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

    static List<String> extractStopNames(String subRouteId) {
        final List<String> stopNames = new ArrayList<>();
        MongoCollection mongoCollection = Mongo.getCollection("icb_stopOfRoute");
        // noinspection unchecked
        MongoCursor<Document> cursor = mongoCollection.find(eq("SubRouteID", subRouteId))
                .iterator();
        while(cursor.hasNext()){
            JsonObject res = new JsonParser().parse(cursor.next().toJson()).getAsJsonObject();
            JsonArray stops = res.get("Stops").getAsJsonArray();
            for(JsonElement stop : stops) {
                String stopName = stop.getAsJsonObject()
                        .get("StopName").getAsJsonObject()
                        .get("Zh_tw").getAsString();
                stopNames.add(stopName);
            }
        }
        return stopNames;
    }

    /*TODO
    static List<String> extractPlateNumbs(String subRouteId) {

    }*/

    private static Date getBeforeMinute() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, ELAPSE_ALLOW);
        return calendar.getTime();
    }
}
