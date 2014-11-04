package com.example.matt.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.EntypoIcon;
import com.atermenji.android.iconicdroid.icon.EntypoSocialIcon;
import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
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
    private ProgressDialog loadingDialog;

    public AuthenticationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new ProgressDialog(getActivity());
        loadingDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_rootView = inflater.inflate(R.layout.fragment_authentication, container, false);

        setOnClickListeners();

        setEntypoIcons();

        setTypefaces();

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

    private void setOnClickListeners() {
        Button loginButton = (Button) m_rootView.findViewById(R.id.login_Button);
        loginButton.setOnClickListener(this);

        Button signupButton = (Button) m_rootView.findViewById(R.id.signup_Button);
        signupButton.setOnClickListener(this);

        TextView forgotPasswordText = (TextView) m_rootView.findViewById(R.id.forgotPassword_Text);
        forgotPasswordText.setOnClickListener(this);

        TextView logInText = (TextView) m_rootView.findViewById(R.id.logIn_Text);
        logInText.setOnClickListener(this);

        TextView createLoginText = (TextView) m_rootView.findViewById(R.id.createLogin_Text);
        createLoginText.setOnClickListener(this);
    }

    private void setEntypoIcons() {
        IconicFontDrawable nameIcon = new IconicFontDrawable(getActivity());
        nameIcon.setIcon(EntypoIcon.USER);
        nameIcon.setIconColor(Color.GRAY);
        m_rootView.findViewById(R.id.nameIcon_View).setBackground(nameIcon);

        IconicFontDrawable emailIcon = new IconicFontDrawable(getActivity());
        emailIcon.setIcon(EntypoIcon.MAIL);
        emailIcon.setIconColor(Color.GRAY);
        m_rootView.findViewById(R.id.emailIcon_View).setBackground(emailIcon);

        IconicFontDrawable passwordIcon = new IconicFontDrawable(getActivity());
        passwordIcon.setIcon(EntypoIcon.KEY);
        passwordIcon.setIconColor(Color.GRAY);
        m_rootView.findViewById(R.id.passwordIcon_View).setBackground(passwordIcon);
    }

    private void setTypefaces() {
        String assetPath = "Lato-Regular.ttf";
        Typeface font;
        try {
            font = Typeface.createFromAsset(getActivity().getAssets(), assetPath);
        } catch (Exception e) {
            Log.e(LOG, "Could not get typeface '" + assetPath
                + "' because " + e.getMessage());
            return;
        }
        TextView txt = (TextView) m_rootView.findViewById(R.id.name_EditText);
        txt.setTypeface(font);
        txt = (TextView) m_rootView.findViewById(R.id.email_EditText);
        txt.setTypeface(font);
        txt = (TextView) m_rootView.findViewById(R.id.password_EditText);
        txt.setTypeface(font);
        txt = (TextView) m_rootView.findViewById(R.id.login_Button);
        txt.setTypeface(font);
        txt = (TextView) m_rootView.findViewById(R.id.signup_Button);
        txt.setTypeface(font);
        txt = (TextView) m_rootView.findViewById(R.id.forgotPassword_Text);
        txt.setTypeface(font);
        txt = (TextView) m_rootView.findViewById(R.id.createLogin_Text);
        txt.setTypeface(font);
        txt = (TextView) m_rootView.findViewById(R.id.haveAnAccount_Text);
        txt.setTypeface(font);
        txt = (TextView) m_rootView.findViewById(R.id.logIn_Text);
        txt.setTypeface(font);
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
                forgotPasswordCallback();
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
        loadingDialog.setMessage("Logging in...");
        loadingDialog.show();
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
     * Called when the user clicks the "Forgot password" text
     */
    public void forgotPasswordCallback() {
        (new DialogFragment() {

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View thisDialogView = inflater.inflate(R.layout.dialog_forgotpassword, null);

                builder.setTitle(R.string.reset_password)
                        .setView(thisDialogView)
                        .setPositiveButton(R.string.reset_password, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendPasswordReset(((EditText)thisDialogView.findViewById(R.id.pwemail_EditText)).getText().toString());
                                loadingDialog.setMessage("Loading...");
                                loadingDialog.show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                return builder.create();
            }

        }).show(getFragmentManager(), "login_failed");
    }

    public void sendPasswordReset( String email ){
        ParseUser.requestPasswordResetInBackground(email,
                                                   new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                loadingDialog.dismiss();
                if (e == null) {
                    NotificationDialogFragment notification = new NotificationDialogFragment();
                    notification.setMessage("An email was sent with instructions on how to reset your password.");
                    notification.show(getFragmentManager(), "resetPassword_success");
                } else {
                    NotificationDialogFragment notification = new NotificationDialogFragment();
                    notification.setMessage("Email invalid.");
                    notification.show(getFragmentManager(), "resetPassword_failed");
                }
            }
        });
    }

    /**
     * Switches to a designated view
     */
    public void switchToView( int viewID ) {
        m_rootView.findViewById(R.id.login_Button).setVisibility(viewID == SIGNUP ? View.GONE : View.VISIBLE);
        m_rootView.findViewById(R.id.forgotPassword_Text).setVisibility(viewID == SIGNUP ? View.GONE : View.VISIBLE);
        m_rootView.findViewById(R.id.createLogin_Text).setVisibility(viewID == SIGNUP ? View.GONE : View.VISIBLE);

        m_rootView.findViewById(R.id.nameEditText_Layout).setVisibility(viewID == SIGNUP ? View.VISIBLE : View.GONE);
        m_rootView.findViewById(R.id.line_View).setVisibility(viewID == SIGNUP ? View.VISIBLE : View.GONE);
        m_rootView.findViewById(R.id.space_View).setVisibility(viewID == SIGNUP ? View.GONE : View.VISIBLE);
        m_rootView.findViewById(R.id.signup_Button).setVisibility(viewID == SIGNUP ? View.VISIBLE : View.GONE);
        m_rootView.findViewById(R.id.logInText_Layout).setVisibility(viewID == SIGNUP ? View.VISIBLE : View.GONE);
    }
}
