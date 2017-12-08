package com.harit.elgamalandroid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;

public class SmsReceiverService extends BroadcastReceiver {

    //interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            String number = smsMessage.getOriginatingAddress();
            //Check the sender to filter messages which we require to read

                String messageBody = smsMessage.getMessageBody();

                //notifikasi

                //Pass the message text to interface
            if(mListener != null) {
                mListener.messageReceived(messageBody, number, sender);
            }else{
                Intent openMainActivity= new Intent(context, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openMainActivity);
            }
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}