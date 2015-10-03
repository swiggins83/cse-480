package edu.oakland.festinfo.activities;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.StringUtils;

@EActivity
public class BaseActivity extends AppCompatActivity {

    @UiThread
    public void showSnackBar(Context context, String message) {
        View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar
                .make(rootView, this.getResources().getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                .setText(StringUtils.capitalizeFirstLetter(message))
                .show();
    }

}
