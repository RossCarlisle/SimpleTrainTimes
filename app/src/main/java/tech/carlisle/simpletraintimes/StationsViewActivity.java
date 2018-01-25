package tech.carlisle.simpletraintimes;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StationsViewActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_view);

        Bundle extras = getIntent().getExtras();

        String fromStationName = extras.getString("fromStationName");
        String toStationName = extras.getString("toStationName");
        String fromStationCode = extras.getString("fromStationCode");
        String toStationCode = extras.getString("toStationCode");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();

        final TextView mTextView = findViewById(R.id.infoTextView);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://transportapi.com/v3/uk/train/station/DMR/2018-01-25/12:53/timetable.json?app_id=2dda32f1&app_key=dfe90240b0786883c8a523955c671a83&calling_at=GLQ&train_status=passenger";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


}
