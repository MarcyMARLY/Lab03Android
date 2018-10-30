package mpip.finki.ukim.mk.lab03.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import mpip.finki.ukim.mk.lab03.model.Movie;

/**
 * Created by Ljupche on 27-Nov-17.
 */

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies m ORDER BY m.title")
    List<Movie> getAll();

    @Query("SELECT * FROM movies WHERE id=:id")
    Movie getById(String id);

    @Insert
    void insert(Movie movies);

    @Insert
    void insertAll(Movie... movies);

    @Update
    void update(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("DELETE FROM movies")
    void deleteAll();
}
