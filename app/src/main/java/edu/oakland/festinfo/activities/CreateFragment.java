package edu.oakland.festinfo.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.oakland.festinfo.R;

/**
 * Created by Devin on 9/29/2015.
 */
public class CreateFragment extends android.app.Fragment {

    public CreateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create, container, false);

        return rootView;
    }

}
