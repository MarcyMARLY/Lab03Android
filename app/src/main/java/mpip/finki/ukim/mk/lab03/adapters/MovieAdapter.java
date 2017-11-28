package mpip.finki.ukim.mk.lab03.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import mpip.finki.ukim.mk.lab03.MovieDetailPreview;
import mpip.finki.ukim.mk.lab03.R;
import mpip.finki.ukim.mk.lab03.adapters.holder.ActivityInfoItem;
import mpip.finki.ukim.mk.lab03.model.Movie;

/**
 * Created by Ljupche on 24-Nov-17.
 */

public class MovieAdapter extends RecyclerView.Adapter<ActivityInfoItem> {
    private List<Movie> data = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    public MovieAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ActivityInfoItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.activity_info_item, null);
        ActivityInfoItem holder = new ActivityInfoItem(rootView);
        holder.poster = rootView.findViewById(R.id.poster);
        holder.title = rootView.findViewById(R.id.item_title);
        holder.year = rootView.findViewById(R.id.item_year);
        rootView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ActivityInfoItem holder, final int position) {
        final Movie movie = data.get(position);

        holder.title.setText(movie.title);
        holder.year.setText(movie.year);

        Picasso
                .with(context)
                .load(movie.poster)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.poster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailPreview.class);
                intent.putExtra("id",movie.getId());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                data.remove(position);
                notifyDataSetChanged();
                return true;
            }

        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Movie> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }
}
