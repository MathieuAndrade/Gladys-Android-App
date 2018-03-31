package com.gladysinc.gladys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.gladysinc.gladys.Settings.settingsActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView user;
    private MenuItem add_button, maps_report_button, maps_location_button;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarTitle(R.string.dashboard);

        initialdeclarations();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, new DashboardFragment()).commit();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        if (id == R.id.dashboard) {

            getSupportFragmentManager().beginTransaction().replace(R.id.layout, new DashboardFragment()).commit();
            setToolbarTitle(R.string.dashboard);

            params.setScrollFlags(0);

            add_button.setVisible(false);
            maps_location_button.setVisible(false);
            maps_report_button.setVisible(false);

        } else if (id == R.id.rooms) {

            getSupportFragmentManager().beginTransaction().replace(R.id.layout, new RoomsFragment()).commit();
            setToolbarTitle(R.string.rooms);

            params.setScrollFlags(0);

            add_button.setVisible(false);
            maps_location_button.setVisible(false);
            maps_report_button.setVisible(false);

        } else if (id == R.id.timeline) {

            getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TimelineFragment()).commit();
            setToolbarTitle(R.string.timeline);

            params.setScrollFlags(0);

            maps_location_button.setVisible(false);
            maps_report_button.setVisible(false);

        } else if (id == R.id.alarm) {

            getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AlarmFragment()).commit();
            setToolbarTitle(R.string.alarm);

            params.setScrollFlags(0);

            maps_location_button.setVisible(false);
            maps_report_button.setVisible(false);

        } else if (id == R.id.brain) {

            getSupportFragmentManager().beginTransaction().replace(R.id.layout, new BrainFragment()).commit();
            setToolbarTitle(R.string.brain);

            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

            add_button.setVisible(false);
            maps_location_button.setVisible(false);
            maps_report_button.setVisible(false);

        }  /*else if (id == R.id.Tchat) {

            getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TchatFragment()).commit();
            setToolbarTitle(R.string.tchat);

            params.setScrollFlags(0);

            add_button.setVisible(false);
            maps_location_button.setVisible(false);
            maps_report_button.setVisible(false);

        }*/ else if (id == R.id.maps) {

            getSupportFragmentManager().beginTransaction().replace(R.id.layout, new MapsFragment()).commit();
            setToolbarTitle(R.string.maps);

            params.setScrollFlags(0);

            add_button.setVisible(false);

        } else if (id == R.id.about) {

            getSupportFragmentManager().beginTransaction().replace(R.id.layout, new InfosFragment()).commit();
            setToolbarTitle(R.string.about);

            params.setScrollFlags(0);

            add_button.setVisible(false);
            maps_location_button.setVisible(false);
            maps_report_button.setVisible(false);

        } else if (id == R.id.settings) {

            Intent intent = new Intent(getApplicationContext(), settingsActivity.class);
            startActivity(intent);

            params.setScrollFlags(0);

            add_button.setVisible(false);
            maps_location_button.setVisible(false);
            maps_report_button.setVisible(false);
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initialdeclarations(){

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        user = header.findViewById(R.id.user);

        FloatingActionButton fab_scroll_up = findViewById(R.id.fab_scroll_up);
        fab_scroll_up.animate().translationY(fab_scroll_up.getHeight() + 400).setInterpolator(new LinearInterpolator()).start();
    }

    public void setToolbarTitle(int title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        add_button = menu.findItem(R.id.add_button);
        maps_report_button = menu.findItem(R.id.report_button);
        maps_location_button = menu.findItem(R.id.location_button);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pref_user_name = preferences.getString("name" , "");
        String pref_user_first_name = preferences.getString("first_name", "");

        String userName = "" + pref_user_first_name + " " + pref_user_name;

        if(!Objects.equals(pref_user_name, "") & !Objects.equals(pref_user_first_name, "")){
            user.setText(userName);
        }
        else if (!Objects.equals(pref_user_first_name, "") & Objects.equals(pref_user_name, "") ){
            user.setText(pref_user_first_name);

        }
        else if (!Objects.equals(pref_user_name, "") & Objects.equals(pref_user_first_name, "")){
            user.setText(pref_user_name);

        }

    }

}
