package mpip.finki.ukim.mk.lab03;

import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mpip.finki.ukim.mk.lab03.adapters.MovieAdapter;
import mpip.finki.ukim.mk.lab03.model.Movie;
import mpip.finki.ukim.mk.lab03.model.MovieSearch;
import mpip.finki.ukim.mk.lab03.persistence.MoviesDatabase;
import mpip.finki.ukim.mk.lab03.services.DownloadAndSaveMovieService;
import mpip.finki.ukim.mk.lab03.web.MoviesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static mpip.finki.ukim.mk.lab03.services.DownloadAndSaveMovieService.DATABASE_UPDATED;
import static mpip.finki.ukim.mk.lab03.services.DownloadAndSaveMovieService.NO_CONNECTION;

public class MoviesActivity extends AppCompatActivity {

    private MoviesAPI service;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private MovieQueryTask task;
    private UpdateCountriesReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        initRecyclerView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        this.receiver = new UpdateCountriesReceiver();

        initUI();
        invokeDataLoadingInService();

        Button search_button = (Button) findViewById(R.id.btn_search);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textView = (EditText) findViewById(R.id.search_text);
                String query = textView.getText().toString();
                initRecyclerView(query);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(DATABASE_UPDATED);
        this.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        if (task != null) {
            task.cancel(true);
        }
        super.onDestroy();
    }

    private void initRecyclerView(String query) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        makeMoviesSearch(query);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movies, menu);


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_button) {
            EditText textView = (EditText) findViewById(R.id.search_text);
            String query = textView.getText().toString();
            initRecyclerView(query);
        }

        return true;
    }*/

    public class MovieQueryTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String search = params[0];
            Gson gson = new GsonBuilder().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://www.omdbapi.com")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            service = retrofit.create(MoviesAPI.class);

            Call<MovieSearch> movies = service.getByTitle(search);
            final MovieSearch[] s = {null};
            movies.enqueue(new Callback<MovieSearch>() {
                @Override
                public void onResponse(Call<MovieSearch> call, Response<MovieSearch> response) {
                    s[0] = response.body();
                    movieAdapter = new MovieAdapter(MoviesActivity.this);
                    movieAdapter.setData(s[0].getSearch());
                    recyclerView.setAdapter(movieAdapter);
                }

                @Override
                public void onFailure(Call<MovieSearch> call, Throwable t) {

                }
            });
            return null;
        }
    }


    private void makeMoviesSearch(String query) {
        new MovieQueryTask().execute(query);
    }

    private class UpdateCountriesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final MoviesDatabase db = Room.databaseBuilder(
                    MoviesActivity.this,
                    MoviesDatabase.class,
                    "movies"
            ).build();

            List<Movie> movies = null;
            try {
                movies = new AsyncTask<Void, Void, List<Movie>>() {

                    @Override
                    protected List<Movie> doInBackground(Void... voids) {
                        return db.movieDao().getAll();
                    }

                    //                @Override
                    //                protected void onPostExecute(List<Movie> movies) {
                    //                    super.onPostExecute(movies);
                    //                }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            MoviesActivity.this.movieAdapter.setData(movies);
        }
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(this.getApplicationContext());
        recyclerView.setAdapter(movieAdapter);
    }


    private void invokeDataLoadingInService() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            EditText textView = (EditText) findViewById(R.id.search_text);
            String query = textView.getText().toString();
            this.startService(
                    new Intent(
                            this,
                            DownloadAndSaveMovieService.class
                    ).putExtra("search", query)
            );
        }
        else {
            final MoviesDatabase db = Room.databaseBuilder(
                    MoviesActivity.this,
                    MoviesDatabase.class,
                    "movies"
            ).build();

            List<Movie> movies = null;
            try {
                movies = new AsyncTask<Void, Void, List<Movie>>() {

                    @Override
                    protected List<Movie> doInBackground(Void... voids) {
                        return db.movieDao().getAll();
                    }

                    //                @Override
                    //                protected void onPostExecute(List<Movie> movies) {
                    //                    super.onPostExecute(movies);
                    //                }
                }.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            movieAdapter = new MovieAdapter(MoviesActivity.this);
            MoviesActivity.this.movieAdapter.setData(movies);
            recyclerView.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
        }
    }

    private void invokeDataLoading(MovieAdapter movieAdapter) {
        task = new MovieQueryTask();
        task.execute();
    }

    Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

    }

    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    private void loadInUiThread(MovieAdapter adapter) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.getString(R.string.movies_api))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesAPI service = retrofit.create(MoviesAPI.class);


        EditText title = (EditText)findViewById(R.id.search_text);
        String titleSearch = title.getText().toString();
        Call<MovieSearch> movies = service.getByTitle(titleSearch);
        final MovieSearch[] s = {null};
        movies.enqueue(new Callback<MovieSearch>() {
            @Override
            public void onResponse(Call<MovieSearch> call, Response<MovieSearch> response) {
                s[0] = response.body();
                movieAdapter = new MovieAdapter(MoviesActivity.this);
                movieAdapter.setData(s[0].getSearch());
                recyclerView.setAdapter(movieAdapter);
            }

            @Override
            public void onFailure(Call<MovieSearch> call, Throwable t) {

            }
        });
    }

    private void initUI() {
        setContentView(R.layout.activity_movies);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


}
