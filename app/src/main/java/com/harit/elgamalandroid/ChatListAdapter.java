package com.harit.elgamalandroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harit on 11/29/2017.
 */

public class ChatListAdapter extends ArrayAdapter<SMSData> {

    // List context
    private final Context context;
    // List values
    private final List<SMSData> smsList;


    public ChatListAdapter(Context context, List<SMSData> smsList) {
        super(context, R.layout.inbox_chat_item, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatListAdapter.ChatViewHolderItem viewHolder;
        SMSData data = smsList.get(position);

        if(convertView == null){
            Log.d("data tyoe: ", data.getType() + " | "+SMSData.TYPE_SENT+ " | "+SMSData.TYPE_INBOX);
            if(data.getType() == SMSData.TYPE_INBOX){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.inbox_chat_item, null);
            }else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.sent_chat_item, null);
            }
        }

        TextView dateSent = (TextView) convertView.findViewById(R.id.tv_date_chat);
        TextView content = (TextView) convertView.findViewById(R.id.tv_content_chat);

        viewHolder = new ChatListAdapter.ChatViewHolderItem(dateSent, content);
        convertView.setTag(viewHolder);

        viewHolder.getDateSent().setText(data.getDateSentInFormat("MM-dd HH:mm"));

        viewHolder.getContent().setText(data.getBody());


        return convertView;
    }

    static class ChatViewHolderItem{
        TextView  dateSent, content;

        public ChatViewHolderItem(TextView dateSent, TextView content) {
            this.dateSent = dateSent;
            this.content = content;
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
