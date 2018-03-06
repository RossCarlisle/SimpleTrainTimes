package tech.carlisle.simpletraintimes;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

    public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {

        private List<Train> trainList;

        class TrainViewHolder extends RecyclerView.ViewHolder {
            TextView departure, departurePlatform, arrival, arrowIcon, status;

            TrainViewHolder(View view) {
                super(view);
                departure = view.findViewById(R.id.departureTextView);
                departurePlatform = view.findViewById(R.id.departurePlatformTextView);
                arrival = view.findViewById(R.id.arrivalTextView);
                arrowIcon = view.findViewById(R.id.arrowIcon);
                status = view.findViewById(R.id.statusTextView);
            }
        }

        TrainAdapter(List<Train> trainList) {
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
            Typeface font = Typeface.createFromAsset( holder.arrowIcon.getContext().getAssets(), "FontAwesome5Solid.otf" );
            if (train.getPlatformDeparture().equals("null")) {
                holder.departurePlatform.setVisibility(View.INVISIBLE);
            } else {
                String platformText = " Platform " + train.getPlatformDeparture();
                holder.departurePlatform.setText(platformText);
            }
            holder.departure.setText(train.getDeparture());
            holder.arrival.setText(train.getArrival());
            holder.arrowIcon.setTypeface(font);

            setStatusColor(holder, train);
            holder.status.setText(train.getStatus());
        }

        private void setStatusColor(TrainViewHolder holder, Train train){

            String status = train.getStatus();
            switch (status) {
                case "cancelled":
                    holder.status.setTextColor(Color.parseColor("#F44334"));
            }

        }

        @Override
        public int getItemCount() {
            return trainList.size();
        }
}
