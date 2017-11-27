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
public class EnkripDekrip {

    private BigInteger gamma;
    private BigInteger delta;
    private char chr;

    public String getEnkripsi(String chrASCII, String rnd, BigInteger g,
            BigInteger p, BigInteger y, String pesan) {

        for (int i = 0; i < pesan.length(); i++) {
            BigInteger m = new BigInteger(chrASCII);
            BigInteger k = new BigInteger(rnd);

            gamma = g.modPow(k, p);

            delta = y.pow(k.intValue()).multiply(m).mod(p);

        }
        return gamma.toString() + " " + delta.toString()+" ";
    }

    public char getDekripsi(String nGamma, String nDelta,
            BigInteger p, BigInteger x, String pesan) {

        for (int i = 0; i < pesan.length(); i++) {

            BigInteger a = new BigInteger(nGamma);
            BigInteger b = new BigInteger(nDelta);

            BigInteger m = b.multiply(a.pow(p.intValue() - 1 - x.intValue())).mod(p);
            int ma = m.intValue();
            chr = (char) ma;
        }
        return chr;
    }
}
