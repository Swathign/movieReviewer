package com.example.swathi.moviereviewer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.swathi.moviereviewer.Datamodel.Images;
import com.example.swathi.moviereviewer.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class GridViewAdapter extends ArrayAdapter<Images>{
    private int resource;
    private Context mContext;
    private ArrayList<Images> movieImages;

    public GridViewAdapter(Context context,int resource,ArrayList<Images> values){
        super(context, resource, values);
        this.mContext = context;
        this.resource = resource;
        this.movieImages = values;
    }

    public static class ViewHolder {
        public ImageView imgPoster;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder grid;
        if(convertView == null){
            grid = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(this.resource,null,true);
            grid.imgPoster = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(grid);
            Log.i("Came Inside",String.valueOf(position));
        } else {
            grid = (ViewHolder) convertView.getTag();
            Log.i("Came Inside else",String.valueOf(position));
        }
        Log.i("Came Inside out",String.valueOf(position));
        Images images = getItem(position);
        Picasso.with(mContext).load(images.getImage()).into(grid.imgPoster);
        return convertView;
    }


}
