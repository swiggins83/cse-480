package edu.oakland.festinfo.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.oakland.festinfo.R;
/**
 * Created by Devin on 9/30/2015.
 */
public class WeatherPageActivity extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_weather_page,container,false);
        Button link = (Button) v.findViewById(R.id.weather_link_button);
        link.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_link_button:
                Uri uri = Uri.parse("http://www.wunderground.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
        }

    }

}
