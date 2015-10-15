package edu.oakland.festinfo.activities;

import android.util.Log;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.ParseUtil;

/**
 * Created by Devin on 10/6/2015.
 */

@EActivity(R.layout.activity_reset_username)
public class ResetUsernameActivity extends BaseActivity {

    @Bean
    ParseUtil parseUtil;

    @ViewById(R.id.reset_username_current_eddittext)
    EditText currentUserNameEditText;
    @ViewById(R.id.reset_username_new_edittext)
    EditText newUserNameEditText;

    @Click(R.id.reset_username_button)
    public void resetUsername() {

        final ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUsername = currentUserNameEditText.getText().toString();
        String newUserName = newUserNameEditText.getText().toString();

        if (currentUser.getUsername().equals(currentUsername)) {
            if (!newUserName.isEmpty()) {
                parseUtil.changeUsername(newUserName, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                       Log.d("NEW USERNAME", currentUser.getUsername());
                    }
                });
            } else {
                showSnackBar(this, "Please enter a new, valid username.");
            }
        } else {
            showSnackBar(this, "Is that your current username?");
        }

    }
    @Click(R.id.cancel_button)
    public void cancelButton(){
        SettingsPageActivity_
                .intent(this)
                .start();

    }

}
