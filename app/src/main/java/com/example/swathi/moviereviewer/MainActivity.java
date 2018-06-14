package com.example.swathi.moviereviewer;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.swathi.moviereviewer.Adapter.GridViewAdapter;
import com.example.swathi.moviereviewer.Datamodel.Images;
import com.example.swathi.moviereviewer.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String result;
    GridView gridView;
    LinearLayout ll;
    RelativeLayout progressBar;
    NetworkUtils networkUtils;
    private GridViewAdapter gdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll = (LinearLayout) findViewById(R.id.no_internet);
        networkUtils = new NetworkUtils(this);
        gridView = (GridView) findViewById(R.id.gridview);
        Log.i("Came Inside out","ghyuhjj");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        startFuntion("popular", "main");
    }




    public void startFuntion(String sortOrder, String fromFn) {
        if(!checkInternetConnection()){
            ll.setVisibility(View.VISIBLE);
            return;
        }
        try {
            result = String.valueOf(new AsyncHttpTask().execute(networkUtils.makeSortOrderUrl(sortOrder).toString(),"GET",fromFn,sortOrder));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doAfterRecieveResult(String res) {
        try {
            result = res;
            JSONObject resultjson = new JSONObject(result);
            ArrayList<JSONObject> allNames = new ArrayList<JSONObject>();
            JSONArray cast = resultjson.getJSONArray("results");
            for (int i=0; i<cast.length(); i++) {
                allNames.add(cast.getJSONObject(i));
            }
            setDataToGridView(allNames,cast);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        public String from = "";
        public String sortOrder = "";
        public String resultData = "";
        @Override
        protected Integer doInBackground(String... params){
            String stringUrl = params[0];
            String method = params[1];
            from = params[2];
            sortOrder = params[3];
            Integer result = 0;
            String inputLine;
            try {
                URL myUrl = new URL(stringUrl);
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                connection.setRequestMethod(method);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                resultData = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                resultData = null;
            }
            return result;
        }
        protected void onPostExecute(Integer result) {
            if(from.equals("main")){
                doAfterRecieveResult(resultData);
            } else {
                onMenuResultreceived(resultData,sortOrder);
            }

        }
    }


    public boolean checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setDataToGridView(ArrayList<JSONObject> resultJson, final JSONArray cast) throws Exception {
        gdAdapter = new GridViewAdapter(this,R.layout.grid_single,getImageResource(resultJson));
        gridView.setAdapter(gdAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,DetailScreen.class);


                try {
                    intent.putExtra("Details",cast.get((int) l).toString());
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.layout.left_animation,R.layout.right_animation).toBundle();
                    startActivity(intent, bndlanimation);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }
        });


    }

    public ArrayList<Images> getImageResource(ArrayList<JSONObject> arr) throws Exception {
        ArrayList<Images> arrayList = new ArrayList<Images>();
        for(int j = 0;j < arr.size();j++){
            JSONObject jb = arr.get(j);
            arrayList.add(new Images(String.valueOf(networkUtils.makeImageUrl("w500")).concat(jb.getString("poster_path"))));
        }
        return arrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public void onMenuResultreceived(final String result, final String sortOrder) {
        JSONObject resultjson = null;
        try {
            resultjson = new JSONObject(result);
            ArrayList<JSONObject> sortOrderJson = new ArrayList<JSONObject>();
            JSONArray movieJson = resultjson.getJSONArray("results");
            for (int i=0; i<movieJson.length(); i++) {
                sortOrderJson.add(movieJson.getJSONObject(i));
            }
            Collections.sort(sortOrderJson,new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    try {
                        if(sortOrder.equals("popular")){
                            return lhs.getString("popularity").compareTo(rhs.getString("popularity"));
                        } else {
                            return lhs.getString("vote_average").compareTo(rhs.getString("vote_average"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            movieJson = sort(movieJson, new Comparator(){
                public int compare(Object a, Object b){
                    JSONObject    ja = (JSONObject)a;
                    JSONObject    jb = (JSONObject)b;
                    if(sortOrder.equals("popular")){
                        return ja.optString("popularity", "").compareTo(jb.optString("popularity", ""));
                    } else {
                        return ja.optString("vote_average", "").compareTo(jb.optString("vote_average", ""));
                    }
                }
            });

            ((GridViewAdapter) gridView.getAdapter()).notifyDataSetChanged();
            setDataToGridView(sortOrderJson,movieJson);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onMovieSortOrderSelected(final String sortOrder) throws Exception {
        if(!checkInternetConnection()) {
            Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }
        startFuntion(sortOrder,"menu");
    }

    public static JSONArray sort(JSONArray array, Comparator c){
        List asList = new ArrayList(array.length());
        for (int i=0; i<array.length(); i++){
            asList.add(array.opt(i));
        }
        Collections.sort(asList, c);
        JSONArray  res = new JSONArray();
        for (Object o : asList){
            res.put(o);
        }
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.popular_movies:
                try{
                    onMovieSortOrderSelected("popular");
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.toprated_movies:
                try{
                    onMovieSortOrderSelected("top_rated");
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    public void startFuntionParam(View view) {
        if(!checkInternetConnection()){
            Toast.makeText(getApplicationContext(),"Please connect to Internet",Toast.LENGTH_SHORT).show();
            return;
        }
        ll.setVisibility(View.GONE);
        startFuntion("popular","main");
    }

}


