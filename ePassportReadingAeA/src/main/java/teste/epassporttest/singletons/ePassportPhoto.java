package teste.epassporttest.singletons;

import android.graphics.Bitmap;

/**
 * Created by Elder on 25/11/2015.
 */
public class ePassportPhoto {

    private Bitmap img;

    private static ePassportPhoto ePassportPhotoInstance;

    public static ePassportPhoto getInstance() {

        if(ePassportPhotoInstance == null)
            ePassportPhotoInstance = new ePassportPhoto();

        return ePassportPhotoInstance;
    }

    private ePassportPhoto() {
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
