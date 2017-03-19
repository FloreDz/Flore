package dz.esi.team.appprototype.utils;

import android.view.View;
import android.widget.TextView;

import dz.esi.team.appprototype.R;


/**
 * Created by azeddine on 07/03/17.
 */

public  class ViewHolder {

    private final TextView title;


    public ViewHolder(View view, int WidgetTitleId) {
        title = (TextView) view.findViewById(WidgetTitleId);
    }

    public TextView getTitle() {
        return title;
    }


}
