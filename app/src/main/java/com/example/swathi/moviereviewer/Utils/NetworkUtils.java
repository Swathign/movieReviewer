package com.example.swathi.moviereviewer.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;


import com.example.swathi.moviereviewer.Adapter.GridViewAdapter;
import com.example.swathi.moviereviewer.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import com.example.swathi.moviereviewer.R;


public class NetworkUtils {
    static final String SCHEME = "http";
    static final String AUTHORITY = "api.themoviedb.org";
    static final String VERSION = "3";
//    private static final String API_KEY = getResource.API_KEY;
    static final String AUTHORITY_IMAGE = "image.tmdb.org";
    static final String NAME_1 = "t";
    static final String NAME_2 = "p";
    private static Context context;


    public NetworkUtils(Context c){
        this.context = c;
    }

    public static URL makeImageUrl(String imageSize) throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY_IMAGE)
                .appendPath(NAME_1)
                .appendPath(NAME_2)
                .appendPath(imageSize);
        return new URL(builder.build().toString());
    }

    public static URL makeSortOrderUrl(String sortOrder) throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(VERSION)
                .appendPath("movie")
                .appendPath(sortOrder)
                .appendQueryParameter("api_key",context.getResources().getString(R.string.API_KEY));
        return new URL(builder.build().toString());
    }

}
