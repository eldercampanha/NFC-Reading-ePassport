package teste.epassporttest.GUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import teste.epassporttest.Facade.Facade;
import teste.epassporttest.R;
import teste.epassporttest.data.Credentials;
import teste.epassporttest.data.Passenger;

public class PassportInfoDisplay extends Activity {

    private TextView name,surnames,Bdate,gender, nationality;
    private ImageView photo;
    private Facade fachada;

    private long t1=0;
    private long t2=0;

//*********************  METHOD USED FOR TEST  ***********************************************************************************
    private ArrayList<Credentials> getFakeData()
    {
        ArrayList<Credentials> allCredentials = new ArrayList<Credentials>();

        try
        {
            // use the info from the passport that will be scanned
            SimpleDateFormat formater_dmy=new SimpleDateFormat("dd/MM/yy");
            Credentials credentialA = new Credentials("XXXXXXXX",formater_dmy.parse("30/12/94"), formater_dmy.parse("07/05/19"));
            Credentials credentialB = new Credentials("XXXXXXXX",formater_dmy.parse("09/10/84"), formater_dmy.parse("25/01/20"));

            allCredentials.add(credentialA);
            allCredentials.add(credentialB);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return allCredentials;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passport_info_display_layout);
        views();

        //method responsable for start the process
        startReading();
    }

    private void startReading()
    {
        fachada = new Facade(getFakeData(),
                new Facade.ReadingPassport()
                {

                        @Override
                        public void readingPassportDataSucess(Passenger passenger) {
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
                        public void readingPassportDataFail(String error) {
                        }

                        @Override
                        public void readingPassportImageFail(String error) {
                        }

                        @Override
                        public void readingPassportFinished()
                        {
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        }

                        @Override
                        public void readingPassportFail(String error)
                        {
                            Toast.makeText(getApplicationContext(), "Passport Reading failed:"+error,Toast.LENGTH_LONG).show();
                            finish();
                        }
                }
        );

        fachada.handleIntent(this.getIntent());
    }

    //ASSOCIA COMPONENTES A VARIAVEIS
    private void views(){
        name=(TextView)findViewById(R.id.resourceName);
        surnames=(TextView)findViewById(R.id.surnamesText);
        Bdate=(TextView)findViewById(R.id.dateofbirthText);
        gender=(TextView)findViewById(R.id.genderText);
        nationality=(TextView)findViewById(R.id.nationalityText);
        photo = (ImageView) findViewById(R.id.passportPhoto);
    }

}

