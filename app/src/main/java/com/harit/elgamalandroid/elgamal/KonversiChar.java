/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harit.elgamalandroid.elgamal;

import java.util.ArrayList;

/**
 *
 * @author isahroni
 */
public class KonversiChar {

    private ArrayList listChar = new ArrayList();

    public ArrayList getCharASCII(String pesan) {

        for (int i = 0; i < pesan.length(); i++) {
            char chr = pesan.charAt(i);
            int in = chr;

            listChar.add(in);
        }

        return listChar;
    }
}
