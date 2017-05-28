package com.meetapp.onlinechat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import include.OnlineDating;
import include.ProgressBarHandler;
import include.User;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mUserName;
    private Spinner mSelectGender;
    private Spinner mSelectAge;
    private OnlineDating mOnlineDating;

    private String age;
    private String gender;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOnlineDating = new OnlineDating();
        mOnlineDating.init(this.getApplicationContext());

        if (mOnlineDating.getSesson()) {

            setContentView(R.layout.activity_chat_view);
            finish();
            Intent intent = new Intent(getBaseContext(), ChatView.class);
            startActivity(intent);

        } else {
            setContentView(R.layout.activity_login);
            if (savedInstanceState == null) {

                // Set up the login form.
                mUserName = (AutoCompleteTextView) findViewById(R.id.username);
                mSelectAge = (Spinner) findViewById(R.id.user_selected_age);
                mSelectGender = (Spinner) findViewById(R.id.user_selected_gender);

                Button user_sign_in_button = (Button) findViewById(R.id.user_sign_in_button);
                user_sign_in_button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                });


                initAges();
                initGender();
            }
        }
    }


    private void initAges() {

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this,
                        R.array.user_age_spinner_dropdown_item,
                        android.R.layout.simple_spinner_item);

        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectAge.setAdapter(staticAdapter);

        age = "";

        mSelectAge
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        age = (position > 0) ? (String) parent
                                .getItemAtPosition(position) : "";

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

    }

    private void initGender() {

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this,
                        R.array.user_gender_spinner_dropdown_item,
                        android.R.layout.simple_spinner_item);

        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectGender.setAdapter(staticAdapter);

        gender = "";

        mSelectGender
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        if (position > 0) {
                            gender = (String) parent.getItemAtPosition(position);
                            //mUserGender = (mUserGender.equals("Male")) ? "Man" : "Woman";

                        } else
                            gender = "";

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

    }

    private void attemptLogin() {

        // Reset errors.
        mUserName.setError(null);
        // Store values at the time of the login attempt.
        String username = mUserName.getText().toString();

        // Check for a valid username, if the user entered one.
        if (!TextUtils.isEmpty(username) && !isUserNameValid(username)) {
            mUserName.setError(getString(R.string.error_invalid_username));
            return;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUserName.setError(getString(R.string.error_field_required));
            return;
        }

        if (!isNumeric(age)) {
            Toast.makeText(getBaseContext(),
                    "Please provide a valid age", Toast.LENGTH_LONG)
                    .show();

            return;
        }

        if (gender == "") {
            Toast.makeText(getBaseContext(),
                    "Please provide a valid gender", Toast.LENGTH_LONG)
                    .show();

            return;
        }

        mOnlineDating = new OnlineDating();
        mOnlineDating.init(this.getBaseContext());
        String _ThumbName = (gender.equals("Female")) ? "http://kazanlachani.com/ify/images/female_icon.png": "http://kazanlachani.com/ify/images/man_icon.png";

        User _user = new User("12345_" + username, username, age, gender, _ThumbName, _ThumbName);
        mOnlineDating.currUser = _user;
        mOnlineDating.setSession(true);

        restartSelf();

    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional
        // '-' and decimal.
    }

    private boolean isUserNameValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    public void restartSelf() {


        Intent mStartActivity = new Intent(getBaseContext(), LoginActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(
                getBaseContext(), mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        @SuppressWarnings("static-access")
        AlarmManager mgr = (AlarmManager) getBaseContext().getSystemService(
                getBaseContext().ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
                mPendingIntent);
        System.exit(0);


    }

}

