package com.harit.elgamalandroid;


public interface SmsListener {
    public void messageReceived(String messageText, String number, String name);
}
