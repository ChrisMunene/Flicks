package com.example.flicks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    //List of movies
    ArrayList<Movie> movies;
    //Config for image Urls
    Config config;
    //Rendering context
    Context context;

//    Initialize movie list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    // Creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Get context and create inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Create the view using the item movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);

        //return new ViewHolder
        return new ViewHolder(movieView);
    }

    // Binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the movie data at the specified position
        Movie movie = movies.get(position);
        // Populate view with movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        //build poster image url
        String imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());

        //load image with placeholder and rounded corners
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(holder.ivPosterImage);
    }

    // Returns total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Create Viewholder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPosterImage;
        TextView tvTitle;
        TextView tvOverview;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tVOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

        }
    }
}
