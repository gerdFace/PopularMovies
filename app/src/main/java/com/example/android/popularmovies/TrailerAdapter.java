package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by gerdface on 3/31/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> trailerList = new ArrayList<>();
    private final TrailerAdapterOnClickHandler trailerClickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer selectedTrailer, int shareOrPlay);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler handler) {
        trailerClickHandler = handler;
    }

    public void addTrailers(ArrayList<Trailer> trailers) {
        trailerList.addAll(trailers);
        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutItemId = R.layout.trailer_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(layoutItemId, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.playImage.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        holder.shareImage.setImageResource(R.drawable.ic_share_black_24dp);
        String trailerName = trailerList.get(position).getTrailerName();
        holder.movieName.setText(trailerName);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }


    class TrailerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView playImage;
        ImageView shareImage;
        TextView movieName;
        Context context;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            playImage = (ImageView) itemView.findViewById(R.id.trailer_play_image);
            shareImage = (ImageView) itemView.findViewById(R.id.trailer_share_image);
            movieName = (TextView) itemView.findViewById(R.id.trailer_movie_name);
            playImage.setOnClickListener(this);
            shareImage.setOnClickListener(this);
            movieName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int shareInt = 0;
            int playInt = 1;
            int trailerAdapterPositionOfTrailerSelected = getAdapterPosition();
            Trailer selectedTrailer = trailerList.get(trailerAdapterPositionOfTrailerSelected);

            switch (v.getId()) {
                case R.id.trailer_movie_name:
                case R.id.trailer_play_image:
                    trailerClickHandler.onClick(selectedTrailer, playInt);
                    break;

                case R.id.trailer_share_image:
                    trailerClickHandler.onClick(selectedTrailer, shareInt);
                    break;

                default:
                    break;
            }
        }
    }
}
