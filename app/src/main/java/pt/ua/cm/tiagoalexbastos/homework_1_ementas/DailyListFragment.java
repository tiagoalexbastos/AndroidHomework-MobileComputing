package pt.ua.cm.tiagoalexbastos.homework_1_ementas;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static pt.ua.cm.tiagoalexbastos.homework_1_ementas.DailyMealsActivity.CLEAR_LIST;
import static pt.ua.cm.tiagoalexbastos.homework_1_ementas.DailyMealsActivity.PREFS_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class DailyListFragment extends Fragment {

//    private static ArrayAdapter<String> mealsListAdapter;
    private static MySimpleArrayAdapter mealsListAdapter;

    private static ListView listView;
    public static final String EMENTA = "EMENTA";
    public static final String OFFLINE_CACHE = "OFFLINE_CACHE";

    public DailyListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_daily_meals, container,
                false);

        mealsListAdapter = new MySimpleArrayAdapter(
                getActivity(), // The current context (this activity)
                new ArrayList<String>());

        String jsonResults;
        if (isNetworkAvailable()) {
            jsonResults = EmentasUAParser.callOpenMeals();
            saveData2Offline(jsonResults);
        } else {
            SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
            jsonResults = settings.getString(OFFLINE_CACHE, "");
            Toast.makeText(getActivity(), "O dispositivo encontra-se offline," +
                            " mas foram carregados os dados em cache.",
                    Toast.LENGTH_LONG).show();
        }

        try {
            String[] entries = EmentasUAParser.getMealDataFromJson(jsonResults);

            mealsListAdapter.clear();
            for (String dayEntry : entries) {
                mealsListAdapter.add(dayEntry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Get a reference to the ListView, and attach this adapter to it.
        listView = (ListView) rootView.findViewById(R.id.list_daily_meals);
        listView.setAdapter(mealsListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (EmentasUAParser.getMeal(position) == EmentasUAParser.ENCERRADO) {
                    Toast.makeText(getActivity(), "Este refeitório encontra-se encerrado", Toast.LENGTH_SHORT).show();
                }
                else
                    showDetailsForEntry(parent.getAdapter().getItem(position).toString(), position);

            }
        });


        return rootView;
    }

    private void saveData2Offline(String jsonResults) {
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(OFFLINE_CACHE, jsonResults);

        // Commit the edits!
        editor.commit();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void refreshList(String jsonResults){

            if (jsonResults.equalsIgnoreCase(CLEAR_LIST))
                mealsListAdapter.clear();

            try {
                String[] entries = EmentasUAParser.getMealDataFromJson(jsonResults);

                mealsListAdapter.clear();
                for (String dayEntry : entries) {
                    mealsListAdapter.add(dayEntry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        // Get a reference to the ListView, and attach this adapter to it.
        listView.setAdapter(mealsListAdapter);

    }

    private void showDetailsForEntry(String message, int position) {

        // Create fragment and give it an argument specifying the article it should show
        DailyMealDetails detailsFragment = new DailyMealDetails();
        Bundle args = new Bundle();

        String ementa = EmentasUAParser.getMeal(position);
        args.putString(EMENTA, ementa);
        detailsFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.daily_meals_layout_fragment, detailsFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

}
