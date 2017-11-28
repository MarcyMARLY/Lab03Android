package mpip.finki.ukim.mk.lab03;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import mpip.finki.ukim.mk.lab03.model.Movie;
import mpip.finki.ukim.mk.lab03.model.MovieDetail;
import mpip.finki.ukim.mk.lab03.persistence.MoviesDatabase;
import mpip.finki.ukim.mk.lab03.web.MoviesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ljupche on 24-Nov-17.
 */

public class MovieDetailPreview extends AppCompatActivity {
    private MoviesAPI service;
    private ShareActionProvider mShareActionProvider;

    private String movieTitle;
    private String year;
    private String director;
    Intent sharingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail_preview);


        String id = this.getIntent().getStringExtra("id");
        MenuItem item = findViewById(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//        textView.setText();

        makeMoviesSearch(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_detail, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);



        return true;
    }

    private void setShareIntentd(Intent shareIntent) {
        mShareActionProvider.setShareIntent(shareIntent);
    }

    public class MovieQueryTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            invalidateOptionsMenu();
            String id = params[0];
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
                Gson gson = new GsonBuilder().create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://www.omdbapi.com")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                service = retrofit.create(MoviesAPI.class);

                Call<MovieDetail> movies = service.getById(id);
                final MovieDetail[] s = {null};
                movies.enqueue(new Callback<MovieDetail>() {
                    @Override
                    public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                        s[0] = response.body();

                        movieTitle = s[0].getTitle();
                        year = s[0].getYear();
                        director = s[0].getDirector();



                        TextView title = (TextView)findViewById(R.id.title);
                        title.setText("Title: " + s[0].getTitle());

                        TextView year = (TextView)findViewById(R.id.year);
                        year.setText("Year: " + s[0].getYear());

                        TextView released = (TextView)findViewById(R.id.released);
                        released.setText("Released: " + s[0].getReleased());

                        TextView runtime = (TextView)findViewById(R.id.runtime);
                        runtime.setText("Runtime: " + s[0].getRuntime());

                        TextView genre = (TextView)findViewById(R.id.genre);
                        genre.setText("Genre: " + s[0].getGenre());

                        TextView director = (TextView)findViewById(R.id.director);
                        director.setText("Director: " + s[0].getDirector());

                        TextView writer = (TextView)findViewById(R.id.writer);
                        writer.setText("Writer: " + s[0].getWriter());

                        ImageView poster = (ImageView)findViewById(R.id.poster2);

                        Picasso
                                .with(getApplicationContext())
                                .load(s[0].getPoster())
                                .placeholder(R.mipmap.ic_launcher)
                                .into(poster);

                        //TODO: All attributes of movie
                    }

                    @Override
                    public void onFailure(Call<MovieDetail> call, Throwable t) {

                    }
                });
            }
            else {
                MoviesDatabase db = Room.databaseBuilder(getApplicationContext(),
                        MoviesDatabase.class, "movies")
                        .build();

                List<Movie> movies = db.movieDao().getById(id);
                for (Movie m :movies) {
                    movieTitle = m.getTitle();
                    year = m.getYear();

//                    sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                    sharingIntent.setType("text/plain");
//                    String shareBody = "Year: " + year;
//                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, movieTitle);
//                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

//                    setShareIntentd(sharingIntent);

                    TextView title = (TextView) findViewById(R.id.title);
                    title.setText("Title: " + movieTitle);

                    TextView year = (TextView) findViewById(R.id.year);
                    year.setText("Year: " + year);

                    ImageView poster = (ImageView) findViewById(R.id.poster2);

                    Picasso
                            .with(getApplicationContext())
                            .load(m.getPoster())
                            .placeholder(R.mipmap.ic_launcher)
                            .into(poster);
                }

            }
            sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Year: " + year;
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, movieTitle);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            setShareIntentd(sharingIntent);
            return null;
        }
    }

    private void makeMoviesSearch(String query) {
        new MovieQueryTask().execute(query);
    }
}
