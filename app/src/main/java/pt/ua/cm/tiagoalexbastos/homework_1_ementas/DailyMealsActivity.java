package pt.ua.cm.tiagoalexbastos.homework_1_ementas;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static pt.ua.cm.tiagoalexbastos.homework_1_ementas.DailyListFragment.OFFLINE_CACHE;
import static pt.ua.cm.tiagoalexbastos.homework_1_ementas.DailyListFragment.refreshList;

public class DailyMealsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String CLEAR_LIST = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_meals);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.daily_meals_layout_fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            DailyListFragment firstFragment = new DailyListFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.daily_meals_layout_fragment, firstFragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ementas, menu);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void saveData2Offline(String jsonResults) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(OFFLINE_CACHE, jsonResults);

        // Commit the edits!
        editor.commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.show_map:
                mostrarMapa();
                return true;
            case R.id.refresh_tv:
                String jsonResults = getResults();
                refreshList(jsonResults);
                return true;

            case R.id.clean_list:

                refreshList(CLEAR_LIST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void mostrarMapa() {
        // Build the intent
        Uri location = Uri.parse("geo:0,0?q=Universidade+de+Aveiro,+Portugal");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        // Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(mapIntent);
        }


    }

    private String getResults() {
        String jsonResults;
        if (isNetworkAvailable()) {
            jsonResults = EmentasUAParser.callOpenMeals();
            saveData2Offline(jsonResults);
        } else {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            jsonResults = settings.getString(OFFLINE_CACHE, "");
            Toast.makeText(getApplicationContext(), "O dispositivo encontra-se offline," +
                            " mas foram carregados os dados em cache.",
                    Toast.LENGTH_SHORT).show();
        }

        return jsonResults;
    }
}
