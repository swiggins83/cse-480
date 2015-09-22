package edu.oakland.festinfo.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.ParseUtil;

/*@EActivity(R.layout.activity_login)*/
public class LoginActivity extends AppCompatActivity {

    @ViewById(R.id.username_edittext)
    EditText usernameEditText;
    @ViewById(R.id.password_edittext)
    EditText passwordEditText;

    @Click(R.id.submit_button)
    public void signIn() {

        if (!usernameEditText.getText().toString().isEmpty() &&
            !passwordEditText.getText().toString().isEmpty()) {

            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            ParseUtil.signIn(this, username, password);
        } else {
            View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar
                    .make(rootView, R.string.incomplete_data, Snackbar.LENGTH_LONG)
                    .show();
        }

    }
}