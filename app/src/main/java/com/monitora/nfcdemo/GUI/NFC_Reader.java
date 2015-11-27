package com.monitora.nfcdemo.GUI;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.monitora.nfcdemo.R;

public class NFC_Reader extends AppCompatActivity {

    private NfcAdapter NFCadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc__reader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch();
    }

    public void enableForegroundDispatch() {

        Context context = this.getApplicationContext();
        NFCadapter = NfcAdapter.getDefaultAdapter(this); // get default nfc adapter

        if (NFCadapter == null)
        {
            // tratar erro
        }
        else if (!NFCadapter.isEnabled())
        {
            // tratar erro
        }
        else
        {
            //prepare the intent to the reader activity
            Intent i = new Intent(context, ePassportInfoDisplay.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pending = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            String[][] techs = new String[][]{new String[]{"android.nfc.tech.IsoDep"}};

            //enable the foregroundDispatch,
            //this will give this PassportIfoDisplay priority over another
            //to manage this intent
            NFCadapter.enableForegroundDispatch(this, pending, null, techs);
        }
    }

    public void disableForegroundDispatch()
    {
        try
        {
            NFCadapter.disableForegroundDispatch(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
