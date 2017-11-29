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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harit on 11/29/2017.
 */

public class ConversationFragment extends Fragment implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private ListView lvConversation;
    public static final int PERMISSION_ALL = 1;
    List<SMSData> smsList = new ArrayList<SMSData>();
OnConversationSelectedListener cListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            cListener = (OnConversationSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }

    }

    public interface OnConversationSelectedListener{
        public void onConversationSelected(String address);
    }

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
        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()

        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS};

        if(!hasPermissions(getContext(), PERMISSIONS)){
           requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }else{
            getActivity().getSupportLoaderManager().initLoader(1, null, this);
        }

        lvConversation.setOnItemClickListener(this);

        //TODO group message by sender, and display sender name
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("permission ", "Granted permission");
                    getActivity().getSupportLoaderManager().initLoader(1, null, this);
                } else {
                    String perm = "";
                    for (String per : permissions) {
                        perm += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://sms");
        CursorLoader cursorLoader = new CursorLoader(getContext(), uri, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        if(c.isClosed())return;
        // Read the sms data and store it in the list
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                //set isi data sms
                SMSData sms = new SMSData();
                sms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")));
                sms.setDatesent(c.getLong(c.getColumnIndexOrThrow("date_sent")));
                sms.setDatereceived(c.getLong(c.getColumnIndexOrThrow("date")));
                sms.setType(c.getInt(c.getColumnIndexOrThrow("type")));
                //kalau belum ada di list tambahin
                if(smsList.contains(sms) == false){
                    smsList.add(sms);
                }else{
                    //kalau udah ada ambil yang paling baru
                    if(smsList.get(smsList.indexOf(sms)).getDateSent().before(sms.getDateSent())){
                        smsList.set(smsList.indexOf(sms), sms);
                    }
                }

                c.moveToNext();
            }
        }
        c.close();

        //set nama kontak jika ada di daftar
        for(SMSData s: smsList){
            if(!s.getNumber().isEmpty())
            s.setContactName(getContactDisplayNameByNumber(s.getNumber()));
        }
        // Set smsList in the ListAdapter
        lvConversation.setAdapter(new ConversationListAdapter(getActivity(), smsList));
    }

    //check all the permissions
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SMSData smsData = (SMSData) smsList.get(position);
        Log.d("item clicked", smsData.getNumber());
        cListener.onConversationSelected(smsData.getNumber());
    }
}
