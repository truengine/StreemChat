package com.example.truengine.myapplication;

/**
 * Created by truengine on 7/31/2017.
 */

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

public class Register extends AppCompatActivity {
    EditText username, password;
    Button registerButton;
    String user, pass;
    TextView login;

    /**
     * the saved state of the app is stored in a bundle, which is then passed
     * to the onCreate method to restore the state of the registration screen
     *
     * @param  savedInstanceState
     * @link Bundle
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText)findViewById(R.id.etUsername);
        password = (EditText)findViewById(R.id.etPassword);
        registerButton = (Button)findViewById(R.id.signupbtn);
        login = (TextView)findViewById(R.id.login);

        /**
         * Initializes firebase library in app-specific context,
         * ie specific to app activities and intents. Kind of like
         *  assigning a library card (setAndroidContext) to a patron(this).
         * */
        Firebase.setAndroidContext(this);

        /**
         * Links user back to login screen when sign in button is clicked from registration screen
         * */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        /**
         * Converts user inputs (username & password) to strings
         * and checks these are not empty, alphanumeric, and minimum character count
         * A somewhat humorous progress dialogue is shown
         * while the firebase database is accessed via a request to the users node
         * which holds JSON key value pairs for username and password.
         * if the user string does not exist, there is a Toast saying so, and registration is successful
         * or if there is a user match, then another Toast is shown saying the user already exists
         * the progress dialogue ends, and then the login request is added to a new Volley RequestQueue
         * */
        registerButton.setOnClickListener(new View.OnClickListener() {
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
                else if(!user.matches("[A-Za-z0-9]+")){
                    username.setError("only alphabet or number allowed");
                }
                else if(user.length()<4){
                    username.setError("at least 4 characters long");
                }
                else if(pass.length()<5){
                    password.setError("at least 5 characters long");
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(Register.this);
                    pd.setMessage("your salary is being raised...");
                    pd.show();

                    String url = "https://androidchatapp-facea.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://androidchatapp-facea.firebaseio.com/users");

                            if(s.equals("null")) {
                                reference.child(user).child("password").setValue(pass);
                                Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(user)) {
                                        reference.child(user).child("password").setValue(pass);
                                        Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Register.this, "username already exists", Toast.LENGTH_LONG).show();
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
                            System.out.println("" + volleyError );
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                    rQueue.add(request);
                }
            }
        });
    }
}