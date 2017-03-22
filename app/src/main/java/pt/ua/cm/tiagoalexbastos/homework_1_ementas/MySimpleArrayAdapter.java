package pt.ua.cm.tiagoalexbastos.homework_1_ementas;

/**
 * Created by tiagoalexbastos on 03-03-2017.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static pt.ua.cm.tiagoalexbastos.homework_1_ementas.EmentasUAParser.ENCERRADO;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    public MySimpleArrayAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label_to_populate);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon_to_populate);
        textView.setText(values.get(position));
        // Change the icon for Windows and iPhone
        String s = EmentasUAParser.getMeal(position);
        if (s == ENCERRADO) {
            imageView.setImageResource(R.drawable.ic_block_black_48dp);
        } else {
            imageView.setImageResource(R.drawable.icon_96);
        }

        return rowView;
    }
}