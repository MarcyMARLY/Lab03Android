package mpip.finki.ukim.mk.lab03.web;

import mpip.finki.ukim.mk.lab03.model.MovieDetail;
import mpip.finki.ukim.mk.lab03.model.MovieSearch;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ljupche on 24-Nov-17.
 */

public interface MoviesAPI {
    @GET("/?apikey=b3aab1e6")
    Call<MovieSearch> getByTitle(@Query("s") String title);

    @GET("/?apikey=b3aab1e6")
    Call<MovieDetail> getById(@Query("i") String id);
}
