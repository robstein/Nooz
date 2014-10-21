package com.example.matt.myfirstapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AuthenticationFragment.OnAuthenticationFragmentInteractionListener} interface
 * to handle interaction events.
 *
 */
public class AuthenticationFragment extends Fragment
            implements View.OnClickListener
{
    public static final String LOG = "AuthenticationFragment";
    public final static int SIGNUP = 0;
    public final static int LOGIN = 1;

    private View m_rootView;
    private OnAuthenticationFragmentInteractionListener m_listener;

    public AuthenticationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_rootView = inflater.inflate(R.layout.fragment_authentication, container, false);

        Button loginButton = (Button) m_rootView.findViewById(R.id.login_Button);
        loginButton.setOnClickListener(this);

        Button signupButton = (Button) m_rootView.findViewById(R.id.signup_Button);
        signupButton.setOnClickListener(this);

        TextView logInText = (TextView) m_rootView.findViewById(R.id.logIn_Text);
        logInText.setOnClickListener(this);

        TextView createLoginText = (TextView) m_rootView.findViewById(R.id.createLogin_Text);
        createLoginText.setOnClickListener(this);

        return m_rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_listener = (OnAuthenticationFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        m_listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnAuthenticationFragmentInteractionListener {
        public void onAuthenticationConfirmed();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_Button:
                loginCallback();
                break;
            case R.id.signup_Button:
                signupCallback();
                break;
            case R.id.forgotPassword_Text:
                // TODO: INSERT FUNCTION
                break;
            case R.id.createLogin_Text:
                switchToView(SIGNUP);
                break;
            case R.id.logIn_Text:
                switchToView(LOGIN);
                break;
        }
    }

    /**
     * Called when the user clicks the Signup button
     */
    public void signupCallback() {
        /* Record the new user's credentials in Parse */
        ParseUser user = new ParseUser();
        EditText editText = (EditText) m_rootView.findViewById(R.id.name_EditText);
        user.put("Name", editText.getText().toString());
        editText = (EditText) m_rootView.findViewById(R.id.email_EditText);
        final String email = editText.getText().toString();
        user.setUsername(email);
        user.setEmail(email);
        editText = (EditText) m_rootView.findViewById(R.id.password_EditText);
        user.setPassword(editText.getText().toString());

        final ProgressDialog loadingDialog = ProgressDialog.show(getActivity(), "", "Signing up...", true);
        user.signUpInBackground( new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                loadingDialog.dismiss();
                if (e == null) {
                    /* Signup succeeded: Launch a confirmation activity */
                    NotificationDialogFragment notification = new NotificationDialogFragment( new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(m_listener != null) {
                              m_listener.onAuthenticationConfirmed();
                          }
                        }
                    });
                    notification.setMessage("Sign up succeeded.\n\nPlease confirm your email by clicking the link in the message we just sent to " + email + ".");
                    notification.show(getFragmentManager(), "signup_succeeded");
                } else {
                    /* Sign up failed. Look at the ParseException to figure out what went wrong */
                    NotificationDialogFragment notification = new NotificationDialogFragment();
                    notification.setMessage("Sign up failed");
                    notification.show(getFragmentManager(), "signup_failed");
                }
            }
        });
    }

    /**
     * Called when the user clicks the Login button
     */
    public void loginCallback() {
        final ProgressDialog loadingDialog = ProgressDialog.show(getActivity(), "", "Logging in...", true);
        ParseUser.logInInBackground(((EditText) m_rootView.findViewById(R.id.email_EditText)).getText().toString(),
                                    ((EditText) m_rootView.findViewById(R.id.password_EditText)).getText().toString(),
                                    new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                loadingDialog.dismiss();
                if (user != null) {
                    /* Login succeeded: Launch a confirmation activity */
                    if(m_listener != null) {
                        m_listener.onAuthenticationConfirmed();
                    }
                } else {
                    /* Login failed. Look at the ParseException to see what happened. */
                    NotificationDialogFragment notification = new NotificationDialogFragment();
                    notification.setMessage("email/password invalid");
                    notification.show(getFragmentManager(), "login_failed");
                }
            }
        });
    }

    /**
     * Switches to a designated view
     */
    public void switchToView( int viewID  ) {
        m_rootView.findViewById(R.id.login_Button).setVisibility(viewID == SIGNUP ? View.GONE : View.VISIBLE);
        m_rootView.findViewById(R.id.forgotPassword_Text).setVisibility(viewID == SIGNUP ? View.GONE : View.VISIBLE);
        m_rootView.findViewById(R.id.createLogin_Text).setVisibility(viewID == SIGNUP ? View.GONE : View.VISIBLE);

        m_rootView.findViewById(R.id.name_EditText).setVisibility(viewID == SIGNUP ? View.VISIBLE : View.GONE);
        m_rootView.findViewById(R.id.signup_Button).setVisibility(viewID == SIGNUP ? View.VISIBLE : View.GONE);
        m_rootView.findViewById(R.id.logInText_Layout).setVisibility(viewID == SIGNUP ? View.VISIBLE : View.GONE);
    }
}
