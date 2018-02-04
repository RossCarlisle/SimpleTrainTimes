package tech.carlisle.simpletraintimes;

public class RecentTrain {

    private String recentTrainFrom, recentTrainTo;

    public RecentTrain(String recentTrainFrom, String recentTrainTo) {

        this.recentTrainFrom = recentTrainFrom;
        this.recentTrainTo = recentTrainTo;

    }

    public String getRecentTrainFrom() {
        return recentTrainFrom;
    }

    public String getRecentTrainTo() {
        return recentTrainTo;
    }

}
