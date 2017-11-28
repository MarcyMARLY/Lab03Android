package mpip.finki.ukim.mk.lab03.adapters.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Ljupche on 24-Nov-17.
 */

public class ActivityInfoItem extends RecyclerView.ViewHolder {
    public ImageView poster;
    public TextView title;
    public TextView year;


    public ActivityInfoItem(View itemView) {
        super(itemView);
    }
}
