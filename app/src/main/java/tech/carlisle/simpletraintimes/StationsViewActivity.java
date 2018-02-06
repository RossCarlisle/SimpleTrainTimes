package tech.carlisle.simpletraintimes;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

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
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final int maxShownTrains = 5; //Maximum amount of trains to get from JSON Request

        //Initialise progress dialog and show
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_view);
        setupToolbar();

        //Initialise RecyclerView components
        trainRecyclerView = findViewById(R.id.trainRecyclerView);
        trainRecyclerView.setHasFixedSize(true);
        trainLayoutManager = new LinearLayoutManager(this);
        trainRecyclerView.setLayoutManager(trainLayoutManager);
        trainAdapter = new TrainAdapter(trainList);
        trainRecyclerView.setAdapter(trainAdapter);

        //Add divider to RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(trainRecyclerView.getContext(),1);
        trainRecyclerView.addItemDecoration(dividerItemDecoration);

        //Grab user input data from Intent, thrown from MainActivity class
        Bundle extras = getIntent().getExtras();
        String fromStationName = extras.getString("fromStationName");
        String toStationName = extras.getString("toStationName");
        String fromStationCode = extras.getString("fromStationCode");
        final String toStationCode = extras.getString("toStationCode");

        setStationTextViews(fromStationName, toStationName);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        String url = "https://transportapi.com/v3/uk/train/station/" + fromStationCode + "/live.json?app_id=" + getString(R.string.transportAppID) + "&app_key=" + getString(R.string.transportAppKey) + "&calling_at=" + toStationCode + "&darwin=false&train_status=passenger&limit=" + maxShownTrains;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    final JSONArray trainDepartures = response.getJSONObject("departures").getJSONArray("all");

                    if (trainDepartures.isNull(0) || !trainDepartures.getJSONObject(0).get("mode").equals("train")) {

                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Could not find any trains", Toast.LENGTH_LONG);
                        toast.show();

                    } else {

                        for (int trainIndex = 0; trainIndex < trainDepartures.length(); trainIndex++) {

                            String serviceUrl = trainDepartures.getJSONObject(trainIndex).getJSONObject("service_timetable").get("id").toString();
                            final int trainAddPosition = trainIndex;
                            getServiceJSON(serviceUrl, new VolleyCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {

                                    try {

                                        JSONArray trainStops = response.getJSONArray("stops");
                                        for (int stopIndex = 0; stopIndex < trainStops.length(); stopIndex++) {
                                            if (trainStops.getJSONObject(stopIndex).get("station_code").toString().equals(toStationCode)) {
                                                Train train = new Train(trainDepartures.getJSONObject(trainAddPosition).get("aimed_departure_time").toString(), trainDepartures.getJSONObject(trainAddPosition).get("platform").toString(), trainStops.getJSONObject(stopIndex).get("aimed_arrival_time").toString());
                                                trainList.add(train);
                                                break;
                                            }

                                        }

                                        if (trainList.size() == trainDepartures.length()) {
                                            Collections.sort(trainList, Train.trainComparator);
                                            trainAdapter.notifyDataSetChanged();
                                            progressDialog.dismiss();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(), "Network error while finding trains", Toast.LENGTH_LONG);
                toast.show();
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

    private void setupToolbar() {

        Toolbar StationsViewActivityToolbar = findViewById(R.id.stationsViewActivityToolbar);
        setSupportActionBar(StationsViewActivityToolbar);
        getSupportActionBar().setTitle(R.string.stationsViewTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
