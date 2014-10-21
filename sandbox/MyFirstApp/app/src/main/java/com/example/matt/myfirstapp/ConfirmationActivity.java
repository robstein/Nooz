package com.example.matt.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;


public class ConfirmationActivity extends Activity
        implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirmation);

        /* Get the message from the intent */
        Intent intent = getIntent();
        String message = intent.getStringExtra(AuthenticationActivity.EXTRA_MESSAGE);
        TextView messageView = (TextView) findViewById(R.id.message_Text);
        messageView.setText(message);

        Button logoutButton = (Button) findViewById(R.id.logout_Button);
        logoutButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* If the user is not logged in, go to the Authentication Activity */
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            final Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.confirmation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml. */
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_Button:
                logoutCallback();
                break;
        }
    }

    /**
     * Called when the user clicks the Logout button
     */
    public void logoutCallback() {
        ParseUser.logOut();
        final Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
    }
}
