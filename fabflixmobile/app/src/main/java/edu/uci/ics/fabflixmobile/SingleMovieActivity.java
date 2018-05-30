package edu.uci.ics.fabflixmobile;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class SingleMovieActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Bundle bundle = getIntent().getExtras();

        TextView titleView = (TextView)findViewById(R.id.title);
        TextView yearView = (TextView)findViewById(R.id.year);
        TextView directorView = (TextView)findViewById(R.id.director);
        TextView genresView = (TextView)findViewById(R.id.genres);
        TextView starsView = (TextView)findViewById(R.id.stars);

        titleView.setText(bundle.getString("title"));
        yearView.setText("Year: " + bundle.getString("year"));
        directorView.setText("Director: " + bundle.getString("director"));
        genresView.setText("Genres: " + bundle.getString("genres"));
        starsView.setText("Stars: " + bundle.getString("stars"));
    }
}
