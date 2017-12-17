package com.harit.elgamalandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.harit.elgamalandroid.elgamal.Elgamal;

/**
 * Created by harit on 12/16/2017.
 */

public class DecryptDialogFragment extends DialogFragment implements View.OnClickListener {
    String cipher;
    TextView tv;
    EditText etP, etX;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        cipher = getArguments().getString("cipher");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_manual, null);

        tv = v.findViewById(R.id.tv_cipher_manual);
        tv.setText(cipher);

        etP = v.findViewById(R.id.et_public_manual);
        etX = v.findViewById(R.id.et_private_manual);

        ImageButton btn = v.findViewById(R.id.btn_encrypt_manual);
        btn.setOnClickListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                .setTitle("Cipher manual")
                // Add action buttons
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                });


        return builder.create();
    }

    @Override
    public void onClick(View view) {
        Elgamal e = new Elgamal(getContext());
        int p = Integer.parseInt(etP.getText().toString());
        int x = Integer.parseInt(etX.getText().toString());

        //check nilai pgx
        if (p < 255) {
            System.out.println("Bilangan Harus Lebih Besar Dari 255");
        } else if (e.isPrime(p) == false) {
            System.out.println("Bilangan Harus Lebih Besar Dari 255");

        } else if (x < 1 | x >= x - 2) {
            System.out.println("Nilai x : 1 < x <= p-2");

        }

        String decrypted = e.decrypt(cipher, p, x);
        tv.setText(decrypted);
    }
}
