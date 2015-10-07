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
@EActivity(R.layout.activity_reset_email)
public class ResetEmailActivity extends BaseActivity {

    @Bean
    ParseUtil parseUtil;

    @ViewById(R.id.reset_email_current_edittext)
    EditText currentEmailEditText;
    @ViewById(R.id.reset_email_new_edittext)
    EditText newEmailEditText;

    @Click(R.id.reset_email_button)
    public void resetEmail() {

        final ParseUser currentUser = ParseUser.getCurrentUser();
        String currentEmail = currentEmailEditText.getText().toString();
        String newEmail = newEmailEditText.getText().toString();

        if (currentUser.getEmail().equals(currentEmail)) {
            if (!newEmail.isEmpty()) {
                parseUtil.changeEmail(newEmail, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("NEW EMAIL", currentUser.getEmail());
                    }
                });
            } else {
                showSnackBar(this, "Please enter a new email address.");
            }
        } else {
            showSnackBar(this, "Is that your current email address?");
        }

    }
}
