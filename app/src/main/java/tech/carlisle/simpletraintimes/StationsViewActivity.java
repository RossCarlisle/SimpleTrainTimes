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
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StationsViewActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private List<Train> trainList = new ArrayList<>();
    private RecyclerView trainRecyclerView;
    private TrainAdapter trainAdapter;
    private RecyclerView.LayoutManager trainLayoutManager;
    public String arrival;
    public List<String> arrivalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_view);

        trainRecyclerView = (RecyclerView) findViewById(R.id.trainRecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        trainRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        trainLayoutManager = new LinearLayoutManager(this);
        trainRecyclerView.setLayoutManager(trainLayoutManager);
        // specify an adapter (see also next example)
        trainAdapter = new TrainAdapter(trainList);
        trainRecyclerView.setAdapter(trainAdapter);

        Bundle extras = getIntent().getExtras();

        String fromStationName = extras.getString("fromStationName");
        String toStationName = extras.getString("toStationName");
        String fromStationCode = extras.getString("fromStationCode");
        final String toStationCode = "GLQ";//extras.getString("toStationCode");

        final TextView mTextView = findViewById(R.id.infoTextView);
        final TextView fromStationTextView = findViewById(R.id.fromStationView);
        final TextView toStationTextView = findViewById(R.id.toStationView);
        fromStationTextView.setText(fromStationName);
        toStationTextView.setText(toStationName);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url ="https://transportapi.com/v3/uk/train/station/DMR/2018-01-25/12:53/timetable.json?app_id=2dda32f1&app_key=dfe90240b0786883c8a523955c671a83&calling_at=GLQ&train_status=passenger";

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray trainDepartures = response.getJSONObject("departures").getJSONArray("all");

                            for(int i = 0; i < trainDepartures.length(); i++) {

                                String serviceUrl = trainDepartures.getJSONObject(i).getJSONObject("service_timetable").get("id").toString();
                                getServiceJSON(serviceUrl, new VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) {

                                        try {
                                            JSONArray trainStops = response.getJSONArray("stops");
                                            for (int x = 0; x < trainStops.length(); x++) {

                                                if (trainStops.getJSONObject(x).get("station_code").toString().equals("GLQ")) {

                                                    arrivalList.add(trainStops.getJSONObject(x).get("aimed_arrival_time").toString());
                                                    mTextView.setText(mTextView.getText() + " " + trainStops.getJSONObject(x).get("aimed_arrival_time"));
                                                    break;

                                                }
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                //String serviceID = trainDepartures.getJSONObject(i).get("service_timetable").toString();
                                Train train = new Train(trainDepartures.getJSONObject(i).get("aimed_departure_time").toString(), trainDepartures.getJSONObject(i).get("platform").toString(), "placeholder");
                                trainList.add(train);
                                trainAdapter.notifyDataSetChanged();
                            }

                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        mTextView.setText("Network error");
                    }
                });

        // Add the request to the RequestQueue.
        for(int i = 0; i < arrivalList.size(); i++) {

            trainList.get(i).setArrival(arrivalList.get(i));

        }


        trainAdapter.notifyDataSetChanged();
        queue.add(jsObjRequest);



    }

    public String findArrivalTime(){


        return "x";
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

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjReq);
    }



}
