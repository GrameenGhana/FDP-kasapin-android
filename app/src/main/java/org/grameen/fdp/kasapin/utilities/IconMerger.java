package org.grameen.fdp.kasapin.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Arrays;
import java.util.LinkedList;

public class IconMerger {
    Context context;

    public IconMerger(Context c){
        this.context = c;
    }


    public Bitmap combineIcons(String activities) {
        String[] names = activities.split(",");
        AppLogger.e(Arrays.toString(names));
        LinkedList<Bitmap> bitmaps = new LinkedList<>();
        if(names.length == 0) return null;

            for(String name : names) {
                Bitmap iconBitmap = bitmapFromDrawable(name);
                if(iconBitmap != null)
                    bitmaps.add(iconBitmap);
            }

        return ImageUtil.mergeIconsToBitmap(bitmaps);
    }


      Bitmap bitmapFromDrawable(String name) {
        int resID = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        if(resID < 0) return null;
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }
}
