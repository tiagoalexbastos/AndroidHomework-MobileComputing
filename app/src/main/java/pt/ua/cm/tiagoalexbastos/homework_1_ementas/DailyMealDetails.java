package pt.ua.cm.tiagoalexbastos.homework_1_ementas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static pt.ua.cm.tiagoalexbastos.homework_1_ementas.DailyListFragment.EMENTA;


/**
 * A simple {@link Fragment} subclass.
 */
public class DailyMealDetails extends Fragment {

    public DailyMealDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_daily_meal_details, container,
                false);
        Bundle args = getArguments();


        TextView textView = (TextView) rootView.findViewById(R.id.daily_meal_detail_tv);
        textView.setText(args.getString(EMENTA));

        return rootView;
    }


}
