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
public class PecahChiperText {

    private String pecah[];
    private ArrayList pGamma = new ArrayList();
    private ArrayList pDelta = new ArrayList();

    public void setChiper(String chiper) {
        pecah = chiper.split(" ");
        
        for (int i = 0; i < pecah.length; i++) {

            if (i % 2 == 0) {
                pGamma.add(pecah[i]);
            } else {
                pDelta.add(pecah[i]);
            }
        }
    }

    public ArrayList getGamma() {
        return pGamma;
    }

    public ArrayList getDelta() {
        return pDelta;
    }
}
