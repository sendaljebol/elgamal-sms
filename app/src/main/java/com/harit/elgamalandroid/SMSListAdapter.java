package com.harit.elgamalandroid;

/**
 * Created by harit on 11/27/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * List adapter for storing SMS data
 *
 * @author itcuties
 *
 */
public class SMSListAdapter extends ArrayAdapter<SMSData> {

    // List context
    private final Context context;
    // List values
    private final List<SMSData> smsList;

    public SMSListAdapter(Context context, List<SMSData> smsList) {
        super(context, R.layout.conversation_list_item, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.conversation_list_item, parent, false);

        TextView senderNumber = (TextView) rowView.findViewById(R.id.tv_from_conv);
        senderNumber.setText(smsList.get(position).getNumber());

        return rowView;
    }

}