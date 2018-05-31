package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class SearchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

    }


    public void goToResults(View view) {
        Intent goToIntent = new Intent(this, ResultsActivity.class);
        String query = ((EditText) findViewById(R.id.query)).getText().toString();
        if (query.length() != 0) {
            goToIntent.putExtra("query", query);
            goToIntent.putExtra("page", 1);
            startActivity(goToIntent);
        }
    }

    public void onSearchClicked(View view) {
        goToResults(view);
    }

}
