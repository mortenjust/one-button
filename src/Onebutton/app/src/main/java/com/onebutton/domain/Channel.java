package com.onebutton.domain;

/**
 * A channel.
 */
public class Channel {

    // The name of the channel.
    private String name;

    // The channel number.
    private String number;

    // The current show.
    private Show currentShow;

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", currentShow=" + currentShow +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (currentShow != null ? !currentShow.equals(channel.currentShow) : channel.currentShow != null)
            return false;
        if (name != null ? !name.equals(channel.name) : channel.name != null) return false;
        if (number != null ? !number.equals(channel.number) : channel.number != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (currentShow != null ? currentShow.hashCode() : 0);
        return result;
    }

    public Show getCurrentShow() {
        return currentShow;
    }

    public void setCurrentShow(Show currentShow) {
        this.currentShow = currentShow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {

        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
