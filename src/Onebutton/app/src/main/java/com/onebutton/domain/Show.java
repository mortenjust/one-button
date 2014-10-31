package com.onebutton.domain;

/**
 * A show.
 */
public class Show {
    // The title
    private String title;

    // The category
    private int category;

    // The plot
    private String plot;

    // The imbd ID.
    private String imdbId;

    // Backdrop URL.
    private String backdropUrl;

    // The rating.
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Show show = (Show) o;

        if (category != show.category) return false;
        if (endtime != show.endtime) return false;
        if (Float.compare(show.rating, rating) != 0) return false;
        if (starttime != show.starttime) return false;
        if (backdropUrl != null ? !backdropUrl.equals(show.backdropUrl) : show.backdropUrl != null)
            return false;
        if (genre != null ? !genre.equals(show.genre) : show.genre != null) return false;
        if (imdbId != null ? !imdbId.equals(show.imdbId) : show.imdbId != null) return false;
        if (plot != null ? !plot.equals(show.plot) : show.plot != null) return false;
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
        result = 31 * result + (plot != null ? plot.hashCode() : 0);
        result = 31 * result + (imdbId != null ? imdbId.hashCode() : 0);
        result = 31 * result + (backdropUrl != null ? backdropUrl.hashCode() : 0);
        result = 31 * result + (rating != +0.0f ? Float.floatToIntBits(rating) : 0);
        result = 31 * result + (int) (starttime ^ (starttime >>> 32));
        result = 31 * result + (int) (endtime ^ (endtime >>> 32));
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (posterUrl != null ? posterUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Show{" +
                "title='" + title + '\'' +
                ", category=" + category +
                ", rating=" + rating +
                ", plot=" + plot +
                ", imdbid=" + imdbId +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                ", year='" + year + '\'' +
                ", genre='" + genre + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                '}';
    }

    public String getPosterUrl() {

        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public void setBackdropUrl(String backdropUrl) {
        this.backdropUrl = backdropUrl;
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

    /**
     * Returns the progress of a current show.
     *
     * @return returns progress as 0 to 100 in percent.
     */
    public int getProgress() {
        long now = System.currentTimeMillis() / 1000;
        long fullTime = getEndtime() - getStarttime();
        return (int) (((now - getStarttime()) * 100) / fullTime);
    }


}
