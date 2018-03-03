package tech.carlisle.simpletraintimes;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileOperations {

    private String fileName;
    private Context context;
    private int maxSearches;

    public FileOperations(String fileName, int maxSearches, Context context) {

        this.fileName = fileName;
        this.context = context;
        this.maxSearches = maxSearches;

    }

    public void removeLine(final int lineIndex) {

        final List<String> lines = new LinkedList<>();
        BufferedReader br = null;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);

            String line = null;
            line = br.readLine();

            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }

        } catch (IOException e) {
            errorIOToast();
        } finally {
            try {
                if (br != null)
                    br.close();

            } catch (IOException e) {
                errorIOToast();
            }
        }

        lines.remove(lineIndex);

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            for (final String currentLine : lines)
                fos.write((currentLine + "\n").getBytes());

        } catch (IOException e) {
            errorIOToast();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                errorIOToast();
            }
        }

/*        final List<String> lines = new LinkedList<>();
        final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
        while (reader.hasNextLine())
            lines.add(reader.nextLine());
        reader.close();
        assert lineIndex >= 0 && lineIndex <= lines.size() - 1;
        lines.remove(lineIndex);
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        for (final String line : lines)
            writer.write(line + "\n");
        writer.flush();
        writer.close();*/
    }

    public void readRecentSearches(ArrayList<RecentTrain> recentList) {

        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String text;
            String[] splitStations;

            while ((text = br.readLine()) != null) {
                splitStations = text.split("->");
                recentList.add(new RecentTrain(splitStations[0], splitStations[1]));
            }

        } catch (IOException e) {
            errorIOToast();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                errorIOToast();
            }
        }
    }

    public void saveToRecentSearches(String fromStationName, String toStationName) {

        String writeData = fromStationName + "->" + toStationName + "\n";
        if (recentSearchesErrorCheck(writeData) != 0) {

        } else {

            FileOutputStream fos = null;

            try {
                fos = context.openFileOutput(fileName, Context.MODE_APPEND);
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
    public int recentSearchesErrorCheck(String writeData) {

        //Removes newline character '\n'
        String searchString = writeData.substring(0, writeData.length() - 1);
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            int lineCount = 0;
            while ((text = br.readLine()) != null) {

                lineCount++;
                if (text.equals(searchString)) {
                    return 1;
                } else if (lineCount > maxSearches - 1) {
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

    private void errorIOToast() {
        Toast toast = Toast.makeText(context.getApplicationContext(), R.string.errorRecentSearchesIO, Toast.LENGTH_LONG);
        toast.show();
    }
}
