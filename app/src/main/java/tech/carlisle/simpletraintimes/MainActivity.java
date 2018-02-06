package tech.carlisle.simpletraintimes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewFrom, autoCompleteTextViewTo;
    private static Map<String, String> stations;
    private static final String FILE_NAME = "recentSearches.txt";
    private static final int MAX_RECENT_SEARCHES = 20;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        makeSwapButton();

        stations = parseStringArray(R.array.arrayStations);
        String[] stationNames = stations.keySet().toArray(new String[stations.size()]);
        loadRecentSearches();

        autoCompleteTextViewFrom = findViewById(R.id.fromStation);
        autoCompleteTextViewTo = findViewById(R.id.toStation);
        ArrayAdapter<String> stationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stationNames);
        autoCompleteTextViewFrom.setAdapter(stationsAdapter);
        autoCompleteTextViewTo.setAdapter(stationsAdapter);

        Button findTrains = findViewById(R.id.findButton);
        findTrains.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                clearUi();
                String fromStationName = autoCompleteTextViewFrom.getText().toString();
                String toStationName = autoCompleteTextViewTo.getText().toString();

                if (fromStationName.isEmpty()) {
                    showErrorDialog(fromStationName, 0);
                } else if (toStationName.isEmpty()) {
                    showErrorDialog(toStationName, 1);
                } else if (fromStationName.equals(toStationName)) {
                    showErrorDialog(fromStationName, 2);
                } else if (!stations.containsKey(fromStationName)) {
                    showErrorDialog(fromStationName, 3);
                } else if (!stations.containsKey(toStationName)) {
                    showErrorDialog(toStationName, 4);
                } else {

                    saveToRecentSearches(fromStationName, toStationName);
                    searchTrains(fromStationName, toStationName);

                }
            }
        });

    }

    private void makeSwapButton() {

        Typeface font = Typeface.createFromAsset( getAssets(), "FontAwesome5Solid.otf" );
        Button swapButton = findViewById(R.id.swapButton);
        swapButton.setTypeface(font);
        swapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String tempToStation;
                tempToStation = autoCompleteTextViewTo.getText().toString();
                autoCompleteTextViewTo.setText(autoCompleteTextViewFrom.getText().toString());
                autoCompleteTextViewFrom.setText(tempToStation);
                autoCompleteTextViewTo.clearFocus();
                autoCompleteTextViewFrom.clearFocus();
            }
        });

    }

    private void searchTrains(String fromStationName, String toStationName) {

        String fromStationCode = stations.get(fromStationName);
        String toStationCode = stations.get(toStationName);
        Intent intent = new Intent(getApplicationContext(), StationsViewActivity.class);
        intent.putExtra("fromStationName", fromStationName);
        intent.putExtra("toStationName", toStationName);
        intent.putExtra("fromStationCode", fromStationCode);
        intent.putExtra("toStationCode", toStationCode);
        startActivity(intent);

    }

    public void saveToRecentSearches(String fromStationName, String toStationName) {

        String writeData = fromStationName + "->" + toStationName + "\n";
        if (recentSearchesErrorCheck(writeData) != 0) {

        } else {

            FileOutputStream fos = null;

            try {
                fos = openFileOutput(FILE_NAME, MODE_APPEND | MODE_PRIVATE);
                fos.write(writeData.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*  Takes in a string to check if it is already in the text file and also to check that there
        is not too many recentSearches. Returns 0 if no errors, returns 1 if input is already present
        and returns 2 if max line count has been reached.
     */
    private int recentSearchesErrorCheck(String writeData) {

        //Removes newline character '\n'
        String searchString = writeData.substring(0, writeData.length() - 1);
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            int lineCount = 0;
            while ((text = br.readLine()) != null) {

                lineCount++;
                if (text.equals(searchString)) {
                    return 1;
                } else if (lineCount > MAX_RECENT_SEARCHES - 1) {
                    return 2;
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public void readRecentSearches(ArrayList<RecentTrain> recentList) {

        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            String[] splitStations;

            while ((text = br.readLine()) != null) {

                splitStations = text.split("->");
                recentList.add(new RecentTrain(splitStations[0], splitStations[1]));

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public void loadRecentSearches() {

        RecyclerView recentTrainRecyclerView;
        RecentTrainAdapter recentTrainAdapter;
        RecyclerView.LayoutManager recentTrainLayoutManager;
        final ArrayList<RecentTrain> recentList = new ArrayList<>();
        readRecentSearches(recentList);
        recentTrainRecyclerView = findViewById(R.id.recentSearchesRecyclerView);
        TextView recentTrainsEmpty = findViewById(R.id.recentTrainsEmpty);
        if (recentList.isEmpty()) {
            recentTrainsEmpty.setVisibility(View.VISIBLE);
            recentTrainRecyclerView.setVisibility(View.INVISIBLE);
        } else {

            recentTrainsEmpty.setVisibility(View.GONE);
            recentTrainRecyclerView.setVisibility(View.VISIBLE);
            recentTrainRecyclerView.setHasFixedSize(true);
            recentTrainLayoutManager = new LinearLayoutManager(this);
            recentTrainAdapter = new RecentTrainAdapter(recentList);

            recentTrainRecyclerView.setLayoutManager(recentTrainLayoutManager);
            recentTrainRecyclerView.setAdapter(recentTrainAdapter);
            recentTrainAdapter.setOnItemClickListener(new RecentTrainAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    searchTrains(recentList.get(position).getRecentTrainFrom(), recentList.get(position).getRecentTrainTo());
                }
            });
        }
    }

    private void setupToolbar() {

        Toolbar mainActivityToolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(mainActivityToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

    }

    /*  Takes in the users string input and a int to say which error has occurred
        errorCode 0 means no data input on origin station, errorCode 1 means no data input on destination station.
        errorCode 2 means fromStation = toStation,
        errorCode 3 means input error on origin station, errorCode 4 means means input error on destination station,
     */
    private void showErrorDialog(String stationInputError, int errorCode) {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");

        switch (errorCode) {

            case 0:
                alertDialog.setMessage("Please enter a origin station");
                break;
            case 1:
                alertDialog.setMessage("Please enter a destination station");
                break;
            case 2:
                alertDialog.setMessage("Origin destination is the same as destination station.");
                break;
            case 3:
                alertDialog.setMessage("Error finding origin station: " + stationInputError);
                break;
            case 4:
                alertDialog.setMessage("Error finding destination station: " + stationInputError);
                break;

        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public Map<String, String> parseStringArray(int stringArrayResourceId) {
        String[] stringArray = getResources().getStringArray(stringArrayResourceId);
        Map<String, String> outputMap = new HashMap<>();

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
            try {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (NullPointerException nullError) {
                Log.e("hideKeyboardException", "Failed to close keyboard null: " + nullError);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.clearRecentAction:
                File directory = getFilesDir();
                File deleteFile = new File(directory, FILE_NAME);
                boolean deleteBoolean = deleteFile.delete();
                loadRecentSearches();
                return deleteBoolean;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        loadRecentSearches();
    }

    private void clearUi() {
        hideKeyboard();
        autoCompleteTextViewTo.clearFocus();
        autoCompleteTextViewFrom.clearFocus();
    }

    //Used for when user clicks in the whitespace to close the keyboard
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clearUi();
        return true;
    }
}
