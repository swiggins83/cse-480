package edu.oakland.festinfo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import edu.oakland.festinfo.R;
import edu.oakland.festinfo.utils.StringUtils;

@EActivity
public class BaseActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    public void colorUpArrow() {
        final Drawable upArrow = getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @UiThread
    public void showSnackBar(Context context, String message) {
        View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar
                .make(rootView, this.getResources().getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                .setText(StringUtils.capitalizeFirstLetter(message))
                .show();
    }

    @UiThread
    public void showSnackBarWithAction(Context context, String message, String actionMessage, View.OnClickListener action) {
        View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar
                .make(rootView, this.getResources().getString(R.string.error_occurred), Snackbar.LENGTH_LONG)
                .setText(StringUtils.capitalizeFirstLetter(message))
                .setAction(actionMessage, action)
                .show();
    }

    public void showSpinner() {
        showSpinner(null);
    }

    @UiThread
    public void showSpinner(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(this, R.style.DialogTheme);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
        }
        if (message != null) {
            dialog.setMessage(message);
        }
        dialog.show();
    }

    @UiThread
    public void dismissSpinner() {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
