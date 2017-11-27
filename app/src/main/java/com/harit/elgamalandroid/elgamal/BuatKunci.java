/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harit.elgamalandroid.elgamal;

import java.math.BigInteger;

/**
 *
 * @author isahroni
 */
public class BuatKunci {

    private BigInteger y;
    private boolean cek;
    private int bilanganPrima;

    public BigInteger getKunci(BigInteger p, BigInteger g, BigInteger x) {
        y = g.modPow(x, p);
        return y;
    }

    public void setPrima(int bilanganPrima) {
        this.bilanganPrima = bilanganPrima;
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
}
