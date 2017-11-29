package com.harit.elgamalandroid;

/**
 * Created by harit on 11/27/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
public class ConversationListAdapter extends ArrayAdapter<SMSData> {

    // List context
    private final Context context;
    // List values
    private final List<SMSData> smsList;

    public ConversationListAdapter(Context context, List<SMSData> smsList) {
        super(context, R.layout.conversation_list_item, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        SMSData data = smsList.get(position);

        if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.conversation_list_item, null);
        }

        TextView senderNumber = (TextView) convertView.findViewById(R.id.tv_from_conv);
        TextView dateSent = (TextView) convertView.findViewById(R.id.tv_date_conv);
        TextView content = (TextView) convertView.findViewById(R.id.tv_content_conv);

        viewHolder = new ViewHolderItem(senderNumber, dateSent, content);
        convertView.setTag(viewHolder);

        if(data.getContactName() == null || data.getContactName().isEmpty() || data.getContactName().equals("?")) {

            viewHolder.getSender().setText(data.getNumber());
        }else{
            viewHolder.getSender().setText(data.getContactName());
        }

        viewHolder.getDateSent().setText(data.getDateSentInFormat("MM-dd HH:mm"));

        viewHolder.getContent().setText(data.getBody());


        return convertView;
    }

    static class ViewHolderItem{
        TextView sender, dateSent, content;

        public ViewHolderItem(TextView sender, TextView dateSent, TextView content) {
            this.sender = sender;
            this.dateSent = dateSent;
            this.content = content;
        }

        public TextView getSender() {
            return sender;
        }

        public void setSender(TextView sender) {
            this.sender = sender;
        }

        public TextView getDateSent() {
            return dateSent;
        }

        public void setDateSent(TextView dateSent) {
            this.dateSent = dateSent;
        }

        public TextView getContent() {
            return content;
        }

        public void setContent(TextView content) {
            this.content = content;
        }
    }

}