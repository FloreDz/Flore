package dz.esi.team.appprototype.glide;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by The King Mohamed on 28/03/2017.
 */

public class CircleTransform extends BitmapTransformation {

    public CircleTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        return ImageUtils.getCircularBitmapImage(source);
    }

    @Override
    public String getId() {
        return "Glide_Circle_Transformation";
    }
}
