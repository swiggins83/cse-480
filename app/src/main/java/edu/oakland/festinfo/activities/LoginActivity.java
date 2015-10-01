package edu.oakland.festinfo.activities;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.ParseUtil;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewById(R.id.username_edittext)
    EditText usernameEditText;
    @ViewById(R.id.password_edittext)
    EditText passwordEditText;

    @Bean
    ParseUtil parseUtil;

    @Click(R.id.submit_button)
    public void signIn() {

        if (!usernameEditText.getText().toString().isEmpty() &&
            !passwordEditText.getText().toString().isEmpty()) {

            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            final Activity parent = this;
            parseUtil.signIn(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        showSnackBar(parent, e.getLocalizedMessage());
                    } else {
                        HomePageActivity_
                                .intent(parent)
                                .start();
                    }
                }
            });
        } else {
            showSnackBar(this, getString(R.string.incomplete_data));
        }

    }

}
