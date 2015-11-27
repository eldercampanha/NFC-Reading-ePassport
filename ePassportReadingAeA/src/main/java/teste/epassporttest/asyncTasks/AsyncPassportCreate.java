package teste.epassporttest.asyncTasks;

import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.util.Log;
import net.sourceforge.scuba.smartcards.CardService;
import net.sourceforge.scuba.smartcards.CardServiceException;
import org.jmrtd.BACDeniedException;
import org.jmrtd.MRTDTrustStore;
import org.jmrtd.Passport;
import org.jmrtd.PassportService;
import teste.epassporttest.singletons.ePassportStaticData;

/**
 * Created by Elder on 24/11/2015.
 */
public class AsyncPassportCreate extends AsyncTask<IsoDep, String, Passport> {

    private long t1=0;
    private long t2=0;
    verifyPassportCreation internInterface;

    public interface verifyPassportCreation {

        void runSucess(Passport passport);
        void fail(String error);
    }

    public AsyncPassportCreate(verifyPassportCreation interfaceTest){

        internInterface = interfaceTest;
    }

    @Override
    protected Passport doInBackground(IsoDep... params) {

        try {

            IsoDep iso=params[0];
            //cardService instance for nfc communication
            CardService cService=CardService.getInstance(iso);
            cService.open();//begin new session
            //passportService for epassport especial case communication
            PassportService pService =new PassportService(cService);
            //add Debug information

				/*	pService.addPlainTextAPDUListener(new APDUListener() {

					@Override
				public void exchangedAPDU(APDUEvent event) {

						Log.i("NFC CHAT: Command", Hex.bytesToPrettyString(event.getCommandAPDU().getBytes()));
						Log.i("NFC CHAT: Response", Hex.bytesToPrettyString(event.getResponseAPDU().getBytes()));
					}

				});

				pService.addAPDUListener(new APDUListener() {

					@Override
					public void exchangedAPDU(APDUEvent event) {

						Log.i("NFC CHAT: Command", Hex.bytesToPrettyString(event.getCommandAPDU().getBytes()));
						Log.i("NFC CHAT: Response", Hex.bytesToPrettyString(event.getResponseAPDU().getBytes()));
					}

				});
				 */


            try
            {

                t1=System.currentTimeMillis();
                Passport p=new Passport(pService,new MRTDTrustStore(),ePassportStaticData.getInstance().getPassportListAsB(),1);
                t2=System.currentTimeMillis();
                return p;

            }
            catch (BACDeniedException cse)
            {
                t2=System.currentTimeMillis();
                internInterface.fail(cse.getMessage());
                Log.d("ERROR", "BACDenied Exception" + cse.getMessage());
            }
            return null;

        }
        catch (CardServiceException cse)
        {
            Log.d("ERROR","CardService Exception "+cse.getMessage());
            cse.printStackTrace();
            return null;
        }
        catch (Exception e)
        {
            Log.d("ERROR", "another exception happened");
            return null;
        }
        finally
        {
            Log.i("TIME","Readding attemp completed in: "+(t2-t1)+" miliseconds");

        }
    }

    protected void onPostExecute(Passport passport) {

        // if (passport == null) { throw new IllegalArgumentException("Failed to get a passport"); }
        if (passport == null) {

            if(internInterface != null)
            {
                internInterface.fail("Failed to get a passport");
            }

        }
        else
        {

            if(internInterface != null)
            {
                internInterface.runSucess(passport);
            }

        }
    }


}
