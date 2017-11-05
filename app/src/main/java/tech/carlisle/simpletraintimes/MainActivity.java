package tech.carlisle.simpletraintimes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewFrom, autoCompleteTextViewTo;
    private static String[] stations;

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
    }



}
