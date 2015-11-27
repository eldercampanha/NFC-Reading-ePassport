package teste.epassporttest.asyncTasks;
import android.os.AsyncTask;
import android.util.Log;
import org.jmrtd.Passport;
import org.jmrtd.PassportService;
import org.jmrtd.lds.DG14File;
import org.jmrtd.lds.DG1File;
import org.jmrtd.lds.DG2File;
import org.jmrtd.lds.DataGroup;
import org.jmrtd.lds.LDS;
import org.jmrtd.lds.LDSFile;
import org.jmrtd.lds.SODFile;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import teste.epassporttest.data.Passenger;
import teste.epassporttest.utils.Utils;

/**
 * Created by Elder on 24/11/2015.
 */

public class AsyncDataDecodification extends AsyncTask<Passport,LDSFile, Integer>{

    private final String TAG = "TAG";
    private Passenger passenger = new Passenger();
    private ReturnUserData returnUserData;


    public interface ReturnUserData
    {
        void start();
        void success(Passenger passenger);
        void fail(String error);
        void AsycnProcessFinished();
    }

    public AsyncDataDecodification(ReturnUserData returnUserData)
    {
        this.returnUserData = returnUserData;

        if(returnUserData != null)
        {
            returnUserData.start();
        }
    }

    protected Integer doInBackground(Passport... params)
    {
        try
        {
            Passport passport=params[0];
            LDS logicDataStructure=passport.getLDS();

            List<Short> DGList= logicDataStructure.getFileList();
            Collections.sort(DGList);

            for(short dg: DGList)
            {
                switch(dg)
                {
                    case PassportService.EF_COM:
                        break;
                    case PassportService.EF_SOD:
                        SODFile sod=logicDataStructure.getSODFile();
                        publishProgress(sod);
                        break;
                    case PassportService.EF_DG1:
                        DG1File dg1=logicDataStructure.getDG1File();
                        publishProgress(dg1);
                        break;
                    case PassportService.EF_DG2:
                        DG2File dg2=logicDataStructure.getDG2File();
                        break;
                    case PassportService.EF_DG14:
                        DG14File dg14=logicDataStructure.getDG14File();
                        publishProgress(dg14);
                        break;

                    default:
                        Log.i(TAG+"DG list", "Ignored DataGroup found "+dg);

                        break;
                }
            }

            return 0;
        }catch(Exception e){
            Log.i(TAG+"Error", e.getMessage());
            return null;
        }
    }

    protected void onProgressUpdate(LDSFile... values)
    {
        LDSFile file=values[0];

        if(file instanceof SODFile)
        {
            X500Principal principal= ((SODFile) file).getIssuerX500Principal();
            String name=principal.getName(X500Principal.RFC1779);
            Log.i(TAG+"DATA","issuerName: "+name);

            try
            {
                X509Certificate cert=((SODFile) file).getDocSigningCertificate();
                PublicKey pkey=cert.getPublicKey();
                Log.i(TAG+"[CERT PK]", Utils.bytesToHex(pkey.getEncoded()));
                Log.i(TAG+"crypto", "DATAGROUP DIGEST ALGORITHM: "+((SODFile)file).getDigestAlgorithm());
            }
            catch (CertificateException e)
            {
                Log.i(TAG+"Error", e.getMessage());
            }

        }
        else if (file instanceof DataGroup)
        {

            if(file instanceof DG1File){
                DG1File dg1_file=(DG1File)file;
                Log.i(TAG+"DATA", dg1_file.getMRZInfo().getSecondaryIdentifier().replace("<", "") + " " + dg1_file.getMRZInfo().getPrimaryIdentifier());

                if(passenger != null)
                {
                    passenger.setName(dg1_file.getMRZInfo().getPrimaryIdentifier());
                    passenger.setSurname(dg1_file.getMRZInfo().getSecondaryIdentifier().replace("<", ""));
                    passenger.setBdate(dg1_file.getMRZInfo().getDateOfBirth());
                    passenger.setGender(dg1_file.getMRZInfo().getGender().toString());
                    passenger.setNationality(dg1_file.getMRZInfo().getNationality());
                    passenger.setPassportNumber(dg1_file.getMRZInfo().getDocumentNumber());
                    passenger.setDocumentID(dg1_file.getMRZInfo().getPersonalNumber());
                    passenger.setExpiryDate(dg1_file.getMRZInfo().getDateOfExpiry());

                    if(returnUserData != null)
                    {
                        returnUserData.success(passenger);
                    }

                    Log.i("SSS", "Data sucess " + passenger.getName());
                }
                else
                {
                    if(returnUserData != null)
                    {
                        returnUserData.fail("error when trying to decodificate Data");
                    }
                }

            }
            if(file instanceof DG14File)
            {
                DG14File f=(DG14File)file;
            }

        }
        else
        {
            returnUserData.fail("Error when trying to read LDS file");
        }

    }

    protected void onPostExecute(Integer i)
    {
        returnUserData.AsycnProcessFinished();
    }

}
