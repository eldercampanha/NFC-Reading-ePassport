package teste.epassporttest.asyncTasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import org.jmrtd.Passport;
import org.jmrtd.PassportService;
import org.jmrtd.lds.DG14File;
import org.jmrtd.lds.DG1File;
import org.jmrtd.lds.DG2File;
import org.jmrtd.lds.FaceImageInfo;
import org.jmrtd.lds.FaceInfo;
import org.jmrtd.lds.LDS;
import org.jmrtd.lds.LDSFile;
import org.jmrtd.lds.SODFile;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import teste.epassporttest.singletons.ePassportPhoto;
import teste.epassporttest.data.Passenger;
import teste.epassporttest.utils.Utils;

/**
 * Created by Elder on 24/11/2015.
 */

public class AsyncImageDecodification extends AsyncTask<Passport,LDSFile, Integer>{

    private final String TAG = "TAG";
    private Passenger passenger = new Passenger();
    private ReturnUserData returnUserData;


    public interface ReturnUserData
    {
        void start();
        void success();
        void fail(String error);
        void AsycnProcessFinished();
    }

    public AsyncImageDecodification(ReturnUserData returnUserData){
        this.returnUserData = returnUserData;
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

                        final List<FaceImageInfo> allFaceImageInfos = new ArrayList<FaceImageInfo>();
                        List<FaceInfo> faceInfos = dg2.getFaceInfos();

                        for (FaceInfo faceInfo : faceInfos)
                        {
                            allFaceImageInfos.addAll(faceInfo.getFaceImageInfos());
                        }

                        if (allFaceImageInfos.size() > 0) {
                            Log.i(TAG + "[INFO]", "This passport has images attached");

                            //GET IMAGE

                            try
                            {
                                FaceImageInfo faceImage=allFaceImageInfos.get(0);
                                Log.i(TAG +"Bprogess: ", "...on doInBackground");

                                int imageLength = faceImage.getImageLength();
                                String mimeType = faceImage.getMimeType();
                                InputStream imageInputStream = faceImage.getImageInputStream(); /* These are buffered by now */
                                DataInputStream dataInputStream = new DataInputStream(imageInputStream);
                                byte[] imageBytes = new byte[imageLength];
                                dataInputStream.readFully(imageBytes);
                                final Bitmap bitmap;
                                bitmap = Utils.read(new ByteArrayInputStream(imageBytes), imageLength, mimeType);

                                ePassportPhoto.getInstance().setImg(bitmap);
                                returnUserData.success();
                                Log.i("SSS", "Image sucess " + passenger.getName());


                            } catch (Exception e) {

                                returnUserData.fail(e.getMessage());
                                System.err.println("DEBUG: EXCEPTION: " + e.getMessage());
                                e.printStackTrace();
                                return null;
                            }


                        }
                        else
                        {
                            returnUserData.fail("0 images found");
                        }

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
            returnUserData.fail("error + "+e.getMessage());
            Log.i(TAG+"Error", e.getMessage());
            return null;
        }

    }

    protected void onProgressUpdate(LDSFile... values) {}

    protected void onPostExecute(Integer i)
    {
        returnUserData.AsycnProcessFinished();
    }

}
