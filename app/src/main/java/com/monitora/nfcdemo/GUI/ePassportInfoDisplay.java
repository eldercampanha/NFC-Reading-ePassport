package com.monitora.nfcdemo.GUI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.monitora.nfcdemo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import teste.epassporttest.Facade.Facade;
import teste.epassporttest.data.Credentials;
import teste.epassporttest.data.Passenger;

public class ePassportInfoDisplay extends Activity
{
    private TextView name,surnames,Bdate,gender, nationality;
    private ImageView photo;
    Facade fachada;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_passport_info_display);
        views();

        startReading();
    }

    private void startReading()
    {
        fachada = new Facade(getFakeData(), new Facade.ReadingPassport()
        {
            @Override
            public void readingPassportDataSucess(Passenger passenger)
            {
                name.setText(passenger.getName());
                surnames.setText(passenger.getSurname());
                Bdate.setText(passenger.getBdate());
                gender.setText(passenger.getGender());
                nationality.setText(passenger.getNationality());
            }

            @Override
            public void readingPassportImageSucess(Bitmap passengerPhoto)
            {
                photo.setImageBitmap(passengerPhoto);
            }

            @Override
            public void readingPassportDataFail(String error) {}

            @Override
            public void readingPassportImageFail(String error) {}

            @Override
            public void readingPassportFinished()
            {
                findViewById(teste.epassporttest.R.id.loadingPanel).setVisibility(View.GONE);
            }

            @Override
            public void readingPassportFail(String error) {}

        });

        fachada.handleIntent(this.getIntent());
    }

    //ASSOCIA COMPONENTES A VARIAVEIS
    private void views(){
        name=(TextView)findViewById(teste.epassporttest.R.id.resourceName);
        surnames=(TextView)findViewById(teste.epassporttest.R.id.surnamesText);
        Bdate=(TextView)findViewById(teste.epassporttest.R.id.dateofbirthText);
        gender=(TextView)findViewById(teste.epassporttest.R.id.genderText);
        nationality=(TextView)findViewById(teste.epassporttest.R.id.nationalityText);
        photo = (ImageView) findViewById(teste.epassporttest.R.id.passportPhoto);
    }

    //*********************  METHOD USED FOR TEST  ***********************************************************************************
    private ArrayList<Credentials> getFakeData()
    {
        ArrayList<Credentials> allCredentials = new ArrayList<Credentials>();

        try
        {
            SimpleDateFormat formater_dmy=new SimpleDateFormat("dd/MM/yy");
            Credentials credentialGabriel = new Credentials("FK083204",formater_dmy.parse("30/12/94"), formater_dmy.parse("07/05/19"));
            Credentials credentialIvan = new Credentials("FM396594",formater_dmy.parse("09/10/84"), formater_dmy.parse("25/01/20"));

            allCredentials.add(credentialGabriel);
            allCredentials.add(credentialIvan);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return allCredentials;
    }


}
