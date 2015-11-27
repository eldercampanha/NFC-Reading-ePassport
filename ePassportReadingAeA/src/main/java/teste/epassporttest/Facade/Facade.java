package teste.epassporttest.Facade;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;
import android.widget.Toast;
import org.jmrtd.Passport;
import java.util.ArrayList;
import java.util.Arrays;
import teste.epassporttest.GUI.PassportInfoDisplay;
import teste.epassporttest.asyncTasks.AsyncDataDecodification;
import teste.epassporttest.asyncTasks.AsyncImageDecodification;
import teste.epassporttest.asyncTasks.AsyncPassportCreate;
import teste.epassporttest.data.Credentials;
import teste.epassporttest.data.Passenger;
import teste.epassporttest.singletons.ePassportPhoto;
import teste.epassporttest.singletons.ePassportStaticData;
import teste.epassporttest.utils.Utils;

/**
 * Created by Elder on 25/11/2015.
 */
public class Facade {

  //  private NfcAdapter NFCadapter;
    private long t1 = 0;
    private long t2 = 0;

    private ReadingPassport internReadingPassportInterface;

    public Facade(){

    }

    public interface ReadingPassport {

        void readingPassportDataSucess(Passenger passenger);

        void readingPassportImageSucess(Bitmap passengerPhoto);

        void readingPassportDataFail(String error);

        void readingPassportImageFail(String error);

        void readingPassportFinished();

        void readingPassportFail(String error);
    }

    public Facade( ArrayList<Credentials> credentialsList, ReadingPassport readingPassport) {
        internReadingPassportInterface = readingPassport;
        ePassportStaticData.getInstance().setPassportList(credentialsList);
    }


    public void handleIntent(Intent intent){
        if (intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {

            if (intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) != null) {

                Tag t = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if (Arrays.asList(t.getTechList()).contains("android.nfc.tech.IsoDep")) {
                    beginePassportCommunication(IsoDep.get(t));
                }

            }

        } else {
            Log.i("error", intent.getAction());
        }

    }

    private void beginePassportCommunication(IsoDep dep) {

        try
        {
            dep.setTimeout(1000);

            new AsyncPassportCreate(new AsyncPassportCreate.verifyPassportCreation() {

                public void runSucess(Passport passport)
                {
                    readPassportData(passport);
                    readPassportImage(passport);
                }

                @Override
                public void fail(String error) {
                    internReadingPassportInterface.readingPassportFail(error);
                }
            }).execute(dep);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void readPassportData(Passport passport) {
        new AsyncDataDecodification(new AsyncDataDecodification.ReturnUserData() {

            @Override
            public void start() {
            }

            @Override
            public void success(Passenger passenger) {
                internReadingPassportInterface.readingPassportDataSucess(passenger);
            }

            @Override
            public void fail(String error) {
                internReadingPassportInterface.readingPassportDataFail(error);
            }

            @Override
            public void AsycnProcessFinished() {

            }
        }).execute(passport);
    }

    public void readPassportImage(Passport passport) {
        new AsyncImageDecodification(new AsyncImageDecodification.ReturnUserData() {
            @Override
            public void start() {
            }

            @Override
            public void success() {
                internReadingPassportInterface.readingPassportImageSucess(ePassportPhoto.getInstance().getImg());
            }

            @Override
            public void fail(String error) {
                internReadingPassportInterface.readingPassportImageFail(error);
            }

            @Override
            public void AsycnProcessFinished() {
                internReadingPassportInterface.readingPassportFinished();
            }

        }).execute(passport);
    }
}