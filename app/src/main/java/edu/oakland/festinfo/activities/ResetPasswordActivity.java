package edu.oakland.festinfo.activities;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import edu.oakland.festinfo.R;

/**
 * Created by Devin on 10/6/2015.
 */

@EActivity(R.layout.activity_reset_password)
public class ResetPasswordActivity extends BaseActivity{

    @Click(R.id.cancel_button)
    public void cancelButton() {
        SettingsPageActivity_
                .intent(this)
                .start();
    }
}
