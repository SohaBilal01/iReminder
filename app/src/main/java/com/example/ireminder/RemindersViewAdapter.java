package com.example.ireminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class RemindersViewAdapter extends ArrayAdapter<ReminderModel> {

    public RemindersViewAdapter(Context context, ArrayList<ReminderModel> values) {
        super(context, 0, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.listview_row, parent, false);
        }

        TextView remName = listItemView.findViewById(R.id.remName);
        TextView address = listItemView.findViewById(R.id.address);


        ReminderModel model = getItem(position);

        remName.setText(model.getRemName());
        address.setText(model.getAddress());

        return listItemView;
    }
}
