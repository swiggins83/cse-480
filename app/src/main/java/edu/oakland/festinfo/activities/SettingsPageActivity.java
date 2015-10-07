package edu.oakland.festinfo.activities;

import android.content.Intent;
import android.view.View;

import org.androidannotations.annotations.EActivity;

import edu.oakland.festinfo.R;

/**
 * Created by Devin on 10/6/2015.
 */

@EActivity(R.layout.activity_settings)
public class SettingsPageActivity extends BaseActivity {

    public void resetPasswordPressed (View view) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);

    }

    public void resetUsernamePressed (View view) {
        Intent intent = new Intent(this, ResetUsernameActivity.class);
        startActivity(intent);
    }

    public void resetEmailPressed (View view) {
        Intent intent = new Intent(this, ResetEmailActivity.class);
        startActivity(intent);
    }



}
