package com.harit.elgamalandroid;

import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.harit.elgamalandroid.elgamal.Elgamal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by harit on 11/29/2017.
 */

public class ChatFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, AdapterView.OnItemClickListener {
    EditText etP, etX;
    private ListView lvChat;
    private ImageButton btnSend, btnSendEncrypted;
    private String address = "";
    private EditText etBody;
    private Elgamal elgamal;
    private DBHelper dbHelper;
    private List<SMSData> smsList;
    private ChatListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chat_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lvChat = view.findViewById(R.id.lv_chat);
        //mulai load data chat
        getActivity().getSupportLoaderManager().restartLoader(2, null, this);

        //getview dari tampilan
        btnSend = view.findViewById(R.id.btn_send_chat);
        btnSendEncrypted = view.findViewById(R.id.btn_send_encrypted_chat);
        etBody = view.findViewById(R.id.et_body_chat);

        //listener button
        btnSend.setOnClickListener(this);
        btnSendEncrypted.setOnClickListener(this);

        elgamal = new Elgamal(getContext());

        lvChat.setOnItemClickListener(this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://sms");

        //ngambil argumen sms yang dikirim dari fragment sebelumnya
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            address = bundle.getString(SMSData.ADDRESS_ARGS);
        }

        //kursor difilter hanya menampilkan address yang dipilih
        CursorLoader cursorLoader = new CursorLoader(getContext(), uri, null,
                Telephony.TextBasedSmsColumns.ADDRESS + "=?", new String[]{address + ""}, "date_sent ASC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        smsList = new ArrayList<SMSData>();
        if (c.isClosed()) return;
        // Read the sms data and store it in the list
        if (c.moveToFirst()) {
            Pattern p = Pattern.compile("[/^[A-z]+$/]");
            for (int i = 0; i < c.getCount(); i++) {
                //set isi data sms
                SMSData sms = new SMSData();
                sms.setBody(c.getString(c.getColumnIndexOrThrow("body")));

                sms.setType(c.getInt(c.getColumnIndexOrThrow("type")));
                if(sms.getType() == SMSData.TYPE_SENT) {
                    sms.setDatesent(c.getLong(c.getColumnIndexOrThrow("date")));
                }else{
                    sms.setDatesent(c.getLong(c.getColumnIndexOrThrow("date_sent")));
                }

                    String message = sms.getBody();

                    //jika isi sms ada textnya, berarti bukan cipher.. ngga usa diurusin
                    if (p.matcher(message).lookingAt() == false && message.length() > 2) {

                        try{
                            Calendar cal = new GregorianCalendar();
                            cal.setTime(sms.getDateSent());

                            String decrypted = elgamal.decrypt(message, cal);
                            sms.setCipher(message);
                            sms.setBody(decrypted);
                        }catch (Exception e){
                            Log.e("dekrip fail: ", message);
                        }
                    }


                //kalau sms gagal, taruh di bawah
                if(sms.getType() == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_FAILED ||
                        sms.getType() == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX ||
                        sms.getType() == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_QUEUED) {
                        sms.setDatesent(new Date().getTime());
                }

                smsList.add(sms);
                c.moveToNext();
            }
        }
        c.close();

        Collections.sort(smsList);
        // Set smsList in the ListAdapter
        adapter = new ChatListAdapter(getActivity(), smsList);
        lvChat.setAdapter(adapter);
        lvChat.setSelection(adapter.getCount()-1);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onClick(View view) {
        //kalau kosong return dulu aja
        if(etBody.getText().toString().isEmpty())return;

        switch (view.getId()) {
            case R.id.btn_send_chat:
                sendLongSMS(address, etBody.getText().toString());
                break;
            case R.id.btn_send_encrypted_chat:

                EncryptDialogFragment encryptDialogFragment = new EncryptDialogFragment();
                encryptDialogFragment.show(getFragmentManager(), "encrypt");
                getActivity().getSupportFragmentManager().executePendingTransactions();

                Dialog d = encryptDialogFragment.getDialog();

                etP = d.findViewById(R.id.et_public_encrypt);
                etX = d.findViewById(R.id.et_private_encrypt);

                Button btn = d.findViewById(R.id.btn_manualencrypt_encrypt);
                btn.setOnClickListener(this);

                Button btn2 = d.findViewById(R.id.btn_autoencrypt_encrypt);
                btn2.setOnClickListener(this);

                break;
            case R.id.btn_autoencrypt_encrypt:

                Calendar c = Calendar.getInstance();

                String encrypted = elgamal.encrypt(etBody.getText().toString(), c);
                Log.d("encrypted", encrypted);
                Log.d("message", "address: " + address + " | msg : " + encrypted);

                sendLongSMS(address, encrypted);

                Fragment prev = getFragmentManager().findFragmentByTag("encrypt");
                if (prev != null) {
                    DialogFragment df = (DialogFragment) prev;
                    df.dismiss();
                }
                break;
            case R.id.btn_manualencrypt_encrypt:
                Elgamal e = new Elgamal(getContext());
                int p = Integer.parseInt(etP.getText().toString());
                int x = Integer.parseInt(etX.getText().toString());

                //check nilai pgx
                if (p < 255) {
                    Toast.makeText(getContext(), "p harus lebih dari 255", Toast.LENGTH_SHORT).show();
                    break;
                } else if (e.isPrime(p) == false) {
                    Toast.makeText(getContext(), "p harus bilangan prima", Toast.LENGTH_SHORT).show();
                    break;
                } else if (x < 1 | x >= p - 2) {
                    Toast.makeText(getContext(), "Nilai x : 1 < x <= p-2", Toast.LENGTH_SHORT).show();
                    break;
                }

                String encrypt = e.encrypt(etBody.getText().toString(), p, x);
                Log.d("message", "address: " + address + " | msg : " + encrypt);
                sendLongSMS(address, encrypt);

                prev = getFragmentManager().findFragmentByTag("encrypt");
                if (prev != null) {
                    DialogFragment df = (DialogFragment) prev;
                    df.dismiss();
                }
                break;
        }
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
            etBody.setText("");
            getActivity().getSupportLoaderManager().restartLoader(2, null, this);

        } catch (Exception ex) {

            Toast.makeText(getContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        etBody.setText("");
    }

    public void sendLongSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);

            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);

            Toast.makeText(getContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
            etBody.setText("");
            SMSData data =new SMSData();
            data.setBody(msg);
            data.setDatesent(new Date().getTime());
            data.setType(SMSData.TYPE_SENT);
            smsList.add(data);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Toast.makeText(getContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        SMSData smsData = (SMSData) smsList.get(position);
        if (smsData.getCipher() != null) {
            DecryptDialogFragment decryptDialogFragment = new DecryptDialogFragment();

            Bundle args = new Bundle();
            args.putString("cipher", smsData.getCipher());
            decryptDialogFragment.setArguments(args);

            decryptDialogFragment.show(getFragmentManager(), "decrypt");
        }
    }
}
