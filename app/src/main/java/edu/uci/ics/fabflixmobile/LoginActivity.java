package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_red, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onLoginClicked(View view) {

        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        if (username.equals("") || password.equals("")) {
            Toast.makeText(LoginActivity.this, "Either missing username or password!", Toast.LENGTH_SHORT).show();
        }

        // Post request form data
        final Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);

        // no user is logged in, so we must connect to the server

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        // 10.0.2.2 is the host machine when running the android emulator
        final StringRequest afterLoginRequest = new StringRequest(Request.Method.GET, "https://10.0.2.2:8443/cs122b-spring18-team-90/api/username",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response2", response);
//                        ((TextView) findViewById(R.id.http_response)).setText(response);
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


        final StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://10.0.2.2:8443/cs122b-spring18-team-90/api/android-login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("response", response);
//                            ((TextView) findViewById(R.id.http_response)).setText(response);

                            // Add the request to the RequestQueue.
                            JSONObject jso = new JSONObject(response);
                            if (jso.getString("status").equals("success")) {
                                queue.add(afterLoginRequest);
                                goToSearch();
                            } else {
                                Toast.makeText(LoginActivity.this, "Either username or password wrong!", Toast.LENGTH_SHORT).show();
                                ((TextView) findViewById(R.id.http_response)).setText("Either username or password wrong!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data
        };

        queue.add(loginRequest);
//        goToSearch(view);


//        SafetyNet.getClient(this).verifyWithRecaptcha("6LcqYVsUAAAAAH519TZH2KyPN6dxzXHRyVuNDr3O")
//                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
//                    @Override
//                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
//                        if (!response.getTokenResult().isEmpty()) {
//                            // Add the request to the RequestQueue.
//                            params.put("g-recaptcha-response", response.getTokenResult());
//                            queue.add(loginRequest);
//                        }
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if (e instanceof ApiException) {
//                            ApiException apiException = (ApiException) e;
//                            Log.d("Login", "Error message: " +
//                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
//                        } else {
//                            Log.d("Login", "Unknown type of error: " + e.getMessage());
//                        }
//                    }
//                });

    }

    public void goToSearch() {
        Intent goToIntent = new Intent(this, SearchActivity.class);

        startActivity(goToIntent);
    }
}
