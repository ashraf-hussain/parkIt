package com.project.parkit;

import com.google.gson.annotations.SerializedName;

class ReservationBody {
    @SerializedName("id")
    public int id;

    public ReservationBody(int minutes) {
        this.minutes = minutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @SerializedName("minutes")
    public int minutes;
}
