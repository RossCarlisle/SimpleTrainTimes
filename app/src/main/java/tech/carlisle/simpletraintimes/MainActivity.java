package tech.carlisle.simpletraintimes;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewFrom, autoCompleteTextViewTo;
    private static String[] stations;

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private ListView stationView;
    private static String url = "";
    ArrayList<HashMap<String, String>> contactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        autoCompleteTextViewFrom = findViewById(R.id.fromStation);
        autoCompleteTextViewTo = findViewById(R.id.toStation);
        stations = getResources().getStringArray(R.array.arrayStations);
        ArrayAdapter<String> stationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,stations);
        autoCompleteTextViewFrom.setAdapter(stationsAdapter);
        autoCompleteTextViewTo.setAdapter(stationsAdapter);

        final Button button = findViewById(R.id.findButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

            }
        });

    }





}
