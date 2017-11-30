package com.harit.elgamalandroid;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harit on 11/29/2017.
 */

public class ChatFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView lvChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chat_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lvChat = view.findViewById(R.id.lv_chat);
        // cek permission buat sms
        getActivity().getSupportLoaderManager().restartLoader(2, null, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://sms");
        String address = "";
        //ngambil argumen sms yang dikirim dari fragment sebelumnya
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            address = bundle.getString(SMSData.ADDRESS_ARGS);
        }

        //kursor difilter hanya menampilkan address yang dipilih
        CursorLoader cursorLoader = new CursorLoader(getContext(), uri, null,
                Telephony.TextBasedSmsColumns.ADDRESS+"=?", new String[]{address+""}, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        List<SMSData> smsList = new ArrayList<SMSData>();
        if(c.isClosed())return;
        // Read the sms data and store it in the list
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                //set isi data sms
                SMSData sms = new SMSData();
                sms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                sms.setDatesent(c.getLong(c.getColumnIndexOrThrow("date_sent")));
                sms.setType(c.getInt(c.getColumnIndexOrThrow("type")));
                smsList.add(sms);
                c.moveToNext();
            }
        }
        c.close();

        // Set smsList in the ListAdapter
        lvChat.setAdapter(new ChatListAdapter(getActivity(), smsList));
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
