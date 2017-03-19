package dz.esi.team.appprototype.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dz.esi.team.appprototype.R;


/**
 * Created by azeddine on 07/03/17.
 */

public  class ViewHolder {

    private final TextView ViewTitel;

    public ViewHolder(View view, int titleWidgetId) {
        ViewTitel = (TextView) view.findViewById(titleWidgetId);

    }

    public TextView getTitle() {
        return ViewTitel;
    }



}
