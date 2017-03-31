package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gerdface on 3/31/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> trailerList = new ArrayList<>();
    private Context context;
    private final TrailerAdapterOnClickHandler trailerClickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer selectedTrailer);
    }

    public TrailerAdapter(Context context, TrailerAdapterOnClickHandler handler) {
        this.context = context;
        this.trailerClickHandler = handler;
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
        holder.imageView.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
        String trailerName = trailerList.get(position).getTrailerName();
        holder.textView.setText(trailerName);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }


    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        Context context;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.trailer_movie_image);
            textView = (TextView) itemView.findViewById(R.id.trailer_movie_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int trailerAdapterPositionOfTrailerSelected = getAdapterPosition();
            Trailer selectedTrailer = trailerList.get(trailerAdapterPositionOfTrailerSelected);
            trailerClickHandler.onClick(selectedTrailer);
        }
    }
}
