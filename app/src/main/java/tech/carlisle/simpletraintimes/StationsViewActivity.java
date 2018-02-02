package tech.carlisle.simpletraintimes;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationsViewActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private List<Train> trainList = new ArrayList<>();
    private RecyclerView trainRecyclerView;
    private TrainAdapter trainAdapter;
    private RecyclerView.LayoutManager trainLayoutManager;
    public static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initialise progress dialog and show
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_view);

        //Initialise RecyclerView components
        trainRecyclerView = (RecyclerView) findViewById(R.id.trainRecyclerView);
        trainRecyclerView.setHasFixedSize(true);
        trainLayoutManager = new LinearLayoutManager(this);
        trainRecyclerView.setLayoutManager(trainLayoutManager);
        trainAdapter = new TrainAdapter(trainList);
        trainRecyclerView.setAdapter(trainAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(trainRecyclerView.getContext(),
                1);
        trainRecyclerView.addItemDecoration(dividerItemDecoration);

        //Grab user input data from Intent, thrown from MainActivity class
        Bundle extras = getIntent().getExtras();
        final String fromStationName = extras.getString("fromStationName");
        final String toStationName = extras.getString("toStationName");
        final String fromStationCode = "DMR";//extras.getString("fromStationCode");
        final String toStationCode = "GLQ";//extras.getString("toStationCode");

        setStationTextViews(fromStationName, toStationName);
        final TextView mTextView = findViewById(R.id.infoTextView);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        String url = "https://transportapi.com/v3/uk/train/station/" + fromStationCode + "/live.json?app_id=2dda32f1&app_key=dfe90240b0786883c8a523955c671a83&calling_at=" + toStationCode + "&darwin=false&train_status=passenger";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    final JSONArray trainDepartures = response.getJSONObject("departures").getJSONArray("all");
                    for(int i = 0; i<trainDepartures.length();i++) {

                        String serviceUrl = trainDepartures.getJSONObject(i).getJSONObject("service_timetable").get("id").toString();
                        final int finalI = i;
                        getServiceJSON(serviceUrl, new VolleyCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {

                                try {

                                    JSONArray trainStops = response.getJSONArray("stops");

                                    for (int x = 0; x < trainStops.length(); x++) {

                                        if (trainStops.getJSONObject(x).get("station_code").toString().equals(toStationCode)) {

                                            Train train = new Train(trainDepartures.getJSONObject(finalI).get("aimed_departure_time").toString(), trainDepartures.getJSONObject(finalI).get("platform").toString(), trainStops.getJSONObject(x).get("aimed_arrival_time").toString());
                                            trainList.add(train);
                                            break;

                                        }
                                    }

                                Collections.sort(trainList, Train.trainComparator);
                                trainAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });

        queue.add(jsObjRequest);

    }

    private void setStationTextViews(String fromStation, String toStation) {

        TextView fromStationTextView = findViewById(R.id.fromStationView);
        TextView toStationTextView = findViewById(R.id.toStationView);
        fromStationTextView.setText(fromStation);
        toStationTextView.setText(toStation);

    }

    public void getServiceJSON(String serviceUrl, final VolleyCallback callback) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, serviceUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });

        queue.add(jsonObjReq);
    }
}
