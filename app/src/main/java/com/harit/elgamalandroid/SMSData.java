package com.harit.elgamalandroid;


import android.provider.Telephony;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents SMS.
 *
 * @author itcuties
 *
 */
public class SMSData {

    public final static int TYPE_INBOX = Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX;
    public final static int TYPE_SENT= Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT;
    public static final java.lang.String ADDRESS_ARGS = "ADDRESS_ARGS";
    // Number from witch the sms was send
    private String number;
    private String ContactName;

    // SMS text body
    private String body;
    private long datesent;
    private long datereceived;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public long getDateSentMilis() {
        return datesent;
    }

    public Date getDateSent(){
        return new Date(datesent);
    }

    public void setDatesent(long datesent) {
        this.datesent = datesent;
    }

    public String getDateSentInFormat(String format){

        SimpleDateFormat sdf = new SimpleDateFormat(format); //Or whatever format fits best your needs.

        return sdf.format(datesent);
    }
    public long getDateReceivedMilis() {
        return datereceived;
    }

    public Date getDateReceived(){
        return new Date(datereceived);
    }

    public void setDatereceived(long datereceived) {
        this.datereceived = datereceived;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if(number == null) number = "";
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean equals(Object o){
        if (o instanceof SMSData){
            SMSData temp = (SMSData) o;
            if (this.number.equals(temp.getNumber()))
                return true;
        }
        return false;
    }
}