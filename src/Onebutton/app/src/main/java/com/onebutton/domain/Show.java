package com.onebutton.domain;

/**
 * A show.
 */
public class Show {
    // The title of the show.
    private String title;

    // The category of a show.
    private int category;

    // The rating of a show.
    private float rating;

    // The start time.
    private long starttime;

    // The end time.
    private long endtime;

    // The year.
    private String year;

    // The genre
    private String genre;
    // The poster
    private String posterUrl;

    @Override
    public String toString() {
        return "Show{" +
                "title='" + title + '\'' +
                ", category=" + category +
                ", rating=" + rating +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                ", year='" + year + '\'' +
                ", genre='" + genre + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Show show = (Show) o;

        if (category != show.category) return false;
        if (endtime != show.endtime) return false;
        if (Float.compare(show.rating, rating) != 0) return false;
        if (starttime != show.starttime) return false;
        if (genre != null ? !genre.equals(show.genre) : show.genre != null) return false;
        if (posterUrl != null ? !posterUrl.equals(show.posterUrl) : show.posterUrl != null)
            return false;
        if (title != null ? !title.equals(show.title) : show.title != null) return false;
        if (year != null ? !year.equals(show.year) : show.year != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + category;
        result = 31 * result + (rating != +0.0f ? Float.floatToIntBits(rating) : 0);
        result = 31 * result + (int) (starttime ^ (starttime >>> 32));
        result = 31 * result + (int) (endtime ^ (endtime >>> 32));
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (posterUrl != null ? posterUrl.hashCode() : 0);
        return result;
    }

    public String getPosterUrl() {

        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getEndtime() {

        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getCategory() {

        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
