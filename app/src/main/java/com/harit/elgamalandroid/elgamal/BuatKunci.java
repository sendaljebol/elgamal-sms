/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harit.elgamalandroid.elgamal;

import java.math.BigInteger;

/**
 * @author isahroni
 */
public class BuatKunci {

    private BigInteger y;
    private boolean cek;
    private int bilanganPrima;

    public BigInteger getKunci(int p, int g, int x) {
        BigInteger a = BigInteger.valueOf(p);
        BigInteger b = BigInteger.valueOf(g);
        BigInteger c = BigInteger.valueOf(x);
        y = b.modPow(c, a);
        return y;
    }

    public boolean isPrima() {
        for (int i = 3; i < bilanganPrima; i += 2) {
            if (bilanganPrima % i == 0) {
                cek = false;
                break;
            } else {
                cek = true;
            }
        }

        return cek;
    }

    public void setPrima(int bilanganPrima) {
        this.bilanganPrima = bilanganPrima;
    }
}
