package tech.carlisle.simpletraintimes;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecentTrainAdapter extends RecyclerView.Adapter<RecentTrainAdapter.RecentTrainViewHolder> {

    private ArrayList<RecentTrain>  recentTrainList;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {

        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public static class RecentTrainViewHolder extends RecyclerView.ViewHolder {

        public TextView recentTrainFromTextView, recentTrainToTextView, arrowIcon, clickArrowIcon;

        public RecentTrainViewHolder(View itemView, final OnItemClickListener clickListener) {
            super(itemView);
            recentTrainFromTextView = itemView.findViewById(R.id.fromRecentTrain);
            recentTrainToTextView = itemView.findViewById(R.id.toRecentTrain);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            clickArrowIcon = itemView.findViewById(R.id.clickArrowIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener !=null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public RecentTrainAdapter(ArrayList<RecentTrain> recentTrainList) {
        this.recentTrainList = recentTrainList;
    }

    @Override
    public RecentTrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_list_row, parent, false);
        return new RecentTrainViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(RecentTrainViewHolder holder, int position) {

        RecentTrain currentTrain =  recentTrainList.get(position);
        Typeface font = Typeface.createFromAsset( holder.arrowIcon.getContext().getAssets(), "FontAwesome5Solid.otf" );
        holder.recentTrainFromTextView.setText(currentTrain.getRecentTrainFrom());
        holder.recentTrainToTextView.setText(currentTrain.getRecentTrainTo());
        holder.arrowIcon.setTypeface(font);
        holder.clickArrowIcon.setTypeface(font);

    }

    @Override
    public int getItemCount() {
        return recentTrainList.size();
    }
}
