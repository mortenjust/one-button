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

    @Override
    public String toString() {
        return "Show{" +
                "title='" + title + '\'' +
                ", category=" + category +
                ", rating=" + rating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Show show = (Show) o;

        if (category != show.category) return false;
        if (Float.compare(show.rating, rating) != 0) return false;
        if (title != null ? !title.equals(show.title) : show.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + category;
        result = 31 * result + (rating != +0.0f ? Float.floatToIntBits(rating) : 0);
        return result;
    }
}
