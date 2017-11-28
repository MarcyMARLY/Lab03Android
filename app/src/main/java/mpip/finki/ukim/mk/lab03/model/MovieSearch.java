package mpip.finki.ukim.mk.lab03.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ljupche on 24-Nov-17.
 */

public class MovieSearch {
    @SerializedName("Search")
    private List<Movie> search;
    @SerializedName("Response")
    private boolean response;
    @SerializedName("Error")
    private String error;

    public boolean getResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Movie> getSearch() {
        return search;
    }

    public void setSearch(List<Movie> search) {
        this.search = search;
    }
}
