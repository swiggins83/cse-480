package edu.oakland.festinfo.activities;

        import android.content.Intent;
        import android.graphics.Color;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;

        import com.github.paolorotolo.appintro.AppIntro;
        import com.github.paolorotolo.appintro.AppIntroFragment;

        import edu.oakland.festinfo.R;

/**
 * Created by Devin on 9/22/2015.
 */
public class FestivalAppIntroActivity extends AppIntro{

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Introduction", "Interactive map with custom markers that makes searching easy.", R.mipmap.effmap, Color.parseColor("red")));
        addSlide(AppIntroFragment.newInstance("Introduction", "Markers can be favorited and viewed on a seperate page to make finding your favorite artists easier.", R.mipmap.markers, Color.parseColor("blue")));
        addSlide(AppIntroFragment.newInstance("Introduction", "Ability to search for bands playing at the festival by artist an location.", R.mipmap.search, Color.parseColor("magenta")));
        addSlide(AppIntroFragment.newInstance("Introduction", "Notifications display as events happen to remind users about events going on.", R.mipmap.push, Color.parseColor("black")));

        setBarColor(Color.parseColor("grey"));
        setSeparatorColor(Color.parseColor("aqua"));
        showSkipButton(true);
        showDoneButton(true);

    }

    private void loadMainActivity(){
        RegistrationActivity_
                .intent(this)
                .start();
    }

    private void loadMain2Activity(){
        LoginActivity_
                .intent(this)
                .start();

    }

    @Override
    public void onSkipPressed(){

        loadMain2Activity();

    }
    @Override
    public void onDonePressed(){

        loadMainActivity();
    }

    public void getStarted(View v)
    {
        loadMainActivity();
    }

}
