package tw.com.zenii.realtime_traffic_info_app;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class Mongo {

    static String call(String method, String arg0) {
        final String NAMESPACE = "http://ws.zenii.com.tw/";
        final String URL = "http://192.168.1.92:9080/MongoService/services/Query?wsdl";

        SoapObject request = new SoapObject(NAMESPACE, method);
        request.addProperty("arg0", arg0);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(URL);
        httpTransport.debug = true;

        // If the response type is a primitive type like Integer or Boolean, use SoapPrimitive;
        // otherwise, use SoapObject for response.
        SoapPrimitive response = null;
        try {
            httpTransport.call(null, envelope);
            response = (SoapPrimitive) envelope.getResponse();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        if(response != null) {
            return response.toString();
        } else {
            return null;
        }
    }
}
