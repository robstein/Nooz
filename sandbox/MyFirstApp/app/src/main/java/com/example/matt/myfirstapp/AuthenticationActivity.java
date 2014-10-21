package com.example.matt.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;


public class AuthenticationActivity extends Activity
        implements AuthenticationFragment.OnAuthenticationFragmentInteractionListener
{
    public final static String EXTRA_MESSAGE = "com.example.matt.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new AuthenticationFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* If the user is already logged in, skip the authentication process */
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            onAuthenticationConfirmed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.authentication, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml. */
        switch(item.getItemId())
        {
            case R.id.action_settings:
                // TODO: INSERT FUNCTION
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onAuthenticationConfirmed() {
        final Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Congratulations! You're a shithead!");
        startActivity(intent);
    }
}
