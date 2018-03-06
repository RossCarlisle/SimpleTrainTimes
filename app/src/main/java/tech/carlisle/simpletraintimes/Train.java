package tech.carlisle.simpletraintimes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Train {

    private String departure, platformDeparture, arrival, status;

    public Train() {

    }

    public Train(String departure, String platformDeparture, String arrival, String status) {

        this.departure = departure;
        this.platformDeparture = platformDeparture;
        this.arrival = arrival;
        this.status = status;
    }

    public String getDeparture() {
        return departure;
    }

    public String getPlatformDeparture() {
        return platformDeparture;
    }

    public String getArrival() {
        return arrival;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public void setPlatformDeparture(String platformDeparture) {
        this.platformDeparture = platformDeparture;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public static Comparator<Train> trainComparator = new Comparator<Train>() {

        public int compare(Train trainOne, Train trainTwo) {

            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            try {
                Date trainOneDate = dateFormat.parse(trainOne.getDeparture());
                Date trainTwoDate = dateFormat.parse(trainTwo.getDeparture());

                return trainOneDate.compareTo(trainTwoDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return 0;
        }};

}
