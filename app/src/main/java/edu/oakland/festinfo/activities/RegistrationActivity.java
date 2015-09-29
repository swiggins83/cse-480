package edu.oakland.festinfo.activities;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.ParseUtil;

@EActivity(R.layout.activity_registration)
public class RegistrationActivity extends AppCompatActivity {

    @ViewById(R.id.email_edittext)
    EditText emailEditText;
    @ViewById(R.id.username_edittext)
    EditText usernameEditText;
    @ViewById(R.id.password_edittext)
    EditText passwordEditText;

    @Bean
    ParseUtil parseUtil;

    @Click(R.id.submit_button)
    public void submitForm() {

        if (!emailEditText.getText().toString().isEmpty()    &&
                !usernameEditText.getText().toString().isEmpty() &&
                !passwordEditText.getText().toString().isEmpty()) {

            String email = emailEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            parseUtil.signUp(this, email, username, password);
        } else {
            View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar
                    .make(rootView, R.string.incomplete_data, Snackbar.LENGTH_LONG)
                    .show();
        }

    }
}
