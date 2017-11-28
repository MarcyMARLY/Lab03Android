package mpip.finki.ukim.mk.lab03.services;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import mpip.finki.ukim.mk.lab03.MoviesActivity;
import mpip.finki.ukim.mk.lab03.R;
import mpip.finki.ukim.mk.lab03.adapters.MovieAdapter;
import mpip.finki.ukim.mk.lab03.model.Movie;
import mpip.finki.ukim.mk.lab03.model.MovieSearch;
import mpip.finki.ukim.mk.lab03.persistence.MoviesDatabase;
import mpip.finki.ukim.mk.lab03.web.MoviesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ljupche on 27-Nov-17.
 */

public class DownloadAndSaveMovieService extends IntentService {
    public static final String DATABASE_UPDATED = "mk.ukim.finki.mpip.lab03.DATABASE_UPDATED";
    public static final String NO_CONNECTION = "mk.ukim.finki.mpip.lab03.NO_CONNECTION";
    private MoviesAPI webApi;

    public DownloadAndSaveMovieService() {
        super("Download and Save movies");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.getString(R.string.movies_api))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.webApi = retrofit.create(MoviesAPI.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            String titleSearch = intent.getStringExtra("search");
            Call<MovieSearch> movies = this.webApi.getByTitle(titleSearch);
            final MovieSearch[] s = {null};
            movies.enqueue(new Callback<MovieSearch>() {
                @Override
                public void onResponse(Call<MovieSearch> call, final Response<MovieSearch> response) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            s[0] = response.body();
                            saveResultsInDb(s[0].getSearch());

                        }
                    });

                }

//                setDataLoadedPref();


                @Override
                public void onFailure(Call<MovieSearch> call, Throwable t) {

                }
            });

        }
        else {
            this.sendBroadcast(new Intent(NO_CONNECTION));
            return;
        }
    }

//    private void setDataLoadedPref() {
//        SharedPreferences prefs = this.getSharedPreferences(MoviesActivity.WEB_)
//    }

    private void saveResultsInDb(List<Movie> result) {
        MoviesDatabase db = Room.databaseBuilder(this, MoviesDatabase.class, "movies")
                .build();

        db.movieDao().deleteAll();
        db.movieDao().insertAll(result.toArray(new Movie[]{}));
        this.sendBroadcast(new Intent(DATABASE_UPDATED));
    }
}
