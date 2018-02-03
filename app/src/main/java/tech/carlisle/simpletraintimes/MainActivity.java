package tech.carlisle.simpletraintimes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewFrom, autoCompleteTextViewTo;
    private static Map<String, String> stations;
    private static String[] stationNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        stations = parseStringArray(R.array.arrayStations);
        stationNames = stations.keySet().toArray(new String[stations.size()]);

        autoCompleteTextViewFrom = findViewById(R.id.fromStation);
        autoCompleteTextViewTo = findViewById(R.id.toStation);
        ArrayAdapter<String> stationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stationNames);
        autoCompleteTextViewFrom.setAdapter(stationsAdapter);
        autoCompleteTextViewTo.setAdapter(stationsAdapter);

        final Button findTrains = findViewById(R.id.findButton);
        findTrains.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                hideKeyboard();

                String fromStationName = autoCompleteTextViewFrom.getText().toString();
                String toStationName = autoCompleteTextViewTo.getText().toString();

                if(!stations.containsKey(fromStationName)) {
                    showErrorDialog(fromStationName, false);
                } else if (!stations.containsKey(toStationName)) {
                    showErrorDialog(toStationName, true);
                } else {

                    String fromStationCode = stations.get(fromStationName);
                    String toStationCode = stations.get(toStationName);

                    Intent intent = new Intent(v.getContext(), StationsViewActivity.class);
                    intent.putExtra("fromStationName", fromStationName);
                    intent.putExtra("toStationName", toStationName);
                    intent.putExtra("fromStationCode", fromStationCode);
                    intent.putExtra("toStationCode", toStationCode);
                    startActivity(intent);
                }
            }
        });

    }

    private void setupToolbar(){

        Toolbar mainActivityToolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(mainActivityToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

    }
    //Takes in the users string input and a boolean fromStation = False and toStation = True to identify what field contains an error
    private void showErrorDialog(String stationInputError, boolean fromToBoolean) {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        if(stationInputError.isEmpty()) {
            if(fromToBoolean) {
                alertDialog.setMessage("Please enter a destination station");
            } else{
                alertDialog.setMessage("Please enter a origin station");
            }
        } else {
            if (fromToBoolean) {
                alertDialog.setMessage("Error finding destination station: " + stationInputError);
            } else {
                alertDialog.setMessage("Error finding origin station: " + stationInputError);
            }
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public Map<String,String> parseStringArray(int stringArrayResourceId) {
        String[] stringArray = getResources().getStringArray(stringArrayResourceId);
        Map<String,String> outputMap = new HashMap<>();

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
            try{
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (NullPointerException nullError) {

            }

        }

    }
}
