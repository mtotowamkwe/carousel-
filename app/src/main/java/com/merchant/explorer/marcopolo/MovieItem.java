package com.merchant.explorer.marcopolo;

import androidx.annotation.NonNull;

public class MovieItem {
    private String mTrackName;
    private String mReleaseDate;
    private String mArtworkUrl100;
    private String mShortDescription;

    @NonNull
    @Override
    public String toString() {
        return "MovieItem: { " +
                "trackName: " +
                mTrackName +
                ", releaseDate: " +
                mReleaseDate +
                ", artworkUrl100: " +
                mArtworkUrl100 +
                ", shortDescription: " +
                mShortDescription +
                "}";
    }

    public String getTrackName() {
        return mTrackName;
    }

    public void setTrackName(String trackName) {
        mTrackName = trackName;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        String[] date = releaseDate.split("T", 2);
        mReleaseDate = date[0];
    }

    public String getArtworkUrl100() {
        return mArtworkUrl100;
    }

    public void setArtworkUrl100(String artworkUrl100) {
        mArtworkUrl100 = artworkUrl100;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        mShortDescription = shortDescription;
    }
}
