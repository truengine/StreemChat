package com.example.truengine.myapplication;

/**
 * Created by truengine on 7/31/2017.
 */

/*
* Here, we import the Android elements we will need for our chat app.
* 1. Bundle: used to contain data retrieved from intents; basically, data objects (parcels) can be formed by intents
*     then consumed by bundles.the point is to generate data in one activity and then use it in another activity.
* 2. AppCompatActivity: according to developer.android.com:
*     Base class for activities that use the support library action bar features. So, allows use of action bar features.
* 3. View, widgets: Views are contexts/containers for functional elements (widgets) like (editable) text boxes, buttons, etc.
* 4. firebase.client.Firebase: Firebase library
* 5. util.HashMap, util.Map: developer.android.com:
*    Hash table based implementation of the Map interface: An object that maps keys to values
* 6. DataSnapshot: from firebase.com:
*           An DataSnapshot instance contains data from a Firebase location.
*           Any time you read Firebase data, you receive the data as a DataSnapshot.
*           DataSnapshots are passed to the methods in listeners that you
*           attach with Query.addValueEventListener(ValueEventListener),
*           Query.addChildEventListener(ChildEventListener),
*           or Query.addListenerForSingleValueEvent(ValueEventListener).
*           They are efficiently-generated immutable copies of the data at a Firebase location.
*           They can't be modified and will never change.
*     So, DataSnapshots are ideal for chat apps where one can store and preserve conversation histories
* */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;

    /**
     * the saved state of the app is stored in a bundle, which is then passed
     * to the onCreate method to restore the state of the chat screen.
     *
     * @param  savedInstanceState
     * @link Bundle
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        /**
         * Initializes firebase library in app-specific context,
         * ie specific to app activities and intents. Kind of like
         *  assigning a library card (setAndroidContext) to a patron(this).
         *  Here, Firebase data elements (references) are initialized to store
         *  communication 'signatures' which will be node names.
         *  nested in these nodes are chat data.
         * */

        Firebase.setAndroidContext(this);


        reference1 = new Firebase("https://androidchatapp-facea.firebaseio.com/messages" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://androidchatapp-facea.firebaseio.com/messages" + UserDetails.chatWith + "_" + UserDetails.username);

        /**
         * When the send button is clicked, the messagearea (EditText) data is converted to a string.
         * if the string is valid, it is stored to a HashMap along with the user data,
         * namely, the usernames of those in the chat, and stored in the database.
         * Then, the messagearea is cleared via setText.
         * */

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        /**
         * When a message is sent, the Firebase message data references are modified and then stored
         * in the database/added to HashMap. We have an event listener for when this happens. If a message
         * is sent and YOU are the sender (ie, reference1), then a message bubble appears on the screen
         * prepended by "you:", otherwise (if reference2), the message is prepended with the username of
         * the person you are chatting with. The message bubble is determined by a method where
         * user 1 (reference1) has a blue, left aligned image bubble, and user 2 (reference2) has a right aligned
         * orange/yellow one.
         * It is important to note a scroll view is used to handle dynamic generation of screen elements.
         * */

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox("You:\n" + message, 1);
                }
                else{
                    addMessageBox(UserDetails.chatWith + ":\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);


        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
