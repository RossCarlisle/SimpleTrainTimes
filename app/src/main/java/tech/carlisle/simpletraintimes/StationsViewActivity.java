package tech.carlisle.simpletraintimes;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        final int maxShownTrains = 5; //Maximum amount of trains to show in the RecyclerView

        setStationTextViews(fromStationName, toStationName);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        String url = "https://transportapi.com/v3/uk/train/station/" + fromStationCode + "/live.json?app_id=" + getString(R.string.transportAppID) + "&app_key=" + getString(R.string.transportAppKey) + "&calling_at=" + toStationCode + "&darwin=false&train_status=passenger";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    final JSONArray trainDepartures = response.getJSONObject("departures").getJSONArray("all");

                    if (trainDepartures.isNull(0)) {

                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Could not find any trains", Toast.LENGTH_LONG);
                        toast.show();

                    } else {

                        int displayedTrains;
                        if (trainDepartures.length() > maxShownTrains) {
                            displayedTrains = maxShownTrains;
                        } else
                            displayedTrains = trainDepartures.length();

                        for (int i = 0; i < displayedTrains; i++) {

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
}
