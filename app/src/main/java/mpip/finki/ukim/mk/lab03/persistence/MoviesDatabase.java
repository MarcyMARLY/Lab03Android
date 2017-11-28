package mpip.finki.ukim.mk.lab03.persistence;

/**
 * Created by Ljupche on 26-Nov-17.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import mpip.finki.ukim.mk.lab03.model.Movie;


@Database(entities = {Movie.class}, version = 1)
public abstract class MoviesDatabase extends RoomDatabase  {
    public abstract MovieDao movieDao();
}
