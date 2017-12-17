/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harit.elgamalandroid.elgamal;

import java.math.BigInteger;

/**
 * @author isahroni
 */
public class EnkripDekrip {

    private BigInteger gamma;
    private BigInteger delta;
    private char chr;

    public String getEnkripsi(String chrASCII, String rnd, int g,
                              int p, int y, String pesan) {

        BigInteger a = BigInteger.valueOf(p);
        BigInteger b = BigInteger.valueOf(g);
        BigInteger c = BigInteger.valueOf(y);

           BigInteger m = new BigInteger(chrASCII);
            BigInteger k = new BigInteger(rnd);

            //gamma = g.modPow(k, p);
            gamma = b.modPow(k, a);
            //delta = y.pow(k.intValue()).multiply(m).mod(p);
            delta = c.pow(k.intValue()).multiply(m).mod(a);
        return gamma.toString() + " " + delta.toString() + " ";
    }


    public char getDekripsi(String nGamma, String nDelta,
                            BigInteger p, BigInteger x) {
            BigInteger a = new BigInteger(nGamma);
            BigInteger b = new BigInteger(nDelta);

            BigInteger m = b.multiply(a.pow(p.intValue() - 1 - x.intValue())).mod(p);
            int ma = m.intValue();
            chr = (char) ma;

        return chr;
    }

}
