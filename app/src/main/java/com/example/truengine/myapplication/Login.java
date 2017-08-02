
/*
* Here, we import the Android elements we will need for our chat app.
* 1. ProgressDialog: used to show progress for tasks
* 2. Intent: used to execute actions with data, such as starting activities; operationally, "connects" activities
* 3. Bundle: used to contain data retrieved from intents; basically, data objects (parcels) can be formed by intents
*     then consumed by bundles.the point is to generate data in one activity and then use it in another activity.
* 4. AppCompatActivity: according to developer.android.com:
*     Base class for activities that use the support library action bar features. So, allows use of action bar features.
* 5. View, widgets: Views are contexts/containers for functional elements (widgets) like (editable) text boxes, buttons, etc.
*    Toast: a special kind of view used to show messages to app user
* 6. volley.Request: used to request/retrieve data from a URL. Here, we need to request JSON objects from our Firebase database
*     Volley: technotalkative.com: library that manages the processing and caching of network requests
* 7. volley.RequestQueue: used to handle (transmit, cache, cancel, etc) requests to database/network communication
* 8. firebase.client.Firebase: Firebase library
* 9. JSONException: used to handle JSON errors
* 10. JSONObject: needed to use JSON objects, which are basically units of database data in key-value pairs
* */


package com.example.truengine.myapplication;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    TextView tv_signup;
    EditText username, password;
    Button signin_btn;
    String user, pass;

    /**
    * the saved state of the app is stored in a bundle, which is then passed
     * to the onCreate method to restore the state of the login screen
     *
     * @param  savedInstanceState
     * @link Bundle
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);
        tv_signup = (TextView)findViewById(R.id.tv_signup);
        username = (EditText)findViewById(R.id.etUsername);
        password = (EditText)findViewById(R.id.etPassword);
        signin_btn = (Button)findViewById(R.id.signin_btn);

/**
 * Links user to registration screen when sign up button is clicked from login screen
 * */
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
/**
 * Converts user inputs (username & password) to strings
 * and checks these are not empty. A progress dialogue is shown
 * while the firebase database is accessed via a request to the users node
 * which holds JSON key value pairs for username and password.
 * if the user string does not exist, there is a Toast saying so,
 * or if there is a user match, but not a password match, then another Toast is shown.
 * the progress dialogue ends, and then the login request is added to a new Volley RequestQueue
 * */
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();

                if(user.equals("")){
                    username.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else{
                    String url = "https://androidchatapp-facea.firebaseio.com/users.json";
                    final ProgressDialog pd = new ProgressDialog(Login.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        startActivity(new Intent(Login.this, Users.class));
                                    }
                                    else {
                                        Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                    rQueue.add(request);
                }

            }
        });
    }
}