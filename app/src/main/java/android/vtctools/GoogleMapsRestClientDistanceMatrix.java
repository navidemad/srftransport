package android.vtctools;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GoogleMapsRestClientDistanceMatrix {

    public static void getDistanceMatrix(final GoogleMapsRestClientDistanceMatrix.Callbacks listener, final String from, final String to, final long departure_time) {
        RequestParams params = new RequestParams();
        params.put("units", "metric");
        params.put("mode", "driving");
        params.put("traffic_model", "pessimistic");
        params.put("language", "fr-FR");
        params.put("avoid", "tolls");
        params.put("departure_time", departure_time);
        params.put("origins", from);
        params.put("destinations", to);
        GoogleMapsRestClient.get("distancematrix/json", params, new JsonHttpResponseHandler() {

            private void parseResponse(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String status = response.getString("status");
                    switch (status) {
                        case "OK":
                            listener.onOk(response);
                            break;
                        case "INVALID_REQUEST":
                            listener.onInvalidRequest(response);
                            break;
                        case "MAX_ELEMENTS_EXCEEDED":
                            listener.onMaxElementsExceeded(response);
                            break;
                        case "OVER_QUERY_LIMIT":
                            listener.onOverQueryLimit(response);
                            break;
                        case "REQUEST_DENIED":
                            listener.onRequestDenied(response);
                            break;
                        case "UNKNOWN_ERROR":
                            listener.onUnknownError(response);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                parseResponse(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // TODO: Multi origins/destinations
            }
        });
    }



    public interface Callbacks {
        void onOk(JSONObject response);
        void onInvalidRequest(JSONObject response);
        void onMaxElementsExceeded(JSONObject response);
        void onOverQueryLimit(JSONObject response);
        void onRequestDenied(JSONObject response);
        void onUnknownError(JSONObject response);
    }

}
