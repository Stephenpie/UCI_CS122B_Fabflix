package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultsActivity extends ActionBarActivity {
    private String query;
    private int page;
    private final RequestQueue queue = NetworkManager.sharedManager(this).queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle bundle = getIntent().getExtras();

        query = bundle.getString("query");
        page = bundle.getInt("page");
        StringRequest searchRequest = setPage();
//        if (query != null && !"".equals(query)) {
//            String url = String.format("http://10.0.2.2:8080/cs122b-spring18-team-90/api/android-search?query=%s&numOfMovies=15&page=%s&sortby=null", query, page);
//
//            final ArrayList<Movie> movies = new ArrayList<>();
//            final StringRequest searchRequest = new StringRequest(Request.Method.GET, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            try {
//                                Log.d("response", response);
//                                int i = 0;
//                                JSONArray jsonarray = new JSONArray(response);
//                                for (; i < jsonarray.length() - 1; i++) {
//                                    JSONObject jso = jsonarray.getJSONObject(i);
//                                    movies.add(new Movie(jso.getString("id"), jso.getString("title"), jso.getString("year"),
//                                            jso.getString("director"), jso.getString("genres"),
//                                            jso.getString("stars")));
//                                }
//                                View prev = findViewById(R.id.prev);
//                                View next = findViewById(R.id.next);
//                                if (page > 1) {
//                                    prev.setVisibility(View.VISIBLE);
//                                }
//                                if (jsonarray.getJSONObject(i).getBoolean("next")) {
//                                    next.setVisibility(View.VISIBLE);
//                                    Log.d("btn","next");
//                                } else {
//                                    Log.d("btn", "no next");
//                                }
//
//
//                                MovieListViewAdapter adapter = new MovieListViewAdapter(movies, getApplicationContext());
//                                ListView listView = (ListView)findViewById(R.id.list);
//                                listView.setAdapter(adapter);
//                            } catch (Exception e) {
//                                Log.d("Error", e.getMessage());
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // error
//                            Log.d("security.error", error.toString());
//                        }
//                    }
//            );

            queue.add(searchRequest);

    }

    public StringRequest setPage() {
        if (query != null && !"".equals(query)) {
            String url = String.format("https://10.0.2.2:8443/cs122b-spring18-team-90/api/android-search?query=%s&numOfMovies=15&page=%s&sortby=null", query, page);

            final ArrayList<Movie> movies = new ArrayList<>();
            final StringRequest searchRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                Log.d("response", response);
                                int i = 0;
                                JSONArray jsonarray = new JSONArray(response);
                                for (; i < jsonarray.length() - 1; i++) {
                                    JSONObject jso = jsonarray.getJSONObject(i);
                                    movies.add(new Movie(jso.getString("id"), jso.getString("title"), jso.getString("year"),
                                            jso.getString("director"), jso.getString("genres"),
                                            jso.getString("stars")));
                                }
                                View prev = findViewById(R.id.prev);
                                View next = findViewById(R.id.next);
                                if (page > 1) {
                                    prev.setVisibility(View.VISIBLE);
                                }
                                if (jsonarray.getJSONObject(i).getBoolean("next")) {
                                    next.setVisibility(View.VISIBLE);
                                }


                                MovieListViewAdapter adapter = new MovieListViewAdapter(movies, getApplicationContext());
                                ListView listView = (ListView) findViewById(R.id.list);
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Movie movie = movies.get(position);
                                        goToSingleMovie(movie);
                                    }
                                });
                            } catch (Exception e) {
                                Log.d("Error", e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("security.error", error.toString());
                        }
                    }
            );
            return searchRequest;
        }
        return null;
    }

    public void onPrevClicked(View view) {
        page--;
        StringRequest searchRequest = setPage();
        queue.add(searchRequest);
    }

    public void onNextClicked(View view) {
        page++;
        StringRequest searchRequest = setPage();
        queue.add(searchRequest);
//        Intent goToIntent = new Intent(this, ResultsActivity.class);
//        goToIntent.putExtra("query", query);
//        goToIntent.putExtra("page", page + 1);
//        startActivity(goToIntent);
    }

    public void goToSingleMovie(Movie movie) {
        Intent goToIntent = new Intent(this, SingleMovieActivity.class);
        goToIntent.putExtra("title", movie.getTitle());
        goToIntent.putExtra("year", movie.getYear());
        goToIntent.putExtra("director", movie.getDirector());
        goToIntent.putExtra("genres", movie.getGenres());
        goToIntent.putExtra("stars", movie.getStars());
        startActivity(goToIntent);
    }

//    public void goToRed(View view) {
//        String msg = ((EditText) findViewById(R.id.blue_2_red_message)).getText().toString();
//
//        Intent goToIntent = new Intent(this, LoginActivity.class);
//
//        goToIntent.putExtra("last_activity", "blue");
//        goToIntent.putExtra("message", msg);
//
//        startActivity(goToIntent);
//    }
//
//    public void goToGreen(View view) {
//        String msg = ((EditText) findViewById(R.id.blue_2_green_message)).getText().toString();
//
//        Intent goToIntent = new Intent(this, SearchActivity.class);
//
//        goToIntent.putExtra("last_activity", "blue");
//        goToIntent.putExtra("message", msg);
//
//        startActivity(goToIntent);
//    }

}
