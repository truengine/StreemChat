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
*
* 11. ArrayList: we need this to store list of users available to chat
*     developer.android.com:
*     Resizable-array implementation of the List interface.
*     Implements all optional list operations, and permits all elements, including null
* 12. Iterator: allows us to traverse and manipulate array items
* */
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Users extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;

    /**
     * the saved state of the app is stored in a bundle, which is then passed
     * to the onCreate method to restore the state of the users screen. Shows
     * list of registered users, if any, else a message saying no users
     *
     * @param  savedInstanceState
     * @link Bundle
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        usersList = (ListView) findViewById(R.id.usersList);
        noUsersText = (TextView) findViewById(R.id.noUsersText);

        pd = new ProgressDialog(Users.this);
        pd.setMessage("Loading...");
        pd.show();

        /**
         * Request made to firebase database. If database is accessible (on success)
         * the database is iterated-through/ traversed to check for new users.
         * if new users exist, then the username is added to the list on the screen
         * and the total users variable is adjusted. if total users is 0, then the list
         * is invisible and a no user message is shown.
         * for the subsequent list of users on the screen, each user name is clickable.
         * if clicked, the user is linked from the users screen to the chat screen
         * */

        String url = "https://androidchatapp-facea.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Users.this);
        rQueue.add(request);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al.get(position);
                startActivity(new Intent(Users.this, Chat.class));
            }
        });
    }

    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()) {
                key = i.next().toString();

                if (!key.equals(UserDetails.username)) {
                    al.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalUsers <= 1) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }

        pd.dismiss();
    }
}
