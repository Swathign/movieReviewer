package com.example.swathi.moviereviewer;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.swathi.moviereviewer.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class DetailScreen extends AppCompatActivity {
    JSONObject DetailsObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);

        TextView moviename = (TextView) findViewById(R.id.movie_name);
        TextView movierating = (TextView) findViewById(R.id.movie_rating);
        ImageView movieimage = (ImageView) findViewById(R.id.movie_image);
        TextView moviedescription = (TextView) findViewById(R.id.movie_description);
        TextView movierelease = (TextView) findViewById(R.id.movie_release_year);
        TextView movieVoters = (TextView) findViewById(R.id.movie_voters);

        Intent data = getIntent();
        Bundle b = data.getExtras();

        if(b!=null){
            String tempStr = (String) b.get("Details");



            try {
                DetailsObject = new JSONObject(tempStr);
                String release = DetailsObject.getString("release_date");
                String releaseslice = (String) release.subSequence(0 , 4);
                moviename.setText(DetailsObject.getString("original_title"));
                movierating.setText(DetailsObject.getString("vote_average"));
                moviedescription.setText(DetailsObject.getString("overview"));
                movierelease.setText(releaseslice);
                movieVoters.setText(DetailsObject.getString("vote_count"));
                Picasso.with(getApplicationContext()).load(String.valueOf(NetworkUtils.makeImageUrl("w500")).concat(DetailsObject.getString("poster_path"))).into(movieimage);

            }
            catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }




    }




}