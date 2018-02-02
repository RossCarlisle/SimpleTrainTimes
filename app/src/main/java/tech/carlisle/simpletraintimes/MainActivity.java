package tech.carlisle.simpletraintimes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

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

                hideKeyboard();

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

    private void hideKeyboard() {

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }
}
