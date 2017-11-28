package mpip.finki.ukim.mk.lab03.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ljupche on 24-Nov-17.
 */
@Entity(tableName = "movies")
public class Movie {

    @PrimaryKey
    @SerializedName("imdbID")
    @NonNull public String id;

    @ColumnInfo
    @SerializedName("Title")
    public String title;

    @ColumnInfo
    @SerializedName("Year")
    public String year;

    @ColumnInfo
    @SerializedName("Poster")
    public String poster;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getPoster() {
        return poster;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
