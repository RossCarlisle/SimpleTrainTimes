package tech.carlisle.simpletraintimes;

/**
 * Created by Ross on 27/01/2018.
 */

public class Train {

    private String departure, platformDeparture, arrival;

    public Train() {

    }

    public Train(String departure, String platformDeparture, String arrival) {

        this.departure = departure;
        this.platformDeparture = platformDeparture;
        this.arrival = arrival;

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

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public void setPlatformDeparture(String platformDeparture) {
        this.platformDeparture = platformDeparture;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }
}
