package com.karenpownall.android.aca.whereitssnap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements ActivityComs{

    private ListView mNavDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    public DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDataManager = new DataManager(getApplicationContext());

        //we will come back here in a minute
        mNavDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mActivityTitle = getTitle().toString();

        //initialize an array with titles from strings.xml
        String [] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        //initialize ArrayAdapter
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navMenuTitles);
        //set adapter to ListView
        mNavDrawerList.setAdapter(mAdapter);

        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mNavDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int whichItem, long id) {

                switchFragment(whichItem);
            }
        });

        switchFragment(0);
    } //end onCreate

    private void switchFragment(int position){

        Fragment fragment = null;
        String fragmentID ="";
        switch (position){
            case 0:
                fragmentID = "TITLES";
                Bundle args = new Bundle();
                args.putString("Tag", "_NO_TAG");
                fragment = new TitlesFragment();
                fragment.setArguments(args);
                break;
            case 1:
                fragmentID = "TAGS";
                fragment = new TagsFragment();
                break;
            case 2:
                fragmentID = "CAPTURE";
                fragment = new CaptureFragment();
                break;
            default:
                break;
        }
        //more code goes here next
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, fragment, fragmentID).commit();

        //close the drawer
        mDrawerLayout.closeDrawer(mNavDrawerList);
    } //end switchFragment

    private void setupDrawer(){
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){

            //called when drawer is opened
            public void onDrawerOpened (View drawerView){
                super.onDrawerOpened(drawerView);

                getSupportActionBar().setTitle("Make selection");

                //triggers call to onPrepareOptionsMenu
                invalidateOptionsMenu();
            }

            //called when drawer is closed
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);

                getSupportActionBar().setTitle(mActivityTitle);

                //triggers call to onPrepareOptionsMenu (redraws menu)
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    } //end setupDrawer

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        //close drawer if open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){

            //drawer is open, so close it
            mDrawerLayout.closeDrawer(mNavDrawerList);
        } else {

            //go back to titles fragment
            //quit if already at titles fragment
            Fragment f = getFragmentManager().findFragmentById(R.id.fragmentHolder);
            if (f instanceof TitlesFragment){
                finish();
                System.exit(0);
            } else {
                switchFragment(0);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } //end onCreateOptionsMenu

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

        //activate navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    } //end onOptionsItemSelected

    @Override
    public void onTitlesListItemSelected(int position) {

        // Load up the bundle with the row _id
        Bundle args = new Bundle();
        args.putInt("Position", position);

        // Create the fragment and add the bundle
        ViewFragment fragment = new ViewFragment();
        fragment.setArguments(args);

        // Start the fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment, "VIEW").commit();

            // update selected item and title, then close the drawer
            mNavDrawerList.setItemChecked(1, true);
            mNavDrawerList.setSelection(1);
            //setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mNavDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    } //end onTitlesListItemSelected

    @Override
    public void onTagsListItemSelected(String clickedTag) {

        // We have just received a String for the TitlesFragment

        // Prepare a new Bundle
        Bundle args = new Bundle();

        // Pack the string into the Bundle, key/value pair
        args.putString("Tag", clickedTag);

        //Create a new instance of TitlesFragment
        TitlesFragment fragment = new TitlesFragment();

        // Load the Bundle into the Fragment
        fragment.setArguments(args);

        // Start the fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment, "TAGS").commit();

        // update selected item and title, then close the drawer
        mNavDrawerList.setItemChecked(1, true);
        mNavDrawerList.setSelection(1);
        mDrawerLayout.closeDrawer(mNavDrawerList);
    } //end onTagsListItemSelected
}//end MainActivity
