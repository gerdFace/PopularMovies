package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> trailerList = new ArrayList<>();
    private final TrailerAdapterOnClickHandler trailerClickHandler;
    private Context mContext;


    public interface TrailerAdapterOnClickHandler {
        void onPlayTrailerClicked(Trailer selectedTrailer);
        void onShareTrailerClicked(Trailer selectedTrailer);
    }

    public TrailerAdapter(Context context, TrailerAdapterOnClickHandler handler) {
        mContext = context;
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
        String trailerThumbnailUrl = "http://img.youtube.com/vi/" + trailerList.get(position).getTrailerKey() + "/0.jpg";
        Log.v(TAG, "Built Trailer Image URL " + trailerThumbnailUrl);
        Picasso.with(mContext).load(trailerThumbnailUrl).into(holder.mTrailerThumbnail);
        holder.shareImage.setImageResource(R.drawable.ic_share_black_20dp);
        String trailerName = trailerList.get(position).getTrailerName();
        holder.movieName.setText(trailerName);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }


    class TrailerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView mTrailerThumbnail;
        ImageView shareImage;
        TextView movieName;
        Context context;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mTrailerThumbnail = (ImageView) itemView.findViewById(R.id.trailer_video_image);
            shareImage = (ImageView) itemView.findViewById(R.id.trailer_share_image);
            movieName = (TextView) itemView.findViewById(R.id.trailer_movie_name);
            mTrailerThumbnail.setOnClickListener(this);
            shareImage.setOnClickListener(this);
            movieName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int trailerAdapterPositionOfTrailerSelected = getAdapterPosition();
            Trailer selectedTrailer = trailerList.get(trailerAdapterPositionOfTrailerSelected);

            switch (v.getId()) {
                case R.id.trailer_movie_name:
                case R.id.trailer_video_image:
                    trailerClickHandler.onPlayTrailerClicked(selectedTrailer);
                    break;

                case R.id.trailer_share_image:
                    trailerClickHandler.onShareTrailerClicked(selectedTrailer);
                    break;

                default:
                    break;
            }
        }
    }
}
