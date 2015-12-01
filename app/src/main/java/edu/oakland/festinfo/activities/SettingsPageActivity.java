package edu.oakland.festinfo.activities;

import android.view.View;

import com.parse.ParseException;
import com.parse.ParseUser;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.StringUtils;

/**
 * Created by Devin on 10/6/2015.
 */

@EActivity(R.layout.activity_settings)
public class SettingsPageActivity extends BaseActivity {

    @Click(R.id.settings_reset_image)
    public void showImageReset() {
        ChangeImageActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.settings_reset_email)
    public void showEmailReset() {
        ResetEmailActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.settings_reset_username)
    public void showUsernameReset() {
        ResetUsernameActivity_
                .intent(this)
                .start();
    }

    @Click(R.id.settings_reset_password)
    public void resetPassword() {
        try {
            ParseUser.requestPasswordReset(ParseUser.getCurrentUser().getEmail());
        } catch (ParseException e) {
            showSnackBar(this, StringUtils.capitalizeFirstLetter(e.getLocalizedMessage()));
            e.printStackTrace();
        }
    }

}
