package edu.oakland.festinfo.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;


import com.parse.ParseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.oakland.festinfo.R;

@EActivity(R.layout.activity_main)
public class HomePageActivity extends BaseActivity {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;


    //@ViewById(R.id.current_user)
    //TextView currentUserTextView;

    //@AfterViews
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //ParseUser parseUser = new ParseUser();
        //currentUserTextView.setText(parseUser.getCurrentUser().getUsername());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with chosen Fragment;
                    case R.id.user_profile:
                        Toast.makeText(getApplicationContext(), "User Profile Selected", Toast.LENGTH_SHORT).show();
                        UserProfileActivity fragment = new UserProfileActivity();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragmentTransaction.commit();
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.search:
                        Toast.makeText(getApplicationContext(), "Search Selected", Toast.LENGTH_SHORT).show();
                        SearchPageActivity fragment2 = new SearchPageActivity();
                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.frame, fragment2);
                        fragmentTransaction2.commit();
                        return true;

                    case R.id.directions:
                        Toast.makeText(getApplicationContext(), "Directions Selected", Toast.LENGTH_SHORT).show();
                        DirectionsPageActivity fragment3 = new DirectionsPageActivity();
                        android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.replace(R.id.frame, fragment3);
                        fragmentTransaction3.commit();
                        return true;

                    case R.id.weather:
                        Toast.makeText(getApplicationContext(), "Weather Selected", Toast.LENGTH_SHORT).show();
                        WeatherPageActivity fragment4 = new WeatherPageActivity();
                        android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction4.replace(R.id.frame, fragment4);
                        fragmentTransaction4.commit();
                        return true;

                    case R.id.tickets:
                        Toast.makeText(getApplicationContext(), "Tickets Selected", Toast.LENGTH_SHORT).show();
                        TicketsPageActivity fragment5 = new TicketsPageActivity();
                        android.support.v4.app.FragmentTransaction fragmentTransaction5 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction5.replace(R.id.frame, fragment5);
                        fragmentTransaction5.commit();
                        return true;

                    case R.id.notifications:
                        Toast.makeText(getApplicationContext(), "Notifications Selected", Toast.LENGTH_SHORT).show();
                        NotificationsPageActivity fragment6 = new NotificationsPageActivity();
                        android.support.v4.app.FragmentTransaction fragmentTransaction6 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction6.replace(R.id.frame, fragment6);
                        fragmentTransaction6.commit();
                        return true;

                    case R.id.logout:
                        Toast.makeText(getApplicationContext(), "Logout Selected", Toast.LENGTH_SHORT).show();
                        LogoutPage fragment7 = new LogoutPage();
                        android.support.v4.app.FragmentTransaction fragmentTransaction7 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction7.replace(R.id.frame, fragment7);
                        fragmentTransaction7.commit();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
