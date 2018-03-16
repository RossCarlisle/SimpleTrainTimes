package tech.carlisle.simpletraintimes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StationsViewActivity extends AppCompatActivity {

    final int MAX_REQUESTED_TRAINS = 5;
    private ProgressDialog progressDialog;
    private List<Train> trainList = new ArrayList<>();
    private TrainAdapter trainAdapter;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initialise progress dialog and show
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data");
        progressDialog.setCancelable(true);
        progressDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_view);
        setupToolbar();
        swipeRefresh();

        //Initialise RecyclerView components
        RecyclerView trainRecyclerView = findViewById(R.id.trainRecyclerView);
        trainRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager trainLayoutManager = new LinearLayoutManager(this);
        trainRecyclerView.setLayoutManager(trainLayoutManager);
        trainAdapter = new TrainAdapter(trainList);
        trainRecyclerView.setAdapter(trainAdapter);

        //Add divider to RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(trainRecyclerView.getContext(), 1);
        trainRecyclerView.addItemDecoration(dividerItemDecoration);

        makeTrainRequest();

    }

    private void makeTrainRequest() {

        trainList.clear();
        //Grab user input data from Intent, thrown from MainActivity class
        Bundle extras = getIntent().getExtras();
        final String fromStationName = extras.getString("fromStationName");
        final String toStationName = extras.getString("toStationName");
        final String fromStationCode = extras.getString("fromStationCode");
        final String toStationCode = extras.getString("toStationCode");
        final boolean searchWithTime = getIntent().hasExtra("searchTime");
        String searchTime = null;
        if (searchWithTime) {
            searchTime = extras.getString("searchTime");
        }

        setStationTextViews(fromStationName, toStationName);
        makeSwapButton();
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        String url = null;
        if (searchWithTime) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());
            url = "https://transportapi.com/v3/uk/train/station/" + fromStationCode + "/" + currentDate + "/" + searchTime + "/timetable.json?app_id=" + getString(R.string.transportAppID) + "&app_key=" + getString(R.string.transportAppKey) + "&calling_at=" + toStationCode + "&darwin=false&train_status=passenger&limit=" + MAX_REQUESTED_TRAINS;
        } else {
            url = "https://transportapi.com/v3/uk/train/station/" + fromStationCode + "/live.json?app_id=" + getString(R.string.transportAppID) + "&app_key=" + getString(R.string.transportAppKey) + "&calling_at=" + toStationCode + "&darwin=false&train_status=passenger&limit=" + MAX_REQUESTED_TRAINS;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    final JSONArray trainDepartures = response.getJSONObject("departures").getJSONArray("all");

                    if (trainDepartures.isNull(0) || !trainDepartures.getJSONObject(0).get("mode").equals("train")) {

                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.noTrainsFound, Toast.LENGTH_LONG);
                        toast.show();

                    } else {

                        // For each train we get we will need to make another request to find out its aimed arrival time at the destination station
                        for (int trainIndex = 0; trainIndex < trainDepartures.length(); trainIndex++) {

                            String serviceUrl = trainDepartures.getJSONObject(trainIndex).getJSONObject("service_timetable").get("id").toString();
                            final int trainAddPosition = trainIndex;
                            getServiceJSON(serviceUrl, new VolleyCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {

                                    try {
                                        // Loop through the train service and look for the stationCode the user want to go to
                                        JSONArray trainStops = response.getJSONArray("stops");
                                        for (int stopIndex = 0; stopIndex < trainStops.length(); stopIndex++) {
                                            if (trainStops.getJSONObject(stopIndex).get("station_code").toString().equals(toStationCode)) {
                                                String aimedDepartureTime = trainDepartures.getJSONObject(trainAddPosition).get("aimed_departure_time").toString();
                                                String platform = trainDepartures.getJSONObject(trainAddPosition).get("platform").toString();
                                                String aimedArrivalTime = trainStops.getJSONObject(stopIndex).get("aimed_arrival_time").toString();
                                                String status = "";
                                                if (!searchWithTime) {
                                                    status = trainStops.getJSONObject(stopIndex).get("status").toString().toLowerCase();

                                                    if (status.equals("late")) {
                                                        status = trainDepartures.getJSONObject(trainAddPosition).get("expected_departure_time").toString() + " expected";
                                                    }
                                                }
                                                Train train = new Train(aimedDepartureTime, platform, aimedArrivalTime, status);
                                                trainList.add(train);
                                                break;
                                            }

                                        }

                                        // When trainList size is equal to the trainDepartures size in our first request then we know it is the last train to add so we sort it and update recyclerView
                                        if (trainList.size() == trainDepartures.length()) {
                                            Collections.sort(trainList, Train.trainComparator);
                                            trainAdapter.notifyDataSetChanged();
                                            SwipeRefreshLayout swipeLayout = findViewById(R.id.refreshView);
                                            swipeLayout.setRefreshing(false);
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
                showErrorToast();
            }
        });

        queue.add(jsObjRequest);

    }

    private void swipeRefresh() {

        SwipeRefreshLayout swipeLayout = findViewById(R.id.refreshView);
        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        makeTrainRequest();
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.clearRecentAction);
        item.setVisible(false);
        return true;

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
                showErrorToast();
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

            case R.id.menuRefresh:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showErrorToast() {

        Toast toast = Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_LONG);
        toast.show();

    }

    private void makeSwapButton() {

        Typeface font = Typeface.createFromAsset(getAssets(), "FontAwesome5Solid.otf");
        Button swapButton = findViewById(R.id.swapButtonStationView);
        swapButton.setTypeface(font);
        swapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bundle extras = getIntent().getExtras();
                Intent intent = new Intent(getApplicationContext(), StationsViewActivity.class);
                intent.putExtra("fromStationName", extras.getString("toStationName"));
                intent.putExtra("toStationName", extras.getString("fromStationName"));
                intent.putExtra("fromStationCode", extras.getString("toStationCode"));
                intent.putExtra("toStationCode", extras.getString("fromStationCode"));
                final boolean searchWithTime = getIntent().hasExtra("searchTime");
                if (searchWithTime) {
                    intent.putExtra("searchTime", extras.getString("searchTime"));
                }
                startActivity(intent);
                finish();
            }
        });

    }
}
