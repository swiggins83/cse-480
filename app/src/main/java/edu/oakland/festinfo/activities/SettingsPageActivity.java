package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.parse.ParseException;
import com.parse.ParseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.StringUtils;

/**
 * Created by Devin on 10/6/2015.
 */
@EActivity(R.layout.activity_settings)
public class SettingsPageActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @Extra
    Intent pastIntent;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, pastIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
