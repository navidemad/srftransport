package android.vtctools;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GoogleMapsRestClient {

    private static final String GOOGLE_MAP_API_KEY = "CHANGE_ME"; // Change this line
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.put("key", GOOGLE_MAP_API_KEY);
        client.get(BASE_URL + url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        params.put("key", GOOGLE_MAP_API_KEY);
        client.post(BASE_URL + url, params, responseHandler);
    }

}
