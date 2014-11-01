package com.onebutton.domain;

import java.util.Comparator;

public class ChannelComparator implements Comparator<Channel> {
    @Override
    public int compare(Channel lhs, Channel rhs) {
        Float rating1 = lhs.getCurrentShow().getRating();
        Float rating2 = rhs.getCurrentShow().getRating();

        // Punish shows with their progress.
        rating1 = rating1 - (lhs.getCurrentShow().getProgress() / 10);
        rating2 = rating2 - (rhs.getCurrentShow().getProgress() / 10);

        // Ensure > 0.
        rating1 = rating1 < 0 ? 0 : rating1;
        rating2 = rating2 < 0 ? 0 : rating2;

        int compare = rating2.compareTo(rating1);
        if (compare == 0) {
            compare = lhs.getName().compareTo(rhs.getName());
        }
        return compare;
    }
}