package teste.epassporttest.singletons;

import org.jmrtd.BACKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import teste.epassporttest.data.Credentials;

/**
 * Created by Elder on 25/11/2015.
 */
public class ePassportStaticData {

    private Vector<BACKeySpec> passportListAsB = new Vector<BACKeySpec>();;

    private static ePassportStaticData ourInstance;

    public static ePassportStaticData getInstance()
    {
        if(ourInstance == null)
            ourInstance = new ePassportStaticData();

        return ourInstance;
    }

    private ePassportStaticData() {}

    public void addCredential(Credentials... arrayCredentials)
    {
        for(Credentials c : arrayCredentials)
            getPassportListAsB().add(c);
    }

    public Vector<BACKeySpec> getPassportListAsB() {
        return passportListAsB;
    }

    public void setPassportListAsB(Vector<BACKeySpec> passportListAsB)
    {
        this.passportListAsB = passportListAsB;
    }

    public ArrayList<Credentials> getEPassports()
    {
        ArrayList<Credentials> result = new ArrayList<Credentials>();

        for (BACKeySpec b : passportListAsB)
        {
            try
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                Date birthDate = dateFormat.parse(b.getDateOfBirth()); //Position 1: Date of birth
                Date expiryDate = dateFormat.parse(b.getDateOfExpiry()); //Position 2: Date of expiration

                Credentials epassport = new Credentials(b.getDocumentNumber(), birthDate.toString(), expiryDate.toString());
                result.add(epassport);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void setPassportList(ArrayList<Credentials> list)
    {
        for (Credentials c: list)
        {
            this.passportListAsB.add(c);
        }
    }
}
