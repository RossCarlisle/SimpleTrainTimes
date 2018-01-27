package tech.carlisle.simpletraintimes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ross on 27/01/2018.
 */

    public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {

        private List<Train> trainList;

        public class TrainViewHolder extends RecyclerView.ViewHolder {
            public TextView departure, departurePlatform, arrival;

            public TrainViewHolder(View view) {
                super(view);
                departure = (TextView) view.findViewById(R.id.departureTextView);
                departurePlatform = (TextView) view.findViewById(R.id.departurePlatformTextView);
                arrival = (TextView) view.findViewById(R.id.arrivalTextView);
            }
        }

        public TrainAdapter(List<Train> trainList) {
            this.trainList = trainList;
        }

        @Override
        public TrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.train_list_row, parent, false);

            return new TrainViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TrainViewHolder holder, int position) {
            Train train = trainList.get(position);
            holder.departure.setText(train.getDeparture());
            holder.departurePlatform.setText(" Platform " + train.getPlatformDeparture());
            holder.arrival.setText(train.getArrival());
        }

        @Override
        public int getItemCount() {
            return trainList.size();
        }
}
