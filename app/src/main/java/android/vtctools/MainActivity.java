package android.vtctools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleMapsRestClientDistanceMatrix.Callbacks {

    private EditText edittext_from;
    private EditText edittext_to;
    private Button button_calculate;
    private LinearLayout linear_layout_result;
    private TextView textview_from;
    private TextView textview_to;
    private TextView textview_distance;
    private TextView textview_duration;
    private TextView textview_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();
        setUpEvents();
    }

    private void setUpViews() {
        this.edittext_from = (EditText) findViewById(R.id.edittext_from);
        this.edittext_to = (EditText) findViewById(R.id.edittext_to);
        this.button_calculate = (Button) findViewById(R.id.button_calculate);
        this.linear_layout_result = (LinearLayout) findViewById(R.id.linear_layout_result);
        this.textview_from = (TextView) findViewById(R.id.textview_from);
        this.textview_to = (TextView) findViewById(R.id.textview_to);
        this.textview_distance = (TextView) findViewById(R.id.textview_distance);
        this.textview_duration = (TextView) findViewById(R.id.textview_duration);
        this.textview_price = (TextView) findViewById(R.id.textview_price);
    }

    private void setUpEvents() {
        this.button_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_from.onEditorAction(EditorInfo.IME_ACTION_DONE);
                edittext_to.onEditorAction(EditorInfo.IME_ACTION_DONE);
                GoogleMapsRestClientDistanceMatrix.getDistanceMatrix(
                        MainActivity.this,
                        edittext_from.getText().toString(),
                        edittext_to.getText().toString(),
                        System.currentTimeMillis()/1000
                );
            }
        });
    }

    public void onOk(JSONObject response) {
        try {
            String origin_addr = response.getJSONArray("origin_addresses").get(0).toString();
            StringBuilder stringBuilderOriginAddr = new StringBuilder();
            for (int i = 0; i < origin_addr.length(); i++)
                if (origin_addr.charAt(i) != '[' && origin_addr.charAt(i) != ']' && origin_addr.charAt(i) != '"')
                    stringBuilderOriginAddr.append(origin_addr.charAt(i));
            final String strCleanOrigin = stringBuilderOriginAddr.toString();

            String destination_addr = response.getJSONArray("destination_addresses").get(0).toString();
            StringBuilder stringBuilderDestinationAddr = new StringBuilder();
            for (int i = 0; i < destination_addr.length(); i++)
                if (destination_addr.charAt(i) != '[' && destination_addr.charAt(i) != ']' && destination_addr.charAt(i) != '"')
                    stringBuilderDestinationAddr.append(destination_addr.charAt(i));
            final String strCleanDestination = stringBuilderDestinationAddr.toString();

            final JSONObject element = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
            final String status = element.getString("status");
            switch (status) {
                case "OK":
                    final JSONObject distance = element.getJSONObject("distance");
                    final JSONObject duration = element.getJSONObject("duration");
                    final String distance_text = distance.getString("text");
                    final String duration_text = duration.getString("text");
                    final Integer distance_in_meters = distance.getInt("value");
                    final Integer duration_in_seconds = duration.getInt("value");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Double price = (((distance_in_meters / 1000.0) * 1.80) + ((duration_in_seconds / 60.0) * 0.30) + 2.50);
                            textview_distance.setText(distance_text);
                            textview_duration.setText(duration_text);
                            textview_price.setText(String.format(Locale.FRENCH, "%.2f", price) + " euros");
                        }
                    });
                    break;
                case "NOT_FOUND":
                    Toast.makeText(this, getString(R.string.api_distance_matrix_unknown_field_for_googlemaps), Toast.LENGTH_SHORT).show();
                    break;
                case "ZERO_RESULTS":
                    Toast.makeText(this, getString(R.string.api_distance_matrix_zero_results), Toast.LENGTH_SHORT).show();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textview_from.setText(strCleanOrigin);
                    textview_to.setText(strCleanDestination);
                    linear_layout_result.setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            Toast.makeText(this, getString(R.string.api_distance_matrix_json_exception), Toast.LENGTH_SHORT).show();
            Log.e("GOOGLEMAPS", e.toString());
        }
    }

    public void onInvalidRequest(JSONObject response) {
        Toast.makeText(this, getString(R.string.api_distance_matrix_invalid_request), Toast.LENGTH_SHORT).show();
        Log.e("GOOGLEMAPS", response.toString());
    }

    public void onMaxElementsExceeded(JSONObject response) {
        Toast.makeText(this, getString(R.string.api_distance_matrix_max_elements_exceeded), Toast.LENGTH_SHORT).show();
        Log.e("GOOGLEMAPS", response.toString());
    }

    public void onOverQueryLimit(JSONObject response) {
        Toast.makeText(this, getString(R.string.api_distance_matrix_over_query_limit), Toast.LENGTH_SHORT).show();
        Log.e("GOOGLEMAPS", response.toString());
    }

    public void onRequestDenied(JSONObject response) {
        Toast.makeText(this, getString(R.string.api_distance_matrix_request_denied), Toast.LENGTH_SHORT).show();
        Log.e("GOOGLEMAPS", response.toString());
    }

    public void onUnknownError(JSONObject response) {
        Toast.makeText(this, getString(R.string.api_distance_matrix_unknown_error), Toast.LENGTH_SHORT).show();
        Log.e("GOOGLEMAPS", response.toString());
    }

}
