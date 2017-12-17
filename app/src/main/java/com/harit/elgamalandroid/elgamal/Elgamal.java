/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harit.elgamalandroid.elgamal;

import android.content.Context;

import com.harit.elgamalandroid.DBHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * @author isahroni
 */
public class Elgamal {

    private static BigInteger p, g, y, x;
    private DBHelper dbHelper;

    public Elgamal(Context context){
        dbHelper = new DBHelper(context);
    }
    public String encrypt(String message, Calendar dateSent){
        int index = dateSent.get(Calendar.HOUR_OF_DAY) + 1;

        int p = dbHelper.getPrimeBasedOnIndex(index);
        int g = p - index;
        int x = dateSent.get(Calendar.DAY_OF_MONTH);

        //check nilai pgx
        if (p < 255) {
            System.out.println("Bilangan Harus Lebih Besar Dari 255");
            return null;
        } else if (g < 1 | g >= p - 1) {
            System.out.println("Nilai g : 1 < g <= p-1");
            return null;
        } else if (x < 1 | x >= p - 2) {
            System.out.println("Nilai x : 1 < x <= p-2");
            return null;
        }else{

        }
        //kelas untuk hitung y
        BuatKunci bk = new BuatKunci();
        KonversiChar converter = new KonversiChar();
        BilanganAcak number = new BilanganAcak();
        EnkripDekrip ed = new EnkripDekrip();

        bk.setPrima(p);

        if (bk.isPrima() == false) {
            System.out.println(p + " bukan bilangan prima");
            return null;
        }


        BigInteger y = bk.getKunci(p, g, x);
        System.out.println("Kunci Public : " + p + "," + g + "," + y);
        System.out.println("Kunci Private : " + x + "\n");

        //konversi char ke ASCII
        ArrayList chr = converter.getCharASCII(message);

        //membuat nilai random
        ArrayList rn = number.getBilanganAcak(message, p);


        System.out.println("Pesan\tASCII\tNilai Random");
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            System.out.println(c + "\t" + chr.get(i).toString()
                    + "\t" + rn.get(i));
        }

        String cipher = "";
        String hasilEnkrip = "";
        //==============================================================
        //Proses Enkripsi
        System.out.println("\n+++++ENKRIPSI+++++");
        for (int i = 0; i < message.length(); i++) {
            cipher = ed.getEnkripsi(chr.get(i).toString(),
                    rn.get(i).toString(), g, p, y.intValue(), message);
            hasilEnkrip += cipher;
        }

        System.out.print("Chiper : " + hasilEnkrip);
        return hasilEnkrip;
    }

    public String decrypt(String message, Calendar dateSent){

        int index = dateSent.get(Calendar.HOUR_OF_DAY) + 1;

        BigInteger p = BigInteger.valueOf(dbHelper.getPrimeBasedOnIndex(index));
        BigInteger x = BigInteger.valueOf(dateSent.get(Calendar.DAY_OF_MONTH));

        System.out.println("p : " + p + ", x" + x);

        //Proses Dekripsi
        PecahChiperText pct = new PecahChiperText();

        pct.setChiper(message);

        ArrayList ngama = pct.getGamma();
        ArrayList ndelta = pct.getDelta();
        EnkripDekrip ed = new EnkripDekrip();

        String hasilDekrip = "";
        for (int i = 0; i < ngama.size(); i++) {
            char dek = ed.getDekripsi(ngama.get(i).toString(),
                    ndelta.get(i).toString(), p, x);
            hasilDekrip += dek;
        }

        return hasilDekrip;

    }

    public String decrypt(String message, int publicKey, int privateKey) {


        BigInteger p = BigInteger.valueOf(publicKey);
        BigInteger x = BigInteger.valueOf(privateKey);


        System.out.println("p : " + p + ", x" + x);

        //Proses Dekripsi
        PecahChiperText pct = new PecahChiperText();

        pct.setChiper(message);

        ArrayList ngama = pct.getGamma();
        ArrayList ndelta = pct.getDelta();
        EnkripDekrip ed = new EnkripDekrip();

        String hasilDekrip = "";
        for (int i = 0; i < ngama.size(); i++) {
            char dek = ed.getDekripsi(ngama.get(i).toString(),
                    ndelta.get(i).toString(), p, x);
            hasilDekrip += dek;
        }

        return hasilDekrip;

    }

    //checks whether an int is prime or not.
    public boolean isPrime(int n) {
        //check if n is a multiple of 2
        if (n % 2 == 0) return false;
        //if not, then just check the odds
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    public String encrypt(String message, int p, int x) {

        int g = p - 4;

        //kelas untuk hitung y
        BuatKunci bk = new BuatKunci();
        KonversiChar converter = new KonversiChar();
        BilanganAcak number = new BilanganAcak();
        EnkripDekrip ed = new EnkripDekrip();

        bk.setPrima(p);
        BigInteger y = bk.getKunci(p, g, x);
        System.out.println("Kunci Public : " + p + "," + g + "," + y);
        System.out.println("Kunci Private : " + x + "\n");

        //konversi char ke ASCII
        ArrayList chr = converter.getCharASCII(message);

        //membuat nilai random
        ArrayList rn = number.getBilanganAcak(message, p);


        System.out.println("Pesan\tASCII\tNilai Random");
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            System.out.println(c + "\t" + chr.get(i).toString()
                    + "\t" + rn.get(i));
        }

        String cipher = "";
        String hasilEnkrip = "";
        //==============================================================
        //Proses Enkripsi
        System.out.println("\n+++++ENKRIPSI+++++");
        for (int i = 0; i < message.length(); i++) {
            cipher = ed.getEnkripsi(chr.get(i).toString(),
                    rn.get(i).toString(), g, p, y.intValue(), message);
            hasilEnkrip += cipher;
        }

        System.out.print("Chiper : " + hasilEnkrip);
        return hasilEnkrip;
    }
}
