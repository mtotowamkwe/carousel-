package com.merchant.explorer.marcopolo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CarouselFragment extends Fragment {

    private static final String TAG = "Carousel";

    private RecyclerView mCarouselRecyclerView;
    private List<MovieItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<MovieItemHolder> mThumbnailDownloader;


    public static Fragment newInstance() {
        return new CarouselFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchMovieItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<MovieItemHolder>() {
                    @Override
                    public void onThumbnailDownloaded(MovieItemHolder movieItemHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        movieItemHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_marco_polo_store_front_activity, container, false);
        mCarouselRecyclerView = (RecyclerView) v
                .findViewById(R.id.fragment_carousel_recycler_view);
        mCarouselRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if (isAdded()) {
            mCarouselRecyclerView.setAdapter(new MovieItemAdapter(mItems));
        }
    }

    private class MovieItemHolder extends RecyclerView.ViewHolder {
        private TextView mTrackNameTextView;
        private TextView mShortDescriptionTextView;
        private TextView mReleaseDateTextView;
        private ImageView mItemImageView;

        public MovieItemHolder(View itemView) {
            super(itemView);

            mTrackNameTextView = itemView.findViewById(R.id.track_name_text_view);
            mShortDescriptionTextView = itemView.findViewById(R.id.short_description_text_view);
            mReleaseDateTextView = itemView.findViewById(R.id.release_date_text_view);
            mItemImageView = itemView.findViewById(R.id.fragment_movie_item_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindGalleryItem(MovieItem item) {
            mTrackNameTextView.setText(item.getTrackName());
            mShortDescriptionTextView.setText(item.getShortDescription());
            mReleaseDateTextView.setText(item.getReleaseDate());
        }
    }

    private class MovieItemAdapter extends RecyclerView.Adapter<MovieItemHolder> {
        private List<MovieItem> mMovieItems;
        public MovieItemAdapter(List<MovieItem> movieItems) {
            mMovieItems = movieItems;
        }
        @Override
        public MovieItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new MovieItemHolder(view);
        }
        @Override
        public void onBindViewHolder(MovieItemHolder movieItemHolder, int position) {
            MovieItem movieItem = mMovieItems.get(position);
            movieItemHolder.bindGalleryItem(movieItem);
            Drawable placeholderImage = getResources().getDrawable(R.drawable.aint_no_telling);
            // Just an image to keep the user occupied as movie images are fetched
            movieItemHolder.bindDrawable(placeholderImage);
            mThumbnailDownloader.queueThumbnail(movieItemHolder, movieItem.getArtworkUrl100());
        }
        @Override
        public int getItemCount() {
            return mMovieItems.size();
        }
    }

    private class FetchMovieItemsTask extends AsyncTask<Void,Void,List<MovieItem>> {
        @Override
        protected List<MovieItem> doInBackground(Void... params) {
            return new MovieItemFetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(List<MovieItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
}
