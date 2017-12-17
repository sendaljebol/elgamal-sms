package com.harit.elgamalandroid;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements ConversationFragment.OnConversationSelectedListener, View.OnClickListener {
    public final static int RESULT_PICK_CONTACT = 2113;
    public final static int NOTIFICATION_ID = 2221;
    private ListView lvConversation;
    private EditText etSMSNumber;
    private SendSMSDialogFragment sendSMSDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSMSChoiceDialog();
            }
        });
        // Create a new Fragment to be placed in the activity layout
        ConversationFragment convFragment = new ConversationFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, convFragment).commit();

        //bikin listener dari fragment manager, terutama ganti title actionbar
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int count = getSupportFragmentManager().getBackStackEntryCount();
                //kalau kembali ke layar utama, ganti judul jadi app lasgi
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setVisibility(FloatingActionButton.GONE);

                if (count == 0) {
                    fab.setVisibility(FloatingActionButton.VISIBLE);
                    getSupportActionBar().setTitle(getString(R.string.app_name));
                }
            }
        });


        SmsReceiverService.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText, String number, String sender) {

                //From the received text string you may do string operations to get the required OTP
                //It depends on your SMS format
                Log.e("Message", messageText);
                Log.e("number: ", number);
                Log.e("sender", sender);
// The id of the channel.
                String channel_id = "elgamal_channel_01";
                Toast.makeText(MainActivity.this, "Message: " + messageText, Toast.LENGTH_LONG).show();


                //jika isi sms ada textnya, berarti bukan cipher.. ngga usa diurusin
                if (Pattern.matches("[a-zA-Z]+", messageText) == false && messageText.length() > 2) {

                    //kalau nougat butuh buat notif chanel
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        // The id of the channel.
                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel mChannel = null;

                            mChannel = new NotificationChannel(channel_id, "inbox", importance);

                        // Configure the notification channel.
                        mChannel.setDescription("shows all inbox sms");
                        mChannel.enableLights(true);
                        // Sets the notification light color for notifications posted to this
                        // channel, if the device supports this feature.
                        mChannel.setLightColor(Color.RED);
                        mChannel.enableVibration(true);
                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        mNotificationManager.createNotificationChannel(mChannel);
                    }

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.send)
                                    .setContentTitle("ElgamalSMS Inbox")
                                    .setContentText(messageText)
                                    .setChannel(channel_id);

                    Intent openMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    openMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    openMainActivity.putExtra("number", number);
                    openMainActivity.putExtra("sender", sender);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your app to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);

                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(openMainActivity);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // mNotificationId is a unique integer your app uses to identify the
                    // notification. For example, to cancel the notification, you can pass its ID
                    // number to NotificationManager.cancel().
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }

            }
        });

        if(getIntent().getStringExtra("number") != null){
            onConversationSelected(getIntent().getStringExtra("number"),
                    getIntent().getStringExtra("sender"));
        }

    }

    public void showSMSChoiceDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Fragment prev = getSupportFragmentManager().findFragmentByTag("contact");
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        sendSMSDialogFragment = new SendSMSDialogFragment();

        sendSMSDialogFragment.show(getSupportFragmentManager(), "contact");

            getSupportFragmentManager().executePendingTransactions();
            Dialog d = sendSMSDialogFragment.getDialog();
            Button btnContact = d.findViewById(R.id.btn_contact_smschoice);
            btnContact.setOnClickListener(this);
            ImageButton btnSend = d.findViewById(R.id.btn_number_smschoice);
            btnSend.setOnClickListener(this);
            etSMSNumber = d.findViewById(R.id.et_contact_smschoice);

    }

    public void showAboutDialog() {
        DialogFragment newFragment = new AboutDialogFragment();
        newFragment.show(getSupportFragmentManager(), "missiles");
    }

    /**
     * Handle the creation of the menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * when the option is selected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.about_menu:
                showAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConversationSelected(String address, String contactName) {
        Log.d("onconversation", address);


        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(SMSData.ADDRESS_ARGS, address);
        args.putString(SMSData.CONTACT_ARGS, contactName);
        chatFragment.setArguments(args);

        if (contactName != null) {
            getSupportActionBar().setTitle(contactName);
        } else {
            getSupportActionBar().setTitle(address);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, chatFragment);
        transaction.addToBackStack(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!this.isDestroyed())
                transaction.commitAllowingStateLoss();
        }
    }


    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "";

        ContentResolver contentResolver = getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_contact_smschoice:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
                break;
            case R.id.btn_number_smschoice:
                try {
                    sendSMSDialogFragment.dismiss();
                }catch (IllegalStateException e){}
                //kalau kosong return aja
                if(etSMSNumber.getText().toString().isEmpty())return;

                //kirim ke chatfragment
                onConversationSelected(etSMSNumber.getText().toString(),
                getContactDisplayNameByNumber(etSMSNumber.getText().toString()));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    try {
                        sendSMSDialogFragment.dismiss();
                    }catch (IllegalStateException e){
                        Log.d("send dialog fragment", "is already closed");
                    }

                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews
            onConversationSelected(phoneNo, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
