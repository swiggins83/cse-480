package edu.oakland.festinfo.activities;

import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_user_profile)
public class UserProfileActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
