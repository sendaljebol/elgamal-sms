package com.harit.elgamalandroid;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by harit on 11/29/2017.
 */

public class ConversationFragment extends Fragment {
    private ListView lvConversation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.conversation_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        lvConversation = view.findViewById(R.id.lv_conversation);

        // cek permission buat sms
        if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            List<SMSData> smsList = new ArrayList<SMSData>();

            Uri uri = Uri.parse("content://sms/inbox");
            Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);
            getActivity().startManagingCursor(c);


            // Read the sms data and store it in the list
            if (c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    SMSData sms = new SMSData();
                    if (i < 10){
                        Log.d("sms Message", c.getString(c.getColumnIndexOrThrow("date")).toString() + "\n" + c.getString(c.getColumnIndexOrThrow("body")).toString());

                        Log.d("column names: ", Arrays.toString(c.getColumnNames()));
                    }
                    sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                    sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                    smsList.add(sms);

                    c.moveToNext();
                }
            }

            c.close();


            // Set smsList in the ListAdapter
            lvConversation.setAdapter(new SMSListAdapter(getActivity(), smsList));

            // setListAdapter(new SMSListAdapter(this, smsList));

        }else{
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        //TODO group message by sender, and display sender name
    }
}
