package edu.oakland.festinfo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.oakland.festinfo.R;

/**
 * Created by Devin on 9/29/2015.
 */
public class NotificationsPageActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_notifications_page,container,false);
        return v;
    }
}
