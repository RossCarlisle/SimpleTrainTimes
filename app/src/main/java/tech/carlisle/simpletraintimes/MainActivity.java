package tech.carlisle.simpletraintimes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewFrom, autoCompleteTextViewTo;
    private ListView stationView;
    private static String url = "";
    private static Map<String, String> stations;
    private static String[] stationNames, stationCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stations = parseStringArray(R.array.arrayStations);
        stationNames = stations.keySet().toArray(new String[stations.size()]);
        stationCodes = stations.values().toArray(new String[stations.size()]);

        autoCompleteTextViewFrom = findViewById(R.id.fromStation);
        autoCompleteTextViewTo = findViewById(R.id.toStation);
        ArrayAdapter<String> stationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stationNames);
        autoCompleteTextViewFrom.setAdapter(stationsAdapter);
        autoCompleteTextViewTo.setAdapter(stationsAdapter);

        final Button button = findViewById(R.id.findButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                String fromStationName = autoCompleteTextViewFrom.getText().toString();
                String toStationName = autoCompleteTextViewTo.getText().toString();
                String fromStationCode = stations.get(fromStationName);
                String toStationCode = stations.get(toStationName);

                Intent intent = new Intent(v.getContext(), StationsViewActivity.class);
                intent.putExtra("fromStationName", fromStationName);
                intent.putExtra("toStationName", toStationName);
                intent.putExtra("fromStationCode", fromStationCode);
                intent.putExtra("toStationCode", toStationCode);
                startActivity(intent);

            }
        });

    }

    public Map<String,String> parseStringArray(int stringArrayResourceId) {
        String[] stringArray = getResources().getStringArray(stringArrayResourceId);
        Map<String,String> outputMap = new HashMap<String, String>();

        for (String entry : stringArray) {
            String[] splitResult = entry.split("\\|", 2);
            outputMap.put(splitResult[1], splitResult[0]);
        }
        return outputMap;
    }

}
